package com.tamin.taminhamrah.di

import com.tamin.taminhamrah.data.content.LegalDocumentRepositoryImpl
import com.tamin.taminhamrah.repository.content.LegalDocumentRepository
import com.tamin.taminhamrah.ui.MainViewModel
import com.tamin.taminhamrah.ui.home.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::HomeViewModel)

    // Static texts served from a bundled asset until the backend exposes them by id.
    single { LegalDocumentRepositoryImpl() } bind LegalDocumentRepository::class
}
