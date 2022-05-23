package ac.misohiyoko.navigatorCom

import android.Manifest
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
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

class ForeGroundNav : Service(){
    companion object{
        const val NOTIFICATION_ID = 10
        const val CHANNEL_ID = "primary_notification_channel"

        fun isActive():Boolean{

        }
    }
    private lateinit var fusedLocationClient:FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                p0.lastLocation.also {
                    ProcessGeolocation(it);
                }
            }
        }

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
        return super.stopService(name)
        stopLocationUpdate()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdate()
        stopSelf()
    }
    private fun stopLocationUpdate(){
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun createLocationRequest() : LocationRequest?{
        val locationRequest = LocationRequest.create()?.apply {
            interval = 5000
            fastestInterval = 2500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        return locationRequest
    }

    private fun ProcessGeolocation(location : Location){
        Log.d(this.javaClass.name, "${location.latitude} : latitude ${location.longitude}: longitude")
    }
}