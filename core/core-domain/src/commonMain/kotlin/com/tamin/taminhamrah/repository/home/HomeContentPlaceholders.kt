package com.tamin.taminhamrah.repository.home

import com.tamin.taminhamrah.model.common.FeatureFlag

/**
 * The home screen's «دسترسی سریع» chip sections that get cached for offline use. Mirrors
 * `HomeServiceSection`'s `QUICK_ACCESS`-placed entries (core-ui) one for one by name — core-domain
 * can't reference that enum (core-ui depends on core-domain, not the reverse), so this is
 * domain's own tag, and core-ui maps between the two by matching entry names.
 */
enum class HomeQuickAccessGroup {
    FREQUENT,
    HISTORY,
    AID,
    PENSIONER,
    EMPLOYER,
}

/**
 * The campaigns / quick-access / special-services rows shown on the home screen until their own
 * endpoints exist. Held here, keyed by [FeatureFlag], so [HomeRepository] implementations never
 * need to invent their own selection or display copy for these features — display copy is resolved
 * from the real (currently mocked) menu at cache-write time (see `HomeRepositoryImpl`), and *which*
 * flags to feature reuses the same groupings the real home screen already renders from, so the
 * offline-first cache can never show something different from what's actually on screen:
 * - [quickAccessGroups] mirrors every `HomeServiceSection` in `QUICK_ACCESS` placement (پرکاربرد،
 *   سابقه، کمک‌هزینه، مستمری، کارفرما) via [HomeServiceMembership], one entry per [HomeQuickAccessGroup].
 * - [specialServiceFlags] mirrors `HomeServiceSection.FEATURED` (خدمات ویژه) via [HomeServiceMembership].
 * - [campaignFlags] mirrors `CampaignKind.entries` (core-ui) — every campaign the carousel can show.
 *
 * Delete this object once the server-driven equivalents ship; callers should switch to mapping the
 * real response instead of reading from here.
 */
object HomeContentPlaceholders {
    val campaignFlags: List<FeatureFlag> = listOf(
        FeatureFlag.HOUSEWIFE_INSURANCE,
        FeatureFlag.FREELANCE_INSURANCE,
        FeatureFlag.STUDENT_INSURANCE,
    )
    val quickAccessGroups: Map<HomeQuickAccessGroup, List<FeatureFlag>> = mapOf(
        HomeQuickAccessGroup.FREQUENT to HomeServiceMembership.frequent,
        HomeQuickAccessGroup.HISTORY to HomeServiceMembership.history,
        HomeQuickAccessGroup.AID to HomeServiceMembership.aid,
        HomeQuickAccessGroup.PENSIONER to HomeServiceMembership.pensioner,
        HomeQuickAccessGroup.EMPLOYER to HomeServiceMembership.employer,
    )
    val specialServiceFlags: List<FeatureFlag> = HomeServiceMembership.featured
}
