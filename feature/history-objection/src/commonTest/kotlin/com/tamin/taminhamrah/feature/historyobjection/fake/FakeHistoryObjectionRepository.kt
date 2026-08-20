package com.tamin.taminhamrah.feature.historyobjection.fake

import com.tamin.taminhamrah.model.historyObjection.NotExistRequestDN
import com.tamin.taminhamrah.model.historyObjection.SaveNotExistRequestDN
import com.tamin.taminhamrah.repository.historyObjection.HistoryObjectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeHistoryObjectionRepository : HistoryObjectionRepository {
    var notExistRequestsResult: List<NotExistRequestDN> = emptyList()
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Error")
    var saveNotExistResult = true
    var shouldThrowOnSave = false
    var saveError: Throwable = RuntimeException("Save error")
    var lastSaveNotExistRequest: SaveNotExistRequestDN? = null

    override fun checkStatusNotExist(): Flow<Boolean> = flow { emit(false) }

    override fun getNotExistRequests(): Flow<List<NotExistRequestDN>> = flow {
        if (shouldThrowError) throw error
        emit(notExistRequestsResult)
    }

    override fun saveNotExist(request: SaveNotExistRequestDN): Flow<Boolean> = flow {
        lastSaveNotExistRequest = request
        if (shouldThrowOnSave) throw saveError
        emit(saveNotExistResult)
    }
}
