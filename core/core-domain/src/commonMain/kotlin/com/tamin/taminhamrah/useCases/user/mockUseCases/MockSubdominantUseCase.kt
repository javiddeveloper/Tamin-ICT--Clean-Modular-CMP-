package com.tamin.taminhamrah.useCases.user.mockUseCases

import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.subdominant.SubdominantDN
import com.tamin.taminhamrah.repository.UserRepository
import com.tamin.taminhamrah.useCases.user.SubdominantUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * A mock version of [com.tamin.taminhamrah.useCases.user.SubdominantUseCase] for UI testing and previews.
 * It bypasses the repository and returns data from [com.tamin.taminhamrah.feature.profile.ui.model.ProfileMocks].
 */
class MockSubdominantUseCase(userRepository: UserRepository) : SubdominantUseCase(userRepository) {
    override suspend fun invoke(filters: List<ApiFilterDN>): Flow<SubdominantDN> = flow {
        delay(500) // Simulate network delay
        emit(ProfileMocks.subdominantMockData)
    }
}
