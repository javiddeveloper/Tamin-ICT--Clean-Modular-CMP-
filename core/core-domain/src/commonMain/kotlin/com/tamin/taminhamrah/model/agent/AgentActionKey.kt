package com.tamin.taminhamrah.model.agent

import com.tamin.taminhamrah.model.common.FeatureFlag

enum class AgentActionKey(val key: String) {

    GENERAL_RESPONSE("general_response"),
    MESSAGE("message"),

    DASTMOZD_INFOS("dastmozd_infos"),
    DASTMOZD_INFOS_LAST("dastmozd_infos_last"),
    DASTMOZD_INFOS_PER_YEAR("dastmozd_infos_per_year"),
    DASTMOZD_INFOS_SALARY("dastmozd_infos_salary"),
    DASTMOZD_INFOS_SUM_TOTAL("dastmozd_infos_sum_total"),
    AVERAGE_DASTMOZD_INFOS("average_dastmozd_infos"),
    AVERAGE_DASTMOZD_INFOS_PER_DATE("average_dastmozd_infos_per_date"),

    HISTORY_JOB_INFOS("history_job_infos"),
    HISTORY_JOB_INFOS_LAST("history_job_infos_last"),
    HISTORY_SERVICES("history_services"),
    HISTORY_SERVICES_LAST("history_services_last"),

    PENSION_INQUIRY_ALL("pension_inquiry_all"),
    PENSION_INQUIRY_LAST("pension_inquiry_last"),
    DASTMOZD_INFOS_PENSION("dastmozd_infos_pension"),
    DASTMOZD_INFOS_ESTEHGHAGH("dastmozd_infos_estehghagh"),
    DASTMOZD_INFOS_CALCILLNESS_PENSIONER("dastmozd_infos_calcIllness_pensioner"),
    ELIGIBLE_AMOUNT_PENSION("eligible_amount_pension"),

    FISH("fish"),
    FISH_LAST("fish_last"),
    PATIENT_HISTORY("patient_history"),
    PATIENT_HISTORY_LAST("patient_history_last"),
    BOOKLET("booklet_req"),

    HOKM("hokm"),
    HOKM_LAST("hokm_last"),

    CALCULATE_ILLNESS("calcIllness"),
    CALCILLNESS_REP("calcIllness_rep"),
    CALCILLNESS_REP_LAST("calcIllness_rep_last"),
    REPILLNESS("repIllness"),
    REPILLNESS_LAST("repIllness_last"),

    TREATMENT_COST("tcr_price_certificate"),
    INCIDENTAL_DAMAGES("Incidental_damages"),

    TRACKING_CODE("tracking_code"),
    LAST_TRACKING_CODE("last_tracking_code"),

    GET_DEPENDENT("get_dependent"),
    ADD_DEPENDENT("add_dependent"),
    DEPENDENT_CANCELLATION("dependent_cancellation"),
    DEPENDENT_CANCELLATION_GET("dependent_cancellation_get"),
    DEPENDENT_CANCELLATION_CONFIRM("dependent_cancellation_confirm"),
    DEPENDENT_CANCELLATION_SUBMIT("dependent_cancellation_submit"),
    DEPENDENT_CANCELLATION_CANCEL("dependent_cancellation_cancel"),

    EDIT_PHONE_NUMBER("edit_mobile"),
    EDIT_PHONE_NUMBER_GET("edit_mobile_get"),
    EDIT_PHONE_NUMBER_SEND_OTP("edit_mobile_send_otp"),
    EDIT_PHONE_NUMBER_VERIFY_OTP("edit_mobile_verify_otp"),
    EDIT_PHONE_NUMBER_CANCEL("edit_mobile_cancel"),
    EDIT_BANK_ACCOUNT_NUMBER("add_account_number"),
    EDIT_BANK_ACCOUNT_GET("edit_bank_account_get"),
    EDIT_BANK_ACCOUNT_SUBMIT("edit_bank_account_submit"),
    EDIT_BANK_ACCOUNT_CANCEL("edit_bank_account_cancel"),

    PROFILE_INFO("profile_info"),

    WORKER_PAYMENT("worker_payment"),

    EXTEND_EDUCATION("extend_education"),
    EXTEND_EDUCATION_GET("extend_education_get"),
    EXTEND_EDUCATION_SUBMIT("extend_education_submit"),
    EXTEND_EDUCATION_CANCEL("extend_education_cancel"),

    PREGNANCY_PAY("pregnancy_pay"),

    SHORT_TERM_ORTHOSIS("short_term_orthosis"),

    WEDDING_PRESENT("wedding_present"),
    WEDDING_PRESENT_GET("wedding_present_get"),
    WEDDING_PRESENT_VALIDATE("wedding_present_validate"),
    WEDDING_PRESENT_CALCULATE("wedding_present_calculate"),
    WEDDING_PRESENT_SUBMIT("wedding_present_submit"),
    WEDDING_PRESENT_CANCEL("wedding_present_cancel"),

    FUNERAL_ALLOWANCE_GET("funeral_allowance"),
    FUNERAL_ALLOWANCE_VALIDATE("funeral_allowance_validate"),
    FUNERAL_ALLOWANCE_SAVE("funeral_allowance_save"),
    FUNERAL_ALLOWANCE_CONFIRM("funeral_allowance_confirm"),
    FUNERAL_ALLOWANCE_CANCEL("funeral_allowance_cancel"),

