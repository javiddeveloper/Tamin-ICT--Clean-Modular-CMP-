package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberListDN
import com.tamin.taminhamrah.repository.WorkShopsRepository
import kotlinx.coroutines.flow.Flow

class GetWorkshopRecentlyAddedMembersUseCase(
    private val repository: WorkShopsRepository
) {
    operator fun invoke(filters: List<ApiFilterDN>): Flow<WorkshopNewMemberListDN?> {
        return repository.getWorkshopRecentlyAddedMembers(filters)
    }
}
