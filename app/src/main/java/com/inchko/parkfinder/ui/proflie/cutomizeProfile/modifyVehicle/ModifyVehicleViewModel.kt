package com.inchko.parkfinder.ui.proflie.cutomizeProfile.modifyVehicle

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inchko.parkfinder.domainModels.Vehicles
import com.inchko.parkfinder.network.RepoUsers
import kotlinx.coroutines.launch

class ModifyVehicleViewModel @ViewModelInject constructor(
    private val userRep: RepoUsers

) : ViewModel() {

    fun modCar(id: String, v: Vehicles) {
        viewModelScope.launch {
            Log.e("modV", "enter view model")
            userRep.updateVehicle(id, v.id, v)
        }
    }
}

