package com.tamin.taminhamrah.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tamin.taminhamrah.data.local.entity.HistoryJobInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryJobInfoDao {
    @Query("SELECT * FROM history_job_infos")
    fun getAllJobInfos(): Flow<List<HistoryJobInfoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobInfos(jobInfos: List<HistoryJobInfoEntity>)

    @Query("DELETE FROM history_job_infos")
    suspend fun clearAll()
}
