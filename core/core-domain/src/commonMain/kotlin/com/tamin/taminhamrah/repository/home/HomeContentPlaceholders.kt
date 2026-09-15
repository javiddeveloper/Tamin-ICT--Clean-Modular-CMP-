package com.tamin.taminhamrah.repository.home

import com.tamin.taminhamrah.model.common.FeatureFlag

/**
 * The campaigns / quick-access / special-services rows shown on the home screen until their own
 * endpoints exist. Held here, keyed by [FeatureFlag], so [HomeRepository] implementations never
 * need to invent their own selection or display copy for these features — display copy is resolved
 * from the real (currently mocked) menu at cache-write time (see `HomeRepositoryImpl`), and *which*
 * flags to feature reuses the same groupings the real home screen already renders from, so the
 * offline-first cache can never show something different from what's actually on screen:
 * - [quickAccessFlags] mirrors `HomeServiceSection.FREQUENT` (پرکاربرد) via [HomeServiceMembership].
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
    val quickAccessFlags: List<FeatureFlag> = HomeServiceMembership.frequent
    val specialServiceFlags: List<FeatureFlag> = HomeServiceMembership.featured
}
