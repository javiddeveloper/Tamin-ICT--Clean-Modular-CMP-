package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param

data class FishFilter(
    val startDate: Long?,
    val endDate: Long?,
    val pensionerId: String?,
    val nationalId: String?,
    val paymentType: String?,
)
