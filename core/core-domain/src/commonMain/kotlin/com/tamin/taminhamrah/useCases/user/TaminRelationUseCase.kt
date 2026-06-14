package com.tamin.taminhamrah.useCases.user

import com.tamin.taminhamrah.model.relation.TaminRelationDN
import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class TaminRelationUseCase(
    private val userRepository: UserRepository,
) {

    suspend operator fun invoke(): Flow<TaminRelationDN> {
        return userRepository.fetchTaminRelation()
    }
}
