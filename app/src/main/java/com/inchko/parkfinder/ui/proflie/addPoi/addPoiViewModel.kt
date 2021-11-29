package com.inchko.parkfinder.ui.proflie.addPoi

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inchko.parkfinder.domainModels.POI
import com.inchko.parkfinder.network.RepoPOI
import kotlinx.coroutines.launch

class addPoiViewModel @ViewModelInject constructor(
    private val poiRepo: RepoPOI

) : ViewModel() {

    fun addPOI(poi: POI) {
        viewModelScope.launch {
            poiRepo.addPOI(poi)
        }
    }
}