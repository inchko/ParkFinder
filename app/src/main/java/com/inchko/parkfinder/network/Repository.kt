package com.inchko.parkfinder.network

import com.inchko.parkfinder.domainModels.Test
import com.inchko.parkfinder.domainModels.Ubi
import com.inchko.parkfinder.domainModels.Zone
import com.inchko.parkfinder.network.models.TestDTO2Test
import com.inchko.parkfinder.network.models.Ubi2UbiDTO
import com.inchko.parkfinder.network.models.ZoneDTO2Zone
import javax.inject.Inject

class Repository @Inject constructor(
    private val apiService: ApiService, //name of the service
    private val testmapper: TestDTO2Test, //mapper from models
    private val zoneMapper: ZoneDTO2Zone,
    private val ubiMapper: Ubi2UbiDTO
) : RepositoryInterface {
    val text: String = "This text is in the class repository"
    override suspend fun test(): Test {
        return testmapper.mapToDomain(apiService.test())
    }

    override suspend fun readZones(): List<Zone> {
        return zoneMapper.listMap2Domain(apiService.readZones())
    }

    override suspend fun readZonesByLoc(ubi: Ubi): List<Zone> {
        return zoneMapper.listMap2Domain(apiService.readZonesByLoc(ubiMapper.mapToDomain(ubi)))
    }

}