package com.inchko.parkfinder.network.models

import android.util.Log
import com.inchko.parkfinder.domainModels.User
import com.inchko.parkfinder.utils.Mapper

class UserDTO2User : Mapper<UserDTO, User> {

    override fun mapToDomain(t: UserDTO): User {
        return User(
            id = t.id,
            name = t.name,
            email = t.email
        )
    }

    fun mapToNetwork(user: User): UserDTO {
        Log.e("login","reached mapper")
        return UserDTO(
            id = user.id,
            name = user.name,
            email = user.email
        )
    }
}