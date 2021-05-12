package com.inchko.parkfinder.ui.rvZones

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.inchko.parkfinder.domainModels.FavZone
import com.inchko.parkfinder.domainModels.Zone
import com.inchko.parkfinder.network.RepoFavZones
import com.inchko.parkfinder.network.Repository
import com.inchko.parkfinder.network.RouteRepo
import com.inchko.parkfinder.network.models.DirectionsResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RvZoneViewModel @ViewModelInject constructor(
    private val rep: Repository,
    private val rr: RouteRepo,
    private val fzRepo: RepoFavZones
) : ViewModel() {

    private var _response = MutableLiveData<Response<DirectionsResponse>>()

    var response = MutableLiveData<Response<DirectionsResponse>>().apply {
        value = null
    }

    fun getDirections(origin: LatLng, destination: LatLng) {
        rr.getDirections(origin, destination).enqueue(object : Callback<DirectionsResponse> {
            override fun onResponse(
                call: Call<DirectionsResponse>,
                res: Response<DirectionsResponse>
            ) {
                _response = MutableLiveData<Response<DirectionsResponse>>().apply {
                    value = res
                    Log.e("rv", "getDirections response recibed value $value")
                }
                response.value = _response.value

            }

            override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                Log.e("error", t.localizedMessage)
            }
        })

    }

    fun addZone(favZone: FavZone) {
        viewModelScope.launch {
            fzRepo.addFavZone(favZone)
        }
    }

}