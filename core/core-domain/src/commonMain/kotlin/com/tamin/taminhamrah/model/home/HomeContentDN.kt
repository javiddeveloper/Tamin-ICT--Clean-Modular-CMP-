package com.tamin.taminhamrah.model.home

import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.MenuServiceStatusDN
import com.tamin.taminhamrah.model.stories.StoryChannelDN
import com.tamin.taminhamrah.repository.home.HomeQuickAccessGroup

data class HomeContentDN(
    val userInfo: UserInfoDN?,
    /** The «تازه‌ها» catalogue — the real domain model from the stories feature, not a placeholder. */
    val stories: List<StoryChannelDN>?,
    val campaigns: List<CampaignDN>?,
    val quickAccess: List<QuickAccessDN>?,
    val specialServices: List<SpecialServiceDN>?,
    val requests: List<RequestDN>?
)

data class UserInfoDN(
    /** Null when the identity fetch/cache genuinely has no usable name — the UI layer (which has
     *  string-resource access, unlike core-data) supplies the localized fallback text for that
     *  case; this is distinct from "not loaded yet" (`HomeContentDN` itself being null). */
    val fullName: String?,
    val hasDarmanCoverage: Boolean?,
    val hasActiveRelation: Boolean?
)

/**
 * A promo card whose destination is the server's answer, not this row's — [flag] is the shared key
 * with [com.tamin.taminhamrah.repository.home.HomeContentPlaceholders]. [title] is not authored
 * here: there is no campaigns endpoint yet, so it is read off the (currently mocked) menu
 * ([com.tamin.taminhamrah.repository.common.CommonRepository.getMainMenu]) by [flag]'s id, the same
 * way every other service's display name is sourced. When a real campaigns endpoint arrives, this
 * gains its own fields straight from the wire and the menu lookup goes away.
 */
data class CampaignDN(
    val flag: FeatureFlag,
    val title: String,
    val bannerUrl: String?,
    /** Mirrors `FeatureStatus.opensSomething` at cache-write time — a campaign whose feature is
     *  currently disabled server-side is still recorded (so it round-trips), but shouldn't be
     *  rendered as tappable. */
    val isOpenable: Boolean
)

data class QuickAccessDN(
    val flag: FeatureFlag,
    val title: String,
    val iconUrl: String?,
    /** Which of the 5 «دسترسی سریع» chip sections this row belongs to. */
    val group: HomeQuickAccessGroup,
    val status: MenuServiceStatusDN?
)

data class SpecialServiceDN(
    val flag: FeatureFlag,
    val title: String,
    val iconUrl: String?,
    val status: MenuServiceStatusDN?
)

data class RequestDN(
    val id: String,
    val title: String,
    val date: String,
    val status: String,
    val refCode: String,
    val statusCode: String,
    val requestTypeId: Long
)
