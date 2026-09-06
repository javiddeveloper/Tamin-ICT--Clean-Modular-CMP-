package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.WorkshopContractRowDN
import com.tamin.taminhamrah.repository.WorkShopsRepository

/** پیمانکاران / قراردادهای یک کارگاه — opened by drilling into a workshop. */
class GetWorkshopContractRowsUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(
        workshopId: String,
        branchCode: String,
        page: Int = 0,
    ): PagedListDN<WorkshopContractRowDN> =
        repository.getWorkshopContractRows(workshopId, branchCode, page)
}
