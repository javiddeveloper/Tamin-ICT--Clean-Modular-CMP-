package com.tamin.taminhamrah.feature.profile.di

import com.tamin.taminhamrah.feature.profile.ui.ProfileViewModel
import com.tamin.taminhamrah.feature.profile.ui.addDependent.AddDependentViewModel
import com.tamin.taminhamrah.feature.profile.ui.dependents.DependentsListViewModel
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.ActiveRelationViewModel
import com.tamin.taminhamrah.feature.profile.ui.contactUs.ContactUsViewModel
import com.tamin.taminhamrah.feature.profile.ui.identity.IdentityInViewModel
import com.tamin.taminhamrah.feature.profile.ui.versionHistory.VersionHistoryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val profileModule = module {
    viewModelOf(::ProfileViewModel)
    viewModelOf(::IdentityInViewModel)
    viewModelOf(::VersionHistoryViewModel)
    viewModelOf(::ActiveRelationViewModel)
    viewModelOf(::ContactUsViewModel)
    viewModelOf(::AddDependentViewModel)
    viewModelOf(::DependentsListViewModel)
}

