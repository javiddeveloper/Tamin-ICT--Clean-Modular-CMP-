package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.workshop.PaymentSheetListDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.WorkShopsRepository

class GetAllPaymentSheetsUseCase(
    private val repository: WorkShopsRepository
) {
    suspend operator fun invoke(
        filters: List<ApiFilterDN> = emptyList()
    ): PaymentSheetListDN? {
        return repository.getPaymentSheets(
            filters = filters
        )
    }
}
