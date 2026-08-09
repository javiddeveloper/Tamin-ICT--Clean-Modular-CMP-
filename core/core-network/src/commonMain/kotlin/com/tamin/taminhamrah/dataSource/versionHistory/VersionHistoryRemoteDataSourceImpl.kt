package com.tamin.taminhamrah.dataSource.versionHistory

import com.tamin.taminhamrah.apiService.VersionHistoryApiService
import com.tamin.taminhamrah.model.versionHistory.VersionHistoryDto

class VersionHistoryRemoteDataSourceImpl(
    private val apiService: VersionHistoryApiService
) : VersionHistoryRemoteDataSource {

    override suspend fun getVersionHistory(): List<VersionHistoryDto> {
        val response = apiService.getVersionHistory()
        return response.data?.list ?: emptyList()
    }
}
