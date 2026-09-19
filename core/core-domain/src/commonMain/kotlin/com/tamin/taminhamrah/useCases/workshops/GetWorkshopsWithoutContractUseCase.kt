package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.WorkshopWithoutContractDN
import com.tamin.taminhamrah.repository.WorkShopsRepository

/** Step 2 — one page of the کارگاه‌های بدون قرارداد list shown while confirming the agreement. */
class GetWorkshopsWithoutContractUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(page: Int = 0): PagedListDN<WorkshopWithoutContractDN> =
        repository.getWorkshopsWithoutContract(page)
}
