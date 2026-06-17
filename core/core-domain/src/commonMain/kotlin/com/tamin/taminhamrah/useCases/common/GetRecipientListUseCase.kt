package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.model.common.RecipientDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.RecipientRepository
import kotlinx.coroutines.flow.Flow

class GetRecipientListUseCase(
    private val recipientRepository: RecipientRepository
) {
    operator fun invoke(
        filters: List<ApiFilterDN> = emptyList()
    ): Flow<List<RecipientDN>> {
        return recipientRepository.getRecipientList(filters)
    }
}
