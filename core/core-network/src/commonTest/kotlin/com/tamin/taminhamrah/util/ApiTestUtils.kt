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

    val checkUserIsNewSuccess: String
        get() = readResourceFile("mocks/check_user_is_new_success.json")

    val recipientsSuccess: String
        get() = readResourceFile("mocks/certificate/recipients_success.json")

    val certificateReportSuccess: String
        get() = readResourceFile("mocks/certificate/certificate_report_success.json")
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

    /** Legacy-shaped `survivor-request/personal` payload for girl survivor. */
    val girlSurvivorPersonalSuccess: String
        get() = readResourceFile("mocks/girl_survivor_personal_success.json")

    /**
     * Live-shaped `survivor-request/personal` payload (includes nested
     * `relationWithTamins` objects with Jackson-style int identity refs).
     */
    val survivorRequestPersonalLiveSuccess: String
        get() = readResourceFile("mocks/survivor_request_personal_live_success.json")

    val girlSurvivorConditionSuccess: String
        get() = readResourceFile("mocks/girl_survivor_condition_success.json")

    val girlSurvivorConditionIneligible: String
        get() = readResourceFile("mocks/girl_survivor_condition_ineligible.json")

    val girlSurvivorConfirmSuccess: String
        get() = readResourceFile("mocks/girl_survivor_confirm_success.json")

    val girlSurvivorConfirmNullData: String
        get() = readResourceFile("mocks/girl_survivor_confirm_null_data.json")

    /** Minimal PDF header bytes used for `survivor-request/report` streaming tests. */
    val girlSurvivorReportPdfBytes: ByteArray =
        ("%PDF-1.4 girl-survivor-commitment").encodeToByteArray()
}

object UserRequestTestData {
    val userRequestsSuccess: String
        get() = readResourceFile("mocks/user_requests_success.json")

    val requestTypesSuccess: String
        get() = readResourceFile("mocks/request_types_success.json")

    val requestErrorsSuccess: String
        get() = """
            {
                "total": 1,
                "list": [
                    {
                        "id": 101,
                        "errorMassage": "نقص مدارک شناسایی",
                        "errorType": "VALIDATION",
                        "errorStatus": "FAILED",
                        "creationTime": 1700000000000
                    }
                ]
            }
        """.trimIndent()

    val smartGuideSuccess: String
        get() = """
            {
                "total": 1,
                "list": [
                    {
                        "id": 201,
                        "question": "شرایط ثبت درخواست چیست؟",
                        "reply": "برای ثبت درخواست داشتن سابقه بیمه حداقل یک سال الزامی است.",
                        "requestCode": "0018",
                        "requestDesc": "درخواست راهنما",
                        "isPublic": true,
                        "title": "راهنمای هوشمند",
                        "description": "توضیحات تکمیلی"
                    }
                ]
            }
        """.trimIndent()

    val salaryDeductionCertificateSuccess: String
        get() = """
            {
                "status": 200,
                "family": "SUCCESSFUL",
                "reason": "OK",
                "traceId": "a57c8114-781e-4b42-9731-950960a5bf9c",
                "data": {
                    "list": [
                        {
                            "id": 491371155,
                            "operation": null,
                            "createdBy": "6319889391",
                            "creationTime": 1785215695428,
                            "lastModifiedBy": null,
                            "lastModificationTime": 1785215798065,
                            "refCode": "1075558440",
                            "userName": "6319889391",
                            "status": {
                                "operation": null,
                                "requestCode": "0018",
                                "requestDesc": "مختومه-تاييد نهايي"
                            },
                            "title": "درخواست گواهي کسر از حقوق",
                            "comment": null,
                            "template": null,
                            "requestType": {
                                "operation": null,
                                "createdBy": null,
                                "creationTime": null,
                                "lastModifiedBy": null,
                                "lastModificationTime": null,
                                "id": 22,
                                "title": "درخواست گواهي کسر از حقوق",
                                "description": "درخواست گواهي کسر از حقوق"
                            },
                            "deliverCode": null,
                            "refrenceid": null,
                            "requestDetails": null,
                            "requestChid": null,
                            "fullName": null,
                            "createByName": "سيدرحمت اله ميرفضلي"
                        }
                    ],
                    "total": 1
                }
            }
        """.trimIndent()
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

    val getAuthenticationCode : String
        get() = readResourceFile("mocks/pension/get_authentication_code.json")

    val retirementRequestInfoSuccess: String
        get() = readResourceFile("mocks/pension/retirement_request_info_success.json")

    val retirementStatusSuccess: String
        get() = readResourceFile("mocks/pension/retirement_status_success.json")

    val sendRetirementDocumentSuccess: String
        get() = readResourceFile("mocks/pension/send_retirement_document_success.json")

    val authenticationAndGetPersonalInfoSuccess: String
        get() = readResourceFile("mocks/pension/get_personal_info_success.json")

    val sendEdictToInboxSuccess: String
        get() = readResourceFile("mocks/pension/send_edict_to_inbox_success.json")

    val inquirePensionCertificateSuccess: String
        get() =  readResourceFile("mocks/pension/inquire_pension_certificate_success.json")
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

