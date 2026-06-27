package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderListDN
import com.tamin.taminhamrah.repository.WorkShopsRepository

class GetWorkshopStackHoldersUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(filters: List<ApiFilterDN> = emptyList()): WorkshopStackHolderListDN? {
        return repository.getWorkshopStackHolders(filters)
    }
}
