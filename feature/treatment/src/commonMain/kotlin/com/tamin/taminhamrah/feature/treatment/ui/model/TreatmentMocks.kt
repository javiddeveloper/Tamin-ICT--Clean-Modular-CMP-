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

    val medicalConfirmation = MedicalAuthoritiesPR(
        firstName = "رضا",
        lastName = "احمدی",
        supportType = "استراحت پزشکی",
        treatmentCenter = "بیمارستان میلاد",
        outpatientRestStartDate = "1402/06/01",
        outpatientRestEndDate = "1402/06/05",
        numberOfOutpatientDays = "5",
        description = "نیاز به استراحت مطلق در منزل",
        confirmInBranch = "بله",
        confirmStatus = "تایید شده",
        insuranceNumber = "12345678",
        nationalCode = "1234567890",
        hospitalizationStartDate = "",
        hospitalizationEndDate = "",
        numberOfHospitalizationDays = "0",
        branch = "شعبه یک تهران",
        fromDateNotConfirm = "",
        toDateNotConfirm = ""
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

    val confirmationsUiState = ConfirmationsUiState(
        medicalAuthorities = listOf(medicalConfirmation, medicalConfirmation.copy(supportType = "کمیسیون تخصصی"))
    )
}
