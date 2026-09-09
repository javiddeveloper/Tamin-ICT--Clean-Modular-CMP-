package com.tamin.taminhamrah.useCases.personal

import com.tamin.taminhamrah.model.personal.survivorDependent.SurvivorDependentDN
import com.tamin.taminhamrah.repository.FakePersonalRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetSurvivorListUseCaseTest : BaseUseCaseTest() {
    private lateinit var personalRepository: FakePersonalRepository
    private lateinit var useCase: GetSurvivorListUseCase

    @BeforeTest
    fun setup() {
        personalRepository = FakePersonalRepository()
        useCase = GetSurvivorListUseCase(personalRepository)
    }

    @Test
    fun `invoke should return survivor list when successful`() = runTest {
        val expected = listOf(
            SurvivorDependentDN(
                firstName = "Ali",
                lastName = "Test",
                nationalId = "0012345678",
                fatherName = "Reza",
                idCardNumber = "123",
                cityOfIssue = "Tehran",
                genderCode = "01",
                genderDesc = "مرد",
                dateOfBirth = 0L,
                insuranceId = "ins-1",
                tendencyCode = "04",
            )
        )
        personalRepository.survivorListResult = expected

        assertEquals(expected, useCase("0011223344").first())
    }

    @Test
    fun `invoke should throw when repository fails`() = runTest {
        personalRepository.shouldThrowError = true

        assertFailsWith<RuntimeException> { useCase("0011223344").first() }
    }
}
