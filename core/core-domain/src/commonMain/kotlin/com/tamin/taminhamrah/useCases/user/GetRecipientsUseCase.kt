package com.tamin.taminhamrah.useCases.user

import com.tamin.taminhamrah.model.certificate.RecipientDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetRecipientsUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        filters: List<ApiFilterDN> = emptyList()
    ): Flow<List<RecipientDN>> {
        return userRepository.getRecipients(filters)
    }
}
