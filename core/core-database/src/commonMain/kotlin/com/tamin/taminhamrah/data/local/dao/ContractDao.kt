package com.tamin.taminhamrah.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.tamin.taminhamrah.data.local.entity.ContractEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContractDao {

    @Query("SELECT * FROM contracts ORDER BY contractNumber DESC")
    fun getContracts(): Flow<List<ContractEntity>>

    @Upsert
    suspend fun upsertContracts(contracts: List<ContractEntity>)

    @Query("DELETE FROM contracts")
    suspend fun clearContracts()

    @Transaction
    suspend fun replaceAll(contracts: List<ContractEntity>) {
        clearContracts()
        upsertContracts(contracts)
    }
}
