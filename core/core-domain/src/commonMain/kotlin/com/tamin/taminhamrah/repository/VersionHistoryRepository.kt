package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.versionHistory.VersionHistoryDN
import kotlinx.coroutines.flow.Flow

interface VersionHistoryRepository {
    suspend fun getVersionHistory(): Flow<List<VersionHistoryDN>>
}
