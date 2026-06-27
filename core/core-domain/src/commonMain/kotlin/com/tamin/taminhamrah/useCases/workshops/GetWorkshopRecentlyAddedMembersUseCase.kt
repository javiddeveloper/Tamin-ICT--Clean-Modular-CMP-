package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberListDN
import com.tamin.taminhamrah.repository.WorkShopsRepository

class GetWorkshopRecentlyAddedMembersUseCase(
    private val repository: WorkShopsRepository
) {
    suspend operator fun invoke(query: ApiQueryParamDN): WorkshopNewMemberListDN? {
        return repository.getWorkshopRecentlyAddedMembers(query)
    }
}
