package com.tamin.taminhamrah.feature.pensionStatusInquiry.di

import com.tamin.taminhamrah.feature.pensionStatusInquiry.ui.PensionStatusInquiryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val pensionStatusInquiryModule = module {
    viewModelOf(::PensionStatusInquiryViewModel)
}
