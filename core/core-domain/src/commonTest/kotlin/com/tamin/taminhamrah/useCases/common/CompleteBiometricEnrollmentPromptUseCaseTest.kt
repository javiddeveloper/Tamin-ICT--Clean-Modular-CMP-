package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.repository.FakeUserPreferencesRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CompleteBiometricEnrollmentPromptUseCaseTest : BaseUseCaseTest() {

    private lateinit var userPreferencesRepository: FakeUserPreferencesRepository
    private lateinit var useCase: CompleteBiometricEnrollmentPromptUseCase

    @BeforeTest
    fun setup() {
        userPreferencesRepository = FakeUserPreferencesRepository()
        useCase = CompleteBiometricEnrollmentPromptUseCase(userPreferencesRepository)
    }

    @Test
    fun `invoke true enables biometric and marks prompt as shown`() = runTest {
        useCase(true)

        val userData = userPreferencesRepository.userData.value
        assertTrue(userData.isBiometricEnabled)
        assertTrue(userData.hasAskedToEnableBiometric)
    }

    @Test
    fun `invoke false leaves biometric disabled but still marks prompt as shown`() = runTest {
        useCase(false)

        val userData = userPreferencesRepository.userData.value
        assertFalse(userData.isBiometricEnabled)
        assertTrue(userData.hasAskedToEnableBiometric)
    }
}
