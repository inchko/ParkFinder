package com.inchko.parkfinder.ui.rvZones

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inchko.parkfinder.domainModels.Zone
import com.inchko.parkfinder.network.Repository
import kotlinx.coroutines.launch

class RvZoneViewModel @ViewModelInject constructor(private val rep: Repository) : ViewModel() {

    private val _num = MutableLiveData<Int>().apply {
        value = 0
    }
    var num: LiveData<Int> = _num
    var test = 0
    fun sum() {
        test++
    }

    private val _zones = MutableLiveData<List<Zone>>().apply {
        viewModelScope.launch {
            value = rep.readZones()
        }
    }
    var zones : LiveData<List<Zone>> = _zones
}