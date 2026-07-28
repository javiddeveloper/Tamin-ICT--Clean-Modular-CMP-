package com.tamin.taminhamrah.feature.treatment.fake

import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.treatment.DependantUserUnderEighteenDN
import com.tamin.taminhamrah.model.treatment.DeservedTreatmentDN
import com.tamin.taminhamrah.model.treatment.TreatmentCostDN

/**
 * Central factory of sample domain models for the treatment dashboard + costs
 * ViewModel tests.

import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionDN
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionDetailDN
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionPriceDN

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

    fun treatmentCost() = TreatmentCostDN(
        accountNumber = "123", bimeCode = "456", datePaz = "14020101",
        famil = "Doe", healthcenterName = "Hospital", mainNational = "111",
        maliCode = "222", name = "John", nameAsli = "John", nameFamil = "Doe",
        noPazir = "789", payNatCode = "333", payOtherService = "0",
        payPrice = "1000", payService = "service", payStatus = "1",
        payType = "Type", province = "Tehran", rahgiriCode = "555",
        releaseDate = "14020102", repId = 1, serviceDate = "14020101",
        status = "Status", statusDesc = "Description", payStatusDesc = "PayDescription",
        returnReason = "None"

    fun prescription() = ElectronicPrescriptionDN(
        id = "1", docId = "doc1", docName = "Doctor", flagSata = "1",
        location = "Location", noteHeadEprescID = 100L, patientID = "patient1",
        patientName = "Patient", prescDate = "14020101", prescName = "Prescription",
        specDesc = "Specialty", prescType = "1", trackingCode = 200L
    )

    fun prescriptionDetail() = ElectronicPrescriptionDetailDN(
        sumPriceItem = 1000L, ssoPayment = 800L, insurancePayment = 200L,
        serviceQuantity = 1, noteHeadEprescID = 100L, serverCode = "srvCode",
        serverName = "srvName", serviceName = "serviceName", drugInst = "instruction",
        registerDate = "14020101", drugInstruction = "drugInstruction",
        deliveredNo = 1, drugAmount = "10"
    )

    fun prescriptionPrice() = ElectronicPrescriptionPriceDN(
        headInsuPayment = 1000L, headSsoPayment = 800L,
        noteHeadEprescID = 100L, requestPrice = 1800L
    )

    /**
     * A downloaded-PDF domain result. The underlying stream is left null because the
     * feature test classpath does not expose ktor's ByteReadChannel; the ViewModels only
     * map this into a presentation marker, so a null stream is sufficient for assertions.
     */
    fun pdf() = PdfDownloadDN(pdf = null)
}
