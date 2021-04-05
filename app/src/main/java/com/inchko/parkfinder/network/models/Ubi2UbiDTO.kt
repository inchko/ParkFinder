package com.inchko.parkfinder.network.models

import com.inchko.parkfinder.domainModels.Ubi
import com.inchko.parkfinder.utils.Mapper

class Ubi2UbiDTO : Mapper<Ubi, UbiDTO> {

    override fun mapToDomain(t: Ubi): UbiDTO {
        return UbiDTO(
            rango = t.rango,
            longitude = t.long,
            latitude = t.lat
        )
    }
}