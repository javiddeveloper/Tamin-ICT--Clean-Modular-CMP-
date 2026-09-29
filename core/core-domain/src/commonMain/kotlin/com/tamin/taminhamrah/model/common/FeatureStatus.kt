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

/**
 * One entry per menu id. The id is the only thing the menu and a flag share, so every id in
 * `MockMenuData` must appear here exactly once — `fromId` takes the *first* match, so a repeated id
 * would silently route one menu row to the wrong feature.
 *
 * **These are the legacy server's own ids** (`1`–`47` insured, `101`–`113` pensioners, `1001`–`1012`
 * employers, `2000` the assistant), not a scheme this app invented — `menu_data_<version>.txt`, once
 * switched back on, has to line up with these without a remap. The numbers are *not* sequential
 * within a band (the server left gaps — e.g. `12`, `24`, `27`–`32` in the insured band — for
 * services this app doesn't carry, or hasn't been assigned) and do **not** decide display order;
 * that's `MockMenuData`'s `sorting` field, read by `MenuDao.getMenuItems()` before `id` is ever
 * consulted as a tie-break. A service that both an insured person and a pensioner reach has one flag
 * per audience, the pensioner's suffixed `_PENSIONER`, when each audience needs its own menu row.
 */
enum class FeatureFlag(val id: Int) {
    // ─── Insured ──────────────────────────────────────────────────────────────
    IDENTITY_INFO(1),
    ACTIVE_RELATION(2),
    BANK_ACCOUNT_LIST(3),
    EDIT_IMAGE(4),
    DEPENDENTS(5),
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
    // No legacy row ever carried an insured-audience "مستمری از کارافتادگی" (the server only ever
    // sent one, pensioner-only, at id 113 — see DISABILITY_PENSION_PENSIONER). This id and the
    // MockMenuData row that uses it are new; 44 is simply an unused gap in the legacy insured band,
    // not a number the server has ever assigned to anything.
    DISABILITY_PENSION(44),
    MY_ELECTRONIC_FILE(46),
    WORKERS_PAYMENT_INFO(47),

    // «اعلام حادثه» and «سامانه قوانین و مقررات» keep the *employer* ids the legacy server assigned
    // them (`1011`/`1012`) even though `MockMenuData` currently places both rows in the insured
    // audience (`showRole = [1]`) — the id is the server's identity for the service, `showRole` is
    // separate, server-supplied audience metadata this mock is only guessing at.
    OCCURRENCE(1011),
    LAWS(1012),

    // ─── Pensioners ───────────────────────────────────────────────────────────
    DESERVED_TREATMENT_PENSIONER(101),
    PENSION_INQUIRY(104),
    PAY_ROLL(105),
    EDICT_PENSIONER(106),
    ISSUANCE_WAGE_CERTIFICATE(107),
    DEFERRED_INSTALLMENT_CERTIFICATE(108),
    GIRL_SURVIVOR(110),
    REQUEST_PENSION_BY_SURVIVOR_PENSIONER(112),
    DISABILITY_PENSION_PENSIONER(113),
    // The legacy server never sent a dedicated pensioner row for this — `CALCULATE_WAGE_PENSION`
    // covered both audiences with one row (id 23, showRole [1, 2]). 109 was already reserved for it
    // in the old enum (as a rowless placeholder) when a service needed a pensioner-only position;
    // reused here now that MockMenuData gives it a real, separate row.
    CALCULATE_WAGE_PENSION_PENSIONER(109),

    // ─── Employers ────────────────────────────────────────────────────────────
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

    // ─── No menu row today ───────────────────────────────────────────────────
    // Legacy server placeholders that never had a menu row — kept under their original ids.
    OBJECTION_INSURANCE_HISTORY_LEGACY(45),
    PRESCRIPTION_PENSIONER(102),

    // ─── AI Assistant / Chatbot ──────────────────────────────────────────────
    /** Controls the entry point for the Agent and chatbot access */
    AGENT(2000),

    // ─── Provisional ids — pending real registration on the server ──────────
    // The screens below don't have a row in the real backend's menu yet. Until they do, each
    // has a row in `mockMenuData` under the placeholder id here (exactly like every other flag
    // above — resolved through `FeatureManager`, dimmable/disable-able the same way, visible to
    // the "Feature flags" dev screen the same way). Once the server registers a real id for one
    // of these, only the id here and its `mockMenuData` row need to be removed — nothing that
    // reads the flag does.
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
