package com.tamin.taminhamrah.di

import com.tamin.taminhamrah.useCases.auth.AuthAuthorizeUrlUseCase
import com.tamin.taminhamrah.useCases.auth.AuthAuthorizeUrlUseCaseImpl
import com.tamin.taminhamrah.useCases.auth.DeepLinkManager
import com.tamin.taminhamrah.useCases.auth.DeepLinkManagerImpl
import com.tamin.taminhamrah.useCases.auth.ExchangeCodeForTokensUseCase
import com.tamin.taminhamrah.useCases.auth.ExchangeCodeForTokensUseCaseImpl
import com.tamin.taminhamrah.useCases.auth.HandleAuthDeepLinkUseCase
import com.tamin.taminhamrah.useCases.auth.HandleAuthDeepLinkUseCaseImpl
import com.tamin.taminhamrah.useCases.bankAccount.GetBankAccountListUseCase
import com.tamin.taminhamrah.useCases.bankAccount.GetBankAccountListUseCaseImpl
import com.tamin.taminhamrah.useCases.file.GetElectronicFileUseCase
import com.tamin.taminhamrah.useCases.file.GetElectronicFileUseCaseImpl
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCaseImpl
import com.tamin.taminhamrah.useCases.user.GetInsuredActiveBranchUseCase
import com.tamin.taminhamrah.useCases.user.GetInsuredActiveBranchUseCaseImpl
import com.tamin.taminhamrah.useCases.user.GetRelationTaminAllUseCase
import com.tamin.taminhamrah.useCases.user.GetRelationTaminAllUseCaseImpl
import com.tamin.taminhamrah.useCases.user.SendImageRequestUseCase
import com.tamin.taminhamrah.useCases.user.SendImageRequestUseCaseImpl
import com.tamin.taminhamrah.useCases.user.SubdominantUseCase
import com.tamin.taminhamrah.useCases.user.SubdominantUseCaseImpl
import com.tamin.taminhamrah.useCases.user.TaminRelationUseCase
import com.tamin.taminhamrah.useCases.user.TaminRelationUseCaseImpl
import com.tamin.taminhamrah.useCases.user.UserProfileImageUseCase
import com.tamin.taminhamrah.useCases.user.UserProfileImageUseCaseImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::DeepLinkManagerImpl) bind DeepLinkManager::class
    factoryOf(::AuthAuthorizeUrlUseCaseImpl) bind AuthAuthorizeUrlUseCase::class
    factoryOf(::ExchangeCodeForTokensUseCaseImpl) bind ExchangeCodeForTokensUseCase::class
    factoryOf(::HandleAuthDeepLinkUseCaseImpl) bind HandleAuthDeepLinkUseCase::class
    factoryOf(::UserProfileImageUseCaseImpl) bind UserProfileImageUseCase::class
    factoryOf(::TaminRelationUseCaseImpl) bind TaminRelationUseCase::class
    factoryOf(::IdentityInfoUseCaseImpl) bind IdentityInfoUseCase::class
    factoryOf(::SendImageRequestUseCaseImpl) bind SendImageRequestUseCase::class
    factoryOf(::SubdominantUseCaseImpl) bind SubdominantUseCase::class
    factoryOf(::GetBankAccountListUseCaseImpl) bind GetBankAccountListUseCase::class
    factoryOf(::GetInsuredActiveBranchUseCaseImpl) bind GetInsuredActiveBranchUseCase::class
    factoryOf(::GetRelationTaminAllUseCaseImpl) bind GetRelationTaminAllUseCase::class
    factoryOf(::GetElectronicFileUseCaseImpl) bind GetElectronicFileUseCase::class
}
