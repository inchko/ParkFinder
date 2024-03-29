package com.inchko.parkfinder.network.models

import com.inchko.parkfinder.domainModels.Test
import com.inchko.parkfinder.utils.Mapper

class TestDTO2Test : Mapper<TestDTO, Test> {
    override fun mapToDomain(t: TestDTO): Test {
        return Test(
            long = t.long,
            lat = t.lat,
            test = t.test
        )
    }

    //Convert a list
    fun listMap2Domain(t: List<TestDTO>): List<Test> {
        return t.map { mapToDomain(it) }
    }
}