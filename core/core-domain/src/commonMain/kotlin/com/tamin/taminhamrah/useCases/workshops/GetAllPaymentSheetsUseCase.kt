package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.workshop.PaymentSheetListDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.WorkShopsRepository

class GetAllPaymentSheetsUseCase(
    private val repository: WorkShopsRepository
) {
    suspend operator fun invoke(
        query: ApiQueryParamDN
    ): PaymentSheetListDN? {
        return repository.getPaymentSheets(
            query = query
        )
    }
}
