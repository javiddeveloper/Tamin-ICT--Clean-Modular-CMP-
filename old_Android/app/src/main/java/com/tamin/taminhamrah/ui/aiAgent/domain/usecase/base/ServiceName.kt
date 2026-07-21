package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base

enum class ServiceNameEnum(val key: String) {
    GENERAL_RESPONSE("general_response"),
    AVERAGE_DASTMOZD_INFOS("average_dastmozd_infos"),
    AVERAGE_DASTMOZD_INFOS_PER_DATE("average_dastmozd_infos_per_date"),
    DASTMOZD_INFOS_PER_YEAR("dastmozd_infos_per_year"),
    DASTMOZD_INFOS_SALARY("dastmozd_infos_salary"),
    DASTMOZD_INFOS_SUM_TOTAL("dastmozd_infos_sum_total"),
    DASTMOZD_INFOS_LAST("dastmozd_infos_last"),
    DASTMOZD_INFOS("dastmozd_infos"),
    HISTORY_JOB_INFOS("history_job_infos"),
    HISTORY_JOB_INFOS_LAST("history_job_infos_last"),
    CALCULATE_ILLNESS("calcIllness"),
    CALCILLNESS_REP("calcIllness_rep"),
    CALCILLNESS_REP_LAST("calcIllness_rep_last"),
    DASTMOZD_INFOS_PENSION("dastmozd_infos_pension"),
    DASTMOZD_INFOS_ESTEHGHAGH("dastmozd_infos_estehghagh"),
    PENSION_INQUIRY_LAST("pension_inquiry_last"),
    PENSION_INQUIRY_ALL("pension_inquiry_all"),
    HOKM_LAST("hokm_last"),
    HOKM("hokm"),
    MESSAGE("message"),
    GET_DEPENDENT("get_dependent"),
    INCIDENTAL_DAMAGES("Incidental_damages"),
    FISH("fish"),
    FISH_LAST("fish_last"),
    PATIENT_HISTORY("patient_history"),
    PATIENT_HISTORY_LAST("patient_history_last"),
    BOOKLET("booklet_req"),
    HISTORY_SERVICES("history_services"),
    HISTORY_SERVICES_LAST("history_services_last"),
    TRACKING_CODE("tracking_code"),
    LAST_TRACKING_CODE("last_tracking_code"),
    APPOINMET("appoinmet"),
    DASTMOZD_INFOS_LAST_PAY("dastmozdinfos_last_pay"),
    LAW("law"),
    REPILLNESS_LAST("repIllness_last"),
    REPILLNESS("repIllness"),
    ELIGIBLE_AMOUNT_PENSION("eligible_amount_pension"),
    DASTMOZD_INFOS_CALCILLNESS_PENSIONER("dastmozd_infos_calcIllness_pensioner"),
    UN_AVAILABLE_SERVICE("un_available_service"),
    ADD_DEPENDENT("add_dependent"),
    PROFILE_INFO("profile_info"),
    TREATMENT_COST("tcr_price_certificate"),
    EDIT_BANK_ACCOUNT_NUMBER("add_account_number"),
    EDIT_BANK_ACCOUNT_GET("edit_bank_account_get"),
    EDIT_BANK_ACCOUNT_SUBMIT("edit_bank_account_submit"),
    EDIT_BANK_ACCOUNT_CANCEL("edit_bank_account_cancel"),
    EDIT_PHONE_NUMBER("edit_mobile"),
    EDIT_PHONE_NUMBER_GET("edit_mobile_get"),
    EDIT_PHONE_NUMBER_SEND_OTP("edit_mobile_send_otp"),
    EDIT_PHONE_NUMBER_VERIFY_OTP("edit_mobile_verify_otp"),
    WORKER_PAYMENT("worker_payment"),
    DEPENDENT_CANCELLATION("dependent_cancellation"),
    DEPENDENT_CANCELLATION_GET("dependent_cancellation_get"),
    DEPENDENT_CANCELLATION_CONFIRM("dependent_cancellation_confirm"),
    DEPENDENT_CANCELLATION_SUBMIT("dependent_cancellation_submit"),
    DEPENDENT_CANCELLATION_CANCEL("dependent_cancellation_cancel"),
    EDIT_PHONE_NUMBER_CANCEL("edit_mobile_cancel"),
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
    DEFFERED_INSTALLMENT_CERTIFICATE("deferred_installment_certificate"),
    CONFIRMATION_MEDICAL_AUTHORITIES("confirmation_medical_authorities"),
    DISABILITY_PENSION("disability_pension"),
    REGISTER_CONTRACT("register_contract"),
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
    COMPLETE_INFO_OF_REAL_WORKSHOP("complete_info_of_real_workshop"),
    OCCURRENCE_REPORT_CANCEL("occurrence_report_cancel");

