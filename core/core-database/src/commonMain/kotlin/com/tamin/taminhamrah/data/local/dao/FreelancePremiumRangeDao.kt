package com.tamin.taminhamrah.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.tamin.taminhamrah.data.local.entity.FreelancePremiumRangeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FreelancePremiumRangeDao {

    @Query("SELECT * FROM freelance_premium_ranges WHERE id = :id LIMIT 1")
    fun getById(id: String): Flow<FreelancePremiumRangeEntity?>

    @Upsert
    suspend fun upsert(entity: FreelancePremiumRangeEntity)
}
