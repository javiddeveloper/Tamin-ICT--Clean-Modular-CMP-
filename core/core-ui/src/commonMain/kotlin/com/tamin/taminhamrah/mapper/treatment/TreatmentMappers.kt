package com.tamin.taminhamrah.mapper.treatment

import com.tamin.taminhamrah.model.treatment.*
import kotlin.jvm.JvmName

fun DeservedTreatmentDN.toPresentation(): DeservedTreatmentPR {
    return DeservedTreatmentPR(
        id = id ?: 0,
        firstName = firstName ?: "",
        lastName = lastName ?: "",
        fullName = "${firstName ?: ""} ${lastName ?: ""}".trim().ifEmpty { "نامشخص" },
        nationalId = nationalId ?: "",
        natCode = natCode ?: "",
        birthDate = birthDate ?: "",
        brhCode = brhCode ?: "",
        brhName = brhName ?: "نامشخص",
        dependenceType = dependenceType ?: "",
        fatherName = fatherName ?: "",
        feranshiz = feranshiz ?: "",
        gender = gender ?: "",
        healthBookletDate = healthBookletDate?.toString() ?: "",
        insuranceType = insuranceType ?: "",
        lastBookletDate = lastBookletDate ?: "",
        parentRisuid = parentRisuid ?: "",
        provinceCode = provinceCode ?: "",
        provinceName = provinceName ?: "نامشخص",
        regWorkshopId = regWorkshopId ?: "",
        regWorkshopName = regWorkshopName ?: "",
        risuid = risuid ?: "",
        message = message ?: "",
        illness = illness ?: "",
        trackingCode = trackingCode ?: ""
    )
}

@JvmName("toDeservedPresentation")
fun List<DeservedTreatmentDN>.toPresentation(): List<DeservedTreatmentPR> {
    return this.map { it.toPresentation() }
}

fun DependantUserUnderEighteenDN.toPresentation(): DependantUserUnderEighteenPR {
    return DependantUserUnderEighteenPR(
        id = id?.toString() ?: "",
        firstName = firstName ?: "",
        lastName = lastName ?: "",
        fullName = "${firstName ?: ""} ${lastName ?: ""}".trim().ifEmpty { "نامشخص" },
        nationalId = nationalId ?: ""
    )
}

@JvmName("toDependantPresentation")
fun List<DependantUserUnderEighteenDN>.toPresentation(): List<DependantUserUnderEighteenPR> {
    return this.map { it.toPresentation() }
}
