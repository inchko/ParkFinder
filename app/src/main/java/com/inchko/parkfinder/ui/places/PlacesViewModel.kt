package com.inchko.parkfinder.ui.places

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.inchko.parkfinder.Apis.Repository


class PlacesViewModel @ViewModelInject constructor(private val rep:Repository):  ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = rep.text
    }
    val text: LiveData<String> = _text
}