package com.inchko.parkfinder.ui.places

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlacesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Fuck my life it finally worked"
    }
    val text: LiveData<String> = _text
}