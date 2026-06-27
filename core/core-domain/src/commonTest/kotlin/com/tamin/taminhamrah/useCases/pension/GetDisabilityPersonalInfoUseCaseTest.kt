package com.tamin.taminhamrah.useCases.pension

import com.tamin.taminhamrah.model.personal.DisabilityPersonalInfoDN
import com.tamin.taminhamrah.model.personal.DisabilityPersonalDN
import com.tamin.taminhamrah.model.personal.DisabilityWorkDN
import com.tamin.taminhamrah.repository.pension.FakePensionRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetDisabilityPersonalInfoUseCaseTest {

    private lateinit var getDisabilityPersonalInfoUseCase: GetDisabilityPersonalInfoUseCase
    private lateinit var fakePensionRepository: FakePensionRepository

    @BeforeTest
    fun setUp() {
        fakePensionRepository = FakePensionRepository()
        getDisabilityPersonalInfoUseCase = GetDisabilityPersonalInfoUseCase(fakePensionRepository)
    }

    @Test
    fun `invoke should return disability personal info from repository`() = runTest {
        val expectedInfo = DisabilityPersonalInfoDN(
            branch = "1",
            branchName = "Branch 1",
            confirmed = true,
            insuranceId = "123",
            mobileNumber = "09121234567",
            personal = DisabilityPersonalDN(
                firstName = "Ali",
                lastName = "Alavi",
                nationalId = "0012345678",
                fatherName = "Reza",
                idCardNumber = "1234",
                cityOfIssue = "Tehran",
                dateOfBirth = 315532800000L,
                genderDesc = "مرد"
            ),
            provinceName = "Tehran",
            work = DisabilityWorkDN(
                jobDescription = "Developer",
                workshopId = "999"
            ),
            yearsAge = "40",
            monthsAge = "0",
            daysAge = "0",
            strAge = "40 years"
        )
        fakePensionRepository.disabilityPersonalInfoResult = expectedInfo

        val result = getDisabilityPersonalInfoUseCase().first()

        assertEquals(expectedInfo, result)
    }
    @Test
    fun `invoke should throw error when repository fails`() = runTest {
        val expectedError = RuntimeException("Error occurred")
        fakePensionRepository.shouldThrowError = true
        fakePensionRepository.error = expectedError

        val exception = assertFailsWith<RuntimeException> {
            getDisabilityPersonalInfoUseCase().first()
        }
        assertEquals(expectedError.message, exception.message)
    }
}
