package com.inchko.parkfinder.ui.proflie

import android.app.Application
import android.location.Geocoder
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseUser
import com.inchko.parkfinder.domainModels.FavZone
import com.inchko.parkfinder.domainModels.POI
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

    private var _poi = MutableLiveData<List<POI>>().apply {
        viewModelScope.launch {
            value = poiRepo.getPOI("test")
        }
    }

    private var _fz = MutableLiveData<List<FavZone>>().apply {
        viewModelScope.launch {
            value = favZoneRep.getFavZones("test")
        }
    }


    private lateinit var generalUser: User
    var poi: LiveData<List<POI>> = _poi
    var fz: LiveData<List<FavZone>> = _fz

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
            val fz = favZoneRep.getFavZones("test")

            Log.e(
                "login",
                "Name of the user: ${generalUser.name}, number of fav zones: ${fz[2].id} "
            )
        }
    }

    fun getPOI() {
        viewModelScope.launch {
            _poi = MutableLiveData<List<POI>>().apply {
                value = poiRepo.getPOI("test")
            }
        }

    }

    fun getFavZones() {
        viewModelScope.launch {
            _fz = MutableLiveData<List<FavZone>>().apply {
                value = favZoneRep.getFavZones("test")
            }
        }

    }

}
