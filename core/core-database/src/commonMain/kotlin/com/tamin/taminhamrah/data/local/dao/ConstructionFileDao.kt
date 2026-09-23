package com.tamin.taminhamrah.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.tamin.taminhamrah.data.local.entity.ConstructionFileEntity
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
}
