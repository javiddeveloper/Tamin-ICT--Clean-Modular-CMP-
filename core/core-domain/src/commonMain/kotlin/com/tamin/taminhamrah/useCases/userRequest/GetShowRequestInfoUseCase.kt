package com.tamin.taminhamrah.useCases.userRequest

import com.tamin.taminhamrah.model.userRequest.UserRequestDetailsDN
import com.tamin.taminhamrah.repository.userRequest.UserRequestRepository

class GetShowRequestInfoUseCase(
    private val repository: UserRequestRepository
) {
    suspend operator fun invoke(
        referenceId: String,
        requestTypeId: Long,
    ): UserRequestDetailsDN? {
        return repository.getShowRequestInfo(referenceId, requestTypeId)
    }
}
