package com.tamin.taminhamrah.feature.profile.di

import com.tamin.taminhamrah.feature.profile.ui.ProfileViewModel
import com.tamin.taminhamrah.feature.profile.ui.bankAccount.BankAccountViewModel
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.ElectronicFileViewModel
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.ActiveRelationViewModel
import com.tamin.taminhamrah.feature.profile.ui.contactUs.ContactUsViewModel
import com.tamin.taminhamrah.feature.profile.ui.identity.IdentityInViewModel
import com.tamin.taminhamrah.feature.profile.ui.versionHistory.VersionHistoryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val profileModule = module {
    viewModelOf(::ProfileViewModel)
    viewModelOf(::IdentityInViewModel)
    viewModelOf(::ElectronicFileViewModel)
    viewModelOf(::VersionHistoryViewModel)
    viewModelOf(::BankAccountViewModel)
    viewModelOf(::ActiveRelationViewModel)
    viewModelOf(::ContactUsViewModel)
}

