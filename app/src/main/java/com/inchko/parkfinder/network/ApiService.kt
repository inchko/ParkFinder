package com.inchko.parkfinder.network

import com.inchko.parkfinder.network.models.TestDTO
import com.inchko.parkfinder.network.models.UbiDTO
import com.inchko.parkfinder.network.models.UserDTO
import com.inchko.parkfinder.network.models.ZoneDTO
import retrofit2.http.*

interface ApiService {
    //URL noseque.com.a?^[Query1][Query2]
    @GET("api/test-location")
    suspend fun test(/*
    @Header("value") nombre:Tipo, @Query("Query1")nombre:Tipo*/
    ): TestDTO

    @GET("api/zones")
    suspend fun readZones(): List<ZoneDTO>

    @POST("api/zonesLoc")
    suspend fun readZonesByLoc(@Body ubication: UbiDTO): List<ZoneDTO>

    //------------------USERS---------------------------
    @POST("api/user-register")
    suspend fun register(@Body user: UserDTO)

    @GET("api/users/{user_id}")
    suspend fun getUser(@Path("user_id") userID: String): UserDTO
}