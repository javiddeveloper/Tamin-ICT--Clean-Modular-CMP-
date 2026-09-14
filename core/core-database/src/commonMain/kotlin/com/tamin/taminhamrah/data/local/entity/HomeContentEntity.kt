package com.tamin.taminhamrah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "home_content")
data class HomeContentEntity(
    @PrimaryKey
    val id: Int = 1, // Single row for home content
    val userInfo: UserInfoEntity?,
    val stories: List<StoryChannelEntity>?,
    val campaigns: List<CampaignEntity>?,
    val quickAccess: List<QuickAccessEntity>?,
    val specialServices: List<SpecialServiceEntity>?,
    val requests: List<RequestEntity>?
)

@Serializable
data class UserInfoEntity(
    val fullName: String,
    val hasDarmanCoverage: Boolean?,
    val hasActiveRelation: Boolean?
)

/** Mirrors [com.tamin.taminhamrah.model.stories.StoryChannelDN] — one publisher on the «تازه‌ها» rail. */
@Serializable
data class StoryChannelEntity(
    val key: String,
    val name: String,
    val shortName: String,
    val time: String,
    val items: List<StoryItemEntity>
)

/** Mirrors [com.tamin.taminhamrah.model.stories.StoryItemDN] — a single slide. */
@Serializable
data class StoryItemEntity(
    val id: String,
    val title: String,
    val body: String,
    val media: StoryMediaEntity,
    val cta: StoryCtaEntity? = null
)

/** Mirrors [com.tamin.taminhamrah.model.stories.StoryMediaDN]. */
@Serializable
sealed interface StoryMediaEntity {
    @Serializable
    data object None : StoryMediaEntity

    @Serializable
    data class Image(val url: String) : StoryMediaEntity

    @Serializable
    data class Video(val url: String) : StoryMediaEntity

    @Serializable
    data class BundledImage(val path: String) : StoryMediaEntity

    @Serializable
    data class BundledVideo(val path: String) : StoryMediaEntity
}

/** Mirrors [com.tamin.taminhamrah.model.stories.StoryCtaDN]. */
@Serializable
data class StoryCtaEntity(
    val label: String,
    val deepLink: String?
)

@Serializable
data class CampaignEntity(
    val id: String,
    val title: String,
    val bannerUrl: String?
)

@Serializable
data class QuickAccessEntity(
    val id: String,
    val title: String,
    val iconUrl: String?
)

@Serializable
data class SpecialServiceEntity(
    val id: String,
    val title: String,
    val iconUrl: String?
)

@Serializable
data class RequestEntity(
    val id: String,
    val title: String,
    val date: String,
    val status: String,
    val refCode: String
)
