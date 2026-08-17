package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.model.FontSizeOption
import com.tamin.taminhamrah.repository.FakeUserPreferencesRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SetFontSizeUseCaseTest : BaseUseCaseTest() {

    private lateinit var userPreferencesRepository: FakeUserPreferencesRepository
    private lateinit var useCase: SetFontSizeUseCase

    @BeforeTest
    fun setup() {
        userPreferencesRepository = FakeUserPreferencesRepository()
        useCase = SetFontSizeUseCase(userPreferencesRepository)
    }

    @Test
    fun `invoke persists selected font size`() = runTest {
        useCase(FontSizeOption.LARGE)

        assertEquals(FontSizeOption.LARGE, userPreferencesRepository.userData.value.fontSize)
    }

    @Test
    fun `invoke overwrites a previously persisted font size`() = runTest {
        useCase(FontSizeOption.LARGE)
        useCase(FontSizeOption.SMALL)

        assertEquals(FontSizeOption.SMALL, userPreferencesRepository.userData.value.fontSize)
    }

    @Test
    fun `invoke defaults to MEDIUM before any selection`() = runTest {
        assertEquals(FontSizeOption.MEDIUM, userPreferencesRepository.userData.value.fontSize)
    }
}
