package com.inchko.parkfinder.network

import com.inchko.parkfinder.domainModels.POI

interface RepoPOIInterface {
    suspend fun addPOI(poi: POI)
    suspend fun getPOI(user_id: String): List<POI>
    suspend fun deletePOI(user_id: String, poi_id: String)
    suspend fun updatePOI(user_id: String, poi_id: String, poi:POI)
}