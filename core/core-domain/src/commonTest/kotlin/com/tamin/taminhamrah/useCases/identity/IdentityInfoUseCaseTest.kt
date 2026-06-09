package com.tamin.taminhamrah.useCases.identity

import app.cash.turbine.test
import com.tamin.taminhamrah.model.identity.IdentityInfoDN
import com.tamin.taminhamrah.repository.FakeCityProvinceRepository
import com.tamin.taminhamrah.repository.FakeUserRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class IdentityInfoUseCaseTest : BaseUseCaseTest() {

    private lateinit var userRepository: FakeUserRepository
    private lateinit var cityProvinceRepository: FakeCityProvinceRepository
    private lateinit var useCase: IdentityInfoUseCase

    @BeforeTest
    fun setup() {
        userRepository = FakeUserRepository()
        cityProvinceRepository = FakeCityProvinceRepository()
        useCase = IdentityInfoUseCase(userRepository, cityProvinceRepository)
    }

    @Test
    fun `invoke should return identity info from repository`() = runTest {
        val expectedInfo = IdentityInfoDN(
            firstName = "John",
            lastName = "Doe",
            cityOfBirthId = null,
            cityOfIssueId = null,
            countryId = null,
            dateOfBirth = null,
            fatherName = null,
            gender = null,
            id = null,
            idCardNumber = null,
            idCardSerial1 = null,
            idCardSerial2 = null,
            nationalId = "1234567890",
            ssn = null
        )
        userRepository.identityInfoResult = expectedInfo

        useCase.invoke().test {
            val result = awaitItem()
            assertEquals("John", result.firstName)
            assertEquals("Doe", result.lastName)
            awaitComplete()
        }
    }
}
