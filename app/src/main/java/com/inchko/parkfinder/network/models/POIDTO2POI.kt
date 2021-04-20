package com.inchko.parkfinder.network.models

import com.inchko.parkfinder.domainModels.POI
import com.inchko.parkfinder.utils.Mapper

class POIDTO2POI : Mapper<POIDTO, POI> {

    override fun mapToDomain(t: POIDTO): POI {
        return POI(
            lat = t.lat,
            long = t.long,
            nombre = t.nombre,
            userID = t.userID,
            id = t.id,
            location = t.location
        )
    }

    fun mapToDTO(t: POI): POIDTO {
        return POIDTO(
            lat = t.lat,
            long = t.long,
            nombre = t.nombre,
            userID = t.userID,
            id = t.id,
            location = t.location
        )
    }

    fun listMap2Domain(t: List<POIDTO>): List<POI> {
        return t.map { mapToDomain(it) }
    }
}