package com.tamin.taminhamrah.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.tamin.taminhamrah.data.local.entity.RecipientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipientDao {
    @Upsert
    suspend fun upsertRecipients(recipients: List<RecipientEntity>)

    @Query("SELECT * FROM recipients")
    fun getRecipients(): Flow<List<RecipientEntity>>

    @Query("DELETE FROM recipients")
    suspend fun clearRecipients()
}
