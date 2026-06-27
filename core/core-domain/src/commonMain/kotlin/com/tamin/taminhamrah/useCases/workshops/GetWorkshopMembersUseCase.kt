package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.workshop.WorkshopMemberListDN
import com.tamin.taminhamrah.repository.WorkShopsRepository

class GetWorkshopMembersUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(filters: List<ApiFilterDN> = emptyList()): WorkshopMemberListDN? {
        return repository.getWorkshopMembers(filters)
    }
}
