package com.tamin.taminhamrah.repository.workshops

import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementListDN
import com.tamin.taminhamrah.repository.WorkShopsRepository

class FakeWorkShopsRepository : WorkShopsRepository {
    var result: EmployerAgreementListDN? = null
    var shouldThrowError: Boolean = false
    var error: Exception = RuntimeException("Fake Network Error")

    override suspend fun getAllEmployerAgreementByNationalId(query: ApiQueryParamDN): EmployerAgreementListDN? {
        if (shouldThrowError) {
            throw error
        }
        return result
    }
}
