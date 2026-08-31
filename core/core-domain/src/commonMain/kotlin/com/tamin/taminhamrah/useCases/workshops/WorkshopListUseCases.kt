package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.model.workshop.WorkshopListQuery
import com.tamin.taminhamrah.repository.WorkShopsRepository

/** The کارگاه‌های کارفرما list itself: one page, with search and status filter applied together. */
class GetEmployerAgreementsUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(query: WorkshopListQuery): PagedListDN<EmployerAgreementDN> =
        repository.getEmployerAgreements(query)
}