    companion object {
        fun fromString(key: String?): ServiceNameEnum? {
            if (key == null) return null
            return when (key) {
                GENERAL_RESPONSE.key -> GENERAL_RESPONSE
                DASTMOZD_INFOS_PER_YEAR.key -> DASTMOZD_INFOS_PER_YEAR
                DASTMOZD_INFOS_SALARY.key -> DASTMOZD_INFOS_SALARY
                DASTMOZD_INFOS_SUM_TOTAL.key -> DASTMOZD_INFOS_SUM_TOTAL
                DASTMOZD_INFOS_LAST.key -> DASTMOZD_INFOS_LAST
                DASTMOZD_INFOS.key -> DASTMOZD_INFOS
                HISTORY_JOB_INFOS.key -> HISTORY_JOB_INFOS
                HISTORY_JOB_INFOS_LAST.key -> HISTORY_JOB_INFOS_LAST
                CALCULATE_ILLNESS.key -> CALCULATE_ILLNESS
                CALCILLNESS_REP.key -> CALCILLNESS_REP
                CALCILLNESS_REP_LAST.key -> CALCILLNESS_REP_LAST
                DASTMOZD_INFOS_PENSION.key -> DASTMOZD_INFOS_PENSION
                DASTMOZD_INFOS_ESTEHGHAGH.key -> DASTMOZD_INFOS_ESTEHGHAGH
                PENSION_INQUIRY_LAST.key -> PENSION_INQUIRY_LAST
                PENSION_INQUIRY_ALL.key -> PENSION_INQUIRY_ALL
                AVERAGE_DASTMOZD_INFOS.key -> AVERAGE_DASTMOZD_INFOS
                AVERAGE_DASTMOZD_INFOS_PER_DATE.key -> AVERAGE_DASTMOZD_INFOS_PER_DATE
                HOKM_LAST.key -> HOKM_LAST
                HOKM.key -> HOKM
                GET_DEPENDENT.key -> GET_DEPENDENT
                INCIDENTAL_DAMAGES.key -> INCIDENTAL_DAMAGES
                FISH.key -> FISH
                FISH_LAST.key -> FISH_LAST
                PATIENT_HISTORY.key -> PATIENT_HISTORY
                BOOKLET.key -> BOOKLET
                HISTORY_SERVICES.key -> HISTORY_SERVICES
                TRACKING_CODE.key -> TRACKING_CODE
                HISTORY_SERVICES_LAST.key -> HISTORY_SERVICES_LAST
                LAST_TRACKING_CODE.key -> LAST_TRACKING_CODE
                APPOINMET.key -> APPOINMET
                DASTMOZD_INFOS_LAST_PAY.key -> DASTMOZD_INFOS_LAST_PAY
                LAW.key -> LAW
                REPILLNESS_LAST.key -> REPILLNESS_LAST
                REPILLNESS.key -> REPILLNESS
                TREATMENT_COST.key -> TREATMENT_COST
                EDIT_PHONE_NUMBER.key -> EDIT_PHONE_NUMBER
                EDIT_BANK_ACCOUNT_GET.key -> EDIT_BANK_ACCOUNT_GET
                EDIT_BANK_ACCOUNT_SUBMIT.key -> EDIT_BANK_ACCOUNT_SUBMIT
                EDIT_BANK_ACCOUNT_CANCEL.key -> EDIT_BANK_ACCOUNT_CANCEL
                EDIT_PHONE_NUMBER_GET.key -> EDIT_PHONE_NUMBER_GET
                EDIT_PHONE_NUMBER_SEND_OTP.key -> EDIT_PHONE_NUMBER_SEND_OTP
                EDIT_PHONE_NUMBER_VERIFY_OTP.key -> EDIT_PHONE_NUMBER_VERIFY_OTP
                EDIT_PHONE_NUMBER_CANCEL.key -> EDIT_PHONE_NUMBER_CANCEL
                ELIGIBLE_AMOUNT_PENSION.key -> ELIGIBLE_AMOUNT_PENSION
                PATIENT_HISTORY_LAST.key -> PATIENT_HISTORY_LAST
                EDIT_BANK_ACCOUNT_NUMBER.key -> EDIT_BANK_ACCOUNT_NUMBER
                PROFILE_INFO.key -> PROFILE_INFO
                MESSAGE.key -> MESSAGE
                WORKER_PAYMENT.key -> WORKER_PAYMENT
                DASTMOZD_INFOS_CALCILLNESS_PENSIONER.key -> DASTMOZD_INFOS_CALCILLNESS_PENSIONER
                ADD_DEPENDENT.key -> ADD_DEPENDENT
                DEPENDENT_CANCELLATION.key -> DEPENDENT_CANCELLATION
                DEPENDENT_CANCELLATION_GET.key -> DEPENDENT_CANCELLATION_GET
                DEPENDENT_CANCELLATION_CONFIRM.key -> DEPENDENT_CANCELLATION_CONFIRM
                DEPENDENT_CANCELLATION_SUBMIT.key -> DEPENDENT_CANCELLATION_SUBMIT
                DEPENDENT_CANCELLATION_CANCEL.key -> DEPENDENT_CANCELLATION_CANCEL
                PREGNANCY_PAY.key -> PREGNANCY_PAY
                EXTEND_EDUCATION.key -> EXTEND_EDUCATION
                EXTEND_EDUCATION_GET.key -> EXTEND_EDUCATION_GET
                EXTEND_EDUCATION_SUBMIT.key -> EXTEND_EDUCATION_SUBMIT
                EXTEND_EDUCATION_CANCEL.key -> EXTEND_EDUCATION_CANCEL
                SHORT_TERM_ORTHOSIS.key -> SHORT_TERM_ORTHOSIS
                WEDDING_PRESENT.key -> WEDDING_PRESENT
                WEDDING_PRESENT_GET.key -> WEDDING_PRESENT_GET
                WEDDING_PRESENT_VALIDATE.key -> WEDDING_PRESENT_VALIDATE
                WEDDING_PRESENT_CALCULATE.key -> WEDDING_PRESENT_CALCULATE
                WEDDING_PRESENT_SUBMIT.key -> WEDDING_PRESENT_SUBMIT
                WEDDING_PRESENT_CANCEL.key -> WEDDING_PRESENT_CANCEL
                DEFFERED_INSTALLMENT_CERTIFICATE.key -> DEFFERED_INSTALLMENT_CERTIFICATE
                CONFIRMATION_MEDICAL_AUTHORITIES.key -> CONFIRMATION_MEDICAL_AUTHORITIES
                DISABILITY_PENSION.key -> DISABILITY_PENSION
                REGISTER_CONTRACT.key -> REGISTER_CONTRACT
                FUNERAL_ALLOWANCE_GET.key -> FUNERAL_ALLOWANCE_GET
                FUNERAL_ALLOWANCE_VALIDATE.key -> FUNERAL_ALLOWANCE_VALIDATE
                FUNERAL_ALLOWANCE_SAVE.key -> FUNERAL_ALLOWANCE_SAVE
                FUNERAL_ALLOWANCE_CONFIRM.key -> FUNERAL_ALLOWANCE_CONFIRM
                FUNERAL_ALLOWANCE_CANCEL.key -> FUNERAL_ALLOWANCE_CANCEL
                OCCURRENCE_REPORT.key -> OCCURRENCE_REPORT
                OCCURRENCE_REPORT_GET.key -> OCCURRENCE_REPORT_GET
                OCCURRENCE_REPORT_WORKSHOP.key -> OCCURRENCE_REPORT_WORKSHOP
                OCCURRENCE_REPORT_PERSONAL.key -> OCCURRENCE_REPORT_PERSONAL
                OCCURRENCE_REPORT_ACCIDENT.key -> OCCURRENCE_REPORT_ACCIDENT
                OCCURRENCE_REPORT_SUBMIT.key -> OCCURRENCE_REPORT_SUBMIT
                OCCURRENCE_REPORT_CANCEL.key -> OCCURRENCE_REPORT_CANCEL
                COMPLETE_INFO_OF_REAL_WORKSHOP.key -> COMPLETE_INFO_OF_REAL_WORKSHOP
                else -> UN_AVAILABLE_SERVICE
            }
        }
    }
}