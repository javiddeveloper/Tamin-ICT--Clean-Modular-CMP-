package com.tamin.taminhamrah.repository.common

import com.tamin.taminhamrah.model.common.BeneficiaryDN
import com.tamin.taminhamrah.model.common.JobTitleListDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadDN
import io.ktor.client.statement.HttpStatement

interface CommonRepository {
    fun getBeneficiary(filters: List<ApiFilterDN>): Flow<List<BeneficiaryDN>>
    suspend fun getRegistrationDeclarationForm(): HttpStatement
    suspend fun getJobTitle(query: ApiQueryParamDN): JobTitleListDN?
}
