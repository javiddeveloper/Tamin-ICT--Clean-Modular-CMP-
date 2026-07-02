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

    val optionalCalculateSalarySuccess: String
        get() = readResourceFile("mocks/optional_calculate_salary_success.json")

    val registrationInfoSuccess: String
        get() = readResourceFile("mocks/registration_info_success.json")

    val branchesListSuccess: String
        get() = readResourceFile("mocks/branches_list_success.json")

    val freeJobWagesSuccess: String
        get() = readResourceFile("mocks/free_job_wages_success.json")

    val uploadImageSuccess: String
        get() = readResourceFile("mocks/upload_image_success.json")

    val insurancePaymentSuccess: String
        get() = readResourceFile("mocks/insurance_payment_success.json")

    val insurancePaymentStatusSuccess: String
        get() = readResourceFile("mocks/insurance_payment_status_success.json")
}

object PensionTestData {
    val payrollSuccess: String
        get() = readResourceFile("mocks/pension_payroll_success.json")


    val userAgeSuccess: String
        get() = readResourceFile("mocks/pension/user_age_success.json")


    val disabilityPersonalInfoSuccess : String
        get() = readResourceFile("mocks/pension/disability_personal_info_success.json")

    val retirementStatusSuccess: String
        get() = readResourceFile("mocks/pension/retirement_status_success.json")
}

object WorkshopTestData {
    val workshopDebitSuccess: String
        get() = readResourceFile("mocks/workshop_debit_success.json")

    val workshopDebtInquirySuccess: String
        get() = readResourceFile("mocks/workshop_debt_inquiry_success.json")
}
