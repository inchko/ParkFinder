package com.inchko.parkfinder.network.models

import com.google.gson.annotations.SerializedName

data class UserDTO(
    @SerializedName("id")
    var id: String,
    @SerializedName("name")
    var name: String,
    @SerializedName("email")
    var email: String

)
