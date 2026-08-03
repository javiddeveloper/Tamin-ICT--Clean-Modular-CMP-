package com.tamin.taminhamrah.mapper.erecords

import com.tamin.taminhamrah.model.erecords.ElectronicFilePR
import com.tamin.taminhamrah.model.erecords.images.ElectronicFileDN
import kotlin.jvm.JvmName

fun ElectronicFileDN.toPresentation(): ElectronicFilePR = ElectronicFilePR(
    id = id ?: "",
    name = name ?: "",
    categoryName = categoryName ?: "",
    thumb = thumb ?: "",
    type = type ?: "",
)

@JvmName("toElectronicFilePresentation")
fun List<ElectronicFileDN>.toPresentation(): List<ElectronicFilePR> = map { it.toPresentation() }
