package com.tamin.taminhamrah.repository.userRequest

import com.tamin.taminhamrah.model.userRequest.UserRequestDetailDN

interface UserRequestDetailRepository {
    suspend fun getShortTermRequestDetails(
        referenceId: String
    ): UserRequestDetailDN
}