    OCCURRENCE_REPORT("occurrence_report"),
    OCCURRENCE_REPORT_GET("occurrence_report_get"),
    OCCURRENCE_REPORT_WORKSHOP("occurrence_report_workshop"),
    OCCURRENCE_REPORT_PERSONAL("occurrence_report_personal"),
    OCCURRENCE_REPORT_ACCIDENT("occurrence_report_accident"),
    OCCURRENCE_REPORT_SUBMIT("occurrence_report_submit"),
    OCCURRENCE_REPORT_CANCEL("occurrence_report_cancel"),
    COMPLETE_INFO_OF_REAL_WORKSHOP("complete_info_of_real_workshop"),

    APPOINTMENT("appoinmet"),
    LAW("law"),
    DASTMOZD_INFOS_LAST_PAY("dastmozdinfos_last_pay"),
    DEFFERED_INSTALLMENT_CERTIFICATE("deferred_installment_certificate"),
    CONFIRMATION_MEDICAL_AUTHORITIES("confirmation_medical_authorities"),
    DISABILITY_PENSION("disability_pension"),
    REGISTER_CONTRACT("register_contract"),

    UNKNOWN("unknown");

    companion object {
        fun fromString(key: String?): AgentActionKey {
            if (key == null) return UNKNOWN
            return entries.find { it.key == key } ?: UNKNOWN
        }
    }
}

/**
 * Maps [AgentActionKey] to the [FeatureFlag] available in the project.
 *
 * If null is returned, it means this action does not need a FeatureFlag check
 * (e.g., general messages or laws).
 */
fun AgentActionKey.toFeatureFlag(): FeatureFlag? = when (this) {
    // Wage History
    AgentActionKey.DASTMOZD_INFOS,
    AgentActionKey.DASTMOZD_INFOS_LAST,
    AgentActionKey.DASTMOZD_INFOS_PER_YEAR,
    AgentActionKey.DASTMOZD_INFOS_SALARY,
    AgentActionKey.DASTMOZD_INFOS_SUM_TOTAL,
    AgentActionKey.AVERAGE_DASTMOZD_INFOS,
    AgentActionKey.AVERAGE_DASTMOZD_INFOS_PER_DATE,
    AgentActionKey.HISTORY_JOB_INFOS,
    AgentActionKey.HISTORY_JOB_INFOS_LAST,
    AgentActionKey.HISTORY_SERVICES,
    AgentActionKey.HISTORY_SERVICES_LAST -> FeatureFlag.WAGE_AND_HISTORY

    // Pension
    AgentActionKey.PENSION_INQUIRY_ALL,
    AgentActionKey.PENSION_INQUIRY_LAST,
    AgentActionKey.ELIGIBLE_AMOUNT_PENSION -> FeatureFlag.PENSION_INQUIRY

    // Treatment / Prescription
    AgentActionKey.FISH,
    AgentActionKey.FISH_LAST -> FeatureFlag.PAY_ROLL

    AgentActionKey.PATIENT_HISTORY,
    AgentActionKey.PATIENT_HISTORY_LAST -> FeatureFlag.PRESCRIPTION

    AgentActionKey.BOOKLET -> FeatureFlag.DESERVED_TREATMENT

    AgentActionKey.TREATMENT_COST -> FeatureFlag.DESERVED_TREATMENT_101

    // Wedding Gift
    AgentActionKey.WEDDING_PRESENT,
    AgentActionKey.WEDDING_PRESENT_GET,
    AgentActionKey.WEDDING_PRESENT_VALIDATE,
    AgentActionKey.WEDDING_PRESENT_CALCULATE,
    AgentActionKey.WEDDING_PRESENT_SUBMIT,
    AgentActionKey.WEDDING_PRESENT_CANCEL -> FeatureFlag.WEDDING_PRESENT

    // Pregnancy
    AgentActionKey.PREGNANCY_PAY -> FeatureFlag.REQUEST_FOR_PREGNANCY_PAY

    // Orthosis / Prosthesis
    AgentActionKey.SHORT_TERM_ORTHOSIS -> FeatureFlag.OROTEZ_PROTEZ

    // Funeral Allowance
    AgentActionKey.FUNERAL_ALLOWANCE_GET,
    AgentActionKey.FUNERAL_ALLOWANCE_VALIDATE,
    AgentActionKey.FUNERAL_ALLOWANCE_SAVE,
    AgentActionKey.FUNERAL_ALLOWANCE_CONFIRM,
    AgentActionKey.FUNERAL_ALLOWANCE_CANCEL -> FeatureFlag.REQUEST_FUNERAL_GRANT

    // Accident Report
    AgentActionKey.OCCURRENCE_REPORT,
    AgentActionKey.OCCURRENCE_REPORT_GET,
    AgentActionKey.OCCURRENCE_REPORT_WORKSHOP,
    AgentActionKey.OCCURRENCE_REPORT_PERSONAL,
    AgentActionKey.OCCURRENCE_REPORT_ACCIDENT,
    AgentActionKey.OCCURRENCE_REPORT_SUBMIT,
    AgentActionKey.OCCURRENCE_REPORT_CANCEL,
    AgentActionKey.COMPLETE_INFO_OF_REAL_WORKSHOP -> FeatureFlag.OCCURRENCE

    // Education Inquiry
    AgentActionKey.EXTEND_EDUCATION,
    AgentActionKey.EXTEND_EDUCATION_GET,
    AgentActionKey.EXTEND_EDUCATION_SUBMIT,
    AgentActionKey.EXTEND_EDUCATION_CANCEL -> FeatureFlag.INQUIRY_EDUCATION

    // Laws
    AgentActionKey.LAW -> FeatureFlag.LAWS

    // Disability Pension
    AgentActionKey.DISABILITY_PENSION -> FeatureFlag.DISABILITY_PENSION

    // General messages — no check required
    AgentActionKey.GENERAL_RESPONSE,
    AgentActionKey.MESSAGE,
    AgentActionKey.UNKNOWN -> null

    // Others default to no check
    else -> null
}
