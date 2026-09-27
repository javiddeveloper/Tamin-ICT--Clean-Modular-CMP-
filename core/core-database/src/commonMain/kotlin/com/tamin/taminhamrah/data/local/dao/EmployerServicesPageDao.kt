package com.tamin.taminhamrah.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.tamin.taminhamrah.data.local.entity.WorkshopContractRowPageEntity
import com.tamin.taminhamrah.data.local.entity.WorkshopWithoutContractPageEntity

@Dao
interface EmployerServicesPageDao {

    // ---- Workshops without an agreement ----

    @Query(
        "SELECT * FROM workshop_without_contract_pages WHERE listKey = :listKey " +
            "ORDER BY position ASC LIMIT :limit OFFSET :offset"
    )
    suspend fun getWorkshopsWithoutContractSlice(
        listKey: String,
        limit: Int,
        offset: Int,
    ): List<WorkshopWithoutContractPageEntity>

    @Upsert
    suspend fun upsertWorkshopsWithoutContract(rows: List<WorkshopWithoutContractPageEntity>)

    @Query("DELETE FROM workshop_without_contract_pages WHERE listKey = :listKey")
    suspend fun clearWorkshopsWithoutContract(listKey: String)

    @Transaction
    suspend fun replaceWorkshopsWithoutContract(listKey: String, rows: List<WorkshopWithoutContractPageEntity>) {
        clearWorkshopsWithoutContract(listKey)
        upsertWorkshopsWithoutContract(rows)
    }

    // ---- Contract rows ----

    @Query(
        "SELECT * FROM workshop_contract_row_pages WHERE listKey = :listKey " +
            "ORDER BY position ASC LIMIT :limit OFFSET :offset"
    )
    suspend fun getContractRowsSlice(listKey: String, limit: Int, offset: Int): List<WorkshopContractRowPageEntity>

    @Upsert
    suspend fun upsertContractRows(rows: List<WorkshopContractRowPageEntity>)

    @Query("DELETE FROM workshop_contract_row_pages WHERE listKey = :listKey")
    suspend fun clearContractRows(listKey: String)

    @Transaction
    suspend fun replaceContractRows(listKey: String, rows: List<WorkshopContractRowPageEntity>) {
        clearContractRows(listKey)
        upsertContractRows(rows)
    }
}
