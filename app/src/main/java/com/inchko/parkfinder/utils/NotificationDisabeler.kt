package com.inchko.parkfinder.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.inchko.parkfinder.MainActivity
import com.inchko.parkfinder.R

class NotificationDisabeler : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_disabeler)
        val ea =
            this.getSharedPreferences("watchZone", Context.MODE_PRIVATE)?.getInt("estadoActual", -1)
        if (ea == 0) {
            this.getSharedPreferences("watchZone", Context.MODE_PRIVATE)?.edit()
                ?.putString("zoneID", "")?.apply()
        }
        val act = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(act)

    }
}