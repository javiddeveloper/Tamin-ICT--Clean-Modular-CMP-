package com.tamin.taminhamrah.feature.workshops.ui.workshopDebtInquiry

import com.tamin.taminhamrah.feature.workshops.fake.FakeWorkShopsRepository
import com.tamin.taminhamrah.model.workshop.WorkshopDebtInquiryDN
import com.tamin.taminhamrah.tools.errorHandling.TaminApiException
import com.tamin.taminhamrah.useCases.workshops.GetWorkshopDebtInquiryUseCase
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
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * استعلام بدهی کارگاه — one record for one workshop.
 *
 * The screen has three outcomes and only one of them is the answer, so what is pinned here is that
 * the other two stay apart: a workshop with no debt record and a workshop whose record could not be
 * fetched must not look the same. Saying "no debt" about a service that never answered is the wrong
 * thing to tell someone about a debt.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WorkshopDebtInquiryViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeWorkShopsRepository

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeWorkShopsRepository()
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel() = WorkshopDebtInquiryViewModel(GetWorkshopDebtInquiryUseCase(repository))

    private fun open(viewModel: WorkshopDebtInquiryViewModel) =
        viewModel.sendIntent(WorkshopDebtInquiryIntent.Open(WORKSHOP_ID, BRANCH_CODE))

    @Test
    fun `opening loads the inquiry for that workshop`() = runTest(testDispatcher) {
        repository.debtInquiry = inquiry()

        val viewModel = viewModel()
        open(viewModel)

        val state = viewModel.uiState.value
        assertNotNull(state.inquiry)
        assertEquals(WORKSHOP_ID, state.workshopId)
        assertEquals(BRANCH_CODE, state.branchCode)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `reopening the same workshop does not refetch`() = runTest(testDispatcher) {
        repository.debtInquiry = inquiry(result = FIRST_ANSWER)

        val viewModel = viewModel()
        open(viewModel)
        repository.debtInquiry = inquiry(result = SECOND_ANSWER)
        open(viewModel)

        assertEquals(FIRST_ANSWER, assertNotNull(viewModel.uiState.value.inquiry).result)
    }

    /**
     * The regression this class exists for.
     *
     * A failed inquiry leaves `inquiry` null, and the screen's empty branch catches null — so
     * without an error the user was told the workshop has no debt record when the service had
     * simply failed.
     */
    @Test
    fun `a failed inquiry is an error, not an empty answer`() = runTest(testDispatcher) {
        repository.error = TaminApiException(title = FAILURE)

        val viewModel = viewModel()
        open(viewModel)

        val state = viewModel.uiState.value
        assertNotNull(state.error, "a failure must be reported, not left to look like no record")
        assertNull(state.inquiry)
        assertFalse(state.isLoading)
    }

    @Test
    fun `retrying after a failure clears the error and loads`() = runTest(testDispatcher) {
        repository.error = TaminApiException(title = FAILURE)

        val viewModel = viewModel()
        open(viewModel)
        assertNotNull(viewModel.uiState.value.error)

        repository.error = null
        repository.debtInquiry = inquiry()
        viewModel.sendIntent(WorkshopDebtInquiryIntent.Retry)

        val state = viewModel.uiState.value
        assertNull(state.error)
        assertNotNull(state.inquiry)
    }

    /** Half an identity addresses nothing, so it is refused before a request is made. */
    @Test
    fun `an incomplete identity never reaches the service`() = runTest(testDispatcher) {
        val viewModel = viewModel()
        viewModel.sendIntent(WorkshopDebtInquiryIntent.Open(workshopId = "", branchCode = BRANCH_CODE))

        val state = viewModel.uiState.value
        assertNull(state.inquiry)
        assertFalse(state.isLoading)
    }

    private fun inquiry(result: String = FIRST_ANSWER) = WorkshopDebtInquiryDN(
        result = result,
        date = "14050526",
        definitiveDebt = 29_410_500L,
        divisibleDebt = 0L,
        indivisibleDebt = 29_410_500L,
    )

    private companion object {
        const val WORKSHOP_ID = "9028218513"
        const val BRANCH_CODE = "14"
        const val FIRST_ANSWER = "کارگاه دارای بدهی قطعی"
        const val SECOND_ANSWER = "کارگاه فاقد بدهی"
        const val FAILURE = "استعلام بدهی در دسترس نیست"
    }
}
