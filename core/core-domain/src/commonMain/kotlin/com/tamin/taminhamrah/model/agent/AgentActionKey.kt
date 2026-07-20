package com.tamin.taminhamrah.model.agent

import com.tamin.taminhamrah.model.common.FeatureFlag

/**
 * شناسه‌های سرویس‌هایی که AI می‌تواند برگرداند
 *
 * هر [key] دقیقاً برابر با مقدار JSON "key" در entity خروجی سرور است.
 * متد [toFeatureFlag] این key را به [FeatureFlag] موجود در پروژه نگاشت می‌کند
 * تا از [com.tamin.taminhamrah.feature.FeatureManager] بتوان بررسی کرد
 * آیا سرویس برای این کاربر فعال است یا نه.
 */
enum class AgentActionKey(val key: String) {

    // ─── پاسخ عمومی / پیام ───────────────────────────────────────────────
    GENERAL_RESPONSE("general_response"),
    MESSAGE("message"),

    // ─── سوابق دستمزد ────────────────────────────────────────────────────
    DASTMOZD_INFOS("dastmozd_infos"),
    DASTMOZD_INFOS_LAST("dastmozd_infos_last"),
    DASTMOZD_INFOS_PER_YEAR("dastmozd_infos_per_year"),
    DASTMOZD_INFOS_SALARY("dastmozd_infos_salary"),
    DASTMOZD_INFOS_SUM_TOTAL("dastmozd_infos_sum_total"),
    AVERAGE_DASTMOZD_INFOS("average_dastmozd_infos"),
    AVERAGE_DASTMOZD_INFOS_PER_DATE("average_dastmozd_infos_per_date"),

    // ─── سوابق شغلی ──────────────────────────────────────────────────────
    HISTORY_JOB_INFOS("history_job_infos"),
    HISTORY_JOB_INFOS_LAST("history_job_infos_last"),
    HISTORY_SERVICES("history_services"),
    HISTORY_SERVICES_LAST("history_services_last"),

    // ─── مستمری ──────────────────────────────────────────────────────────
    PENSION_INQUIRY_ALL("pension_inquiry_all"),
    PENSION_INQUIRY_LAST("pension_inquiry_last"),
    DASTMOZD_INFOS_PENSION("dastmozd_infos_pension"),
    DASTMOZD_INFOS_ESTEHGHAGH("dastmozd_infos_estehghagh"),
    DASTMOZD_INFOS_CALCILLNESS_PENSIONER("dastmozd_infos_calcIllness_pensioner"),
    ELIGIBLE_AMOUNT_PENSION("eligible_amount_pension"),

    // ─── بیمه درمان ──────────────────────────────────────────────────────
    FISH("fish"),
    FISH_LAST("fish_last"),
    PATIENT_HISTORY("patient_history"),
    PATIENT_HISTORY_LAST("patient_history_last"),
    BOOKLET("booklet_req"),

    // ─── حکم ─────────────────────────────────────────────────────────────
    HOKM("hokm"),
    HOKM_LAST("hokm_last"),

    // ─── محاسبه بیماری ───────────────────────────────────────────────────
    CALCULATE_ILLNESS("calcIllness"),
    CALCILLNESS_REP("calcIllness_rep"),
    CALCILLNESS_REP_LAST("calcIllness_rep_last"),
    REPILLNESS("repIllness"),
    REPILLNESS_LAST("repIllness_last"),

    // ─── خدمات درمانی ────────────────────────────────────────────────────
    TREATMENT_COST("tcr_price_certificate"),
    INCIDENTAL_DAMAGES("Incidental_damages"),

    // ─── پیگیری و ردیابی ─────────────────────────────────────────────────
    TRACKING_CODE("tracking_code"),
    LAST_TRACKING_CODE("last_tracking_code"),

    // ─── عائله ───────────────────────────────────────────────────────────
    GET_DEPENDENT("get_dependent"),
    ADD_DEPENDENT("add_dependent"),
    DEPENDENT_CANCELLATION("dependent_cancellation"),
    DEPENDENT_CANCELLATION_GET("dependent_cancellation_get"),
    DEPENDENT_CANCELLATION_CONFIRM("dependent_cancellation_confirm"),
    DEPENDENT_CANCELLATION_SUBMIT("dependent_cancellation_submit"),
    DEPENDENT_CANCELLATION_CANCEL("dependent_cancellation_cancel"),

