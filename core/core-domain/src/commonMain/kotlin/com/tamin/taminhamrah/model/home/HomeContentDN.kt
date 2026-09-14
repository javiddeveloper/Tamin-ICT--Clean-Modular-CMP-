package com.tamin.taminhamrah.model.home

import com.tamin.taminhamrah.model.stories.StoryChannelDN

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
    val fullName: String,
    val hasDarmanCoverage: Boolean?,
    val hasActiveRelation: Boolean?
)

data class CampaignDN(
    val id: String,
    val title: String,
    val bannerUrl: String?
)

data class QuickAccessDN(
    val id: String,
    val title: String,
    val iconUrl: String?
)

data class SpecialServiceDN(
    val id: String,
    val title: String,
    val iconUrl: String?
)

data class RequestDN(
    val id: String,
    val title: String,
    val date: String,
    val status: String,
    val refCode:String
)
