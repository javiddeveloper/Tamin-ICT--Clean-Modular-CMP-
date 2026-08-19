package com.tamin.taminhamrah.repository.occurrence

import com.tamin.taminhamrah.model.occurrence.InsuredRelationDN
import com.tamin.taminhamrah.model.occurrence.OccurrenceDocTypeDN
import com.tamin.taminhamrah.model.occurrence.OccurrencePersonalInfoDN
import com.tamin.taminhamrah.model.occurrence.OccurrenceResultDN
import com.tamin.taminhamrah.model.occurrence.OccurrenceSubmitRequestDN
import com.tamin.taminhamrah.model.occurrence.WorkshopItemDN

interface OccurrenceRepository {
    suspend fun getPersonalInfo(
        nationalCode: String,
        birthDate: String,
        workshopCode: String,
        branchCode: String,
    ): OccurrencePersonalInfoDN
    suspend fun getAllWorkshops(nationalCode: String): List<WorkshopItemDN>
    suspend fun getWorkshopSpec(workshopCode: String, branchCode: String): WorkshopItemDN
    suspend fun getInsuredRelation(nationalCode: String): InsuredRelationDN
    suspend fun getDocumentTypes(): List<OccurrenceDocTypeDN>
    suspend fun uploadImage(fileName: String, fileBytes: ByteArray): String
    suspend fun submitOccurrence(request: OccurrenceSubmitRequestDN): OccurrenceResultDN
}
