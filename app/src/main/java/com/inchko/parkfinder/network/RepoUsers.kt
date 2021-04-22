package com.inchko.parkfinder.network

import android.util.Log
import com.inchko.parkfinder.domainModels.User
import com.inchko.parkfinder.domainModels.Vehicles
import com.inchko.parkfinder.network.models.UserDTO2User
import com.inchko.parkfinder.network.models.VehicleDTO2Vehicle
import javax.inject.Inject

class RepoUsers @Inject constructor(
    private val api: ApiService,
    private val um: UserDTO2User,
    private val vm: VehicleDTO2Vehicle
) :
    RepoUserInterface {

    override suspend fun register(user: User) {
        Log.e("login", "Reached repo")
        api.register(um.mapToNetwork(user))
    }

    override suspend fun getUser(id: String): User {
        Log.e("login", "Reached getUser")
        return um.mapToDomain(api.getUser(id))
    }

    override suspend fun getVehicles(id: String): List<Vehicles> {
        return vm.listMap2Domain(api.getVehicles(id))
    }

    override suspend fun addVehicle(id:String, v: Vehicles) {
        api.addVehicle(id,vm.mapToNetwork(v))
    }

    override suspend fun updateVehicle(id: String, idv: String, v: Vehicles) {
        api.updateVehicle(id, idv, vm.mapToNetwork(v))
    }

    override suspend fun deleteVehicles(id: String, idv: String) {
        api.deleteVehicle(id, idv)
    }
}