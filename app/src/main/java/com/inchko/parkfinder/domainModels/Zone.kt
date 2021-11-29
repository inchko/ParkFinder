package com.inchko.parkfinder.domainModels

data class Zone(
    val lat: Double? = null,
    val long: Double? = null,
    val id: String? = null,
    val area: Double? = null,
    var plazasLibres: Int? = null,
    val plazasTotales: Int? = null,
    val tipo: Int? = null,
    var plazasGl: Int? = null,
    val plazasGrandes: Int? = null,
    var plazasPl: Int? = null,
    val plazasPeq: Int? = null,
    var plazasMl: Int? = null,
    val plazasMoto: Int? = null,
    var distancia : Double? = null,
    val placeID: String? = null
)
