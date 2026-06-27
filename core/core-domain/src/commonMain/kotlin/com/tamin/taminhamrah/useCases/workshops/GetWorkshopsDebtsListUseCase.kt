package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListDN
import com.tamin.taminhamrah.repository.WorkShopsRepository

class GetWorkshopsDebtsListUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(
        workshopId: String,
        branchId: String,
        query: ApiQueryParamDN
    ): WorkshopsDebtListDN? {
        return repository.getWorkshopsDebtsList(workshopId, branchId, query)
    }
}
