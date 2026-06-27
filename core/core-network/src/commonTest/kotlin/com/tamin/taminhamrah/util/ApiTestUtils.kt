package com.tamin.taminhamrah.util

object ApiTestUtils {
    fun createJsonResponse(
        dataJson: String,
        status: Int = 200,
        family: String = "SUCCESSFUL",
        reason: String = "OK"
    ): String = """
        {
            "status": $status,
            "family": "$family",
            "reason": "$reason",
            "traceId": "mock-trace-id",
            "data": $dataJson
        }
    """.trimIndent()
}

object UserTestData {
    val identityInfoSuccess: String
        get() = readResourceFile("mocks/identity_info_success.json")

    val changeMobileSuccess: String
        get() = readResourceFile("mocks/change_mobile_success.json")

    val verifyMobileSuccess: String
        get() = readResourceFile("mocks/verify_mobile_success.json")

    val checkUserIsNewSuccess: String
        get() = readResourceFile("mocks/check_user_is_new_success.json")
}

object CommonTestData {
    val jobTitleSuccess: String
        get() = readResourceFile("mocks/job_title_success.json")
}

object PersonalTestData {
    val insuredDocSuccess: String
        get() = readResourceFile("mocks/insured_doc_success.json")

    val requestSummarySuccess: String
        get() = readResourceFile("mocks/request_summary_success.json")
}

object UserRequestTestData {
    val userRequestsSuccess: String
        get() = readResourceFile("mocks/user_requests_success.json")

    val requestTypesSuccess: String
        get() = readResourceFile("mocks/request_types_success.json")
}

object PersonalInboxTestData {
    val inboxItemsSuccess: String
        get() = readResourceFile("mocks/inbox_items_success.json")

    val inboxSizeSuccess: String
        get() = readResourceFile("mocks/inbox_size_success.json")
}

object ContractsTestData {
    val contractsListSuccess: String
        get() = readResourceFile("mocks/contracts_list_success.json")

    val spcPremiumRateSuccess: String
        get() = readResourceFile("mocks/spc_premium_rate_success.json")

    val freelancePremiumRangeSuccess: String
        get() = readResourceFile("mocks/freelance_premium_range_success.json")

    val freelanceCalculateSalarySuccess: String
        get() = readResourceFile("mocks/freelance_calculate_salary_success.json")

    val freelanceMakeContractSuccess: String
        get() = readResourceFile("mocks/freelance_make_contract_success.json")
}

object PensionTestData {
    val payrollSuccess: String
        get() = readResourceFile("mocks/pension_payroll_success.json")

    val disabilityPersonalInfoSuccess: String
        get() = readResourceFile("mocks/disability_personal_info.json")

    val userAgeSuccess: String
        get() = readResourceFile("mocks/pension/user_age_success.json")
}

object WorkshopTestData {
    val workshopDebitSuccess: String
        get() = readResourceFile("mocks/workshop_debit_success.json")

    val workshopDebtInquirySuccess: String
        get() = readResourceFile("mocks/workshop_debt_inquiry_success.json")

    val employerAgreementSuccess: String
        get() = readResourceFile("mocks/employer_agreement_success.json")

    val paymentSheetsSuccess: String
        get() = readResourceFile("mocks/payment_sheets_success.json")

    val objectionableDebitSuccess: String
        get() = readResourceFile("mocks/objectionable_debit_success.json")

    val recentlyAddedMembersSuccess: String
        get() = readResourceFile("mocks/recently_added_members_success.json")

    val managementDebitSuccess: String
        get() = readResourceFile("mocks/management_debit_success.json")

    val workshopMembersSuccess: String
        get() = readResourceFile("mocks/workshop_members_success.json")

    val workshopStackholdersSuccess: String
        get() = readResourceFile("mocks/workshop_stackholders_success.json")
}
