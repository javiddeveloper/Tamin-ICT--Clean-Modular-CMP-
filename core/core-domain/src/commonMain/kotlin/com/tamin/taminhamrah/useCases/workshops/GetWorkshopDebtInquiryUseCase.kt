package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryDN
import com.tamin.taminhamrah.repository.WorkShopsRepository

class GetWorkshopDebtInquiryUseCase(
    private val repository: WorkShopsRepository
) {
    suspend operator fun invoke(workshopId: String, branchCode: String): WorkshopDebtInquiryDN? {
        return repository.getWorkshopDebtInquiry(workshopId, branchCode)
    }
}
