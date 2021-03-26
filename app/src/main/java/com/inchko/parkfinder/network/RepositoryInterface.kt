package com.inchko.parkfinder.network

import com.inchko.parkfinder.domainModels.Test

interface RepositoryInterface {
    suspend fun test(): Test
}