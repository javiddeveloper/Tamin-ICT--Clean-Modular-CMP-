package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.DebitReasonDN
import com.tamin.taminhamrah.model.workshop.PaymentSheetDN
import com.tamin.taminhamrah.model.workshop.PaymentSheetQuery
import com.tamin.taminhamrah.repository.WorkShopsRepository

/** برگ پرداخت‌ها of one workshop, with whatever the search sheet set. */
class GetPaymentSheetsUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(query: PaymentSheetQuery): PagedListDN<PaymentSheetDN> =
        repository.getPaymentSheets(query)
}

/** The علت ایجاد بدهی picker behind the payment-sheet search. */
class GetDebitReasonsUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(page: Int = 0): PagedListDN<DebitReasonDN> =
        repository.getDebitReasons(page)
}
