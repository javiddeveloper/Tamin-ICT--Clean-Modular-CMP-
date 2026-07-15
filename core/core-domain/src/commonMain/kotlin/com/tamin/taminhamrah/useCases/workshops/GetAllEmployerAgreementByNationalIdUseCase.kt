package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementListDN
import com.tamin.taminhamrah.repository.WorkShopsRepository

class GetAllEmployerAgreementByNationalIdUseCase(
    private val repository: WorkShopsRepository
) {
    suspend operator fun invoke(
        filters: List<ApiFilterDN>
    ): EmployerAgreementListDN? {
        return repository.getAllEmployerAgreementByNationalId(
            filters = filters
        )
    }
}
