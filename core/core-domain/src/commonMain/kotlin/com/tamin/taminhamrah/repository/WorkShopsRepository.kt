package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.workshop.EmployerAgreementListDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN

interface WorkShopsRepository {
    suspend fun getAllEmployerAgreementByNationalId(
        query: ApiQueryParamDN
    ): EmployerAgreementListDN?
}
