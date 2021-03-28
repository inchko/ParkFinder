package com.inchko.parkfinder.ui.places

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inchko.parkfinder.network.Repository
import kotlinx.coroutines.launch
import javax.inject.Named


class PlacesViewModel @ViewModelInject constructor(
    private val rep: Repository,
    @Named("Test_Text") private val str: String
) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = str
    }
    val text: LiveData<String> = _text


    private val _test = MutableLiveData<String>().apply {
        viewModelScope.launch {
            value = rep.test().test
        }
    }
    val test: LiveData<String> = _test;
}
