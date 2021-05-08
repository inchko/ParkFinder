package com.inchko.parkfinder.network

import com.inchko.parkfinder.domainModels.Test
import com.inchko.parkfinder.domainModels.Ubi
import com.inchko.parkfinder.domainModels.Zone

interface RepositoryInterface {
    suspend fun test(): Test
    suspend fun readZones(): List<Zone>
    suspend fun readZonesByLoc(ubi: Ubi): List<Zone>
}