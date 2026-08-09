package com.tamin.taminhamrah.useCases.personalInbox

import com.tamin.taminhamrah.model.inbox.PersonalInboxItemDN
import com.tamin.taminhamrah.repository.personalInbox.FakePersonalInboxRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetMyRequestPdfUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakePersonalInboxRepository
    private lateinit var useCase: GetMyRequestPdfUseCase

    @BeforeTest
    fun setup() {
        repository = FakePersonalInboxRepository()
        useCase = GetMyRequestPdfUseCase(repository)
    }

    @Test
    fun `invoke should return pdf item from repository`() = runTest {
        val requestId = "req-123"
        val expectedItem = sampleInboxItem()
        repository.inboxItemsResult = listOf(expectedItem)

        val result = useCase(requestId)

        assertEquals(expectedItem, result)
    }

    @Test
    fun `invoke should throw error when repository fails`() = runTest {
        val requestId = "req-123"
        val expectedException = RuntimeException("PDF failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        val actualException = assertFailsWith<RuntimeException> {
            useCase(requestId)
        }
        assertEquals(expectedException.message, actualException.message)
    }

    private fun sampleInboxItem() = PersonalInboxItemDN(
        id = 12345L,
        nationalCode = "0946168113",
        mobileNumber = "09123456789",
        email = null,
        read = "0",
        data = null,
        sentDate = 1780398668987L,
        receiveDate = 1780398668987L,
        seenDate = null,
        seen = false,
        hasImage = false,
        hasText = true,
        hasPdf = false,
        updateable = true,
        status = "1",
        referenceId = null,
        pdf = null,
        type = null,
        subType = null,
        permission = null
    )
}
