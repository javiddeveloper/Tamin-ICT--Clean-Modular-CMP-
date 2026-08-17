package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.repository.FakeUserPreferencesRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SetBiometricEnabledUseCaseTest : BaseUseCaseTest() {

    private lateinit var userPreferencesRepository: FakeUserPreferencesRepository
    private lateinit var useCase: SetBiometricEnabledUseCase

    @BeforeTest
    fun setup() {
        userPreferencesRepository = FakeUserPreferencesRepository()
        useCase = SetBiometricEnabledUseCase(userPreferencesRepository)
    }

    @Test
    fun `invoke true persists biometric enabled flag`() = runTest {
        useCase(true)

        assertTrue(userPreferencesRepository.userData.value.isBiometricEnabled)
    }

    @Test
    fun `invoke false persists biometric disabled flag`() = runTest {
        useCase(true)
        useCase(false)

        assertFalse(userPreferencesRepository.userData.value.isBiometricEnabled)
    }

    @Test
    fun `invoke does not touch hasAskedToEnableBiometric`() = runTest {
        useCase(true)

        assertEquals(false, userPreferencesRepository.userData.value.hasAskedToEnableBiometric)
    }
}
