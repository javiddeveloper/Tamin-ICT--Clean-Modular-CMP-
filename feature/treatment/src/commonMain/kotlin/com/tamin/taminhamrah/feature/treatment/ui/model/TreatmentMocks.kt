package com.tamin.taminhamrah.feature.treatment.ui.model

import com.tamin.taminhamrah.feature.treatment.ui.contract.*
import com.tamin.taminhamrah.model.treatment.*

object TreatmentMocks {
    val patientMain = PatientItem(
        nationalId = "1234567890",
        fullName = "رضا احمدی",
        isDependent = false,
        brhName = "شعبه یک تهران",
        insuranceType = "اجباری"
    )

    val deservedTreatment = DeservedTreatmentPR(
        id = 1,
        firstName = "رضا",
        lastName = "احمدی",
        fullName = "رضا احمدی",
        nationalId = "1234567890",
        natCode = "1234567890",
        birthDate = "1360/01/15",
        brhCode = "1001",
        brhName = "شعبه یک تهران",
        dependenceType = "اصلی",
        fatherName = "محمد",
        feranshiz = "10",
        gender = "مرد",
        healthBookletDate = "1402/12/29",
        insuranceType = "اجباری",
        lastBookletDate = "1401/12/29",
        parentRisuid = "",
        provinceCode = "01",
        provinceName = "تهران",
        regWorkshopId = "12345",
        regWorkshopName = "شرکت تست",
        risuid = "67890",
        message = "مشمول حمایت درمانی",
        illness = "",
        trackingCode = ""
    )

    val mainUiState = TreatmentUiState(
        deservedList = listOf(deservedTreatment),
        dependantList = listOf(
            DependantUserUnderEighteenPR(
                id = "1",
                firstName = "سارا",
                lastName = "احمدی",
                fullName = "سارا احمدی",
                nationalId = "0987654321"
            )
        ),
        mainUserNationalCode = "1234567890",
        selectedNationalCode = "1234567890",
        selectedPatientName = "رضا احمدی",
        activeFlow = TreatmentFlow.MAIN
    )
}
