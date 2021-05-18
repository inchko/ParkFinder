package com.inchko.parkfinder


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import com.inchko.parkfinder.service.BackgroundService
import com.inchko.parkfinder.ui.proflie.cutomizeProfile.addVehicle.AddVehicle
import com.inchko.parkfinder.ui.rvZones.RvZoneFragment
import com.inchko.parkfinder.ui.settings.SettingsActivity
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var drawerLayout: DrawerLayout? = null
    private var toolbar: Toolbar? = null
    private var navController: NavController? = null
    private var open: Boolean = false
    private val mainFragment = RvZoneFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        /* val fab: FloatingActionButton = findViewById(R.id.fab)
         fab.setOnClickListener { view ->
             Log.e("shower", "Button clicked, value: $open")
             if (!open) {
                 open = true
                 supportFragmentManager.beginTransaction().add(R.id.replacable, mainFragment)
                     .commit()

             } else {
                 open = false
                 mainFragment.test()
             }
         }*/
        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_map, R.id.nav_places, R.id.nav_settings
            ), drawerLayout
        )
        setupActionBarWithNavController(navController!!, appBarConfiguration)
        navView.setupWithNavController(navController!!)
        //traductions
        // Lingver.init(application, "es")
        // Lingver.getInstance().setLocale(this, "en", "")
        createNotificationChannel()
        intent?.handleIntent()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        //  Log.i("check", "up")
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            Log.e("settings", "menu clicked")
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)

    }

    override fun onStart() {
        super.onStart()
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        Log.e("background", "finshed")
        startService(Intent(this, BackgroundService::class.java))
    }

    override fun onResume() {
        super.onResume()
        stopService(Intent(this, BackgroundService::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.unregisterOnSharedPreferenceChangeListener(this)
        stopService(Intent(this, BackgroundService::class.java))

    }


    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        Log.e("pref", p1)
        if (p0 != null) {
            if (p1 == "Languages") {
                Log.e("pref", p0.getString(p1, "es"))
                val len = p0.getString(p1, "es")
                if (len != null) {
                    Lingver.getInstance().setLocale(application, len, "")
                    recreate()
                }
            }
        }

    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(0.toString(), name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    //APP ACTIONS

    private fun Intent.handleIntent() {
        Log.e("appActions", "bye bitch from google assistant")
        when (action) {
            // When the action is triggered by a deep-link, Intent.ACTION_VIEW will be used
            Intent.ACTION_VIEW -> handleDeepLink(data)
            // Otherwise start the app as you would normally do.
            else -> doNothing()
        }
    }

    private fun handleDeepLink(data: Uri?) {

        when (data?.path) {
            "/test" -> {
                val intent = Intent(this, AddVehicle::class.java)
                startActivity(intent)
            }
            "/stop" -> {
                // Stop the tracking service if any and return to home screen.
                Log.e("appActions", "bye bitch from google assistant")
            }
            else -> {
                // Path is not supported or invalid, start normal flow.
                doNothing()
            }
        }

    }

    private fun doNothing() {
        Log.e("appActions", "link not found bro")
    }

}
