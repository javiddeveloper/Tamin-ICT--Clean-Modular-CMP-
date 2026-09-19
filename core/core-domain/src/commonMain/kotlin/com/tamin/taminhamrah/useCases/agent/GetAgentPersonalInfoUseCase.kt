package com.tamin.taminhamrah.useCases.agent

import kotlin.coroutines.cancellation.CancellationException
import com.tamin.taminhamrah.model.agent.AgentPersonalInfoDN
import com.tamin.taminhamrah.model.common.UserType
import com.tamin.taminhamrah.repository.TokenStoreManager
import com.tamin.taminhamrah.repository.UserRepository
import com.tamin.taminhamrah.repository.pension.PensionRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * The `personal_info` block of an assistant prompt, as the native app built it: national id and
 * name from the identity the app already caches, and the pensioner id for pensioners only.
 *
 * Returns null when the national id is unknown (signed out, or the identity could not be read):
 * the prompt is then sent without it rather than not sent. The pensioner id is looked up once per
 * national id, so a second user signing in on the device never gets the first user's id.
 */
fun interface GetAgentPersonalInfoUseCase {
    suspend operator fun invoke(): AgentPersonalInfoDN?
}

class GetAgentPersonalInfoUseCaseImpl(
    private val userRepository: UserRepository,
    private val pensionRepository: PensionRepository,
    private val tokenStoreManager: TokenStoreManager,
) : GetAgentPersonalInfoUseCase {
    private val lock = Mutex()
    private var pensionerIdFor: Pair<String, String?>? = null

    override suspend operator fun invoke(): AgentPersonalInfoDN? {
        if (tokenStoreManager.getToken().isNullOrBlank()) return null
        val identity = try {
            userRepository.getIdentityInfo().firstOrNull()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
        val nationalId = identity?.nationalId?.takeIf { it.isNotBlank() && it != NO_NATIONAL_ID } ?: return null
        return AgentPersonalInfoDN(
            nationalId = nationalId,
            pensionerId = pensionerId(nationalId),
            firstName = identity.firstName?.takeIf { it.isNotBlank() },
            lastName = identity.lastName?.takeIf { it.isNotBlank() },
        )
    }

    private suspend fun pensionerId(nationalId: String): String? = lock.withLock {
        if (UserType.fromNameOrNull(tokenStoreManager.getUserType()) != UserType.PENSIONER) return null
        pensionerIdFor?.takeIf { it.first == nationalId }?.let { return it.second }
        val id = try {
            pensionRepository.getPensionerId().firstOrNull()?.firstOrNull()?.pensionerId
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return null
        }?.takeIf { it.isNotBlank() }
        pensionerIdFor = nationalId to id
        id
    }

    private companion object {
        /** The identity service's placeholder for "no national id". */
        const val NO_NATIONAL_ID = "0"
    }
}
