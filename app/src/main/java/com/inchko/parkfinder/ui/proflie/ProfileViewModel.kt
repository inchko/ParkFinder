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
    private var generalUser: User? = null
    private var _poi = MutableLiveData<List<POI>>()
    private var _fz = MutableLiveData<List<FavZone>>()

    var poi: MutableLiveData<List<POI>> = _poi
    var fz: MutableLiveData<List<FavZone>> = _fz

    fun registerOrLoginUser(user: FirebaseUser) {

        viewModelScope.launch {
            val currentUser = User(
                id = user.uid,
                name = user.displayName!!,
                email = user.email!!
            )
            userRep.register(currentUser)
            Log.e("login", "register ok")
            generalUser = currentUser
        }
    }

    fun getPOI(userid: String) {
        viewModelScope.launch {
            _poi = MutableLiveData<List<POI>>().apply {
                value = poiRepo.getPOI(userid)
                poi.value = _poi.value
            }

            Log.e("rvpoi", "getPOI with $userid , size ${poi.value?.size}")
        }
    }


    fun removePOI(userid: String, id: String) {
        viewModelScope.launch {
            poiRepo.deletePOI(userid, id)
        }
        getPOI(userid)
    }

    fun getFavZones(userid: String) {
        viewModelScope.launch {
            _fz = MutableLiveData<List<FavZone>>().apply {
                value = favZoneRep.getFavZones(userid)
                fz.value = _fz.value
            }

        }
    }

    fun removeFZ(userid: String, id: String) {
        viewModelScope.launch {
            favZoneRep.deleteFavZones(userid, id)
        }
        getFavZones(userid)
    }


    fun updateGeneralUser(user: FirebaseUser) {
        generalUser = User(
            id = user.uid,
            name = user.displayName!!,
            email = user.email!!
        )
    }

}
