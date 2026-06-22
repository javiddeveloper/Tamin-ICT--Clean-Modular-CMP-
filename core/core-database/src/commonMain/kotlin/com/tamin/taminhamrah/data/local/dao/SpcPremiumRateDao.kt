package com.tamin.taminhamrah.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.tamin.taminhamrah.data.local.entity.SpcPremiumRateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SpcPremiumRateDao {

    @Query("SELECT * FROM spc_premium_rates ORDER BY spcrateCode ASC")
    fun getSpcPremiumRates(): Flow<List<SpcPremiumRateEntity>>

    @Upsert
    suspend fun upsertSpcPremiumRates(rates: List<SpcPremiumRateEntity>)

    @Query("DELETE FROM spc_premium_rates")
    suspend fun clearAll()

    @Transaction
    suspend fun replaceAll(rates: List<SpcPremiumRateEntity>) {
        clearAll()
        upsertSpcPremiumRates(rates)
    }
}
