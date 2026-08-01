package com.tamin.taminhamrah.useCases.agent

import com.tamin.taminhamrah.repository.UserRepository
import kotlinx.coroutines.flow.firstOrNull

/**
 * Resolves the signed-in user's national code, used to scope cached conversations.
 *
 * Declared as a `fun interface` so callers depend on one tiny capability instead of the
 * whole user repository, and tests can substitute it in a single line.
 */
fun interface GetCurrentUserNationalCodeUseCase {
    suspend operator fun invoke(): String?
}

class GetCurrentUserNationalCodeUseCaseImpl(
    private val userRepository: UserRepository
) : GetCurrentUserNationalCodeUseCase {
    override suspend fun invoke(): String? =
        runCatching { userRepository.getIdentityInfo().firstOrNull()?.nationalId }
            .getOrNull()
            ?.takeIf { it.isNotBlank() }
}
