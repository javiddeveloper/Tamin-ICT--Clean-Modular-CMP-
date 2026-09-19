package com.tamin.taminhamrah.useCases.common

import app.cash.turbine.test
import com.tamin.taminhamrah.model.common.JobTitleDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.common.FakeCommonRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetJobTitlePageUseCaseTest : BaseUseCaseTest() {

    private lateinit var commonRepository: FakeCommonRepository
    private lateinit var useCase: GetJobTitlePageUseCase

    @BeforeTest
    fun setup() {
        commonRepository = FakeCommonRepository()
        useCase = GetJobTitlePageUseCase(commonRepository)
    }

    @Test
    fun `invoke should return job title page from repository`() = runTest {
        val expectedPage = PageDN(
            items = listOf(
                JobTitleDN(jobCode = "1", jobDescription = "Developer", status = "Active", statusDate = ""),
                JobTitleDN(jobCode = "2", jobDescription = "Manager", status = "Active", statusDate = ""),
            ),
            total = 250,
        )
        commonRepository.jobTitlePageResult = expectedPage

        useCase.invoke(ApiQueryParamDN(page = 0, limit = 10)).test {
            val result = awaitItem()
            assertEquals(2, result.items.size)
            assertEquals("Developer", result.items[0].jobDescription)
            assertEquals(250, result.total)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Search Error")
        commonRepository.shouldThrowError = true
        commonRepository.getJobTitleError = expectedException

        useCase.invoke(ApiQueryParamDN()).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
