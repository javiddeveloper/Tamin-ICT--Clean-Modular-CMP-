package com.tamin.taminhamrah.feature.addDependent.di

import com.tamin.taminhamrah.feature.addDependent.ui.AddDependentViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val addDependentModule = module {
    viewModel {
        AddDependentViewModel(
            getActiveBranchesUseCase = get(),
            getFamilyRelationshipsFromProxyUseCase = get(),
            inquiryRegistryUseCase = get(),
            inquiryEducationCodeUseCase = get(),
            uploadDependentImageUseCase = get(),
            addNewDependentUseCase = get(),
            getCitiesUseCase = get()
        )
    }
}
