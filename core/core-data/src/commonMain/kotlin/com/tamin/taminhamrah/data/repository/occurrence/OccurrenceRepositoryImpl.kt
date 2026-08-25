package com.tamin.taminhamrah.data.repository.occurrence

import com.tamin.taminhamrah.data.mapper.occurrence.toDomain
import com.tamin.taminhamrah.data.mapper.occurrence.toDTO
import com.tamin.taminhamrah.dataSource.occurrence.OccurrenceRemoteDataSource
import com.tamin.taminhamrah.model.occurrence.InsuredRelationDN
import com.tamin.taminhamrah.model.occurrence.OccurrenceDocTypeDN
import com.tamin.taminhamrah.model.occurrence.OccurrencePersonalInfoDN
import com.tamin.taminhamrah.model.occurrence.OccurrenceResultDN
import com.tamin.taminhamrah.model.occurrence.OccurrenceSubmitRequestDN
import com.tamin.taminhamrah.model.occurrence.WorkshopItemDN
import com.tamin.taminhamrah.repository.occurrence.OccurrenceRepository

class OccurrenceRepositoryImpl(
    private val remoteDataSource: OccurrenceRemoteDataSource,
) : OccurrenceRepository {

    override suspend fun getPersonalInfo(
        nationalCode: String,
        birthDate: String,
        workshopCode: String,
        branchCode: String,
    ): OccurrencePersonalInfoDN =
        remoteDataSource.getPersonalInfo(nationalCode, birthDate, workshopCode, branchCode).toDomain()

    override suspend fun getAllWorkshops(nationalCode: String): List<WorkshopItemDN> =
        remoteDataSource.getAllWorkshops(nationalCode).list?.map { it.toDomain() } ?: emptyList()

    override suspend fun getWorkshopSpec(workshopCode: String, branchCode: String): WorkshopItemDN =
        remoteDataSource.getWorkshopSpec(workshopCode, branchCode).toDomain()

    override suspend fun getInsuredRelation(nationalCode: String): InsuredRelationDN =
        remoteDataSource.getInsuredRelation(nationalCode).toDomain()

    override suspend fun getDocumentTypes(): List<OccurrenceDocTypeDN> =
        remoteDataSource.getDocumentTypes().list?.map { it.toDomain() } ?: emptyList()

    override suspend fun uploadImage(fileName: String, fileBytes: ByteArray): String =
        remoteDataSource.uploadImage(fileName, fileBytes)

    override suspend fun submitOccurrence(request: OccurrenceSubmitRequestDN): OccurrenceResultDN =
        remoteDataSource.submitOccurrence(request.toDTO()).toDomain()
}
