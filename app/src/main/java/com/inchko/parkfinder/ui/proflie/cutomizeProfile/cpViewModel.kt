package com.inchko.parkfinder.ui.proflie.cutomizeProfile

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inchko.parkfinder.domainModels.POI
import com.inchko.parkfinder.domainModels.Vehicles
import com.inchko.parkfinder.network.RepoFavZones
import com.inchko.parkfinder.network.RepoPOI
import com.inchko.parkfinder.network.RepoUsers
import com.inchko.parkfinder.network.Repository
import kotlinx.coroutines.launch
import javax.inject.Named

class cpViewModel @ViewModelInject constructor(
    private val userRep: RepoUsers

) : ViewModel() {


    private var _cars = MutableLiveData<List<Vehicles>>()
    var cars: MutableLiveData<List<Vehicles>> = _cars

    fun getVehicles(id: String) {
        Log.e("custom", "Reached vm")
        viewModelScope.launch {
            _cars = MutableLiveData<List<Vehicles>>().apply {
                value = userRep.getVehicles(id)
                Log.e("custom", "size ${value!!.size}")
            }
            cars.value = _cars.value
        }

    }

    fun deleteVehicles(id: String, vid: String) {
        viewModelScope.launch {
            userRep.deleteVehicles(id, vid)
        }
    }
}