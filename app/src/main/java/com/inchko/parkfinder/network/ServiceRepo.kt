package com.inchko.parkfinder.network

import com.inchko.parkfinder.domainModels.Test
import com.inchko.parkfinder.domainModels.Ubi
import com.inchko.parkfinder.domainModels.Zone
import com.inchko.parkfinder.network.models.TestDTO2Test
import com.inchko.parkfinder.network.models.Ubi2UbiDTO
import com.inchko.parkfinder.network.models.ZoneDTO2Zone
import javax.inject.Inject


class ServiceRepo @Inject constructor(
    private val apiService: ApiService, //name of the service
    private val testmapper: TestDTO2Test, //mapper from models
    private val zoneMapper: ZoneDTO2Zone,
    private val ubiMapper: Ubi2UbiDTO
) : RepoServiceInterface {

    override suspend fun readZone(zoneid: String): Zone {
        return zoneMapper.mapToDomain(apiService.readZone(zoneid))
    }

}