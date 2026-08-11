package com.tamin.taminhamrah.feature.userRequest.di

import com.tamin.taminhamrah.feature.userRequest.ui.UserRequestsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val userRequestModule = module {
    viewModelOf(::UserRequestsViewModel)
}
