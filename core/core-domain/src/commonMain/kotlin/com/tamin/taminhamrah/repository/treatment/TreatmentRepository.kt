package com.tamin.taminhamrah.repository.treatment

import com.tamin.taminhamrah.model.treatment.*
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import kotlinx.coroutines.flow.Flow

interface TreatmentRepository {
    suspend fun getDeservedTreatment(nationalCode: String): Flow<List<DeservedTreatmentDN>>

    suspend fun getDependantUnderEighteen(
        nationalCode: String,
        filters: List<ApiFilterDN> = emptyList()
    ): Flow<List<DependantUserUnderEighteenDN>>

    suspend fun getTreatmentCosts(
        filters: List<ApiFilterDN> = emptyList()
    ): Flow<List<TreatmentCostDN>>

    suspend fun getTreatmentCostsPDF(repId: String): Flow<PdfDownloadDN>

    suspend fun sendToInboxTreatmentCosts(repId: String): Flow<String>
}
