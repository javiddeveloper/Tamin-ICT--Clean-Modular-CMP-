package com.tamin.taminhamrah.feature.profile.data.mapper

import com.tamin.taminhamrah.model.erecords.images.ElectronicFileDN
import com.tamin.taminhamrah.model.erecords.images.ElectronicFileDTO

fun ElectronicFileDTO.toDomain(): ElectronicFileDN {
    return ElectronicFileDN(
        categoryName = categoryName,
        contentServer = contentServer,
        countNumger = countNumger,
        id = id,
        name = name,
        rowNumber = rowNumber,
        thumb = thumb,
        type = type
    )
}
