package com.tamin.taminhamrah.repository.occurrence

import com.tamin.taminhamrah.model.occurrence.InsuredRelationDN
import com.tamin.taminhamrah.model.occurrence.OccurrenceDocTypeDN
import com.tamin.taminhamrah.model.occurrence.OccurrencePersonalInfoDN
import com.tamin.taminhamrah.model.occurrence.OccurrenceResultDN
import com.tamin.taminhamrah.model.occurrence.OccurrenceSubmitRequestDN
import com.tamin.taminhamrah.model.occurrence.WorkshopItemDN

class FakeOccurrenceRepository : OccurrenceRepository {
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Fake Occurrence Repository Error")

    var personalInfoResult: OccurrencePersonalInfoDN = OccurrencePersonalInfoDN(
        nationalCode = "0012345678",
        firstName = "علی",
        lastName = "رضایی",
        fatherName = "محمد",
        gender = "01",
        birthDate = "1370/01/01",
        insuranceNumber = "1234567",
        branchCode = "10",
        nationality = "ایرانی",
        insuranceType = "اصلی",
    )
    var allWorkshopsResult: List<WorkshopItemDN> = emptyList()
    var workshopSpecResult: WorkshopItemDN = WorkshopItemDN(
        id = "1",
        workshopCode = "1412345",
        branchCode = "014",
        name = "کارگاه تولیدی الف",
        employerName = "شرکت الف",
        employerPhone = "02112345678",
        address = "تهران",
        postalCode = "1234567890",
        phone = "02112345678",
        nationality = "ایرانی",
        nationalityCode = "1",
    )
    var insuredRelationResult: InsuredRelationDN = InsuredRelationDN(
        insuranceTypeCode = "01",
        insuranceType = "اصلی",
        branchCode = "10",
        branchName = "شعبه مرکزی",
    )
    var documentTypesResult: List<OccurrenceDocTypeDN> = emptyList()
    var uploadImageResult: String = "uploaded-guid"
    var submitResult: OccurrenceResultDN = OccurrenceResultDN(trackingCode = "TRACK-1")

    var lastPersonalInfoParams: List<String>? = null
    var lastAllWorkshopsNationalCode: String? = null
    var lastWorkshopSpecParams: Pair<String, String>? = null
    var lastInsuredRelationNationalCode: String? = null
    var lastUploadImageParams: Pair<String, ByteArray>? = null
    var lastSubmitRequest: OccurrenceSubmitRequestDN? = null

    override suspend fun getPersonalInfo(
        nationalCode: String,
        birthDate: String,
        workshopCode: String,
        branchCode: String,
    ): OccurrencePersonalInfoDN {
        lastPersonalInfoParams = listOf(nationalCode, birthDate, workshopCode, branchCode)
        if (shouldThrowError) throw error
        return personalInfoResult
    }

    override suspend fun getAllWorkshops(nationalCode: String): List<WorkshopItemDN> {
        lastAllWorkshopsNationalCode = nationalCode
        if (shouldThrowError) throw error
        return allWorkshopsResult
    }

    override suspend fun getWorkshopSpec(workshopCode: String, branchCode: String): WorkshopItemDN {
        lastWorkshopSpecParams = workshopCode to branchCode
        if (shouldThrowError) throw error
        return workshopSpecResult
    }

    override suspend fun getInsuredRelation(nationalCode: String): InsuredRelationDN {
        lastInsuredRelationNationalCode = nationalCode
        if (shouldThrowError) throw error
        return insuredRelationResult
    }

    override suspend fun getDocumentTypes(): List<OccurrenceDocTypeDN> {
        if (shouldThrowError) throw error
        return documentTypesResult
    }

    override suspend fun uploadImage(fileName: String, fileBytes: ByteArray): String {
        lastUploadImageParams = fileName to fileBytes
        if (shouldThrowError) throw error
        return uploadImageResult
    }

    override suspend fun submitOccurrence(request: OccurrenceSubmitRequestDN): OccurrenceResultDN {
        lastSubmitRequest = request
        if (shouldThrowError) throw error
        return submitResult
    }
}
