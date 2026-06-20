package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.workshop.EmployerAgreementListDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.WorkShopsRepository

class GetAllEmployerAgreementByNationalIdUseCase(
    private val repository: WorkShopsRepository
) {
    suspend operator fun invoke(
        query: ApiQueryParamDN
    ): EmployerAgreementListDN? {
        return repository.getAllEmployerAgreementByNationalId(
            query = query
        )
    }
}
