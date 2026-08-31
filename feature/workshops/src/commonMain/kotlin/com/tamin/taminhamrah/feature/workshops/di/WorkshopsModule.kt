package com.tamin.taminhamrah.feature.workshops.di

import com.tamin.taminhamrah.feature.workshops.ui.WorkshopsViewModel
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.CompleteEmployerInfoViewModel
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachmentUploader
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val workshopsModule = module {
    // Shared by the three forms that attach evidence.
    factoryOf(::WorkshopAttachmentUploader)

    viewModelOf(::WorkshopsViewModel)
    viewModelOf(::CompleteEmployerInfoViewModel)
}
