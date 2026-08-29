package com.tamin.taminhamrah.feature.workshops.di

import com.tamin.taminhamrah.feature.workshops.ui.WorkshopsViewModel
import com.tamin.taminhamrah.feature.workshops.ui.paymentSheets.PaymentSheetsViewModel
import com.tamin.taminhamrah.feature.workshops.ui.workshopDebit.WorkshopDebitViewModel
import com.tamin.taminhamrah.feature.workshops.ui.workshopDebtInquiry.WorkshopDebtInquiryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

import com.tamin.taminhamrah.feature.workshops.ui.objectionableDebit.ObjectionableDebitViewModel
import com.tamin.taminhamrah.feature.workshops.ui.managementDebit.ManagementDebitViewModel
import com.tamin.taminhamrah.feature.workshops.ui.workshopMembers.WorkshopMembersViewModel
import com.tamin.taminhamrah.feature.workshops.ui.workshopStackholders.WorkshopStackholdersViewModel
import com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.WorkshopRecentlyAddedMembersViewModel
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.workshops.LegalRepresentativeWorkshopsViewModel
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.otp.LegalRepresentativeOtpViewModel
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.list.LegalRepresentativeListViewModel
import com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.add.AddLegalRepresentativeViewModel

val workshopsModule = module {
    viewModelOf(::WorkshopsViewModel)
    viewModelOf(::PaymentSheetsViewModel)
    viewModelOf(::WorkshopDebitViewModel)
    viewModelOf(::WorkshopDebtInquiryViewModel)
    viewModelOf(::ObjectionableDebitViewModel)
    viewModelOf(::ManagementDebitViewModel)
    viewModelOf(::WorkshopMembersViewModel)
    viewModelOf(::WorkshopStackholdersViewModel)
    viewModelOf(::WorkshopRecentlyAddedMembersViewModel)
    viewModelOf(::LegalRepresentativeWorkshopsViewModel)
    viewModelOf(::LegalRepresentativeOtpViewModel)
    viewModelOf(::LegalRepresentativeListViewModel)
    viewModelOf(::AddLegalRepresentativeViewModel)
}
