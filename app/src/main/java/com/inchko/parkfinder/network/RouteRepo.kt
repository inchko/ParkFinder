package com.inchko.parkfinder.network

import com.google.android.gms.maps.model.LatLng
import com.inchko.parkfinder.network.models.DirectionsResponse
import retrofit2.Call
import javax.inject.Inject

class RouteRepo @Inject constructor(private val rs: RouteService) : RouteRepoInterface {

    override fun getDirections(origin: LatLng, destination: LatLng): Call<DirectionsResponse> {
        val og = origin.latitude.toString() + "," + origin.longitude.toString()
        val des = destination.latitude.toString() + "," + destination.longitude.toString()
        return rs.getDirection(og, des, "AIzaSyAlRQ6D8rFLvjFhvFHqgeQzmzYyLR4bGGE")


    }
}