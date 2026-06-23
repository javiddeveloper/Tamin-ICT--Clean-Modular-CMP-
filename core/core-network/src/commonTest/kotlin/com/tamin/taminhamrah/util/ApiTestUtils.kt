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
}

object PensionTestData {
    val payrollSuccess: String
        get() = readResourceFile("mocks/pension_payroll_success.json")

    val disabilityPersonalInfoSuccess: String
        get() = """
            {
                "branch": "123",
                "branchName": "شعبه یک",
                "confirmed": true,
                "insuranceId": "12345678",
                "mobileNumber": "09121234567",
                "personal": {
                    "firstName": "Ali",
                    "lastName": "Alavi",
                    "nationalId": "0012345678",
                    "fatherName": "Reza",
                    "idCardNumber": "123",
                    "cityOfIssue": {
                        "code": "1",
                        "description": "تهران"
                    },
                    "dateOfBirth": 315532800000,
                    "gender": {
                        "genderCode": 1,
                        "genderDesc": "مرد"
                    }
                },
                "provinceName": "تهران",
                "work": {
                    "job": {
                        "jobCode": "101",
                        "jobDescription": "برنامه نویس"
                    },
                    "workshopId": "987654321"
                },
                "yearsAge": "40",
                "monthsAge": "5",
                "daysAge": "10",
                "strAge": "40 سال و 5 ماه و 10 روز"
            }
        """.trimIndent()
}

object WorkshopTestData {
    val workshopDebitSuccess: String
        get() = readResourceFile("mocks/workshop_debit_success.json")

    val workshopDebtInquirySuccess: String
        get() = readResourceFile("mocks/workshop_debt_inquiry_success.json")
}