    // خدمات غیرحضوری کارفرما (employerEservicesAgreement)

    val employerCommitmentInfoSuccess: String
        get() = readResourceFile("mocks/employer_commitment_info_success.json")

    val employerWorkshopsWithoutContractSuccess: String
        get() = readResourceFile("mocks/employer_workshops_without_contract_success.json")

    val employerWorkshopContractRowsSuccess: String
        get() = readResourceFile("mocks/employer_workshop_contract_rows_success.json")

    val employerAgreementByWorkshopSuccess: String
        get() = readResourceFile("mocks/employer_agreement_by_workshop_success.json")

    val employerRequestTicketSuccess: String
        get() = readResourceFile("mocks/employer_request_ticket_success.json")
}

object OccurrenceTestData {
    val personalInfoSuccess: String
        get() = readResourceFile("mocks/occurrence/personal_info_success.json")

    val allWorkshopsSuccess: String
        get() = readResourceFile("mocks/occurrence/all_workshops_success.json")

    val workshopSpecSuccess: String
        get() = readResourceFile("mocks/occurrence/workshop_spec_success.json")

    val insuredRelationSuccess: String
        get() = readResourceFile("mocks/occurrence/insured_relation_success.json")

    val documentTypesSuccess: String
        get() = readResourceFile("mocks/occurrence/document_types_success.json")

    val uploadImageSuccess: String
        get() = readResourceFile("mocks/occurrence/upload_image_success.json")

    val submitOccurrenceSuccess: String
        get() = readResourceFile("mocks/occurrence/submit_occurrence_success.json")
}

object FuneralAllowanceTestData {
    /** `data` block of `funeral-no-presence/getFuneralNoPresenceLoadData` — the normal (no bank issue) case. */
    val infoSuccess: String
        get() = readResourceFile("mocks/funeralAllowance/info_success.json")

    /** Same endpoint, but `flag=true` with a stuck `request` block the branch could not confirm. */
    val infoBankAccountIssueSuccess: String
        get() = readResourceFile("mocks/funeralAllowance/info_bank_account_issue_success.json")

    /** `data` of `shortterm/validateFuneral/{nationalCode}` — the positional string array. */
    val validateDeceasedSuccess: String
        get() = readResourceFile("mocks/funeralAllowance/validate_deceased_success.json")

    /** `data` of `funeral-no-presence/saveShorttremFuneral` — a bare success-message string. */
    val submitSuccess: String
        get() = readResourceFile("mocks/funeralAllowance/submit_funeral_success.json")

    /** `data` of `funeral-no-presence/confirmShorttremFuneral/{requestId}` — a bare success-message string. */
    val confirmAccountCorrectionSuccess: String
        get() = readResourceFile("mocks/funeralAllowance/confirm_account_correction_success.json")
}

object HistoryTestData {
    val dastmozdInfosSuccess: String
        get() = readResourceFile("mocks/history/dastmozd_infos_success.json")

    val dastmozdInfosEmpty: String
        get() = readResourceFile("mocks/history/dastmozd_infos_empty.json")

    val talfighInfosSuccess: String
        get() = readResourceFile("mocks/history/talfigh_infos_success.json")

    val talfighInfosEmpty: String
        get() = readResourceFile("mocks/history/talfigh_infos_empty.json")

    val userInfoSuccess: String
        get() = readResourceFile("mocks/history/user_info_success.json")

    val historyJobInfosSuccess: String
        get() = readResourceFile("mocks/history/history_job_infos_success.json")

    val historyJobInfosEmpty: String
        get() = readResourceFile("mocks/history/history_job_infos_empty.json")

    val sendToInstitutionSuccess: String
        get() = readResourceFile("mocks/history/send_to_institution_success.json")
}

object InspectionTestData {
    val inspectionPerformedListSuccess: String
        get() = readResourceFile("mocks/inspection_performed_list_success.json")

    val inspectionBranchesListSuccess: String
        get() = readResourceFile("mocks/inspection_branches_list_success.json")

    val inspectionJobsListSuccess: String
        get() = readResourceFile("mocks/inspection_jobs_list_success.json")

    val inspectionSubmitSuccess: String
        get() = readResourceFile("mocks/inspection_submit_success.json")
}

object WorkersPaymentTestData {
    val paymentInfoSuccess: String
        get() = readResourceFile("mocks/workers_payment_info_success.json")

    val payDebitSuccess: String
        get() = readResourceFile("mocks/workers_payment_pay_debit_success.json")

    val inspectTicketSuccess: String
        get() = readResourceFile("mocks/workers_payment_inspect_ticket_success.json")
}

object ConstructionInsuranceTestData {
    val constructionFilesSuccess: String
        get() = readResourceFile("mocks/construction_files_success.json")

    val beneficiariesSuccess: String
        get() = readResourceFile("mocks/construction_beneficiaries_success.json")

    val paymentSheetsSuccess: String
        get() = readResourceFile("mocks/construction_payment_sheets_success.json")

    val installmentLettersSuccess: String
        get() = readResourceFile("mocks/construction_installment_letters_success.json")
}

