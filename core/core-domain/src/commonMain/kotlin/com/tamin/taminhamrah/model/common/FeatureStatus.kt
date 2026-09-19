package com.tamin.taminhamrah.model.common

sealed class FeatureStatus {
    object Enabled : FeatureStatus()
    data class Disabled(val message: String?) : FeatureStatus()
    data class TemporaryDisabled(val message: String?) : FeatureStatus()
    data class EnabledWithError(val message: String?) : FeatureStatus()
    data class WebView(val url: String) : FeatureStatus()

    /** True when tapping the service opens something — the only cases that are worth showing. */
    val opensSomething: Boolean
        get() = this is Enabled || this is EnabledWithError || this is WebView
}

/**
 * How one menu row reads as a feature's state. A `null` receiver means the server did not return
 * the service at all, which is [FeatureStatus.Disabled] with no message to show.
 *
 * Lives here rather than inside `FeatureManagerImpl` so that a caller which already holds the menu
 * — the home screen holds it to render the service list — can ask the same question without
 * triggering a second fetch, and cannot answer it differently.
 */
fun MainServiceDN?.toFeatureStatus(): FeatureStatus {
    if (this == null) return FeatureStatus.Disabled(null)
    if (active == false) return FeatureStatus.Disabled(message)

    return when (status) {
        MenuServiceStatusDN.ACTIVE -> FeatureStatus.Enabled
        MenuServiceStatusDN.TEMPORARY_DISABLED -> FeatureStatus.TemporaryDisabled(message)
        MenuServiceStatusDN.DISABLED -> FeatureStatus.Disabled(message)
        MenuServiceStatusDN.COMPLETELY_DISABLED -> FeatureStatus.Disabled(message)
        MenuServiceStatusDN.ENABLED_WITH_ERROR -> FeatureStatus.EnabledWithError(message)
        MenuServiceStatusDN.WEB_VIEW -> url?.let { FeatureStatus.WebView(it) } ?: FeatureStatus.Enabled
        null -> FeatureStatus.Enabled
    }
}

/** The state of [flag] according to a menu that has already been loaded. */
fun List<MainServiceDN>.featureStatusOf(flag: FeatureFlag): FeatureStatus =
    find { it.id == flag.id }.toFeatureStatus()

/**
 * One entry per menu id. The id is the only thing the menu and a flag share, so every id in
 * `MockMenuData` must appear here exactly once — `fromId` takes the *first* match, so a repeated id
 * would silently route one menu row to the wrong feature.
 *
 * Ids are banded by audience and, within a band, ascend in the order the rows are shown (the menu is
 * read back from Room ordered by `sorting`, then `id`): `1`–`37` insured, `101`–`110` pensioners,
 * `1001`–`1010` employers, `2000` the assistant. A service that both an insured person and a
 * pensioner reach has one flag per audience, the pensioner's suffixed `_PENSIONER`, when each
 * audience needs its own menu row.
 */
enum class FeatureFlag(val id: Int) {
    // ─── Insured (menu ids 1–37) ─────────────────────────────────────────────
    OBJECTION_NON_EXISTENT_HISTORY(1),
    SEND_INSURANCE_HISTORY_TO_INSTITUTION(2),
    COMBINED_RECORD(3),
    OROTEZ_PROTEZ(4),
    WEDDING_PRESENT(5),
    VIEW_TITLE_JOB(6),
    REQUEST_FUNERAL_GRANT(7),
    REQUEST_PAYMENT_FOR_ILL_DAYS(8),
    REQUEST_FOR_PREGNANCY_PAY(9),
    FRACTION_CONTRACT(10),
    CALCULATE_WAGE_PENSION(11),
    LIST_OF_INSPECTIONS_PERFORMED(12),
    INQUIRY_EDUCATION(13),
    OPTIONAL_INSURANCE(14),
    CONTRACTS(15),
    RETIREMENT_PENSION(16),
    DISABILITY_PENSION(17),
    OBJECTION_INSURANCE_HISTORY(18),
    OCCURRENCE(19),
    WORKERS_PAYMENT_INFO(20),
    REQUEST_PENSION_BY_SURVIVOR(21),
    LAWS(22),
    VIEW_SHORT_TERM(23),
    CALCULATE_MARRIAGE_ALLOWANCE(24),
    CALCULATE_WAGE_ILL_DAYS(25),
    CALCULATE_WAGE_PREGNANCY(26),
    DESERVED_TREATMENT(27),
    PRESCRIPTION(28),
    FREELANCE_INSURANCE(29),
    STUDENT_INSURANCE(30),
    HOUSEWIFE_INSURANCE(31),
    MY_ELECTRONIC_FILE(32),
    IDENTITY_INFO(33),
    ACTIVE_RELATION(34),
    BANK_ACCOUNT_LIST(35),
    EDIT_IMAGE(36),
    DEPENDENTS(37),

    // ─── Pensioners (menu ids 101–110) ───────────────────────────────────────
    EDICT_PENSIONER(101),
    PAY_ROLL(102),
    PENSION_INQUIRY(103),
    CALCULATE_WAGE_PENSION_PENSIONER(104),
    DEFERRED_INSTALLMENT_CERTIFICATE(105),
    ISSUANCE_WAGE_CERTIFICATE(106),
    DISABILITY_PENSION_PENSIONER(107),
    REQUEST_PENSION_BY_SURVIVOR_PENSIONER(108),
    GIRL_SURVIVOR(109),
    DESERVED_TREATMENT_PENSIONER(110),

    // ─── Employers (menu ids 1001–1010) ──────────────────────────────────────
    COMPLETE_WORKSHOP_INFO(1001),
    ASSIGNER_CONTRACT(1002),
    WORKSHOPS(1003),
    FOLLOW_PROTEST_STATUS(1004),
    PERFORMED_INSPECTION(1005),
    REGISTER_AGREEMENT(1006),
    CONSTRUCTION_INSURANCE(1007),
    STACK_HOLDER_LIST(1008),
    CONTRACT_INFO(1009),
    INSTALLMENT_DEBT(1010),

    // ─── No menu row today ───────────────────────────────────────────────────
    // Nothing in `MockMenuData` carries these ids, so `featureStatusOf` reads them as Disabled and
    // the deep links and assistant actions pointing at them are gated off. They are kept because
    // those callers still name them. The ids are unique keys only — the 90s band is deliberately
    // clear of the insured band so it can never shadow a real menu row in `fromId`.
    MERGE_HISTORY(90),
    WAGE_AND_HISTORY(91),
    OBJECTION_INSURANCE_HISTORY_LEGACY(92),
    PRESCRIPTION_PENSIONER(93),

    // ─── AI Assistant / Chatbot ──────────────────────────────────────────────
    /** Controls the entry point for the Agent and chatbot access */
    AGENT(2000);

    companion object {
        fun fromId(id: Int?) = entries.find { it.id == id }
    }
}
