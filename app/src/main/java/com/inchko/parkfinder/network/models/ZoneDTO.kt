package com.inchko.parkfinder.network.models

import com.google.gson.annotations.SerializedName

data class ZoneDTO(

    @SerializedName("lat")
    val lat: Double? = null,

    @SerializedName("long")
    val long: Double? = null,

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("area")
    val area: Double? = null,

    @SerializedName("zonasLibres")
    val zonasLibres: Int? = null,

    @SerializedName("zonasOcupadas")
    val zonasOcupadas: Int? = null,

    @SerializedName("tipo")
    val tipo: Int? = null,

    @SerializedName("zonasGl")
    val zonasGL: Int? = null,

    @SerializedName("zonasGrandes")
    val zonasGrandes: Int? = null,

    @SerializedName("zonasPl")
    val zonasPl: Int? = null,

    @SerializedName("zonasPeq")
    val zonasPeque: Int? = null
)
