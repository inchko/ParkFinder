package com.inchko.parkfinder.network

import com.inchko.parkfinder.domainModels.FavZone
import com.inchko.parkfinder.network.models.FavZoneDTO2FavZone
import javax.inject.Inject

class RepoFavZones @Inject constructor(
    private val api: ApiService, //name of the service
    private val zm: FavZoneDTO2FavZone
) : RepoFavZoneInterface {

    override suspend fun addFavZone(favZone: FavZone) {
        api.addFavZone(zm.mapToDTO(favZone))
    }

    override suspend fun getFavZones(user_id: String): List<FavZone> {
        return zm.listMap2Domain(api.getFavZones(user_id))
    }

    override suspend fun deleteFavZones(user_id: String, zone_id: String) {
        api.deleteFavZones(user_id, zone_id)
    }

    override suspend fun updateFavZones(user_id: String, zone_id: String, zone: FavZone) {
        api.updateFavZones(user_id, zone_id, zm.mapToDTO(zone))
    }
}