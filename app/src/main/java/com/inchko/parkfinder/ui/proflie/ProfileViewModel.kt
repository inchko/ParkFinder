package com.inchko.parkfinder.ui.proflie

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.inchko.parkfinder.domainModels.User
import com.inchko.parkfinder.network.RepoFavZones
import com.inchko.parkfinder.network.RepoPOI
import com.inchko.parkfinder.network.RepoUsers
import com.inchko.parkfinder.network.Repository
import kotlinx.coroutines.launch
import javax.inject.Named


class ProfileViewModel @ViewModelInject constructor(
    private val rep: Repository,
    @Named("Test_Text") private val str: String,
    private val userRep: RepoUsers,
    private val favZoneRep: RepoFavZones,
    private val poiRepo: RepoPOI

) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = str
    }
    val text: LiveData<String> = _text

    private lateinit var generalUser: User
    private val _test = MutableLiveData<String>().apply {
        viewModelScope.launch {
            value = rep.test().test
        }
    }
    val test: LiveData<String> = _test;

    fun registerOrLoginUser(user: FirebaseUser) {

        viewModelScope.launch {
            val currentUser = User(
                id = user.uid,
                name = user.displayName!!,
                email = user.email!!
            )
            userRep.register(currentUser)
            Log.e("login", "register ok")
            generalUser = userRep.getUser(currentUser.id)
            val poi = poiRepo.getPOI("test")
            val fz = favZoneRep.getFavZones("test")

            Log.e(
                "login",
                "Name of the user: ${generalUser.name}, number of  POI : ${poi.size}, number of fav zones: ${fz[2].id} "
            )
        }
    }
}
