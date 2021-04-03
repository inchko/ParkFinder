package com.inchko.parkfinder.network.models

import com.inchko.parkfinder.domainModels.Zone
import com.inchko.parkfinder.utils.Mapper

class ZoneDTO2Zone : Mapper<ZoneDTO, Zone> {
    override fun mapToDomain(t: ZoneDTO): Zone {
        return Zone(
            area = t.area,
            id = t.id,
            lat = t.lat,
            long = t.long,
            zonasGL = t.zonasGL,
            zonasGrandes = t.zonasGrandes,
            zonasTotales = t.zonasLibres,
            zonasOcupadas = t.zonasOcupadas,
            zonasPeque = t.zonasPeque,
            zonasPl = t.zonasPl,
            tipo = t.tipo
        )
    }

    fun listMap2Domain(t: List<ZoneDTO>): List<Zone> {
        return t.map { mapToDomain(it) }
    }
}
