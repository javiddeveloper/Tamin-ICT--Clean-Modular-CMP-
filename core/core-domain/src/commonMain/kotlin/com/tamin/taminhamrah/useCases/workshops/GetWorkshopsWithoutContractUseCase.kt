package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.workshop.WorkshopWithoutContractDN
import com.tamin.taminhamrah.repository.WorkShopsRepository
import kotlinx.coroutines.flow.Flow

/**
 * Step 2 — one page of the کارگاه‌های بدون قرارداد list shown while confirming the agreement.
 *
 * Offline-first: emits the cached page (if any), then the network page. Collect the whole flow
 * (`Paginator(loadPages = …)`), not `.first()`.
 */
class GetWorkshopsWithoutContractUseCase(private val repository: WorkShopsRepository) {
    operator fun invoke(page: Int = 0): Flow<PageDN<WorkshopWithoutContractDN>> =
        repository.getWorkshopsWithoutContract(page)
}
