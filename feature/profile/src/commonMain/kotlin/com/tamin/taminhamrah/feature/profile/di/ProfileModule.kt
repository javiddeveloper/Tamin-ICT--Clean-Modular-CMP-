package com.tamin.taminhamrah.feature.profile.di

import com.tamin.taminhamrah.feature.profile.data.repository.UserRepositoryImpl
import com.tamin.taminhamrah.feature.profile.ui.ProfileViewModel
import com.tamin.taminhamrah.repository.UserRepository
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val profileModule = module {
    singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
    viewModelOf(::ProfileViewModel)
}
