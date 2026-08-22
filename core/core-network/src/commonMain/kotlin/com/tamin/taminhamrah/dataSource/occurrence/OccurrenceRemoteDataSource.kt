package com.tamin.taminhamrah.dataSource.occurrence

import com.tamin.taminhamrah.model.occurrence.InsuredRelationDTO
import com.tamin.taminhamrah.model.occurrence.OccurrenceDocTypeDTO
import com.tamin.taminhamrah.model.occurrence.OccurrencePersonalInfoDTO
import com.tamin.taminhamrah.model.occurrence.OccurrenceRequestDTO
import com.tamin.taminhamrah.model.occurrence.OccurrenceResponseDTO
import com.tamin.taminhamrah.model.occurrence.WorkshopItemDTO
import com.tamin.taminhamrah.model.occurrence.WorkshopListItemDTO
import com.tamin.taminhamrah.model.utils.ListData

interface OccurrenceRemoteDataSource {
    suspend fun getPersonalInfo(
        nationalCode: String,
        birthDate: String,
        workshopCode: String,
        branchCode: String,
    ): OccurrencePersonalInfoDTO
    suspend fun getAllWorkshops(nationalCode: String): ListData<WorkshopListItemDTO>
    suspend fun getWorkshopSpec(workshopCode: String, branchCode: String): WorkshopItemDTO
    suspend fun getInsuredRelation(nationalCode: String): InsuredRelationDTO
    suspend fun getDocumentTypes(): ListData<OccurrenceDocTypeDTO>
    suspend fun uploadImage(fileName: String, fileBytes: ByteArray): String
    suspend fun submitOccurrence(request: OccurrenceRequestDTO): OccurrenceResponseDTO
}
