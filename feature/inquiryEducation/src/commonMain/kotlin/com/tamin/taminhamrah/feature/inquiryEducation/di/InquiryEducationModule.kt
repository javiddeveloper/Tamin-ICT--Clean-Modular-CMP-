package com.tamin.taminhamrah.feature.inquiryEducation.di

import com.tamin.taminhamrah.feature.inquiryEducation.ui.InquiryEducationViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val inquiryEducationModule = module {
    viewModelOf(::InquiryEducationViewModel)
}
