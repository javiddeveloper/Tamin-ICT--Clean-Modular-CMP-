package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.data.local.entity.CampaignEntity
import com.tamin.taminhamrah.data.local.entity.QuickAccessEntity
import com.tamin.taminhamrah.data.local.entity.SpecialServiceEntity
import com.tamin.taminhamrah.data.local.entity.StoryChannelEntity
import com.tamin.taminhamrah.data.local.entity.StoryCtaEntity
import com.tamin.taminhamrah.data.local.entity.StoryItemEntity
import com.tamin.taminhamrah.data.local.entity.StoryMediaEntity
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.home.CampaignDN
import com.tamin.taminhamrah.model.home.QuickAccessDN
import com.tamin.taminhamrah.model.home.SpecialServiceDN
import com.tamin.taminhamrah.model.stories.StoryChannelDN
import com.tamin.taminhamrah.model.stories.StoryCtaDN
import com.tamin.taminhamrah.model.stories.StoryItemDN
import com.tamin.taminhamrah.model.stories.StoryMediaDN

fun StoryChannelEntity.toDomain(): StoryChannelDN = StoryChannelDN(
    key = key,
    name = name,
    shortName = shortName,
    time = time,
    items = items.map { it.toDomain() },
)

fun StoryItemEntity.toDomain(): StoryItemDN = StoryItemDN(
    id = id,
    title = title,
    body = body,
    media = media.toDomain(),
    cta = cta?.toDomain(),
)

fun StoryMediaEntity.toDomain(): StoryMediaDN = when (this) {
    is StoryMediaEntity.None -> StoryMediaDN.None
    is StoryMediaEntity.Image -> StoryMediaDN.Image(url)
    is StoryMediaEntity.Video -> StoryMediaDN.Video(url)
    is StoryMediaEntity.BundledImage -> StoryMediaDN.BundledImage(path)
    is StoryMediaEntity.BundledVideo -> StoryMediaDN.BundledVideo(path)
}

fun StoryCtaEntity.toDomain(): StoryCtaDN = StoryCtaDN(label = label, deepLink = deepLink)

fun StoryChannelDN.toEntity(): StoryChannelEntity = StoryChannelEntity(
    key = key,
    name = name,
    shortName = shortName,
    time = time,
    items = items.map { it.toEntity() },
)

fun StoryItemDN.toEntity(): StoryItemEntity = StoryItemEntity(
    id = id,
    title = title,
    body = body,
    media = media.toEntity(),
    cta = cta?.toEntity(),
)

fun StoryMediaDN.toEntity(): StoryMediaEntity = when (this) {
    is StoryMediaDN.None -> StoryMediaEntity.None
    is StoryMediaDN.Image -> StoryMediaEntity.Image(url)
    is StoryMediaDN.Video -> StoryMediaEntity.Video(url)
    is StoryMediaDN.BundledImage -> StoryMediaEntity.BundledImage(path)
    is StoryMediaDN.BundledVideo -> StoryMediaEntity.BundledVideo(path)
}

fun StoryCtaDN.toEntity(): StoryCtaEntity = StoryCtaEntity(label = label, deepLink = deepLink)

/**
 * A cached row whose [CampaignEntity.flagId] no longer matches a known [FeatureFlag] (the flag was
 * retired) has nothing left to render, so it is dropped rather than surfaced with a null identity —
 * callers should use `mapNotNull` over these.
 */
fun CampaignEntity.toDomain(): CampaignDN? = FeatureFlag.fromId(flagId)?.let { CampaignDN(it, title, bannerUrl, isOpenable) }
fun QuickAccessEntity.toDomain(): QuickAccessDN? = FeatureFlag.fromId(flagId)?.let { QuickAccessDN(it, title, iconUrl, group, status) }
fun SpecialServiceEntity.toDomain(): SpecialServiceDN? = FeatureFlag.fromId(flagId)?.let { SpecialServiceDN(it, title, iconUrl, status) }
