package com.tamin.taminhamrah.useCases.personal

import app.cash.turbine.test
import com.tamin.taminhamrah.model.personal.PersonalDN
import com.tamin.taminhamrah.model.personal.PersonalInfoDN
import com.tamin.taminhamrah.repository.FakePersonalRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetPersonalInfoUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakePersonalRepository
    private lateinit var useCase: GetPersonalInfoUseCase

    @BeforeTest
    fun setup() {
        repository = FakePersonalRepository()
        useCase = GetPersonalInfoUseCase(repository)
    }

    @Test
    fun `invoke should return personal info from repository`() = runTest {
        val expectedInfo = PersonalInfoDN(
            insuranceId = "123",
            branch = "Branch 1",
            mobileNumber = "09123456789",
            provinceName = "Province",
            personal = PersonalDN(
                firstName = "John",
                lastName = "Doe",
                fatherName = "Father",
                nationalId = "1234567890",
                ssn = "111",
                genderDesc = "Male",
                dateOfBirth = 123456789L
            )
        )
        repository.personalInfoResult = expectedInfo

        useCase().test {
            val result = awaitItem()
            assertEquals(expectedInfo, result)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase().test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
