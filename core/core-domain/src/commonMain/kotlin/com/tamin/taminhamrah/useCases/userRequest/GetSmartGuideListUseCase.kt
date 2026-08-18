package com.tamin.taminhamrah.useCases.userRequest

import com.tamin.taminhamrah.model.userRequest.SmartGuideDN
import com.tamin.taminhamrah.model.userRequest.SmartGuideSearchParams
import com.tamin.taminhamrah.repository.userRequest.UserRequestRepository

class GetSmartGuideListUseCase(
    private val repository: UserRequestRepository,
) {
    suspend operator fun invoke(
        params: SmartGuideSearchParams = SmartGuideSearchParams()
    ): List<SmartGuideDN> {
        return repository.getSmartGuideList(params)
    }
}
