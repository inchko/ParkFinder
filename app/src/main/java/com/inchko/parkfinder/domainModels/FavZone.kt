package com.inchko.parkfinder.domainModels

data class FavZone(
    var lat: String,
    val long: String,
    val plazasTotales: Int,
    val tipo: Int,
    val userID: String,
    val zoneID: String,
    val id:String,
    var location: String
)