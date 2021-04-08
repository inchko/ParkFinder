package com.inchko.parkfinder.ui.map

import android.location.Location
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.inchko.parkfinder.domainModels.Test
import com.inchko.parkfinder.domainModels.Ubi
import com.inchko.parkfinder.domainModels.Zone
import com.inchko.parkfinder.network.Repository
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sqrt

class MapViewModel @ViewModelInject constructor(private val rep: Repository) : ViewModel() {


    private val _location = MutableLiveData<LatLng>().apply {
        viewModelScope.launch {
            val temp: Test = rep.test()
            val tempLoc = LatLng(temp.lat!!, temp.long!!)

            value = tempLoc
        }

    }
    val location: LiveData<LatLng> = _location

    var currentLocation: LatLng? = null
    var mMap: GoogleMap?= null

    private val _zones = MutableLiveData<List<Zone>>()

    var zones: MutableLiveData<List<Zone>> = _zones

    fun updateZones() {
        zones = MutableLiveData<List<Zone>>().apply {
            viewModelScope.launch {
                value = currentLocation?.let {
                    rep.readZonesByLoc(
                        Ubi(
                            1,
                            it.longitude, it.latitude
                        )
                    )
                }
                // value = rep.readZones()
                calculateDistance()
            }

        }
    }

    private fun calculateDistance() {
        if (zones.value != null) {
            for (z in zones.value!!) {
                z.distancia = currentLocation?.let { distance(it, LatLng(z.lat!!, z.long!!)) }
                Log.e("vm","${z.distancia}")
            }
        }
    }

    fun updateCurrentLocation(l: LatLng) {
        currentLocation = l
        Log.e("holder", "updatedCurrent loc: $currentLocation")
    }

    fun distance(cl: LatLng, zl: LatLng): Double {
        val p = 0.017453292519943295;    // PI / 180
        val a = 0.5 - cos((zl.latitude - cl.latitude) * p) / 2 +
                cos(cl.latitude * p) * cos(zl.latitude * p) *
                (1 - cos((zl.longitude - cl.longitude) * p)) / 2;

        return 12742 * asin(sqrt(a)); // 2 * R; R = 6371 km R is radius of earth
    }

    fun updateGM(g:GoogleMap){
        mMap=g
    }
}
