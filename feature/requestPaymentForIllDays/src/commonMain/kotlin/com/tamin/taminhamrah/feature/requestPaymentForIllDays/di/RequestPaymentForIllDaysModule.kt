package com.tamin.taminhamrah.feature.requestPaymentForIllDays.di

import com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.calculate.IllDaysCalculateViewModel
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.intro.IllDaysIntroViewModel
import com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.wizard.IllDaysWizardViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val requestPaymentForIllDaysModule = module {
    viewModelOf(::IllDaysIntroViewModel)
    viewModelOf(::IllDaysCalculateViewModel)
    viewModelOf(::IllDaysWizardViewModel)
}
