package com.inchko.parkfinder.network

import com.inchko.parkfinder.domainModels.Test
import com.inchko.parkfinder.domainModels.Zone

interface RepositoryInterface {
    suspend fun test(): Test
    suspend fun readZones():List<Zone>
}