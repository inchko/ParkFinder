package com.inchko.parkfinder.network.models

import com.google.gson.annotations.SerializedName

data class FavZoneDTO(
    @SerializedName("lat")
    val lat: String,
    @SerializedName("long")
    val long: String,
    @SerializedName("plazasTotales")
    val plazasTotales: Int,
    @SerializedName("tipo")
    val tipo: Int,
    @SerializedName("userID")
    val userID: String,
    @SerializedName("zoneID")
    val zoneID: String,
    @SerializedName("id")
    val id: String
)