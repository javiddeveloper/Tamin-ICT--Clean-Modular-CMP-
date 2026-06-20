package com.tamin.taminhamrah.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.tamin.taminhamrah.data.local.entity.UserRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserRequestDao {

    @Query("SELECT * FROM user_requests ORDER BY refCode DESC")
    fun getUserRequests(): Flow<List<UserRequestEntity>>

    @Upsert
    suspend fun upsertUserRequests(requests: List<UserRequestEntity>)

    @Query("DELETE FROM user_requests")
    suspend fun clearUserRequests()

    @Transaction
    suspend fun replaceAll(requests: List<UserRequestEntity>) {
        clearUserRequests()
        upsertUserRequests(requests)
    }
}
