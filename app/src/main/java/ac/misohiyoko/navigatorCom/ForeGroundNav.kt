package ac.misohiyoko.navigatorCom

import android.Manifest
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.io.PrintWriter

class ForeGroundNav : Service(){
    companion object{
        const val NOTIFICATION_ID = 10
        const val CHANNEL_ID = "primary_notification_channel"
        const val ACTION_IS_ACTIVE = "ac.hiyoko.NavCom.ForeGroundNaV.Active"
        fun createIntent(context: Context) = Intent(context, ForeGroundNav::class.java)
        fun isActive(context:Context):Boolean{
            return LocalBroadcastManager.getInstance(context).sendBroadcast(Intent(ACTION_IS_ACTIVE))
        }
    }
    private lateinit var fusedLocationClient:FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var dataDumpStream:FileOutputStream;
    private lateinit var printWriter:PrintWriter;
    private val localBroadcastManager by lazy {LocalBroadcastManager.getInstance(applicationContext)}
    private val broadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            //Nasi
        }
    }
    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        localBroadcastManager.registerReceiver(broadcastReceiver, IntentFilter(ACTION_IS_ACTIVE))
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
        dataDumpStream = openFileOutput(getNowDate(), MODE_APPEND)
        printWriter = PrintWriter(OutputStreamWriter(dataDumpStream,"UTF-8"))

        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let {notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
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

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdate()
        localBroadcastManager.unregisterReceiver(broadcastReceiver)
        stopSelf()
    }
    private fun stopLocationUpdate(){

        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun createLocationRequest() : LocationRequest?{
        val locationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 2500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        return locationRequest
    }

    private fun processGeolocationData(location : Location){
        Log.d(this.javaClass.name, "${location.latitude} : latitude ${location.longitude}: longitude")
        val textToBeWrite = "${location.latitude} : latitude ${location.longitude}: longitude"
        dataDumpStream.write(textToBeWrite.toByteArray())
    }
}