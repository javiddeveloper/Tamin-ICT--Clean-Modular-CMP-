package com.tamin.taminhamrah.useCases.personalInbox

import app.cash.turbine.test
import com.tamin.taminhamrah.model.inbox.InboxPermissionDN
import com.tamin.taminhamrah.model.inbox.InboxTypeDN
import com.tamin.taminhamrah.model.inbox.PersonalInboxItemDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.personalInbox.FakePersonalInboxRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetPersonalInboxItemsPageUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakePersonalInboxRepository
    private lateinit var useCase: GetPersonalInboxItemsPageUseCase

    @BeforeTest
    fun setup() {
        repository = FakePersonalInboxRepository()
        useCase = GetPersonalInboxItemsPageUseCase(repository)
    }

    @Test
    fun `invoke should return the page and its total from repository`() = runTest {
        val expectedList = listOf(sampleInboxItem())
        repository.inboxItemsResult = expectedList
        repository.inboxItemsTotal = 42

        useCase(ApiQueryParamDN()).test {
            val page = awaitItem()
            assertEquals(expectedList, page.items)
            assertEquals(42, page.total)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should pass the page query to repository`() = runTest {
        val query = ApiQueryParamDN(page = 3, start = 20, limit = 10)

        useCase(query).test {
            awaitItem()
            awaitComplete()
        }

        assertEquals(query, repository.lastQuery)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        val expectedException = RuntimeException("Failed")
        repository.shouldThrowError = true
        repository.error = expectedException

        useCase(ApiQueryParamDN()).test {
            val actualException = awaitError()
            assertEquals(expectedException.message, actualException.message)
        }
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
        type = InboxTypeDN(typeDesc = "سیستم تامین", typeCode = "01"),
        subType = InboxTypeDN(typeDesc = "اعلامیه", typeCode = "02"),
        permission = InboxPermissionDN(
            password = 1234L,
            dateFrom = null,
            dateTo = 1780398668987L,
        ),
    )
}
