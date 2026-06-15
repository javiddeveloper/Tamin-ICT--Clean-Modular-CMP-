package com.tamin.taminhamrah.feature.pensionInquiry.di

import com.tamin.taminhamrah.feature.pensionInquiry.ui.PensionInquiryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val pensionInquiryModule = module {
    viewModelOf(::PensionInquiryViewModel)
}
