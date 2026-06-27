package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.workshop.WorkshopMemberListDN
import com.tamin.taminhamrah.repository.WorkShopsRepository

class GetWorkshopMembersUseCase(private val repository: WorkShopsRepository) {
    suspend operator fun invoke(query: ApiQueryParamDN): WorkshopMemberListDN? {
        return repository.getWorkshopMembers(query)
    }
}
