package com.tamin.taminhamrah.useCases.common

import app.cash.turbine.test
import com.tamin.taminhamrah.model.common.RecipientDN
import com.tamin.taminhamrah.repository.FakeCityProvinceRepository
import com.tamin.taminhamrah.repository.FakeGetRecipientRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetRecipientListUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeGetRecipientRepository
    private lateinit var useCase: GetRecipientListUseCase

    @BeforeTest
    fun setup() {
        repository = FakeGetRecipientRepository()
        useCase = GetRecipientListUseCase(repository)
    }

    @Test
    fun `invoke should return recipient list from repository`() = runTest {
        val expectedList = listOf(
            RecipientDN(recipientCode = "1", recipientName = "Name 1"),
            RecipientDN(recipientCode = "2", recipientName = "Name 2")
        )
        repository.recipientListResult = expectedList

        useCase().test {
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

        useCase().test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
