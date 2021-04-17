package com.inchko.parkfinder.domainModels

data class POI(
    val lat: String,
    val long: String,
    val nombre: String,
    val userID: String,
    val id: String,
    var location: String?
)