package com.inchko.parkfinder.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.inchko.parkfinder.MainActivity
import com.inchko.parkfinder.R
import com.inchko.parkfinder.network.ServiceRepo
import com.inchko.parkfinder.utils.NotificationDisabeler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.timerTask
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class BackgroundService : Service(), CoroutineScope {

    private var coroutineJob: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob

    val scope = CoroutineScope(coroutineContext + Dispatchers.Main)

    @Inject
    lateinit var repo: ServiceRepo


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private var counter = 0
    private val time = Timer()
    override fun onCreate() {
        super.onCreate()
        Log.e("background", "created")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "test"
            val descriptionText = "test channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(2.toString(), name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        // startService(Intent(this, backgroundService::class.java))
    }

    override fun onDestroy() {
        //  stopService(Intent(this, backgroundService::class.java))
        time.cancel()
        coroutineJob.cancel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flap: Int, startId: Int): Int {
        val context = this
        val notification: Notification = Notification.Builder(this, 0.toString())
            .setContentTitle("testBackground")
            .setContentText("this is a test")
            .build()
        startForeground(2001, notification)

        val act = Intent(this, NotificationDisabeler::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            /*  context.getSharedPreferences("watchZone", Context.MODE_PRIVATE)?.edit()
                  ?.putString("zoneID", "")?.apply()*/
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(act)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val builder = NotificationCompat.Builder(this, 2.toString())
            .setSmallIcon(R.drawable.parking_marker_icon_background)
            .setContentTitle(getString(R.string.notificationHeader))
            .setContentText(getString(R.string.notificationDescription))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(getString(R.string.notificationDescription))
            )

            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true)


        for (i in 1..1000) {
            val p = timerTask {
                val sp = context.getSharedPreferences("watchZone", Context.MODE_PRIVATE)
                val shpf = context.getSharedPreferences("vehicle", Context.MODE_PRIVATE)
                val nameZone = sp?.getString("zoneID", "")
                val user = sp?.getString("zoneUserID", "")
                val typeOfCar = shpf?.getInt("type", -1)
                val sizeOfCar = shpf?.getInt("size", -1)
                val userCar = shpf?.getString("caruserID", "")
                if (Firebase.auth.currentUser != null && nameZone != "" && Firebase.auth.currentUser.uid == user) {
                    scope.launch {
                        val z = nameZone?.let { repo.readZone(it) }
                        if (z != null) {
                            if (typeOfCar == -1) {
                                if (z.plazasLibres == 0) {
                                    with(context.let { NotificationManagerCompat.from(it) }) {
                                        // notificationId is a unique int for each notification that you must define
                                        if (builder != null) {
                                            this.notify(1, builder.build())
                                        }
                                    }
                                }
                            } else {
                                if (typeOfCar == 1 && Firebase.auth.currentUser.uid == userCar) {
                                    if (z.plazasMl == 0) {
                                        with(context.let { NotificationManagerCompat.from(it) }) {
                                            // notificationId is a unique int for each notification that you must define
                                            if (builder != null) {
                                                this.notify(1, builder.build())
                                            }
                                        }
                                    }
                                } else {
                                    if (sizeOfCar == 1 && Firebase.auth.currentUser.uid == userCar) {
                                        if (z.plazasPl == 0) {
                                            with(context.let { NotificationManagerCompat.from(it) }) {
                                                // notificationId is a unique int for each notification that you must define
                                                if (builder != null) {
                                                    this.notify(1, builder.build())
                                                }
                                            }
                                        }
                                    } else if (sizeOfCar == 0 && Firebase.auth.currentUser.uid == userCar) {
                                        if (z.plazasGl == 0) {
                                            with(context.let { NotificationManagerCompat.from(it) }) {
                                                // notificationId is a unique int for each notification that you must define
                                                if (builder != null) {
                                                    this.notify(1, builder.build())
                                                }
                                            }
                                        }

                                    }
                                }
                            }
                        }//z!=null
                    }
                }
            }
            time.schedule(p, (60000 * i).toLong())

        }


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




