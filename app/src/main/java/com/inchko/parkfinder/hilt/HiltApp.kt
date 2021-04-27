package com.inchko.parkfinder.hilt

import android.app.Application
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HiltApp : Application() {
    /* override fun onCreate() {
         super.onCreate()
         //traductions
         Lingver.init(this, "en")
     }*/
}