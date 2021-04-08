package com.inchko.parkfinder.network

import com.inchko.parkfinder.network.models.DirectionsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RouteService {
    //https://maps.googleapis.com/maps/api/directions/json?origin=ADDRESS_1&destination=ADDRESS_2&waypoints=ADDRESS_X|ADDRESS_Y&key=API_KEY
    @GET("directions/json")
     fun getDirection(@Query("origin") origin:String, @Query("destination") destination: String, @Query("key") key: String) : Call<DirectionsResponse>
}