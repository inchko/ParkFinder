package com.inchko.parkfinder.network

import com.inchko.parkfinder.domainModels.POI
import com.inchko.parkfinder.network.models.POIDTO2POI

import javax.inject.Inject

class RepoPOI @Inject constructor(
    private val api: ApiService, //name of the service
    private val pm: POIDTO2POI
) : RepoPOIInterface {

    override suspend fun addPOI(poi: POI) {
        api.addPOI(pm.mapToDTO(poi))
    }

    override suspend fun getPOI(user_id: String): List<POI> {
        return pm.listMap2Domain(api.getPOI(user_id))
    }

    override suspend fun deletePOI(user_id: String, poi_id: String) {
        api.deletePOI(user_id, poi_id)
    }

    override suspend fun updatePOI(user_id: String, poi_id: String, poi: POI) {
        api.updatePOI(user_id, poi_id, pm.mapToDTO(poi))
    }
}