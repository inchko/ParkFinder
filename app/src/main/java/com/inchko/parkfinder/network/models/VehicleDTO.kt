package com.inchko.parkfinder.network.models

import com.google.gson.annotations.SerializedName

data class VehicleDTO(
    @SerializedName("model")
    val model: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("size")
    val size: Int,
    @SerializedName("type")
    val type: Int

)
