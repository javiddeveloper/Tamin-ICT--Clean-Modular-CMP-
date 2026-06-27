package com.tamin.taminhamrah.useCases.contracts

import app.cash.turbine.test
import com.tamin.taminhamrah.model.contracts.RegistrationContactDN
import com.tamin.taminhamrah.model.contracts.RegistrationInfoDN
import com.tamin.taminhamrah.model.contracts.RegistrationPersonalInfoDN
import com.tamin.taminhamrah.repository.contracts.FakeContractsRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetRegistrationInfoUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeContractsRepository
    private lateinit var useCase: GetRegistrationInfoUseCase

    @BeforeTest
    fun setup() {
        repository = FakeContractsRepository()
        useCase = GetRegistrationInfoUseCase(repository)
    }

    @Test
    fun `invoke should return registration info from repository`() = runTest {
        val expectedInfo = RegistrationInfoDN(
            personalInfo = RegistrationPersonalInfoDN(
                firstName = "علی",
                lastName = "احمدی",
                nationalId = "1234567890",
                dateOfBirth = 631152000000L,
                genderCode = "01",
                genderDesc = "مرد",
                ssn = "2487741923",
            ),
            insuranceIdValidity = true,
            mobileNumber = "09121234567",
            insuranceId = "12345678901",
            lastContact = RegistrationContactDN(
                address = "تهران",
                zipCode = "1234567890",
                mobile = "09121234567",
                phoneNumber = "02112345678",
            ),
        )
        repository.registrationInfoResult = expectedInfo

        useCase().test {
            assertEquals(expectedInfo, awaitItem())
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
