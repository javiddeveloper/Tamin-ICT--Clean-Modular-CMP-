package com.tamin.taminhamrah.feature.contractaffair.di

import com.tamin.taminhamrah.feature.contractaffair.ui.ContractAffairsViewModel
import com.tamin.taminhamrah.feature.contractaffair.ui.paymentCalcDetail.ContractPaymentCalcDetailViewModel
import com.tamin.taminhamrah.feature.contractaffair.ui.premiumPayment.ContractPremiumPaymentViewModel
import com.tamin.taminhamrah.feature.contractaffair.ui.paymentHistory.ContractPaymentHistoryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val contractAffairModule = module {
    viewModelOf(::ContractAffairsViewModel)
    viewModelOf(::ContractPaymentHistoryViewModel)
    viewModelOf(::ContractPremiumPaymentViewModel)
    viewModelOf(::ContractPaymentCalcDetailViewModel)
}
