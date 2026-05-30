package com.tamin.taminhamrah.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.tamin.taminhamrah.data.local.entity.IdentityInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Upsert
    suspend fun upsertIdentityInfo(item: IdentityInfoEntity)

    @Query("SELECT * FROM identity_info LIMIT 1")
    fun getIdentityInfo(): Flow<IdentityInfoEntity?>
}
