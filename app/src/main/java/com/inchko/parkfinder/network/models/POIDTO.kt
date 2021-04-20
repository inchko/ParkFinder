package com.inchko.parkfinder.network.models

import com.google.gson.annotations.SerializedName


data class POIDTO(
    @SerializedName("lat")
    val lat: String,
    @SerializedName("long")
    val long: String,
    @SerializedName("nombre")
    val nombre: String,
    @SerializedName("userID")
    val userID: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("location")
    val location: String?

)