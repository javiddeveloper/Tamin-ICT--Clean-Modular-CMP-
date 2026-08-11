package com.tamin.taminhamrah.feature.myinbox.di

import com.tamin.taminhamrah.feature.myinbox.ui.MyInboxViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val myInboxModule = module {
    viewModelOf(::MyInboxViewModel)
}
