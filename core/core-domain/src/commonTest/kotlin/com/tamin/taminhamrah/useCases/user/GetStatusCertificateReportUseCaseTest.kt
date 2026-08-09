package com.tamin.taminhamrah.useCases.user

import app.cash.turbine.test
import com.tamin.taminhamrah.repository.FakeUserRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetStatusCertificateReportUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeUserRepository
    private lateinit var useCase: GetStatusCertificateReportUseCase

    @BeforeTest
    fun setup() {
        repository = FakeUserRepository()
        useCase = GetStatusCertificateReportUseCase(repository)
    }

    @Test
    fun `invoke should return certificate report`() = runTest {
        val expected = "OK"
        repository.statusCertificateReportResult = expected

        useCase(emptyList()).test {
            val result = awaitItem()
            assertEquals("OK", result)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should throw error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase(emptyList()).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
