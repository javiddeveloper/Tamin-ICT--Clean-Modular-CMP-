package com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.sms

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.workshops.fake.FakeWorkShopsRepository
import com.tamin.taminhamrah.model.util.PagedListDN
import com.tamin.taminhamrah.model.workshop.SmsMessageDN
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionStatus
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionType
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.useCases.workshops.GetWorkShopObjectionSmsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * پیامک‌های an objection — paging (load more appends, not replaces) and the "re-opening the same
 * seqNo does not refetch" shortcut the sibling-screen navigation relies on to keep the already-
 * loaded page when the user bounces between سند اعتراض and پیامک‌ها.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ObjectionSmsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeWorkShopsRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeWorkShopsRepository()
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = ObjectionSmsViewModel(GetWorkShopObjectionSmsUseCase(repository))

    private fun open(seqNo: Long = 1001L) = ObjectionSmsIntent.Open(
        seqNo = seqNo,
        debitNumber = "88214",
        objectionType = WorkShopObjectionType.ESTIMATE,
        objectionStatus = WorkShopObjectionStatus.SUBMITTED,
    )

    @Test
    fun `opening loads the first page for that seqNo`() = runTest(testDispatcher) {
        repository.objectionSms = smsPage(count = 2, total = 2)
        val viewModel = viewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(open(seqNo = 1001L))
            val loaded = awaitUntil { it.list.items.isNotEmpty() }

            assertEquals(2, loaded.list.items.size)
            assertEquals(1001L, repository.lastObjectionSmsSeqNo)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `load more appends rows instead of replacing them`() = runTest(testDispatcher) {
        repository.objectionSms = smsPage(count = 10, total = 20, startAt = 0)
        val viewModel = viewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(open(seqNo = 1001L))
            awaitUntil { it.list.items.size == 10 }

            repository.objectionSms = smsPage(count = 10, total = 20, startAt = 10)
            viewModel.sendIntent(ObjectionSmsIntent.LoadMore)
            val afterLoadMore = awaitUntil { it.list.items.size > 10 }

            assertEquals(20, afterLoadMore.list.items.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `re-opening the same seqNo with a page already loaded does not refetch`() = runTest(testDispatcher) {
        repository.objectionSms = smsPage(count = 2, total = 2)
        val viewModel = viewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(open(seqNo = 1001L))
            awaitUntil { it.list.items.isNotEmpty() }

            // A second page staged in the repository would show up in state if Open re-fetched.
            repository.objectionSms = smsPage(count = 5, total = 5)
            viewModel.sendIntent(open(seqNo = 1001L))

            assertEquals(2, viewModel.uiState.value.list.items.size)
            expectNoEvents()
        }
    }

    @Test
    fun `a refused load surfaces the service's own wording`() = runTest(testDispatcher) {
        repository.error = TaminApiException(title = "دسترسی مجاز نیست")
        val viewModel = viewModel()

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(open())
            val failed = awaitUntil { it.list.error != null }

            assertEquals("دسترسی مجاز نیست", failed.list.error)
            assertTrue(!failed.list.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun smsPage(count: Int, total: Int, startAt: Int = 0) = PagedListDN(
        items = List(count) {
            SmsMessageDN(
                id = (startAt + it + 1).toLong(),
                description = "پیامک ${startAt + it + 1}",
                status = WorkShopObjectionStatus.SUBMITTED,
            )
        },
        total = total,
    )

    private suspend fun ReceiveTurbine<ObjectionSmsUiState>.awaitUntil(
        predicate: (ObjectionSmsUiState) -> Boolean,
    ): ObjectionSmsUiState {
        while (true) {
            val item = awaitItem()
            if (predicate(item)) return item
        }
    }
}
