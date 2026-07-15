package com.tamin.taminhamrah.feature.treatment.fake

import com.tamin.taminhamrah.model.treatment.DependantUserUnderEighteenDN
import com.tamin.taminhamrah.model.treatment.DeservedTreatmentDN

/**
 * Central factory of sample domain models for the treatment dashboard ViewModel tests.
 *
 * Keeping fixtures in one place (instead of inlined per fake/test) keeps the test
 * doubles small and lets every dashboard test share consistent, realistic data.
 */
object TreatmentTestData {

    const val MAIN_NATIONAL_CODE = "1234567890"
    const val DEPENDANT_NATIONAL_CODE = "9876543210"

    fun deserved(natCode: String = MAIN_NATIONAL_CODE) = DeservedTreatmentDN(
        birthDate = "13280407",
        brhCode = null,
        brhName = "شعبه یک کرج",
        dependenceType = "اصلی",
        fatherName = null,
        feranshiz = null,
        firstName = "Seyed",
        gender = "مرد",
        healthBookletDate = 14991229L,
        id = null,
        idNumber = null,
        insuranceType = "مستمری بگیر",
        lastBookletDate = null,
        lastName = "Rahmatollah",
        natCode = natCode,
        nationalId = null,
        parentRisuid = null,
        provinceCode = "31",
        provinceName = "البرز",
        regWorkshopId = null,
        regWorkshopName = null,
        risuid = null,
        message = null,
        illness = null,
        trackingCode = null
    )

    fun dependant(nationalId: String = DEPENDANT_NATIONAL_CODE) = DependantUserUnderEighteenDN(
        firstName = "Child",
        lastName = "Name",
        nationalId = nationalId,
        id = 1L
    )
}
