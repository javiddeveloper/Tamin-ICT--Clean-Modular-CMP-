package com.tamin.taminhamrah.useCases.user

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.subdominant.SubdominantDN
import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow

open class SubdominantUseCase(private val userRepository: UserRepository) {
    open suspend operator fun invoke(
        filters: List<ApiFilterDN> = emptyList()
    ): Flow<SubdominantDN> {
        return userRepository.getSubDominantsInfo(filters)
    }
}
