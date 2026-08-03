package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.data.local.entity.VersionHistoryEntity
import com.tamin.taminhamrah.model.versionHistory.VersionHistoryDN
import com.tamin.taminhamrah.model.versionHistory.VersionHistoryDto

internal fun VersionHistoryDto.toEntity(maxVersionCode: Int): VersionHistoryEntity = VersionHistoryEntity(
    versionCode = versionCode,
    versionName = versionName,
    releaseDate = releaseDate,
    isLatest = versionCode == maxVersionCode,
    newFeaturesRaw = newFeatures.joinToString("\n"),
    debugRaw = debug.joinToString("\n")
)

internal fun VersionHistoryDto.toDomain(maxVersionCode: Int): VersionHistoryDN = VersionHistoryDN(
    versionName = versionName,
    versionCode = versionCode,
    releaseDate = releaseDate,
    isLatest = versionCode == maxVersionCode,
    newFeatures = newFeatures,
    debug = debug
)

internal fun VersionHistoryEntity.toDomain(): VersionHistoryDN = VersionHistoryDN(
    versionName = versionName,
    versionCode = versionCode,
    releaseDate = releaseDate,
    isLatest = isLatest,
    newFeatures = if (newFeaturesRaw.isBlank()) emptyList() else newFeaturesRaw.split("\n"),
    debug = if (debugRaw.isBlank()) emptyList() else debugRaw.split("\n")
)
