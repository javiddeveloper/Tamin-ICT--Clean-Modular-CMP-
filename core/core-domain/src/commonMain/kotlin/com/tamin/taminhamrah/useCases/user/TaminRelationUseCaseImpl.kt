package com.tamin.taminhamrah.useCases.user

import com.tamin.taminhamrah.model.user.TaminRelationDN
import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class TaminRelationUseCaseImpl(
    private val userRepository: UserRepository,
) : TaminRelationUseCase {

    override suspend fun invoke(): Flow<TaminRelationDN> {
        return userRepository.fetchTaminRelation()
    }
}
