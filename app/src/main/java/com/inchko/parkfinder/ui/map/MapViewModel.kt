package com.inchko.parkfinder.ui.map

import android.location.Location
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.inchko.parkfinder.domainModels.Test
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
}
