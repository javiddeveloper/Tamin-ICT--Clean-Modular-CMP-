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

    val treatmentCost = TreatmentCostPR(
        repId = "1",
        nameFamil = "رضا احمدی",
        healthcenterName = "داروخانه شبانه‌روزی مرکزی",
        payPrice = "850000",
        payStatusDesc = "پرداخت شده",
        rahgiriCode = "REF-98765",
        serviceDate = "1402/04/20",
        statusDesc = "نهایی",
        accountNumber = "0100000000",
        bimeCode = "12345678",
        datePaz = "1402/04/20",
        famil = "احمدی",
        mainNational = "1234567890",
        maliCode = "M1",
        name = "رضا",
        nameAsli = "رضا",
        noPazir = "NP1",
        payNatCode = "1234567890",
        payOtherService = "0",
        payService = "850000",
        payStatus = "1",
        payType = "1",
        province = "تهران",
        releaseDate = "1402/04/21",
        status = "1",
        returnReason = ""
    )

    val prescription = ElectronicPrescriptionPR(
        id = "1",
        docId = "1001",
        docName = "علی علوی",
        flagSata = "0",
        location = "تهران",
        noteHeadEprescID = "10001",
        patientID = "2001",
        patientName = "رضا احمدی",
        prescDate = "1402/05/10",
        prescName = "نسخه دارو",
        specDesc = "متخصص قلب و عروق",
        prescType = "دارو",
        trackingCode = "TRK123456"
    )

    val prescriptionDetail = ElectronicPrescriptionDetailPR(
        sumPriceItem = "150000",
        ssoPayment = "120000",
        insurancePayment = "30000",
        serviceQuantity = "30",
        noteHeadEprescID = "10001",
        serverCode = "S1",
        serverName = "خدمت ۱",
        serviceName = "قرص آسپیرین 80 میلی‌گرم",
        drugInst = "D1",
        registerDate = "1402/05/10",
        drugInstruction = "روزی یک عدد بعد از غذا",
        deliveredNo = "30",
        drugAmount = "80mg"
    )

    val prescriptionPrice = ElectronicPrescriptionPricePR(
        requestPrice = "450000",
        headSsoPayment = "380000",
        headInsuPayment = "70000",
        noteHeadEprescID = "10001"
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

    val costsUiState = CostsUiState(
        treatmentCostList = listOf(
            treatmentCost,
            treatmentCost.copy(healthcenterName = "آزمایشگاه نیلو", payPrice = "1200000"),
        )
    )

    val prescriptionsUiState = PrescriptionsUiState(
        prescriptionList = listOf(prescription, prescription.copy(trackingCode = "TRK654321", docName = "مریم رضایی")),
        prescriptionDetailList = listOf(prescriptionDetail, prescriptionDetail.copy(serviceName = "کپسول آموکسی‌سیلین")),
        prescriptionPriceList = listOf(prescriptionPrice)
    )
}
