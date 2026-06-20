package com.tamin.taminhamrah.dataSource.workshopsSource

import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDTO

interface WorkShopsRemoteDataSource {
    suspend fun getAllEmployerAgreementByNationalId(
        page: String,
        start: String,
        limit: String,
        filter: String = "[]",
        sort: String = "[]"
    ): ListData<EmployerAgreementDTO>?
}
