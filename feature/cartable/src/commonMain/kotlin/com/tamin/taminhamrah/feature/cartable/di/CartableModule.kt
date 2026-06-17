package com.tamin.taminhamrah.feature.cartable.di

import com.tamin.taminhamrah.feature.cartable.ui.CartableViewModel
import com.tamin.taminhamrah.feature.cartable.ui.MyRequestsViewModel
import com.tamin.taminhamrah.feature.cartable.ui.PersonalInboxViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val cartableModule = module {
    viewModelOf(::CartableViewModel)
    viewModelOf(::MyRequestsViewModel)
    viewModelOf(::PersonalInboxViewModel)
}
