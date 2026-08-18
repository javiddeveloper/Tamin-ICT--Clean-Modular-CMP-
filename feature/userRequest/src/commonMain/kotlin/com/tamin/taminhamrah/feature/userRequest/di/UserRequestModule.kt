package com.tamin.taminhamrah.feature.userRequest.di

import com.tamin.taminhamrah.feature.userRequest.ui.UserRequestsViewModel
import com.tamin.taminhamrah.feature.userRequest.ui.screens.UserRequestDetailViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val userRequestModule = module {
    viewModelOf(::UserRequestsViewModel)
    viewModelOf(::UserRequestDetailViewModel)
}
