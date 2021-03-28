package com.inchko.parkfinder.network

import com.inchko.parkfinder.network.models.TestDTO
import retrofit2.http.GET

interface ApiService {
    //URL noseque.com.a?^[Query1][Query2]
    @GET("api/test-location")
    suspend fun test(/*
    @Header("value") nombre:Tipo, @Query("Query1")nombre:Tipo*/
    ): TestDTO
}