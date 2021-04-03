package com.inchko.parkfinder.domainModels

data class Zone(
    val lat: Double? = null,
    val long: Double? = null,
    val id: String? = null,
    val area: Double? = null,
    val zonasTotales: Int? = null,
    val zonasOcupadas: Int? = null,
    val tipo: Int? = null,
    val zonasGL: Int? = null,
    val zonasGrandes: Int? = null,
    val zonasPl: Int? = null,
    val zonasPeque: Int? = null
)
