package com.tamin.taminhamrah.feature.profile.ui.model

import com.tamin.taminhamrah.model.common.FeatureFlag

/**
 * A row of the profile menu. [flag] is the server feature flag that switches the row on or off, or
 * null for a row that is always available (settings, support, logout, …).
 */
enum class ProfileMenuItem(val flag: FeatureFlag? = null) {
    IDENTITY_INFO(FeatureFlag.IDENTITY_INFO),
    ACTIVE_RELATION(FeatureFlag.ACTIVE_RELATION),
    DEPENDENTS(FeatureFlag.DEPENDENTS),
    ELECTRONIC_FILE(FeatureFlag.MY_ELECTRONIC_FILE),
    BANK_ACCOUNTS(FeatureFlag.BANK_ACCOUNT_LIST),
    CHANGE_MOBILE(FeatureFlag.CHANGE_MOBILE),
    REQUESTS(FeatureFlag.MY_REQUESTS),
    PERSONAL_INBOX(FeatureFlag.PERSONAL_INBOX),
    /** Shares its flag with the home screen's «تازه‌ها» story rail — see [FeatureFlag.STORIES_AND_SAVE_EVENTS]. */
    SAVE_EVENTS(FeatureFlag.STORIES_AND_SAVE_EVENTS),
    SECURITY,
    SETTINGS,
    SUPPORT,
    CONTACT_ME,
    SHARE,
    VERSION_HISTORY,
    LOGOUT,
    DEVELOPER_OPTIONS;

    companion object {
        /** Every flag the profile screen reads, for one lookup of the menu. */
        val gatedFlags: Set<FeatureFlag> = entries.mapNotNull { it.flag }.toSet()
    }
}
