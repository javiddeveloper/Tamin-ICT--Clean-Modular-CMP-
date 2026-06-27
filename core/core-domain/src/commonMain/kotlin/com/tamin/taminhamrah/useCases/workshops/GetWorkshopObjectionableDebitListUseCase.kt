package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.workshop.WorkShopDebtListDN
import com.tamin.taminhamrah.repository.WorkShopsRepository

class GetWorkshopObjectionableDebitListUseCase(
    private val repository: WorkShopsRepository
) {
    suspend operator fun invoke(
        workshopNumber: String,
        branchCode: String,
        filters: List<ApiFilterDN> = emptyList()
    ): WorkShopDebtListDN? {
        return repository.getWorkshopObjectionableDebitList(workshopNumber, branchCode, filters)
    }
}
