package com.tamin.taminhamrah.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.tamin.taminhamrah.data.local.entity.JobTitlePageEntity

@Dao
interface JobTitlePageDao {

    @Query(
        "SELECT * FROM job_title_pages WHERE listKey = :listKey " +
            "ORDER BY position ASC LIMIT :limit OFFSET :offset"
    )
    suspend fun getPageSlice(listKey: String, limit: Int, offset: Int): List<JobTitlePageEntity>

    @Upsert
    suspend fun upsertPage(rows: List<JobTitlePageEntity>)

    @Query("DELETE FROM job_title_pages WHERE listKey = :listKey")
    suspend fun clearPages(listKey: String)

    /** A fresh first page replaces that list's cached pages atomically; other lists are untouched. */
    @Transaction
    suspend fun replacePages(listKey: String, rows: List<JobTitlePageEntity>) {
        clearPages(listKey)
        upsertPage(rows)
    }
}
