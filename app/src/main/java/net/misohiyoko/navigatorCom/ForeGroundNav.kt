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
import android.os.Bundle
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
import java.util.jar.Attributes.Name

class ForeGroundNav : Service(){
    companion object{
        const val NOTIFICATION_ID = 10
        const val CHANNEL_ID = "primary_notification_channel"
        const val ACTION_IS_ACTIVE = "net.hiyoko.NavCom.ForeGroundNaV.Active"
        const val ACTION_DESTINATION = "net.hiyoko.NavCom.ForeGroundNaV.Action.Destination"
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
    override fun onCreate() {
        super.onCreate()
        ///locationService
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

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdate()
        localBroadcastManager.unregisterReceiver(broadcastReceiver)
        stopSelf()
    }
    private fun stopLocationUpdate(){

        fusedLocationClient.removeLocationUpdates(locationCallback)

        this.openFileOutput(System.currentTimeMillis().toString() + ".csv", Context.MODE_PRIVATE).use {
            val textToBeWrite = locationProfile.writeStringCSV()
            val bytesToBeWrite = textToBeWrite.toByteArray()
            it.write(bytesToBeWrite)
        }
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

        //val textToBeWrite = "${location.latitude} : latitude ${location.longitude}: longitude"
        locationProfile.locationList.add(location)
    }

    public fun getLastLocation():Location{
        return locationProfile.locationList.last()
    }
}