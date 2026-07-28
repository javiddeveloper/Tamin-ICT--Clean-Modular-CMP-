package com.tamin.taminhamrah.useCases.common

import app.cash.turbine.test
import com.tamin.taminhamrah.model.common.JobTitleDN
import com.tamin.taminhamrah.model.common.JobTitleListDN
import com.tamin.taminhamrah.repository.common.FakeCommonRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetJobTitleUseCaseTest : BaseUseCaseTest() {

    private lateinit var commonRepository: FakeCommonRepository
    private lateinit var useCase: GetJobTitleUseCase

    @BeforeTest
    fun setup() {
        commonRepository = FakeCommonRepository()
        useCase = GetJobTitleUseCase(commonRepository)
    }

    @Test
    fun `invoke should return job title list from repository`() = runTest {
        val expectedList = JobTitleListDN(
            list = listOf(
                JobTitleDN(jobCode = "1", jobDescription = "Developer", status = "Active", statusDate = ""),
                JobTitleDN(jobCode = "2", jobDescription = "Manager", status = "Active", statusDate = "")
            ),
            total = 2
        )
        commonRepository.jobTitleResult = expectedList

        useCase.invoke(emptyList()).test {
            val result = awaitItem()
            assertEquals(2, result?.list?.size)
            assertEquals("Developer", result?.list?.get(0)?.jobDescription)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Search Error")
        commonRepository.shouldThrowError = true
        commonRepository.getBeneficiaryError = expectedException

        useCase.invoke(emptyList()).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
