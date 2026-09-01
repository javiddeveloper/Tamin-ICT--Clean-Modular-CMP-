package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.EmployerAgreementByWorkshopDN
import com.tamin.taminhamrah.repository.WorkShopsRepository

/** Management side — تعهدهای ثبت‌شده‌ی یک کارگاه. */
class GetEmployerAgreementsByWorkshopUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(
        workshopId: String,
        branchCode: String,
        page: Int = 0,
    ): PagedListDN<EmployerAgreementByWorkshopDN> =
        repository.getEmployerAgreementsByWorkshop(workshopId, branchCode, page)
}
