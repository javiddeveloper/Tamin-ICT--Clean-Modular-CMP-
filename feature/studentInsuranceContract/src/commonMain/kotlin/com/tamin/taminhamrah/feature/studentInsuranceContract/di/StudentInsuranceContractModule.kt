package com.tamin.taminhamrah.feature.studentInsuranceContract.di

import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.StudentInsuranceContractViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val studentInsuranceContractModule = module {
    viewModelOf(::StudentInsuranceContractViewModel)
}
