package com.inchko.parkfinder.ui.map

import android.location.Location
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.inchko.parkfinder.domainModels.Test
import com.inchko.parkfinder.domainModels.Ubi
import com.inchko.parkfinder.domainModels.Zone
import com.inchko.parkfinder.network.Repository
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    private val _zones = MutableLiveData<List<Zone>>().apply {
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
        }
    }
    var zones: MutableLiveData<List<Zone>> = _zones

    fun updateZones(){
        zones  = MutableLiveData<List<Zone>>().apply {
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
            }
        }
    }
    fun updateCurrentLocation(l: LatLng) {
        currentLocation = l
        Log.e("holder", "updatedCurrent loc: $currentLocation")
    }
}
