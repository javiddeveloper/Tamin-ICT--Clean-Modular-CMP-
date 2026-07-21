package com.tamin.taminhamrah.data.remote.models.services.payment

data class PaymentUrlRequest(
    var enteredNcodeByUser: String?,
    var personType: String? //0:current-user, //1:real, //2:legal, //3: atba(fida)
)