package com.tamin.taminhamrah.useCases.pension

import app.cash.turbine.test
import com.tamin.taminhamrah.model.pension.retirementInfo.RetirementRequestDN
import com.tamin.taminhamrah.repository.pension.FakePensionRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetRetirementRequestInfoUseCaseTest : BaseUseCaseTest() {

    private lateinit var pensionRepository: FakePensionRepository
    private lateinit var useCase: GetRetirementRequestInfoUseCase

    @BeforeTest
    fun setup() {
        pensionRepository = FakePensionRepository()
        useCase = GetRetirementRequestInfoUseCase(pensionRepository)
    }

    @Test
    fun `invoke should return retirement request list from repository`() = runTest {
        val expectedList = listOf(
            RetirementRequestDN(
                activityType = "Type A",
                address = "Address",
                age = "60",
                birthDate = 123456789L,
                branchCode = "123",
                fatherName = "Father",
                firstName = "First",
                gender = "Male",
                insuranceNumber = "456",
                issuePlace = "Place",
                idNumber = "789",
                lastName = "Last",
                mobileNumber = "0912",
                nationalCode = "001",
                phoneNumber = "021",
                workshopAddress = "Work Address",
                workshopCode = "W123",
                workshopName = "Work Name",
                managerName = "Manager"
            )
        )
        pensionRepository.retirementRequestInfoResult = expectedList

        useCase.invoke(emptyList()).test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertEquals("First", result[0].firstName)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error`() = runTest {
        val expectedException = RuntimeException("get retirement request failed")
        pensionRepository.shouldThrowError = true
        pensionRepository.error = expectedException

        useCase.invoke(emptyList()).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
