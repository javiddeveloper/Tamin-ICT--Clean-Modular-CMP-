package com.tamin.taminhamrah.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.tamin.taminhamrah.data.local.entity.IdentityInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    // identity_info is keyed by the server's per-user id, so it can hold rows for more than
    // one user across login sessions. Clearing it before every insert keeps it a true
    // single-row cache of the current user, so getIdentityInfo()'s unscoped LIMIT 1 can never
    // return a previous user's row.
    @Transaction
    suspend fun upsertIdentityInfo(item: IdentityInfoEntity) {
        clearIdentityInfo()
        insertIdentityInfo(item)
    }

    @Insert
    suspend fun insertIdentityInfo(item: IdentityInfoEntity)

    @Query("DELETE FROM identity_info")
    suspend fun clearIdentityInfo()

    @Query("SELECT * FROM identity_info LIMIT 1")
    fun getIdentityInfo(): Flow<IdentityInfoEntity?>
}
