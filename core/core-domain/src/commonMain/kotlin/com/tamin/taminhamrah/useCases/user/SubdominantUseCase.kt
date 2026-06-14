package com.tamin.taminhamrah.useCases.user

import com.tamin.taminhamrah.model.subdominant.SubdominantDN
import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class SubdominantUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(
        page: String,
        start: String,
        limit: String,
        filter: String,
        sort: String
    ): Flow<SubdominantDN> {
        return userRepository.getSubDominantsInfo(page, start, limit, filter, sort)
    }
}
