package com.tamin.taminhamrah.model.agent

/**
 * Extension to map Agent Actions to user-friendly Persian strings for the Processing Extension Card.
 * Returns null if the action should be silent (not show up as a separate processing step).
 */
fun AgentActionKey.toProcessingStepTitle(): String? = when (this) {
    // History & Wage
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
    AgentActionKey.HISTORY_SERVICES_LAST -> "در حال دریافت اطلاعات سوابق از تامین اجتماعی"

    // Pension
    AgentActionKey.PENSION_INQUIRY_ALL,
    AgentActionKey.PENSION_INQUIRY_LAST,
    AgentActionKey.ELIGIBLE_AMOUNT_PENSION -> "در حال بررسی وضعیت مستمری"

    // Pay Roll
    AgentActionKey.FISH,
    AgentActionKey.FISH_LAST -> "در حال دریافت فیش حقوقی"

    // Prescription & Medical
    AgentActionKey.PATIENT_HISTORY,
    AgentActionKey.PATIENT_HISTORY_LAST,
    AgentActionKey.BOOKLET -> "در حال بررسی نسخه‌ها و اطلاعات درمانی"

    // General or structural actions shouldn't add a visible loading step
    AgentActionKey.GENERAL_RESPONSE,
    AgentActionKey.MESSAGE,
    AgentActionKey.UNKNOWN -> null

    // Default fallback for other backend calls
    else -> "در حال دریافت اطلاعات سیستم"
}
