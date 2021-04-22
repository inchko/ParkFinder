package com.inchko.parkfinder.network.models

import com.inchko.parkfinder.domainModels.Vehicles
import com.inchko.parkfinder.domainModels.Zone
import com.inchko.parkfinder.utils.Mapper

class VehicleDTO2Vehicle : Mapper<VehicleDTO, Vehicles> {
    override fun mapToDomain(t: VehicleDTO): Vehicles {
        return Vehicles(
            model = t.model,
            type = t.type,
            size = t.size,
            id = t.id
        )
    }

    fun mapToNetwork(t: Vehicles): VehicleDTO {
        return VehicleDTO(
            model = t.model,
            type = t.type,
            size = t.size,
            id = t.id
        )
    }

    fun listMap2Domain(t: List<VehicleDTO>): List<Vehicles> {
        return t.map { mapToDomain(it) }
    }

}