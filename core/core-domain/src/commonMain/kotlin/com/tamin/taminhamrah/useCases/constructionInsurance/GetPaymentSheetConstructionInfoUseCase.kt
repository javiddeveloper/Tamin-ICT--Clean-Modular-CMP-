package com.tamin.taminhamrah.useCases.constructionInsurance

import com.tamin.taminhamrah.model.constructionInsurance.PaymentSheetConstructionFileDN
import com.tamin.taminhamrah.repository.constructionInsurance.ConstructionInsuranceRepository
import kotlinx.coroutines.flow.Flow

class GetPaymentSheetConstructionInfoUseCase(
    private val repository: ConstructionInsuranceRepository
) {
    operator fun invoke(debitNumber: String): Flow<List<PaymentSheetConstructionFileDN>> =
        repository.getPaymentSheetConstructionInfo(debitNumber)
}
