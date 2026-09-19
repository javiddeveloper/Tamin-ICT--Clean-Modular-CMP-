package com.tamin.taminhamrah.data.local

import com.tamin.taminhamrah.repository.AgentAccessStore
import com.tamin.taminhamrah.repository.LocalDataClearer

class LocalDataClearerImpl(
    private val database: TaminXDatabase,
    private val agentAccessStore: AgentAccessStore,
) : LocalDataClearer {
    override suspend fun clearAll() {
        // The assistant's permission and chat token belong to the account that just left.
        agentAccessStore.clear()
        database.clearAllTables()
    }
}
