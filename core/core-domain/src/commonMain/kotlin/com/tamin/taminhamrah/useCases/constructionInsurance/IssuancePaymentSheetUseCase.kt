package com.tamin.taminhamrah.useCases.constructionInsurance

import com.tamin.taminhamrah.repository.constructionInsurance.ConstructionInsuranceRepository
import kotlinx.coroutines.flow.Flow

class IssuancePaymentSheetUseCase(
    private val repository: ConstructionInsuranceRepository
) {
    operator fun invoke(debitNumber: String): Flow<String> =
        repository.issuancePaymentSheet(debitNumber)
}
