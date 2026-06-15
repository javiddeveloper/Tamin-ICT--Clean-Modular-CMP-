package com.tamin.taminhamrah.useCases.user

import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetRelationTaminAllUseCase(
    private val userRepository: UserRepository
)  {
     suspend fun invoke(
        filters: List<ApiFilterDN> = emptyList()
    ): Flow<List<ActiveRelationDN>> {
        return userRepository.getRelationTaminAll(filters)
    }
}
