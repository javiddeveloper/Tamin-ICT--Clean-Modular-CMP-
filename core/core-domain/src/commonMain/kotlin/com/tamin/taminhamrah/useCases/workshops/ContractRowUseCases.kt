package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.ContractRowQuery
import com.tamin.taminhamrah.model.workshop.EmployerAgreementDN
import com.tamin.taminhamrah.model.workshop.WorkshopContractDN
import com.tamin.taminhamrah.repository.WorkShopsRepository

/*
 * ردیف پیمان‌های یک کارگاه.
 *
 * Two use cases rather than one with a flag: they return different domain models, so a single
 * entry point would have to hand back a union the caller unpacks anyway. The screen's tab picks
 * which one runs.
 */

/** ردیف پیمان‌های a workshop that has a تعهدنامه — carries the contact and address columns. */
class GetContractRowsWithAgreementUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(query: ContractRowQuery): PagedListDN<EmployerAgreementDN> =
        repository.getContractRowsWithAgreement(query)
}

/** ردیف پیمان‌های a workshop with no تعهدنامه — the four basic columns only. */
class GetContractRowsWithoutAgreementUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(query: ContractRowQuery): PagedListDN<WorkshopContractDN> =
        repository.getContractRowsWithoutAgreement(query)
}
