package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.workshop.WorkshopDebitListDN

import com.tamin.taminhamrah.repository.WorkShopsRepository

class GetWorkshopDebitUseCase(
    private val repository: WorkShopsRepository
) {
    suspend operator fun invoke(
        workshopId: String,
        branchCode: String
    ): WorkshopDebitListDN? {
        return repository.getWorkshopDebit(
            workshopId = workshopId,
            branchCode = branchCode
        )
    }
}
