package net.misohiyoko.navigatorCom

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.os.*
import android.os.PowerManager.WakeLock
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class ForeGroundNav : Service(), TextToSpeech.OnInitListener, CoroutineScope{
    companion object{
        const val NOTIFICATION_ID = 10
        const val CHANNEL_ID = "primary_notification_channel"
        const val ACTION_IS_ACTIVE = "net.hiyoko.NavCom.ForeGroundNaV.Active"
        const val ACTION_DESTINATION = "net.hiyoko.NavCom.ForeGroundNaV.Action.Destination"
        const val UTTERANCE_ID = "net.hiyoko.NavCom.ForeGroundNaV:Utterance.id"
        const val WAKELOCK_TAG = "net.hiyoko.NavCom.ForeGroundNaV:WakeLockTag"
        fun createIntent(context: Context) = Intent(context, ForeGroundNav::class.java)
        /// is Navigation Active
        fun isActive(context:Context):Boolean{
            return LocalBroadcastManager.getInstance(context).sendBroadcast(Intent(ACTION_IS_ACTIVE))
        }
    }
    private lateinit var fusedLocationClient:FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var destination:NamedLocation
    private lateinit var locationProfile:GPSDataDump
    private val localBroadcastManager by lazy {LocalBroadcastManager.getInstance(applicationContext)}
    ///Dummy Receiver
    private val broadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            //Nasi
        }
    }
    //TTS
    private lateinit var textToSpeech:TextToSpeech
    private val ttsParams:Bundle = Bundle()
    private var isTTSAvailable:Boolean = false
    ///Wake Lock
    private lateinit var powerManager:PowerManager
    private lateinit var wakeLock:WakeLock
    ///Announce CoolDown
    private var announcedDate:Long = System.currentTimeMillis()
    ///Coroutine
    private val job: Job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = this.job
    override fun onCreate() {
        super.onCreate()
        ///locationService
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        localBroadcastManager.registerReceiver(broadcastReceiver, IntentFilter(ACTION_IS_ACTIVE))
        powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK_TAG)
        ttsParams.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1f)
        textToSpeech = TextToSpeech(this, this)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                p0.lastLocation.also {
                    processGeolocationData(it);
                }
            }

        }
        locationProfile = GPSDataDump("destination")
        if (intent != null) {
            destination = NamedLocation(intent.getBundleExtra(MainActivity.INTENT_DEST) ?: return START_NOT_STICKY)
        }else{
            destination = NamedLocation(0.0,0.0,"","")
            return START_NOT_STICKY
        }



        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let {notificationIntent ->
                notificationIntent.putExtra(ACTION_IS_ACTIVE, true)
                notificationIntent.putExtra(ACTION_DESTINATION, destination.makeBundle())
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            }
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(resources.getString(R.string.notification_title))
            .setContentText(resources.getString(R.string.notification_text))
            .setSmallIcon(R.drawable.icon)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        startLocationUpdate()

        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    //@SuppressLint("MissingPermission")
    @SuppressLint("MissingPermission")
    private fun startLocationUpdate(){
        val locationRequest = createLocationRequest() ?: return
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

    }

    override fun stopService(name: Intent?): Boolean {
        stopLocationUpdate()

        return super.stopService(name)

    }
    private fun shutDownTTS(){
        isTTSAvailable = false
        textToSpeech.stop()
        textToSpeech.shutdown()

    }


    private fun speakAnnouncementToDestination(accurateLocation: Location) {
        speakText(
            resources.getString(R.string.distance_to_destination_is) +
                    (accurateLocation.distanceTo(destination.getLocation()).toInt())
                        .toString() +
                    resources.getString(R.string.meter) + ". " +
                    resources.getString(R.string.direction_to_destination) +
                    if (Locale.getDefault() == Locale.JAPAN)
                        changeAlphabetHalfToFull(
                            (deltaAngleTo(
                                accurateLocation,
                                destination.getLocation()
                            ) / 30).toInt().toString()
                        ) + resources.getString(R.string.times_direction) + ". "
                    else (deltaAngleTo(
                        accurateLocation,
                        destination.getLocation()
                    ) / 30).toInt().toString() + resources.getString(R.string.times_direction) + ". "
        )
    }

    private fun cancelCoroutine(){
        this.launch(Job()) {
            job.cancelAndJoin()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdate()
        shutDownTTS()
        cancelCoroutine()
        localBroadcastManager.unregisterReceiver(broadcastReceiver)
        stopSelf()
    }
    private fun stopLocationUpdate(){

        fusedLocationClient.removeLocationUpdates(locationCallback)

        this.openFileOutput(System.currentTimeMillis().toString()+"Time:"+"Destination"+destination.toString() + ".csv", Context.MODE_PRIVATE).use {
            val textToBeWrite = locationProfile.writeStringCSV()
            val bytesToBeWrite = textToBeWrite.toByteArray()
            it.write(bytesToBeWrite)
        }
    }

    private fun createLocationRequest() : LocationRequest?{
        val locationRequest = LocationRequest.create().apply {
            interval = 4000
            fastestInterval = 3000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        return locationRequest
    }

    //TTS init
    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){
            val locale = Locale.getDefault()
            if(textToSpeech.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE){
                textToSpeech.apply {
                    setLanguage(locale)

                    setOnUtteranceProgressListener(object : UtteranceProgressListener(){
                        override fun onDone(utteranceId : String?) {
                            if(wakeLock.isHeld){
                                wakeLock.release()
                            }
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onError(p0: String?) {

                        }

                        override fun onStart(utteranceId : String?) {
                            wakeLock.acquire(10*60*1000L /*10 minutes*/)
                        }

                        override fun onError(utteranceId : String, errorCode : Int) {
                            if(wakeLock.isHeld){
                                wakeLock.release()
                            }
                        }

                    })

                }


                isTTSAvailable = true
            }


        }
    }

    private fun speakText(text:String):Boolean{
        return if(isTTSAvailable){
            textToSpeech.speak(
                text,
                TextToSpeech.QUEUE_ADD,
                ttsParams,
                UTTERANCE_ID
            )
            true
        }else{
            false
        }
    }
    /*
    ///没になりました
    private fun startAnnouncement(){
        this.launch {
            var announcedTime:Int = 0
            while (true){

                announcedTime++
                Log.d(javaClass.name,"$announcedTime")
                delay(1000)
            }
        }
    }
    */
    private fun processGeolocationData(location : Location){
        Log.d(this.javaClass.name, "${location.latitude} : latitude ${location.longitude}: longitude ${location.accuracy}:Acc ${location.bearing}:Bearing")
        ///speakText("${location.latitude} : latitude ${location.longitude}: longitude")
        //val textToBeWrite = "${location.latitude} : latitude ${location.longitude}: longitude"
        val rangeToDestination = location.distanceTo(destination.getLocation())
        val headingToDestination = location.bearingTo(destination.getLocation())
        val headingDelta = deltaAngleTo(location,destination.getLocation())
        Log.d(this.javaClass.name, "${headingToDestination}:Heading ${rangeToDestination}:range ${headingDelta}:delta")

        Log.d(this.javaClass.name, "${(System.currentTimeMillis() - announcedDate)}")
        locationProfile.locationList.add(location)
        val accurateLocation = getAccurateLastLocation()
        if(accurateLocation != null){
            Log.d(this.javaClass.name, "${accurateLocation.bearing}")
            when{

                System.currentTimeMillis() - announcedDate >= 30*1000-> {
                    speakAnnouncementToDestination(accurateLocation)
                    announcedDate = System.currentTimeMillis()
                }
                System.currentTimeMillis() - announcedDate >= 15*1000->{
                    if(deltaAngleToAbs(
                            accurateLocation,
                            destination.getLocation()
                        ) > 60){
                        speakAnnouncementToDestination(accurateLocation)
                        announcedDate = System.currentTimeMillis()
                    }

                }
            }
        }
    }

    private fun getAccurateLastLocation():Location?{

        var lastLocations: List<Location> = if(Build.VERSION.SDK_INT > 25){
            locationProfile.locationList.filterIndexed{
                    index, it ->
                    it.hasBearing() && it.accuracy <= 20f
            }
        }else{
            locationProfile.locationList.filterIndexed{ index, it ->
                    it.hasBearing() && it.accuracy <= 20f
            }
        }
        ///50mいないのみ
        val lastLocation = lastLocations.last()
        lastLocations = lastLocations.filter {
            it ->
            it.distanceTo(lastLocation) < 50
        }

        Log.d(this.javaClass.name, "${getAngularRange( lastLocations.map { it.bearing })}:Range,${lastLocations.lastOrNull()}:Result")
        ///speakText("${getAngularRange( lastLocations.map { it.bearing }).toInt()}")
        return if(getAngularRange( lastLocations.map { it.bearing }) < 31f){
            lastLocations.lastOrNull()
        } else{
            null
        }

    }


}