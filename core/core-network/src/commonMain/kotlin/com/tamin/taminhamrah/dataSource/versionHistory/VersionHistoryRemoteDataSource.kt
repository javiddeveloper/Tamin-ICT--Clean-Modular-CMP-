package com.tamin.taminhamrah.dataSource.versionHistory

import com.tamin.taminhamrah.model.versionHistory.VersionHistoryDto

interface VersionHistoryRemoteDataSource {
    suspend fun getVersionHistory(): List<VersionHistoryDto>
}
