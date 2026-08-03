package com.tamin.taminhamrah.data.repository

import com.tamin.taminhamrah.data.local.dao.VersionHistoryDao
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toEntity
import com.tamin.taminhamrah.dataSource.versionHistory.VersionHistoryRemoteDataSource
import com.tamin.taminhamrah.model.versionHistory.VersionHistoryDN
import com.tamin.taminhamrah.repository.VersionHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class VersionHistoryRepositoryImpl(
    private val versionHistoryDao: VersionHistoryDao,
    private val versionHistoryRemoteDataSource: VersionHistoryRemoteDataSource
) : VersionHistoryRepository {

    override suspend fun getVersionHistory(): Flow<List<VersionHistoryDN>> = flow {
        // 1. Emit cached offline data if available
        val cachedEntities = versionHistoryDao.getVersionHistory().firstOrNull()
        if (!cachedEntities.isNullOrEmpty()) {
            emit(cachedEntities.map { it.toDomain() })
        }

        // 2. Fetch remote data from mock API service
        try {
            val remoteData = versionHistoryRemoteDataSource.getVersionHistory()
            if (remoteData.isNotEmpty()) {
                val maxVersionCode = remoteData.maxOfOrNull { it.versionCode } ?: 0
                val entities = remoteData.map { it.toEntity(maxVersionCode) }
                val domainItems = remoteData.sortedByDescending { it.versionCode }.map { it.toDomain(maxVersionCode) }

                // 3. Save to local Room database
                versionHistoryDao.upsertVersionHistory(entities)
                // 4. Emit fresh remote domain data
                emit(domainItems)
            }
        } catch (e: Exception) {
            if (cachedEntities.isNullOrEmpty()) {
                throw e
            }
        }
    }
}
