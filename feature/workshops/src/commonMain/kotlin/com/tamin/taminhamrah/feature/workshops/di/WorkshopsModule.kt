package com.tamin.taminhamrah.feature.workshops.di

import com.tamin.taminhamrah.feature.workshops.ui.WorkshopsViewModel
import com.tamin.taminhamrah.feature.workshops.ui.paymentSheets.PaymentSheetsViewModel
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachmentUploader
import com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.document.ObjectionDocumentViewModel
import com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.list.ObjectionStatusViewModel
import com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.sms.ObjectionSmsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val workshopsModule = module {
    // Shared by the three forms that attach evidence.
    factoryOf(::WorkshopAttachmentUploader)

    viewModelOf(::WorkshopsViewModel)
    viewModelOf(::PaymentSheetsViewModel)
    viewModelOf(::ObjectionStatusViewModel)
    viewModelOf(::ObjectionSmsViewModel)
    viewModelOf(::ObjectionDocumentViewModel)
}
