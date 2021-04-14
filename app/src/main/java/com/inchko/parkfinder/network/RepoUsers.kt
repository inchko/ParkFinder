package com.inchko.parkfinder.network

import android.util.Log
import com.inchko.parkfinder.domainModels.User
import com.inchko.parkfinder.network.models.UserDTO2User
import javax.inject.Inject

class RepoUsers @Inject constructor(private val api: ApiService, private val um: UserDTO2User) :
    RepoUserInterface {

    override suspend fun register(user: User) {
        Log.e("login","Reached repo")
        api.register(um.mapToDTO(user))
    }

    override suspend fun getUser(id: String): User {
        Log.e("login","Reached getUser")
        return um.mapToDomain(api.getUser(id))
    }
}