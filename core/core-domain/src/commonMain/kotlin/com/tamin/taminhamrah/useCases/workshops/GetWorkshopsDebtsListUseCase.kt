package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.workshop.WorkshopsDebtListDN
import com.tamin.taminhamrah.repository.WorkShopsRepository
import kotlinx.coroutines.flow.Flow

class GetWorkshopsDebtsListUseCase(private val repository: WorkShopsRepository) {
    operator fun invoke(
        workshopId: String,
        branchId: String,
        filters: List<ApiFilterDN>
    ): Flow<WorkshopsDebtListDN?> {
        return repository.getWorkshopsDebtsList(workshopId, branchId, filters)
    }
}