    // ─── ویرایش اطلاعات ──────────────────────────────────────────────────
    EDIT_PHONE_NUMBER("edit_mobile"),
    EDIT_PHONE_NUMBER_GET("edit_mobile_get"),
    EDIT_PHONE_NUMBER_SEND_OTP("edit_mobile_send_otp"),
    EDIT_PHONE_NUMBER_VERIFY_OTP("edit_mobile_verify_otp"),
    EDIT_PHONE_NUMBER_CANCEL("edit_mobile_cancel"),
    EDIT_BANK_ACCOUNT_NUMBER("add_account_number"),
    EDIT_BANK_ACCOUNT_GET("edit_bank_account_get"),
    EDIT_BANK_ACCOUNT_SUBMIT("edit_bank_account_submit"),
    EDIT_BANK_ACCOUNT_CANCEL("edit_bank_account_cancel"),

    // ─── پروفایل ─────────────────────────────────────────────────────────
    PROFILE_INFO("profile_info"),

    // ─── پرداخت کارگری ───────────────────────────────────────────────────
    WORKER_PAYMENT("worker_payment"),

    // ─── ادامه تحصیل ─────────────────────────────────────────────────────
    EXTEND_EDUCATION("extend_education"),
    EXTEND_EDUCATION_GET("extend_education_get"),
    EXTEND_EDUCATION_SUBMIT("extend_education_submit"),
    EXTEND_EDUCATION_CANCEL("extend_education_cancel"),

    // ─── بارداری ─────────────────────────────────────────────────────────
    PREGNANCY_PAY("pregnancy_pay"),

    // ─── ارتز/پروتز ──────────────────────────────────────────────────────
    SHORT_TERM_ORTHOSIS("short_term_orthosis"),

    // ─── هدیه ازدواج ─────────────────────────────────────────────────────
    WEDDING_PRESENT("wedding_present"),
    WEDDING_PRESENT_GET("wedding_present_get"),
    WEDDING_PRESENT_VALIDATE("wedding_present_validate"),
    WEDDING_PRESENT_CALCULATE("wedding_present_calculate"),
    WEDDING_PRESENT_SUBMIT("wedding_present_submit"),
    WEDDING_PRESENT_CANCEL("wedding_present_cancel"),

    // ─── کمک هزینه کفن و دفن ─────────────────────────────────────────────
    FUNERAL_ALLOWANCE_GET("funeral_allowance"),
    FUNERAL_ALLOWANCE_VALIDATE("funeral_allowance_validate"),
    FUNERAL_ALLOWANCE_SAVE("funeral_allowance_save"),
    FUNERAL_ALLOWANCE_CONFIRM("funeral_allowance_confirm"),
    FUNERAL_ALLOWANCE_CANCEL("funeral_allowance_cancel"),

    // ─── گزارش حادثه ─────────────────────────────────────────────────────
    OCCURRENCE_REPORT("occurrence_report"),
    OCCURRENCE_REPORT_GET("occurrence_report_get"),
    OCCURRENCE_REPORT_WORKSHOP("occurrence_report_workshop"),
    OCCURRENCE_REPORT_PERSONAL("occurrence_report_personal"),
    OCCURRENCE_REPORT_ACCIDENT("occurrence_report_accident"),
    OCCURRENCE_REPORT_SUBMIT("occurrence_report_submit"),
    OCCURRENCE_REPORT_CANCEL("occurrence_report_cancel"),
    COMPLETE_INFO_OF_REAL_WORKSHOP("complete_info_of_real_workshop"),

    // ─── سایر ────────────────────────────────────────────────────────────
    APPOINTMENT("appoinmet"),
    LAW("law"),
    DASTMOZD_INFOS_LAST_PAY("dastmozdinfos_last_pay"),
    DEFFERED_INSTALLMENT_CERTIFICATE("deferred_installment_certificate"),
    CONFIRMATION_MEDICAL_AUTHORITIES("confirmation_medical_authorities"),
    DISABILITY_PENSION("disability_pension"),
    REGISTER_CONTRACT("register_contract"),

    /** سرویسی که شناسه آن در enum تعریف نشده */
    UNKNOWN("unknown");

