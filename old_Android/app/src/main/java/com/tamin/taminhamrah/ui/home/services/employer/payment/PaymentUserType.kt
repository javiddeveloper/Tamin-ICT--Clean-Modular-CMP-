package com.tamin.taminhamrah.ui.home.services.employer.payment

enum class PaymentUserType(var typeCode: Int) {
    CURRENT_USER(0),
    OTHER_USER(1),
    NATIONAL_ID(2),
    FOREIGNERS(3)
}