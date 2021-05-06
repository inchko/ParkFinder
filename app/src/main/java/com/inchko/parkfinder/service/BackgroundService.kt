package com.inchko.parkfinder.service

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.inchko.parkfinder.network.Repository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BackgroundService : Service() {
    @Inject
    lateinit var repo: Repository
    override fun onBind(intent: Intent): IBinder? {
        return null
    }


    override fun onCreate() {
        Log.e("background", "created")
        // startService(Intent(this, backgroundService::class.java))
    }

    override fun onDestroy() {
        //  stopService(Intent(this, backgroundService::class.java))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification: Notification = Notification.Builder(this, 0.toString())
            .setContentTitle("testBackground")
            .setContentText("this is a test")
            .build()
        startForeground(2001, notification)
        /*
        val sp = this?.getSharedPreferences("watchZone", Context.MODE_PRIVATE)
        val nameZone = sp?.getString("zoneID", "")
        if (nameZone != null) {
            val z = repo.readZone(nameZone)
            if (z.plazasLibres == 0) Log.e("background", "Las plazas libres son nulas")
        }*/
        return Service.START_STICKY
    }
}




