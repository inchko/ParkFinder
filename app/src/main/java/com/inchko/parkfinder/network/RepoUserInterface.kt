package com.inchko.parkfinder.network

import com.inchko.parkfinder.domainModels.User
import com.inchko.parkfinder.domainModels.Vehicles

interface RepoUserInterface {
    suspend fun register(user: User)
    suspend fun getUser(id: String): User
    suspend fun getVehicles(id: String): List<Vehicles>
    suspend fun addVehicle(id:String,v: Vehicles)
    suspend fun updateVehicle(id: String, idv: String, v: Vehicles)
    suspend fun deleteVehicles(id: String, idv: String)
}