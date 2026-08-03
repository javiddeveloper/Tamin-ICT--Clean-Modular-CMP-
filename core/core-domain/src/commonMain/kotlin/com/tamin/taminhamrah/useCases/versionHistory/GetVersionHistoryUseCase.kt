package com.tamin.taminhamrah.useCases.versionHistory

import com.tamin.taminhamrah.model.versionHistory.VersionHistoryDN
import com.tamin.taminhamrah.repository.VersionHistoryRepository
import kotlinx.coroutines.flow.Flow

class GetVersionHistoryUseCase(
    private val repository: VersionHistoryRepository
) {
    suspend operator fun invoke(): Flow<List<VersionHistoryDN>> {
        return repository.getVersionHistory()
    }
}
