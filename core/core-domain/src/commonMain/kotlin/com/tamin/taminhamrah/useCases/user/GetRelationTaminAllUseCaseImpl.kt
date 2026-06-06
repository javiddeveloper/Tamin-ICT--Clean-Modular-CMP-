package com.tamin.taminhamrah.useCases.user

import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDN
import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetRelationTaminAllUseCaseImpl(
    private val userRepository: UserRepository
) : GetRelationTaminAllUseCase {
    override suspend fun invoke(
        page: String,
        start: String,
        limit: String,
        filter: String,
        sort: String
    ): Flow<List<ActiveRelationDN>> {
        return userRepository.getRelationTaminAll(page, start, limit, filter, sort)
    }
}
