package com.tamin.taminhamrah.feature.retirementPension.di

import com.tamin.taminhamrah.feature.retirementPension.ui.RetirementPensionViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Drives the service from in-memory data instead of the real endpoints.
 *
 * **Set this to `false` before shipping.** It exists because the retirement service only answers
 * for an account that actually has a pension case, so the eight steps cannot otherwise be walked on
 * an emulator. Only the pension and history calls are replaced — your own name, branch and image
 * uploads still come from the real services.
 *
 * See [retirementPensionMockModule] for what the fake answers, and `MOCK_REJECTED_OTP` for the code
 * that exercises the failed-verification path.
 */
const val RETIREMENT_PENSION_USE_MOCK_DATA: Boolean = false

val retirementPensionModule: Module = module {
    viewModelOf(::RetirementPensionViewModel)
}

/**
 * What `sharedModules` registers for this feature: the real module, and the mock on top of it when
 * [RETIREMENT_PENSION_USE_MOCK_DATA] is on. Kept here so the flag has one reader.
 */
val retirementPensionModules: List<Module>
    get() = if (RETIREMENT_PENSION_USE_MOCK_DATA) {
        listOf(retirementPensionModule, retirementPensionMockModule)
    } else {
        listOf(retirementPensionModule)
    }