    companion object {
        fun fromString(key: String?): AgentActionKey {
            if (key == null) return UNKNOWN
            return entries.find { it.key == key } ?: UNKNOWN
        }
    }
}

/**
 * نگاشت [AgentActionKey] به [FeatureFlag] موجود در پروژه.
 *
 * اگر null برگردد یعنی این action نیاز به بررسی FeatureFlag ندارد
 * (مثلاً پیام‌های عمومی یا قوانین).
 */
fun AgentActionKey.toFeatureFlag(): FeatureFlag? = when (this) {
    // سوابق دستمزد
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

    // مستمری
    AgentActionKey.PENSION_INQUIRY_ALL,
    AgentActionKey.PENSION_INQUIRY_LAST,
    AgentActionKey.ELIGIBLE_AMOUNT_PENSION -> FeatureFlag.PENSION_INQUIRY

    // درمان / نسخه
    AgentActionKey.FISH,
    AgentActionKey.FISH_LAST -> FeatureFlag.PAY_ROLL

    AgentActionKey.PATIENT_HISTORY,
    AgentActionKey.PATIENT_HISTORY_LAST -> FeatureFlag.PRESCRIPTION

    AgentActionKey.BOOKLET -> FeatureFlag.DESERVED_TREATMENT

    AgentActionKey.TREATMENT_COST -> FeatureFlag.DESERVED_TREATMENT_101

    // هدیه ازدواج
    AgentActionKey.WEDDING_PRESENT,
    AgentActionKey.WEDDING_PRESENT_GET,
    AgentActionKey.WEDDING_PRESENT_VALIDATE,
    AgentActionKey.WEDDING_PRESENT_CALCULATE,
    AgentActionKey.WEDDING_PRESENT_SUBMIT,
    AgentActionKey.WEDDING_PRESENT_CANCEL -> FeatureFlag.WEDDING_PRESENT

    // بارداری
    AgentActionKey.PREGNANCY_PAY -> FeatureFlag.REQUEST_FOR_PREGNANCY_PAY

    // ارتز/پروتز
    AgentActionKey.SHORT_TERM_ORTHOSIS -> FeatureFlag.OROTEZ_PROTEZ

    // کفن و دفن
    AgentActionKey.FUNERAL_ALLOWANCE_GET,
    AgentActionKey.FUNERAL_ALLOWANCE_VALIDATE,
    AgentActionKey.FUNERAL_ALLOWANCE_SAVE,
    AgentActionKey.FUNERAL_ALLOWANCE_CONFIRM,
    AgentActionKey.FUNERAL_ALLOWANCE_CANCEL -> FeatureFlag.REQUEST_FUNERAL_GRANT

    // گزارش حادثه
    AgentActionKey.OCCURRENCE_REPORT,
    AgentActionKey.OCCURRENCE_REPORT_GET,
    AgentActionKey.OCCURRENCE_REPORT_WORKSHOP,
    AgentActionKey.OCCURRENCE_REPORT_PERSONAL,
    AgentActionKey.OCCURRENCE_REPORT_ACCIDENT,
    AgentActionKey.OCCURRENCE_REPORT_SUBMIT,
    AgentActionKey.OCCURRENCE_REPORT_CANCEL,
    AgentActionKey.COMPLETE_INFO_OF_REAL_WORKSHOP -> FeatureFlag.OCCURRENCE

    // ادامه تحصیل
    AgentActionKey.EXTEND_EDUCATION,
    AgentActionKey.EXTEND_EDUCATION_GET,
    AgentActionKey.EXTEND_EDUCATION_SUBMIT,
    AgentActionKey.EXTEND_EDUCATION_CANCEL -> FeatureFlag.INQUIRY_EDUCATION

    // قوانین
    AgentActionKey.LAW -> FeatureFlag.LAWS

    // مستمری معلولیت
    AgentActionKey.DISABILITY_PENSION -> FeatureFlag.DISABILITY_PENSION

    // پیام‌های عمومی — نیاز به چک ندارند
    AgentActionKey.GENERAL_RESPONSE,
    AgentActionKey.MESSAGE,
    AgentActionKey.UNKNOWN -> null

    // بقیه به صورت پیش‌فرض بدون چک
    else -> null
}
