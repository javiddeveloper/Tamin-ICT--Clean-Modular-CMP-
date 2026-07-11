package com.tamin.taminhamrah.feature.treatment.fake

import com.tamin.taminhamrah.model.health.DrugItemAllergiesDN
import com.tamin.taminhamrah.model.health.PatientGeneralDN
import com.tamin.taminhamrah.model.health.PatientSelfDeclarativeDN
import com.tamin.taminhamrah.model.treatment.DependantUserUnderEighteenDN
import com.tamin.taminhamrah.model.treatment.DeservedTreatmentDN

/**
 * Central factory of sample domain models for treatment/health ViewModel tests.
 *
 * Keeping fixtures in one place (instead of inlined per fake/test) keeps the test
 * doubles small and lets every ViewModel test share consistent, realistic data.
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

    fun patientGeneral(natCode: String = MAIN_NATIONAL_CODE) = PatientGeneralDN(
        ptientID = 1,
        patientName = "Seyed",
        patientFamily = "Mirfazli",
        patientNatCode = natCode,
        patientAge = "70",
        patientGender = "مرد",
        patientBirthDate = "13280407",
        patientMobile = null,
        patientAddress = null,
        patientFather = null
    )

    fun selfDeclarative(patientID: Int = 1) = PatientSelfDeclarativeDN(
        alcoholDesc = null,
        alcoholUsage = null,
        alcoholUsageTitle = null,
        exerciseDesc = null,
        exerciseFreq = null,
        exerciseFreqTitle = null,
        lastUpdateDate = null,
        objectID = patientID,
        smokingDesc = null,
        smokingStatus = null,
        smokingStatusTitle = null,
        substanceDesc = null,
        substanceUsage = null,
        substanceUsageTitle = null
    )

    fun drugAllergy() = DrugItemAllergiesDN(
        allergyComments = "Comments", drugId = 1, drugName = "Drug"
    )
}
