package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.treatment.*
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.treatment.TreatmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeTreatmentRepository : TreatmentRepository {
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Fake Treatment Repository Error")

    var getDeservedTreatmentResult: List<DeservedTreatmentDN> = emptyList()
    var getDependantUnderEighteenResult: List<DependantUserUnderEighteenDN> = emptyList()
    var getTreatmentCostsResult: List<TreatmentCostDN> = emptyList()
    var getTreatmentCostsPDFResult: PdfDownloadDN = PdfDownloadDN(pdf = InputStreamDN(pdf = null))
    var sendToInboxTreatmentCostsResult: String = ""

    override suspend fun getDeservedTreatment(nationalCode: String): Flow<List<DeservedTreatmentDN>> = flow {
        if (shouldThrowError) throw error
        emit(getDeservedTreatmentResult)
    }

    override suspend fun getDependantUnderEighteen(
        nationalCode: String, filters: List<ApiFilterDN>
    ): Flow<List<DependantUserUnderEighteenDN>> = flow {
        if (shouldThrowError) throw error
        emit(getDependantUnderEighteenResult)
    }

    override suspend fun getTreatmentCosts(filters: List<ApiFilterDN>): Flow<List<TreatmentCostDN>> = flow {
        if (shouldThrowError) throw error
        emit(getTreatmentCostsResult)
    }

    override suspend fun getTreatmentCostsPDF(repId: String): Flow<PdfDownloadDN> = flow {
        if (shouldThrowError) throw error
        emit(getTreatmentCostsPDFResult)
    }

    override suspend fun sendToInboxTreatmentCosts(repId: String): Flow<String> = flow {
        if (shouldThrowError) throw error
        emit(sendToInboxTreatmentCostsResult)
    }
}
