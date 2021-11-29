package com.inchko.parkfinder.network

import com.inchko.parkfinder.domainModels.Zone

interface RepoServiceInterface {
    suspend fun readZone(zoneid: String): Zone
}