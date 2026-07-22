package com.tamin.taminhamrah.feature.treatment.ui.model

import com.tamin.taminhamrah.feature.treatment.ui.contract.*
import com.tamin.taminhamrah.model.treatment.*
import com.tamin.taminhamrah.model.health.*

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

    val patientGeneral = PatientGeneralPR(
        ptientID = 101,
        patientName = "رضا",
        patientFamily = "احمدی",
        patientNatCode = "1234567890",
        patientAge = "42",
        patientGender = "مرد",
        patientBirthDate = "1360/01/15",
        patientMobile = "09123456789",
        patientAddress = "تهران، خیابان ولیعصر، کوچه دوم، پلاک ۱۰",
        patientFather = "محمد"
    )

    val selfDeclarative = PatientSelfDeclarativePR(
        alcoholDesc = "",
        alcoholUsage = 0,
        alcoholUsageTitle = "عدم مصرف",
        exerciseDesc = "",
        exerciseFreq = 3,
        exerciseFreqTitle = "هفته‌ای سه بار",
        lastUpdateDate = "1402/03/15",
        objectID = 101,
        smokingDesc = "",
        smokingStatus = 0,
        smokingStatusTitle = "غیر سیگاری",
        substanceDesc = "",
        substanceUsage = 0,
        substanceUsageTitle = "عدم مصرف"
    )

    val drugAllergy = DrugItemAllergiesPR(
        allergyComments = "حساسیت شدید پوستی و تنگی نفس",
        drugId = 505,
        drugName = "پنی‌سیلین"
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
        insuredShareTotal = 65_910L,
        organizationShareTotal = 153_790L,
        healthProfileCompleted = true
    )
}
