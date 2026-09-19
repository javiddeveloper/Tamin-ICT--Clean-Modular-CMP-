package com.tamin.taminhamrah.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tamin.taminhamrah.data.local.entity.HomeContentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeContentDao {
    @Query("SELECT * FROM home_content WHERE id = 1")
    fun getHomeContent(): Flow<HomeContentEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(content: HomeContentEntity)

    @Query("DELETE FROM home_content")
    suspend fun clear()
}
