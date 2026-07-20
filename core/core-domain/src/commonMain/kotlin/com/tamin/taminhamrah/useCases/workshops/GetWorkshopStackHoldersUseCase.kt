package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderListDN
import com.tamin.taminhamrah.repository.WorkShopsRepository
import kotlinx.coroutines.flow.Flow

class GetWorkshopStackHoldersUseCase(private val repository: WorkShopsRepository) {
    operator fun invoke(filters: List<ApiFilterDN>): Flow<WorkshopStackHolderListDN?> {
        return repository.getWorkshopStackHolders(filters)
    }
}
