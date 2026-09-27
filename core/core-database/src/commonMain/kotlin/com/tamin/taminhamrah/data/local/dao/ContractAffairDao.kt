package com.tamin.taminhamrah.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.tamin.taminhamrah.data.local.entity.ContractAffairPageEntity

@Dao
interface ContractAffairDao {

    @Query(
        "SELECT * FROM contract_affair_pages WHERE listKey = :listKey " +
            "ORDER BY position ASC LIMIT :limit OFFSET :offset"
    )
    suspend fun getPageSlice(listKey: String, limit: Int, offset: Int): List<ContractAffairPageEntity>

    @Upsert
    suspend fun upsertPage(rows: List<ContractAffairPageEntity>)

    @Query("DELETE FROM contract_affair_pages WHERE listKey = :listKey")
    suspend fun clearPages(listKey: String)

    /** A fresh first page replaces that list's cached pages atomically; other lists are untouched. */
    @Transaction
    suspend fun replacePages(listKey: String, rows: List<ContractAffairPageEntity>) {
        clearPages(listKey)
        upsertPage(rows)
    }
}
