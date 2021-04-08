package com.inchko.parkfinder.network

import com.google.android.gms.maps.model.LatLng
import com.inchko.parkfinder.network.models.DirectionsResponse
import retrofit2.Call

interface RouteRepoInterface {
    fun getDirections(origin: LatLng, destination: LatLng): Call<DirectionsResponse>
}