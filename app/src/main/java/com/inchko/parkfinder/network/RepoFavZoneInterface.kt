package com.inchko.parkfinder.network

import com.inchko.parkfinder.domainModels.FavZone

interface RepoFavZoneInterface {
    suspend fun addFavZone(favZone: FavZone)
    suspend fun getFavZones(user_id: String): List<FavZone>
    suspend fun deleteFavZones(user_id: String, zone_id: String)
    suspend fun updateFavZones(user_id: String, zone_id: String, zone: FavZone)
}