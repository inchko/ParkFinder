package com.inchko.parkfinder.network.models

import com.inchko.parkfinder.domainModels.FavZone
import com.inchko.parkfinder.utils.Mapper

class FavZoneDTO2FavZone : Mapper<FavZoneDTO, FavZone> {

    override fun mapToDomain(t: FavZoneDTO): FavZone {
        return FavZone(
            long = t.long,
            lat = t.lat,
            userID = t.userID,
            zoneID = t.zoneID,
            plazasTotales = t.plazasTotales,
            tipo = t.tipo,
            id = t.id,
            location = "Location not found"
        )
    }

    fun mapToDTO(t: FavZone): FavZoneDTO {
        return FavZoneDTO(
            long = t.long,
            lat = t.lat,
            userID = t.userID,
            zoneID = t.zoneID,
            plazasTotales = t.plazasTotales,
            tipo = t.tipo,
            id = t.id
        )
    }

    fun listMap2Domain(t: List<FavZoneDTO>): List<FavZone> {
        return t.map { mapToDomain(it) }
    }
}