package com.tamin.taminhamrah.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.tamin.taminhamrah.data.local.entity.VersionHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VersionHistoryDao {
    @Upsert
    suspend fun upsertVersionHistory(items: List<VersionHistoryEntity>)

    @Query("SELECT * FROM version_history ORDER BY versionCode DESC")
    fun getVersionHistory(): Flow<List<VersionHistoryEntity>>
}
