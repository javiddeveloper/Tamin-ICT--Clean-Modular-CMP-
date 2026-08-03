package com.tamin.taminhamrah.mapper.versionHistory

import com.tamin.taminhamrah.model.versionHistory.VersionHistoryDN
import com.tamin.taminhamrah.model.versionHistory.VersionHistoryPR

fun VersionHistoryDN.toPresentation(isDefaultExpanded: Boolean = false): VersionHistoryPR = VersionHistoryPR(
    versionName = versionName,
    versionCode = versionCode,
    releaseDate = releaseDate,
    isLatest = isLatest,
    newFeatures = newFeatures,
    debug = debug,
    isExpanded = isLatest || isDefaultExpanded
)
