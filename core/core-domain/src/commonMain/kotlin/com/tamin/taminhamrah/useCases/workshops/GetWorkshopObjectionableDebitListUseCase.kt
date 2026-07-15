package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.workshop.WorkShopDebtListDN
import com.tamin.taminhamrah.repository.WorkShopsRepository
import kotlinx.coroutines.flow.Flow

class GetWorkshopObjectionableDebitListUseCase(
    private val repository: WorkShopsRepository
) {
    operator fun invoke(
        workshopNumber: String,
        branchCode: String,
        filters: List<ApiFilterDN>
    ): Flow<WorkShopDebtListDN?> {
        return repository.getWorkshopObjectionableDebitList(workshopNumber, branchCode, filters)
    }
}
