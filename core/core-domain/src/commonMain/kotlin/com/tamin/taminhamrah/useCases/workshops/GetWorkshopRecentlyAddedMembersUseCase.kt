package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberListDN
import com.tamin.taminhamrah.repository.WorkShopsRepository

class GetWorkshopRecentlyAddedMembersUseCase(
    private val repository: WorkShopsRepository
) {
    suspend operator fun invoke(filters: List<ApiFilterDN> = emptyList()): WorkshopNewMemberListDN? {
        return repository.getWorkshopRecentlyAddedMembers(filters)
    }
}
