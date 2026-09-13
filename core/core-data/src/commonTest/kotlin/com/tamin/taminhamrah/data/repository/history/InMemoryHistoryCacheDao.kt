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

    /*
     * Both inserts replace by primary key and keep the stored order, because the real DAO is
     * declared `OnConflictStrategy.REPLACE` and Room reads back ordered by `position`. Appending
     * instead would let a test pass on duplicated rows that production would have collapsed.
     */
    override suspend fun insertYears(years: List<HistoryYearEntity>) {
        val incoming = years.associateBy { it.hisYear }
        this.years.value = (this.years.value.filterNot { it.hisYear in incoming } + years)
            .sortedBy { it.position }
    }

    override suspend fun insertWageRows(rows: List<HistoryWageRowEntity>) {
        val incoming = rows.associateBy { it.id }
        wageRows.value = (wageRows.value.filterNot { it.id in incoming } + rows)
            .sortedBy { it.position }
    }

    override suspend fun clearYears() {
        years.value = emptyList()
    }

    override suspend fun clearWageRows() {
        wageRows.value = emptyList()
    }
}
