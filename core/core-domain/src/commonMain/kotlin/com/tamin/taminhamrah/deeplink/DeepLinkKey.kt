package com.tamin.taminhamrah.deeplink

import com.tamin.taminhamrah.model.common.FeatureFlag

/**
 * Every destination a deep link may name, and the [FeatureFlag] that gates it.
 *
 * This is the only allowlist: a key that is not here never becomes navigation, whoever sent it
 * (the assistant, a story, a notification, the OS). Keys are the ones the backend already uses in
 * the assistant's markdown links (`[label](@key)`), so the same link works in every channel.
 *
 * Several keys may share a flag. That is deliberate for entries such as [INSURANCE_PAYMENT] and
 * [CANCEL_CONTRACT]: their own screens need a contract the user picks first, so a link can only
 * safely land on the contract list, which is where that choice is made.
 */
enum class DeepLinkKey(val key: String, val flag: FeatureFlag) {
    IDENTITY_INFO_INQUIRY("identity_info_inquiry", FeatureFlag.IDENTITY_INFO),
    ACTIVE_RELATION_INQUIRY("active_relation_inquiry", FeatureFlag.ACTIVE_RELATION),
    BANK_ACCOUNT_LIST("bank_account_list", FeatureFlag.BANK_ACCOUNT_LIST),
    DEPENDENTS_LIST("dependents_list", FeatureFlag.DEPENDENTS),
    MERGE_HISTORY("merge_history", FeatureFlag.MERGE_HISTORY),
    WAGE_AND_HISTORY("wage_and_history", FeatureFlag.WAGE_AND_HISTORY),
    ALL_HISTORY_INSURANCE("all_history_insurance", FeatureFlag.WAGE_AND_HISTORY),
    COMBINED_RECORD("combined_record", FeatureFlag.COMBINED_RECORD),
    SEND_INSURANCE_HISTORY_TO_INSTITUTION("send_insurance_history_to_institution", FeatureFlag.SEND_INSURANCE_HISTORY_TO_INSTITUTION),
    OBJECTION_NON_EXISTENT_HISTORY("objection_non_existent_history", FeatureFlag.OBJECTION_NON_EXISTENT_HISTORY),
    OBJECTION_INSURANCE_HISTORY("objection_insurance_history", FeatureFlag.OBJECTION_INSURANCE_HISTORY),
    VIEW_TITLE_JOB("view_title_job", FeatureFlag.VIEW_TITLE_JOB),
    VIEW_SHORT_TERM_SUPPORT("view_short_term_support", FeatureFlag.VIEW_SHORT_TERM),
    WEDDING_PRESENT("wedding_present", FeatureFlag.WEDDING_PRESENT),
    OROTEZ_PROTEZ("orotez_protez", FeatureFlag.OROTEZ_PROTEZ),
    PREGNANCY_PAY("pregnancy_pay", FeatureFlag.REQUEST_FOR_PREGNANCY_PAY),
    REQUEST_PAYMENT_ILL_DAYS("request_payment_ill_days", FeatureFlag.REQUEST_PAYMENT_FOR_ILL_DAYS),
    REQUEST_FUNERAL_GRANT("request_funeral_grant", FeatureFlag.REQUEST_FUNERAL_GRANT),
    LIST_OF_INSPECTIONS_PERFORMED("list_of_inspections_performed", FeatureFlag.LIST_OF_INSPECTIONS_PERFORMED),
    INSPECTION_REPORT("inspection_report", FeatureFlag.LIST_OF_INSPECTIONS_PERFORMED),
    CALCULATE_MARRIAGE_ALLOWANCE("calculate_marriage_allowance", FeatureFlag.CALCULATE_MARRIAGE_ALLOWANCE),
    CALCULATE_WAGE_ILL_DAYS("calculate_wage_ill_days", FeatureFlag.CALCULATE_WAGE_ILL_DAYS),
    CALCULATE_WAGE_PREGNANCY("calculate_wage_pregnancy", FeatureFlag.CALCULATE_WAGE_PREGNANCY),
    CALCULATE_WAGE_PENSION("calculate_wage_pension", FeatureFlag.CALCULATE_WAGE_PENSION),
    DESERVED_TREATMENT("deserved_treatment", FeatureFlag.DESERVED_TREATMENT_101),
    ELECTRONIC_PRESCRIPTION_LIST("electronic_prescription_list", FeatureFlag.PRESCRIPTION),
    PRESCRIPTION_DETAIL("prescription_detail", FeatureFlag.PRESCRIPTION),
    CONTRACT_FREELANCE("contract_freelance", FeatureFlag.FREELANCE_INSURANCE),
    CONTRACT_STUDENT("contract_student", FeatureFlag.STUDENT_INSURANCE),
    CONTRACT_WOMAN("contract_woman", FeatureFlag.HOUSEWIFE_INSURANCE),
    CONTRACT_OPTIONAL("contract_optional", FeatureFlag.OPTIONAL_INSURANCE),
    CONTRACT_LIST("contract_list", FeatureFlag.CONTRACTS),
    INSURANCE_PAYMENT("insurance_payment", FeatureFlag.CONTRACTS),
    CANCEL_CONTRACT("cancel_contract", FeatureFlag.CONTRACTS),
    NEW_INSURANCE_CONTRACT("new_insurance_contract", FeatureFlag.CONTRACTS),
    INQUIRY_EDUCATION("inquiry_education", FeatureFlag.INQUIRY_EDUCATION),
    CONTRACT_FRACTION("contract_fraction", FeatureFlag.FRACTION_CONTRACT),
    PENSION_SURVIVOR("pension_survivor", FeatureFlag.REQUEST_PENSION_BY_SURVIVOR),
    RETIREMENT_PENSION("retirement_pension", FeatureFlag.RETIREMENT_PENSION),
    MY_ELECTRONIC_FILE("my_electronic_file", FeatureFlag.MY_ELECTRONIC_FILE),
    WORKERS_PAYMENT_INFO("workers_payment_info", FeatureFlag.WORKERS_PAYMENT_INFO),
    INQUIRE_PENSION_STATUS("inquire_pension_status", FeatureFlag.PENSION_INQUIRY),
    PENSIONER_PAY_ROLL("pensioner_pay_roll", FeatureFlag.PAY_ROLL),
    EDICT_PENSIONER("edict_pensioner", FeatureFlag.EDICT_PENSIONER),
    ISSUANCE_WAGE_CERTIFICATE("issuance_wage_certificate", FeatureFlag.ISSUANCE_WAGE_CERTIFICATE),
    DEFERRED_INSTALLMENT_CERTIFICATE("deferred_installment_certificate", FeatureFlag.DEFERRED_INSTALLMENT_CERTIFICATE),
    GIRL_SURVIVOR("girl_survivor", FeatureFlag.GIRL_SURVIVOR),
    DISABILITY_PENSION("disability_pension", FeatureFlag.DISABILITY_PENSION),
    WORKSHOP_INFO("workshop_info", FeatureFlag.WORKSHOPS),
    CONTRACT_INFO("contract_info", FeatureFlag.CONTRACT_INFO),
    ASSIGNER_CONTRACT("assigner_contract", FeatureFlag.ASSIGNER_CONTRACT),
    COMPLETE_WORKSHOP_INFO("complete_workshop_info", FeatureFlag.COMPLETE_WORKSHOP_INFO),
    LEGAL_STACK_HOLDER_LIST("legal_stack_holder_list", FeatureFlag.STACK_HOLDER_LIST),
    FOLLOW_PROTEST_STATUS("follow_protest_status", FeatureFlag.FOLLOW_PROTEST_STATUS),
    REGISTER_AGREEMENT("register_agreement", FeatureFlag.REGISTER_AGREEMENT),
    PERFORMED_INSPECTION("performed_inspection", FeatureFlag.PERFORMED_INSPECTION),
    INSTALLMENT_DEBT("installment_debt", FeatureFlag.INSTALLMENT_DEBT),
    CONSTRUCTION_INSURANCE_PREMIUM("construction_insurance_premium", FeatureFlag.CONSTRUCTION_INSURANCE),
    OCCURRENCE_REPORT("occurrence_report", FeatureFlag.OCCURRENCE),
    LAWS("laws", FeatureFlag.LAWS),
    AGENT("agent", FeatureFlag.AGENT);

    companion object {
        /**
         * Looks a key up by its wire name, or by the [FeatureFlag] name older links carry
         * (`tamin://feature/AGENT`). Case-insensitive; unknown keys return null.
         */
        fun fromKey(raw: String?): DeepLinkKey? {
            val value = raw?.trim()?.takeIf { it.isNotEmpty() } ?: return null
            entries.firstOrNull { it.key.equals(value, ignoreCase = true) }?.let { return it }
            val flag = FeatureFlag.entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
                ?: return null
            return entries.firstOrNull { it.flag == flag }
        }

        /** The canonical key that opens [flag], if any link may open it. */
        fun forFlag(flag: FeatureFlag): DeepLinkKey? = entries.firstOrNull { it.flag == flag }
    }
}
