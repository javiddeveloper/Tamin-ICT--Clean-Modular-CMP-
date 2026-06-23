package com.tamin.taminhamrah.useCases.personal

import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoDN
import com.tamin.taminhamrah.repository.FakePersonalRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetDeceasedInfoUseCaseTest : BaseUseCaseTest() {
    private lateinit var personalRepository: FakePersonalRepository
    private lateinit var useCase: GetDeceasedInfoUseCase

    @BeforeTest
    fun setup() {
        personalRepository = FakePersonalRepository()
        useCase = GetDeceasedInfoUseCase(personalRepository)
    }

    @Test
    fun `invoke should return deceased info when successful`() = runTest {
        // Given
        val nationalId = "1234567890"
        val expectedDeceasedInfo = DeceasedInfoDN(
            branchCode = "123",
            branchName = "Test Branch",
            deadDate = "1402/01/01",
            insuranceId = "987654321",
            pensionerId = "p123",
            personal = null,
            yearsAge = "70",
            monthsAge = "0",
            daysAge = "0",
            related = "Father"
        )
        personalRepository.deceasedInfoResult = expectedDeceasedInfo

        // When
        val result = useCase(nationalId).first()

        // Then
        assertEquals(expectedDeceasedInfo, result)
    }

    @Test
    fun `invoke should throw error when repository fails`() = runTest {
        // Given
        val nationalId = "1234567890"
        personalRepository.shouldThrowError = true
        val expectedError = RuntimeException("Network Error")
        personalRepository.error = expectedError

        // When & Then
        assertFailsWith<RuntimeException> {
            useCase(nationalId).first()
        }
    }
}
