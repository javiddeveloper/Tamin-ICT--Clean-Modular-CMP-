package com.tamin.taminhamrah.useCases.user

import com.tamin.taminhamrah.model.user.CurrentUserDN
import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.Flow

/**
 * The signed-in account in full.
 *
 * `GetUserProfileUseCase` answers the same question through a narrower model; reach for this one
 * when the roles or the organization matter, which is what the کارفرما flows need.
 */
class GetCurrentUserUseCase(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(): Flow<CurrentUserDN> = repository.getCurrentUser()
}
