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

    @SerializedName("plazasLibres")
    val plazasLibres: Int? = null,

    @SerializedName("plazasTotales")
    val plazasTotales: Int? = null,

    @SerializedName("tipo")
    val tipo: Int? = null,

    @SerializedName("plazasGl")
    val plazasGl: Int? = null,

    @SerializedName("plazasGrandes")
    val plazasGrandes: Int? = null,

    @SerializedName("plazasPl")
    val plazasPl: Int? = null,

    @SerializedName("plazasPeq")
    val plazasPeq: Int? = null,

    @SerializedName("plazasMl")
    val plazasMl: Int? = null,

    @SerializedName("plazasMoto")
    val plazasMoto: Int? = null,

    @SerializedName("placeID")
    val placeID: String? = null

)
