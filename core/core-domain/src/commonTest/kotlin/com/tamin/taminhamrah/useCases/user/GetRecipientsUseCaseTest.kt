package com.tamin.taminhamrah.useCases.user

import app.cash.turbine.test
import com.tamin.taminhamrah.model.certificate.RecipientDN
import com.tamin.taminhamrah.repository.FakeUserRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetRecipientsUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakeUserRepository
    private lateinit var useCase: GetRecipientsUseCase

    @BeforeTest
    fun setup() {
        repository = FakeUserRepository()
        useCase = GetRecipientsUseCase(repository)
    }

    @Test
    fun `invoke should return recipient list with correct data`() = runTest {
        val expected = listOf(
            RecipientDN(recipientCode = "001", recipientName = "دادگاه عمومي"),
            RecipientDN(recipientCode = "01", recipientName = "بانک رفاه کارگران")
        )
        repository.recipientsResult = expected

        useCase().test {
            val result = awaitItem()
            assertEquals(2, result.size)
            assertEquals("001", result[0].recipientCode)
            assertEquals("دادگاه عمومي", result[0].recipientName)
            assertEquals("01", result[1].recipientCode)
            assertEquals("بانک رفاه کارگران", result[1].recipientName)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should throw error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase().test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
    }
}
