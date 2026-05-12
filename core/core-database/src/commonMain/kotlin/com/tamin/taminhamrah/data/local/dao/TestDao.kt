package com.tamin.taminhamrah.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tamin.taminhamrah.data.local.entity.TestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TestDao {
    @Insert
    suspend fun insert(entity: TestEntity)

    @Query("SELECT * FROM test_table")
    fun getAll(): Flow<List<TestEntity>>
}
