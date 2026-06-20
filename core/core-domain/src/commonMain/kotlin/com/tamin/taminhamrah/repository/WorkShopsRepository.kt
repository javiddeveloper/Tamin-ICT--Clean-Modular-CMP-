package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.utils.ListData
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDTO

interface WorkShopsRepository {
    suspend fun getAllEmployerAgreementByNationalId(
        page: String,
        start: String,
        limit: String,
        filter: String = "[]",
        sort: String = "[]"
    ): ListData<EmployerAgreementDTO>?
}
