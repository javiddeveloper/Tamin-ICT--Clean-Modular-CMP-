package com.tamin.taminhamrah.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.tamin.taminhamrah.data.local.entity.PersonalInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonalDao {
    @Upsert
    suspend fun upsertPersonalInfo(item: PersonalInfoEntity)

    @Query("SELECT * FROM personal_info LIMIT 1")
    fun getPersonalInfo(): Flow<PersonalInfoEntity?>

    @Query("DELETE FROM personal_info")
    suspend fun clearPersonalInfo()
}
