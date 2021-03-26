package com.inchko.parkfinder.network

import com.inchko.parkfinder.domainModels.Test
import com.inchko.parkfinder.network.models.TestDTO2Test
import javax.inject.Inject

class Repository @Inject constructor(
    private val apiService: ApiService, //name of the service
    private val mapper: TestDTO2Test //mapper from models
) : RepositoryInterface {
    val text: String = "This text is in the class repository"
    override suspend fun test(): Test {
        return mapper.mapToDomain(apiService.test())
    }
}