package com.tamin.taminhamrah.repository.historyObjection

import kotlinx.coroutines.flow.Flow

interface HistoryObjectionRepository {
    fun checkStatusNotExist(): Flow<Boolean>
}
