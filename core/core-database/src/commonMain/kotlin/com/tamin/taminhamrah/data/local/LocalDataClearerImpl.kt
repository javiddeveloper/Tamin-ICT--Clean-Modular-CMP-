package com.tamin.taminhamrah.data.local

import com.tamin.taminhamrah.repository.LocalDataClearer

class LocalDataClearerImpl(
    private val database: TaminXDatabase
) : LocalDataClearer {
    override suspend fun clearAll() {
        database.clearAllTables()
    }
}
