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

    fun wrapInListData(listJson: String, total: Int = 1, isArray: Boolean = true): String {
        val listContent = if (isArray) listJson else "[$listJson]"
        return """
            {
                "total": $total,
                "list": $listContent
            }
        """.trimIndent()
    }
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
}

object TreatmentTestData {
    val emptyListSuccess: String
        get() = readResourceFile("mocks/treatment_empty_list.json")
    val sendToInboxSuccess: String
        get() = readResourceFile("mocks/treatment_send_to_inbox_success.json")
    val deservedTreatmentSuccess: String
        get() = ApiTestUtils.wrapInListData(readResourceFile("mocks/treatment_deserved_success.json"), 1)
    val prescriptionsSuccess: String
        get() = ApiTestUtils.wrapInListData(readResourceFile("mocks/treatment_prescriptions_success.json"), 2)
    val prescriptionDetailsSuccess: String
        get() = ApiTestUtils.wrapInListData(readResourceFile("mocks/treatment_prescription_details_success.json"), 2)
    val prescriptionPriceSuccess: String
        get() = ApiTestUtils.wrapInListData(readResourceFile("mocks/treatment_prescription_price_success.json"), 1, isArray = false)
    val dependantsSuccess: String
        get() = ApiTestUtils.wrapInListData(readResourceFile("mocks/treatment_dependants_success.json"), 1)
    val costsSuccess: String
        get() = ApiTestUtils.wrapInListData(readResourceFile("mocks/treatment_costs_success.json"), 2)
    val medicalConfirmationsSuccess: String
        get() = ApiTestUtils.wrapInListData(readResourceFile("mocks/treatment_medical_confirmations_success.json"), 2)
}

object HealthTestData {
    val patientGeneralSuccess: String
        get() = readResourceFile("mocks/health_patient_general_success.json")
    val patientSelfDeclarativeSuccess: String
        get() = readResourceFile("mocks/health_patient_self_declarative_success.json")
    val patientDrugAllergiesSuccess: String
        get() = readResourceFile("mocks/health_patient_drug_allergies_success.json")
    val emptyListSuccess: String
        get() = readResourceFile("mocks/treatment_empty_list.json")
    val emptyArraySuccess: String
        get() = "[]"
    val emptyObjectSuccess: String
        get() = "{}"
}


object WorkshopTestData {
    val workshopDebitSuccess: String
        get() = readResourceFile("mocks/workshop_debit_success.json")

    val workshopDebtInquirySuccess: String
        get() = readResourceFile("mocks/workshop_debt_inquiry_success.json")
}
