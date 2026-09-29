package com.tamin.taminhamrah.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.tamin.taminhamrah.data.local.entity.ConstructionFileEntity
import com.tamin.taminhamrah.data.local.entity.ConstructionFilePageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConstructionFileDao {

    @Query("SELECT * FROM construction_files")
    fun getConstructionFiles(): Flow<List<ConstructionFileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(files: List<ConstructionFileEntity>)

    @Query("DELETE FROM construction_files")
    suspend fun clearAll()

    @Transaction
    suspend fun replaceAll(files: List<ConstructionFileEntity>) {
        clearAll()
        insertAll(files)
    }

    // ---- Paged list (offline-first) ----

    @Query(
        "SELECT * FROM construction_file_pages WHERE listKey = :listKey " +
            "ORDER BY position ASC LIMIT :limit OFFSET :offset"
    )
    suspend fun getPageSlice(listKey: String, limit: Int, offset: Int): List<ConstructionFilePageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPage(rows: List<ConstructionFilePageEntity>)

    @Query("DELETE FROM construction_file_pages WHERE listKey = :listKey")
    suspend fun clearPages(listKey: String)

    /** A fresh first page replaces that list's cached pages atomically; other lists are untouched. */
    @Transaction
    suspend fun replacePages(listKey: String, rows: List<ConstructionFilePageEntity>) {
        clearPages(listKey)
        upsertPage(rows)
    }
}
