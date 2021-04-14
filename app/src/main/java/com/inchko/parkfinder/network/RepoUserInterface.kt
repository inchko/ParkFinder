package com.inchko.parkfinder.network

import com.inchko.parkfinder.domainModels.User

interface RepoUserInterface {
    suspend fun register(user: User)
    suspend fun getUser(id: String): User
}