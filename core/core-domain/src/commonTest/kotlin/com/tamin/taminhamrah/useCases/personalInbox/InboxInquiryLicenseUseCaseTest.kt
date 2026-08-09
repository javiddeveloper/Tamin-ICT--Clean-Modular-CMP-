package com.tamin.taminhamrah.useCases.personalInbox

import com.tamin.taminhamrah.repository.personalInbox.FakePersonalInboxRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class InboxInquiryLicenseUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakePersonalInboxRepository
    private lateinit var useCase: InboxInquiryLicenseUseCase

    @BeforeTest
    fun setup() {
        repository = FakePersonalInboxRepository()
        useCase = InboxInquiryLicenseUseCase(repository)
    }

    @Test
    fun `invoke should call inboxInquiryLicense on repository`() = runTest {
        val requestId = "req-123"
        val operation = "verify"
        val duration = "12"

        useCase(requestId, operation, duration).collect()

        assertEquals(Triple(requestId, operation, duration), repository.lastInquiryParams)
    }

    @Test
    fun `invoke should throw error when repository fails`() = runTest {
        val requestId = "req-123"
        val expectedException = RuntimeException("Inquiry failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        val actualException = assertFailsWith<RuntimeException> {
            useCase(requestId, "op", "dur").collect()
        }
        assertEquals(expectedException.message, actualException.message)
    }
}
