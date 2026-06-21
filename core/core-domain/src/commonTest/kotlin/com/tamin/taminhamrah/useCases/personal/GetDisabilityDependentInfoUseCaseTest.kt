package com.tamin.taminhamrah.useCases.personal

import app.cash.turbine.test
import com.tamin.taminhamrah.model.personal.DisabilityDependentDN
import com.tamin.taminhamrah.repository.FakePersonalRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetDisabilityDependentInfoUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakePersonalRepository
    private lateinit var useCase: GetDisabilityDependentInfoUseCase

    @BeforeTest
    fun setup() {
        repository = FakePersonalRepository()
        useCase = GetDisabilityDependentInfoUseCase(repository)
    }

    @Test
    fun `invoke should return disability dependent info from repository`() = runTest {
        val expectedList = listOf(
            DisabilityDependentDN(
                firstName = "John",
                lastName = "Doe",
                nationalId = "1234567890",
                dateOfBirth = 123456789L,
                fatherName = "Father",
                genderDesc = "Male",
                relation = "Son",
                tendencyDescription = "Tendency"
            )
        )
        repository.disabilityDependentInfoResult = expectedList

        useCase(emptyList()).test {
            val result = awaitItem()
            assertEquals(expectedList, result)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase(emptyList()).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
