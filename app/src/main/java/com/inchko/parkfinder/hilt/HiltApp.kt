package com.inchko.parkfinder.hilt

import android.app.Application
import com.google.android.libraries.places.api.Places
import com.inchko.parkfinder.R
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HiltApp : Application() {
    override fun onCreate() {
        super.onCreate()
        //traductions
        Lingver.init(this, "es")
        // Initialize the SDK
        Places.initialize(applicationContext, getString(R.string.google_api_key))

        // Create a new PlacesClient instance
        val placesClient = Places.createClient(this)
    }
}