package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.workshop.WorkshopContractRowDN
import com.tamin.taminhamrah.repository.WorkShopsRepository
import kotlinx.coroutines.flow.Flow

/**
 * پیمانکاران / قراردادهای یک کارگاه — opened by drilling into a workshop.
 *
 * Offline-first: emits the cached page (if any), then the network page. Collect the whole flow
 * (`Paginator(loadPages = …)`), not `.first()`.
 */
class GetWorkshopContractRowsUseCase(private val repository: WorkShopsRepository) {
    operator fun invoke(
        workshopId: String,
        branchCode: String,
        page: Int = 0,
    ): Flow<PageDN<WorkshopContractRowDN>> =
        repository.getWorkshopContractRows(workshopId, branchCode, page)
}
