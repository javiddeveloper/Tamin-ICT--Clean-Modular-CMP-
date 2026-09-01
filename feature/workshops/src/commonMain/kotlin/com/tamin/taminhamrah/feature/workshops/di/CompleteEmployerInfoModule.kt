package com.tamin.taminhamrah.feature.workshops.di

import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.CompleteEmployerInfoViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * تکمیل اطلاعات کارفرمایی's own Koin module.
 *
 * Separate from [workshopsModule] for the same reason its navigation is separate: the workshop
 * services arrive one merge request at a time and each registers a ViewModel in that module, so a
 * registration of this feature's among them would collide with every one of them in turn.
 */
val completeEmployerInfoModule = module {
    viewModelOf(::CompleteEmployerInfoViewModel)
}
