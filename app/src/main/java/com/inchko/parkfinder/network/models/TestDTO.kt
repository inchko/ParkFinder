package com.inchko.parkfinder.network.models

import android.location.Location
import com.google.gson.annotations.SerializedName

data class TestDTO (
    @SerializedName("valor")
    var loc: Location? = null,

    @SerializedName("number")
    var number: Int? = null
)