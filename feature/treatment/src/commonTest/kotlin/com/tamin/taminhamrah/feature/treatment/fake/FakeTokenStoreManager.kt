package com.tamin.taminhamrah.feature.treatment.fake

import com.tamin.taminhamrah.repository.TokenStoreManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Fake [TokenStoreManager] whose stored user id can be configured per test
 * (defaults to the main insured national code). Only the user-id accessors carry
 * behaviour; the rest are no-ops as the treatment ViewModels don't use them.
 */
class FakeTokenStoreManager(
    private var storedUserId: String? = TreatmentTestData.MAIN_NATIONAL_CODE
) : TokenStoreManager {
    override fun saveToken(token: String?) {}
    override fun getToken(): String? = null
    override fun saveRefreshToken(refreshToken: String?) {}
    override fun getRefreshToken(): String? = null
    override fun saveUserId(userId: String?) { this.storedUserId = userId }
    override fun getUserId(): String? = storedUserId
    override fun saveCodeVerifier(codeVerifier: String?) {}
    override fun getCodeVerifier(): String? = null
    override fun tokenValidFlow(): Flow<Boolean> = flowOf(true)
    override suspend fun setTokenValid(isValid: Boolean) {}
}
