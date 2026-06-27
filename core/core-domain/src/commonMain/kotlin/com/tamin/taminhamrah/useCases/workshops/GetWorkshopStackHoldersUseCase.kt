package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.workshop.WorkshopStackHolderListDN
import com.tamin.taminhamrah.repository.WorkShopsRepository

class GetWorkshopStackHoldersUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(query: ApiQueryParamDN): WorkshopStackHolderListDN? {
        return repository.getWorkshopStackHolders(query)
    }
}
