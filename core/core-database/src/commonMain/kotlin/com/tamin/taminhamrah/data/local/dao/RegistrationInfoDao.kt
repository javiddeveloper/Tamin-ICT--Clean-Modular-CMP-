package com.tamin.taminhamrah.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.tamin.taminhamrah.data.local.entity.RegistrationInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistrationInfoDao {

    @Query("SELECT * FROM registration_info WHERE id = :id LIMIT 1")
    fun getRegistrationInfo(id: Int = RegistrationInfoEntity.SINGLE_ROW_ID): Flow<RegistrationInfoEntity?>

    @Upsert
    suspend fun upsertRegistrationInfo(info: RegistrationInfoEntity)

    @Query("DELETE FROM registration_info")
    suspend fun clearRegistrationInfo()
}
