package com.inchko.parkfinder.network.models

import com.google.gson.annotations.SerializedName

data class UbiDTO(
    @SerializedName("rango")
    val rango:Int,
    @SerializedName("long")
    val longitude: Double,
    @SerializedName("lat")
    val latitude: Double
)
