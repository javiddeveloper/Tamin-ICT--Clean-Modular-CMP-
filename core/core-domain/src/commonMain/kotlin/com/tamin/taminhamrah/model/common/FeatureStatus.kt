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

    /** The server's note for this state — the reason a service is off, or the warning on one that is on. */
    val serverMessage: String?
        get() = when (this) {
            is Disabled -> message
            is TemporaryDisabled -> message
            is EnabledWithError -> message
            Enabled, is WebView -> null
        }

    /** What a tap on a service in this state must do — the one decision every screen shares. */
    fun toGate(): FeatureGate = when (this) {
        Enabled -> FeatureGate.Open
        is EnabledWithError -> FeatureGate.OpenWithWarning(message)
        is Disabled -> FeatureGate.Blocked(message)
        is TemporaryDisabled -> FeatureGate.Blocked(message)
        is WebView -> FeatureGate.OpenWeb(url)
    }
}

/** The outcome of asking a [FeatureStatus] whether a tap may go through. */
sealed interface FeatureGate {
    data object Open : FeatureGate

    /** Opens, but the server has a warning to show first. */
    data class OpenWithWarning(val message: String?) : FeatureGate

    /** Does not open; [message] is the server's reason, when it gave one. */
    data class Blocked(val message: String?) : FeatureGate

    data class OpenWeb(val url: String) : FeatureGate
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

enum class FeatureFlag(val id: Int) {
    IDENTITY_INFO(1),
    ACTIVE_RELATION(2),
    BANK_ACCOUNT_LIST(3),
    EDIT_IMAGE(4),
    DEPENDENTS(5),
    MERGE_HISTORY(6),
    WAGE_AND_HISTORY(7),
    COMBINED_RECORD(8),
    SEND_INSURANCE_HISTORY_TO_INSTITUTION(9),
    OBJECTION_NON_EXISTENT_HISTORY(10),
    VIEW_TITLE_JOB(11),
    VIEW_SHORT_TERM(13),
    WEDDING_PRESENT(14),
    OROTEZ_PROTEZ(15),
    REQUEST_FOR_PREGNANCY_PAY(16),
    REQUEST_PAYMENT_FOR_ILL_DAYS(17),
    REQUEST_FUNERAL_GRANT(18),
    LIST_OF_INSPECTIONS_PERFORMED(19),
    CALCULATE_MARRIAGE_ALLOWANCE(20),
    CALCULATE_WAGE_ILL_DAYS(21),
    CALCULATE_WAGE_PREGNANCY(22),
    CALCULATE_WAGE_PENSION(23),
    DESERVED_TREATMENT(25),
    PRESCRIPTION(26),
    FREELANCE_INSURANCE(33),
    STUDENT_INSURANCE(34),
    CONTRACTS(35),
    HOUSEWIFE_INSURANCE(36),
    OPTIONAL_INSURANCE(37),
    INQUIRY_EDUCATION(38),
    FRACTION_CONTRACT(39),
    REQUEST_PENSION_BY_SURVIVOR(40),
    RETIREMENT_PENSION(41),
    OBJECTION_INSURANCE_HISTORY(42),
    OBJECTION_INSURANCE_HISTORY_45(45),
    MY_ELECTRONIC_FILE(46),
    WORKERS_PAYMENT_INFO(47),
    DESERVED_TREATMENT_101(101),
    PRESCRIPTION_102(102),
    PENSION_INQUIRY(104),
    PAY_ROLL(105),
    EDICT_PENSIONER(106),
    ISSUANCE_WAGE_CERTIFICATE(107),
    DEFERRED_INSTALLMENT_CERTIFICATE(108),
    CALCULATE_WAGE_PENSION_109(109),
    GIRL_SURVIVOR(110),
    REQUEST_PENSION_BY_SURVIVOR_112(112),
    DISABILITY_PENSION(113),
    WORKSHOPS(1001),
    CONTRACT_INFO(1002),
    ASSIGNER_CONTRACT(1003),
    COMPLETE_WORKSHOP_INFO(1004),
    STACK_HOLDER_LIST(1005),
    FOLLOW_PROTEST_STATUS(1006),
    REGISTER_AGREEMENT(1007),
    PERFORMED_INSPECTION(1008),
    INSTALLMENT_DEBT(1009),
    CONSTRUCTION_INSURANCE(1010),
    OCCURRENCE(1011),
    LAWS(1012),

    // ─── AI Assistant / Chatbot ──────────────────────────────────────────────
    /** Controls the entry point for the Agent and chatbot access */
    AGENT(2000),

    // ─── Client-only — no server menu id yet ────────────────────────────────
    // Every flag above mirrors a row the server's menu already carries by this exact id.
    // The ones below gate a screen the current menu says nothing about at all; each is
    // resolved through `FeatureManager` exactly like any other flag (Enabled unless the
    // server, or a "Feature flags" dev-screen override, says otherwise) so the day the
    // server starts sending a real row for one of these, only the id here needs to change
    // to that row's — nothing that reads the flag has to.
    CHANGE_MOBILE(3001),
    PERSONAL_INBOX(3002),
    /** «لیست درخواست‌ها» in profile's کارتابل section. */
    MY_REQUESTS(3003),
    /** Shared by «تازه‌ها» (the home story rail) and «ذخیره رویدادها» — one flag gates both. */
    STORIES_AND_SAVE_EVENTS(3004),
    HEALTH_PROFILE(3005),
    CONTRACTED_CENTERS(3006),
    /** The treatment hub's yearly insured/organization spend card. */
    CURRENT_YEAR_TREATMENT_COSTS(3007),
    /** «آخرین درخواست‌ها» on the home dashboard. */
    HOME_LAST_REQUESTS(3008);

    companion object {
        fun fromId(id: Int?) = entries.find { it.id == id }
    }
}
