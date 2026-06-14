package com.tamin.taminhamrah.di

import com.tamin.taminhamrah.useCases.auth.AuthAuthorizeUrlUseCase
import com.tamin.taminhamrah.useCases.auth.AuthAuthorizeUrlUseCaseImpl
import com.tamin.taminhamrah.useCases.auth.DeepLinkManager
import com.tamin.taminhamrah.useCases.auth.DeepLinkManagerImpl
import com.tamin.taminhamrah.useCases.auth.ExchangeCodeForTokensUseCase
import com.tamin.taminhamrah.useCases.auth.ExchangeCodeForTokensUseCaseImpl
import com.tamin.taminhamrah.useCases.auth.HandleAuthDeepLinkUseCase
import com.tamin.taminhamrah.useCases.auth.HandleAuthDeepLinkUseCaseImpl
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.useCases.user.ChangeMobileUseCase
import com.tamin.taminhamrah.useCases.user.SendImageRequestUseCase
import com.tamin.taminhamrah.useCases.user.TaminRelationUseCase
import com.tamin.taminhamrah.useCases.user.UserProfileImageUseCase
import com.tamin.taminhamrah.useCases.user.VerifyChangeMobileUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::DeepLinkManagerImpl) bind DeepLinkManager::class
    factoryOf(::AuthAuthorizeUrlUseCaseImpl) bind AuthAuthorizeUrlUseCase::class
    factoryOf(::ExchangeCodeForTokensUseCaseImpl) bind ExchangeCodeForTokensUseCase::class
    factoryOf(::HandleAuthDeepLinkUseCaseImpl) bind HandleAuthDeepLinkUseCase::class
    factoryOf(::UserProfileImageUseCase)
    factoryOf(::TaminRelationUseCase)
    factoryOf(::IdentityInfoUseCase)
    factoryOf(::SendImageRequestUseCase)
    factoryOf(::ChangeMobileUseCase)
    factoryOf(::VerifyChangeMobileUseCase)
}
