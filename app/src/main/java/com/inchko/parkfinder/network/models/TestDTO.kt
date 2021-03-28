package com.inchko.parkfinder.network.models

import android.location.Location
import com.google.gson.annotations.SerializedName

data class TestDTO (
    @SerializedName("long")
    var long: Double? = null,

    @SerializedName("lat")
    var lat: Double? = null,

    @SerializedName("test")
    var test: String? = null

)