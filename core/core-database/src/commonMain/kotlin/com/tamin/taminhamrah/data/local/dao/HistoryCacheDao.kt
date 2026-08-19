package com.tamin.taminhamrah.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.tamin.taminhamrah.data.local.entity.HistoryWageRowEntity
import com.tamin.taminhamrah.data.local.entity.HistoryYearEntity
import kotlinx.coroutines.flow.Flow

/**
 * The cached «کلیه سوابق» — the years and the employers behind them.
 *
 * Both tables are replaced together in [replaceYears] / [replaceWageRows]: a load that returned
 * fewer rows than the cache holds must leave the cache matching the service, not a merge of the two.
 */
@Dao
interface HistoryCacheDao {

    @Query("SELECT * FROM history_years ORDER BY position ASC")
    fun observeYears(): Flow<List<HistoryYearEntity>>

    @Query("SELECT * FROM history_wage_rows ORDER BY position ASC")
    fun observeWageRows(): Flow<List<HistoryWageRowEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertYears(years: List<HistoryYearEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWageRows(rows: List<HistoryWageRowEntity>)

    @Query("DELETE FROM history_years")
    suspend fun clearYears()

    @Query("DELETE FROM history_wage_rows")
    suspend fun clearWageRows()

    @Transaction
    suspend fun replaceYears(years: List<HistoryYearEntity>) {
        clearYears()
        insertYears(years)
    }

    @Transaction
    suspend fun replaceWageRows(rows: List<HistoryWageRowEntity>) {
        clearWageRows()
        insertWageRows(rows)
    }
}
