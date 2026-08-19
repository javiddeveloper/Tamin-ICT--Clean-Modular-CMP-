package com.tamin.taminhamrah.data.repository.history

import com.tamin.taminhamrah.data.local.dao.HistoryCacheDao
import com.tamin.taminhamrah.data.local.entity.HistoryWageRowEntity
import com.tamin.taminhamrah.data.local.entity.HistoryYearEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** The history cache, in memory: what was written last is what comes back. */
internal class InMemoryHistoryCacheDao : HistoryCacheDao {
    private val years = MutableStateFlow(emptyList<HistoryYearEntity>())
    private val wageRows = MutableStateFlow(emptyList<HistoryWageRowEntity>())

    override fun observeYears(): Flow<List<HistoryYearEntity>> = years

    override fun observeWageRows(): Flow<List<HistoryWageRowEntity>> = wageRows

    override suspend fun insertYears(years: List<HistoryYearEntity>) {
        this.years.value += years
    }

    override suspend fun insertWageRows(rows: List<HistoryWageRowEntity>) {
        wageRows.value += rows
    }

    override suspend fun clearYears() {
        years.value = emptyList()
    }

    override suspend fun clearWageRows() {
        wageRows.value = emptyList()
    }
}
