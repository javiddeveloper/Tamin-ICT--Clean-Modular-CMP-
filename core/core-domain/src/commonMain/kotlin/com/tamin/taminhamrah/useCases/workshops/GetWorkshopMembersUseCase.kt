package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.workshop.WorkshopMemberListDN
import com.tamin.taminhamrah.repository.WorkShopsRepository
import kotlinx.coroutines.flow.Flow

class GetWorkshopMembersUseCase(private val repository: WorkShopsRepository) {
    operator fun invoke(filters: List<ApiFilterDN>): Flow<WorkshopMemberListDN?> {
        return repository.getWorkshopMembers(filters)
    }
}
