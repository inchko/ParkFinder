package com.inchko.parkfinder.network

import com.inchko.parkfinder.network.models.*
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

    //----------------------FavZones---------------------------------------------
    @POST("api/addFavZones")
    suspend fun addFavZone(@Body favZone: FavZoneDTO)

    @GET("api/favZones/{user_id}")
    suspend fun getFavZones(@Path("user_id") userID: String): List<FavZoneDTO>

    @DELETE("api/deletefavZone/{user_id}/{zone_id}")
    suspend fun deleteFavZones(@Path("user_id") userID: String, @Path("zone_id") zone_id: String)

    @PUT("api/updateFavZone/{user_id}/{zone_id}")
    suspend fun updateFavZones(
        @Path("user_id") userID: String,
        @Path("zone_id") zone_id: String,
        @Body favZone: FavZoneDTO
    )

    //------------------------POI---------------------------------------------------------------------

    @POST("api/addPOI")
    suspend fun addPOI(@Body POI: POIDTO)

    @GET("api/POI/{user_id}")
    suspend fun getPOI(@Path("user_id") userID: String): List<POIDTO>

    @DELETE("api/deletePOI/{user_id}/{POI_id}")
    suspend fun deletePOI(@Path("user_id") userID: String, @Path("POI_id") POI_id: String)

    @PUT("api/updatePOI/{user_id}/{POI_id}")
    suspend fun updatePOI(
        @Path("user_id") userID: String,
        @Path("POI_id") POI_id: String,
        @Body POI: POIDTO
    )

    //-------------------------------Vehices------------------------------
    @GET("api/Vehicles/{user_id}")
    suspend fun getVehicles(@Path("user_id") userID: String): List<VehicleDTO>

    @POST("api/addVehicle/{user_id}")
    suspend fun addVehicle(@Path("user_id") userID: String, @Body v: VehicleDTO)

    @DELETE("api/deleteVehicle/{user_id}/{v_id}")
    suspend fun deleteVehicle(@Path("user_id") userID: String, @Path("v_id") v_id: String)

    @PUT("api/updateVehicles/{user_id}/{v_id}")
    suspend fun updateVehicle(
        @Path("user_id") userID: String,
        @Path("v_id") v_id: String,
        @Body v: VehicleDTO
    )
}