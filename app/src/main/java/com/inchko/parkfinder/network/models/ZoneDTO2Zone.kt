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
            plazasGl = t.plazasGl,
            plazasGrandes = t.plazasGrandes,
            plazasLibres = t.plazasLibres,
            plazasTotales = t.plazasTotales,
            plazasPeq = t.plazasPeq,
            plazasPl = t.plazasPl,
            plazasMl = t.plazasMl,
            plazasMoto = t.plazasMoto,
            tipo = t.tipo
        )
    }

    fun listMap2Domain(t: List<ZoneDTO>): List<Zone> {
        return t.map { mapToDomain(it) }
    }
}
