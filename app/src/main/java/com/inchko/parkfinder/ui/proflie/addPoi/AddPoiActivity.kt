package com.inchko.parkfinder.ui.proflie.addPoi

import android.app.Activity
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.model.LatLng
import com.inchko.parkfinder.MainActivity
import com.inchko.parkfinder.R
import com.inchko.parkfinder.domainModels.POI
import com.inchko.parkfinder.ui.proflie.ProfileFragment
import com.inchko.parkfinder.ui.proflie.cutomizeProfile.CustomizeProfile
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddPoiActivity : AppCompatActivity() {
    private lateinit var add: Button
    private lateinit var exit: Button
    private lateinit var dir: EditText
    private lateinit var name: EditText

    private val apViewModel: addPoiViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addpoi_view)
        add = findViewById(R.id.apSend)
        add.setOnClickListener {
            createPOI()
        }
        exit = findViewById(R.id.apCancel)
        exit.setOnClickListener {
            exitMenu()
        }
        dir = findViewById(R.id.apDirection)
        name = findViewById(R.id.apName)
    }

    private fun createPOI() {
        Log.e("addpoi", "add clicked")
        val poiDirection = dir.text
        val poiName = name.text.toString()
        val coords = getCoords(poiDirection.toString())
        val poi = POI(
            id = "0,",
            nombre = poiName,
            userID = intent.getStringExtra("user"),
            location = poiDirection.toString(),
            lat = coords.latitude.toString(),
            long = coords.longitude.toString()
        )
        apViewModel.addPOI(poi)
        finish()
    }

    private fun exitMenu() {
        Log.e("addpoi", "exit clicked")
        finish()
    }

    private fun getCoords(dir: String): LatLng {
        val gc = Geocoder(this)
        var result = gc.getFromLocationName(dir, 1)
        while (result.isEmpty()) {
            result = gc.getFromLocationName(dir, 1)
        }
        return (LatLng(result.get(0).latitude, result.get(0).longitude))
    }
}