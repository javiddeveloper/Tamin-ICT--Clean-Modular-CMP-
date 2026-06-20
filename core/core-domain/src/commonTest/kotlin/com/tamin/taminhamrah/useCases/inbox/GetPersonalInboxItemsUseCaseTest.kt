package com.tamin.taminhamrah.useCases.inbox

import app.cash.turbine.test
import com.tamin.taminhamrah.model.inbox.InboxPermissionDN
import com.tamin.taminhamrah.model.inbox.InboxTypeDN
import com.tamin.taminhamrah.model.inbox.PersonalInboxItemDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.inbox.FakePersonalInboxRepository
import com.tamin.taminhamrah.useCases.BaseUseCaseTest
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetPersonalInboxItemsUseCaseTest : BaseUseCaseTest() {

    private lateinit var repository: FakePersonalInboxRepository
    private lateinit var useCase: GetPersonalInboxItemsUseCase

    @BeforeTest
    fun setup() {
        repository = FakePersonalInboxRepository()
        useCase = GetPersonalInboxItemsUseCase(repository)
    }

    @Test
    fun `invoke should return inbox items from repository`() = runTest {
        val expectedList = listOf(sampleInboxItem())
        repository.inboxItemsResult = expectedList

        useCase().test {
            val result = awaitItem()
            assertEquals(expectedList, result)
            awaitComplete()
        }

        assertEquals(GetPersonalInboxItemsUseCase.defaultQuery(), repository.lastQuery)
    }

    @Test
    fun `invoke should pass query to repository`() = runTest {
        val query = ApiQueryParamDN(page = 1, start = 10, limit = 20)
        repository.inboxItemsResult = emptyList()

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

        useCase().test {
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
