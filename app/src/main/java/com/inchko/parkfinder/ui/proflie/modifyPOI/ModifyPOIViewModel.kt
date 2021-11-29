package com.inchko.parkfinder.ui.proflie.modifyPOI

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inchko.parkfinder.domainModels.POI
import com.inchko.parkfinder.network.RepoPOI
import com.inchko.parkfinder.network.Repository
import kotlinx.coroutines.launch

class ModifyPOIViewModel @ViewModelInject constructor(
    private val rep: RepoPOI
) : ViewModel() {

    fun send(poi: POI) {
        viewModelScope.launch {
            rep.updatePOI(poi.userID, poi.id, poi)
        }
    }
}