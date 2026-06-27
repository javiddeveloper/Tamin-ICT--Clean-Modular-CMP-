package com.tamin.taminhamrah.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.tamin.taminhamrah.data.local.entity.BranchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BranchDao {

    @Query("SELECT * FROM branches WHERE cityCode = :cityCode ORDER BY name ASC")
    fun getBranchesByCityCode(cityCode: String): Flow<List<BranchEntity>>

    @Upsert
    suspend fun upsertBranches(branches: List<BranchEntity>)

    @Query("DELETE FROM branches WHERE cityCode = :cityCode")
    suspend fun clearBranchesByCityCode(cityCode: String)

    @Transaction
    suspend fun replaceAllForCity(cityCode: String, branches: List<BranchEntity>) {
        clearBranchesByCityCode(cityCode)
        upsertBranches(branches)
    }
}
