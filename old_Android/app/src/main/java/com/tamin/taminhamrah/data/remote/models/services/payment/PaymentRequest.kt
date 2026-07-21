package com.tamin.taminhamrah.data.remote.models.services.payment

data class PaymentRequest(
    var branchCode: String?,
    var workshopId: String?,
    var  debitNumber: String?,
    var  peymanSequence: String?,
    var  seporde: String?
)