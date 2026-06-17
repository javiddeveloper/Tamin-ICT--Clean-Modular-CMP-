package com.tamin.taminhamrah.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.tamin.taminhamrah.data.local.entity.RequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RequestDao {

    @Query("SELECT * FROM requests ORDER BY refCode DESC")
    fun getRequests(): Flow<List<RequestEntity>>

    @Upsert
    suspend fun upsertRequests(requests: List<RequestEntity>)

    @Query("DELETE FROM requests")
    suspend fun clearRequests()

    @Transaction
    suspend fun replaceAll(requests: List<RequestEntity>) {
        clearRequests()
        upsertRequests(requests)
    }
}
