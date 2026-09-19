package com.tamin.taminhamrah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tamin.taminhamrah.model.common.MenuServiceStatusDN
import com.tamin.taminhamrah.repository.home.HomeQuickAccessGroup
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
    val fullName: String?,
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

/** Mirrors [com.tamin.taminhamrah.model.home.CampaignDN] — [flagId] is a [com.tamin.taminhamrah.model.common.FeatureFlag] id, not a free-standing key. */
@Serializable
data class CampaignEntity(
    val flagId: Int,
    val title: String,
    val bannerUrl: String?,
    val isOpenable: Boolean = false
)

/** Mirrors [com.tamin.taminhamrah.model.home.QuickAccessDN]. Defaults exist so a row cached
 *  before this field was added still deserializes (this list is a JSON blob column, not SQL
 *  columns — see `TaminHamrahConverters.fromQuickAccessEntityList`). */
@Serializable
data class QuickAccessEntity(
    val flagId: Int,
    val title: String,
    val iconUrl: String?,
    val group: HomeQuickAccessGroup = HomeQuickAccessGroup.FREQUENT,
    val status: MenuServiceStatusDN? = null
)

/** Mirrors [com.tamin.taminhamrah.model.home.SpecialServiceDN]. */
@Serializable
data class SpecialServiceEntity(
    val flagId: Int,
    val title: String,
    val iconUrl: String?,
    val status: MenuServiceStatusDN? = null
)

@Serializable
data class RequestEntity(
    val id: String,
    val title: String,
    val date: String,
    val status: String,
    val refCode: String,
    val statusCode: String = "",
    val requestTypeId: Long = 0L
)
