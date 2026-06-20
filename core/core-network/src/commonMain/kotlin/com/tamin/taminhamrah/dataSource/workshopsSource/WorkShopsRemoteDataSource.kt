package com.tamin.taminhamrah.dataSource.workshopsSource

import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN

interface WorkShopsRemoteDataSource {
    suspend fun getAllEmployerAgreementByNationalId(
        query: ApiQueryParamDN
    ): ListData<EmployerAgreementDTO>?
}
