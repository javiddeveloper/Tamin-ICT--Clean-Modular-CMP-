package com.tamin.taminhamrah.dataSource.treatment

import com.tamin.taminhamrah.model.treatment.*
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.utils.ListData

interface TreatmentRemoteDataSource {
    suspend fun getDeservedTreatment(nationalCode: String): ListData<DeservedTreatmentDTO>?

    suspend fun getDependantUnderEighteen(
        nationalCode: String,
        query: ApiQueryParamDN
    ): ListData<DependantUserUnderEighteenDTO>?

    suspend fun getTreatmentCosts(
        query: ApiQueryParamDN
    ): ListData<TreatmentCostDTO>?

    suspend fun getTreatmentCostsPDF(repId: String): PdfDownloadDTO

    suspend fun sendToInboxTreatmentCosts(repId: String): String
}
