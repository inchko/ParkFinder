package com.inchko.parkfinder.ui.proflie.cutomizeProfile.addVehicle

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inchko.parkfinder.domainModels.Vehicles
import com.inchko.parkfinder.network.RepoUsers
import kotlinx.coroutines.launch

class AddVehicleViewModel @ViewModelInject constructor(
    private val userRep: RepoUsers

) : ViewModel() {

    fun addCar(id:String, v: Vehicles) {
        viewModelScope.launch {
            userRep.addVehicle(id,v)
        }
    }
}