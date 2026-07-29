package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.model

import com.tamin.taminhamrah.R

enum class WorkshopActionsEnumClass(var titleResource:Int, var id:String) {
    PAYMENT_SHEET(titleResource = R.string.payment_sheet, id = "0"),
    DEBIT_ACCOUNT_TURNOVER_DETAILS(titleResource = R.string.debit_account_turnover_details, id = "1"),
    INQUIRY_DEBITS_WORKSHOP(titleResource = R.string.inquiry_debits_workshop, id = "2"),
    OBJECTION_TO_DEBIT(titleResource = R.string.objection_to_debit, id = "3"),
    INSURED_ABSENTEE_REGISTRATION(titleResource = R.string.insured_absentee_registration, id = "4"),
    REGISTRATION_DEBIT_ARTICLE16(titleResource = R.string.registration_debit_article16, id = "5"),
    EMPLOYEES(titleResource = R.string.label_employees, id = "6"),
    STACK_HOLDERS(titleResource = R.string.label_stack_holders, id = "7"),
//    DISTANT_CORRESPONDENCE(titleResource = R.string.label_distant_correspondence_with_branch, id = "8"),
    CORRESPONDENCE_AND_ANNOUNCEMENT_LIST(titleResource = R.string.label_correspondence_and_announcement, id = "9")
}

