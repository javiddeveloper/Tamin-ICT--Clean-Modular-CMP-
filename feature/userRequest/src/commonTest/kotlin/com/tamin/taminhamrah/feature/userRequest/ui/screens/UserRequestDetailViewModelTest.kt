package com.tamin.taminhamrah.feature.userRequest.ui.screens

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.userRequest.ui.screens.contract.UserRequestDetailIntent
import com.tamin.taminhamrah.model.userRequest.DeferredInstallmentDetailDN
import com.tamin.taminhamrah.model.userRequest.UserRequestDetailsDN
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeIds
import com.tamin.taminhamrah.useCases.userRequest.GetShowRequestInfoUseCase
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

@OptIn(ExperimentalCoroutinesApi::class)
class UserRequestDetailViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeUserRequestRepository
    private lateinit var viewModel: UserRequestDetailViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeUserRequestRepository()
        viewModel = UserRequestDetailViewModel(
            getShowRequestInfoUseCase = GetShowRequestInfoUseCase(repository),
        )
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `load detail calls show request info with list reference id not request header`() = runTest(testDispatcher) {
        repository.showRequestInfoResult = UserRequestDetailsDN(
            deferredInstallment = DeferredInstallmentDetailDN(
                borrowerName = "علی محمدی",
                pensionerFirstName = "کاربر",
                pensionerLastName = "کاربری",
            )
        )

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(
                UserRequestDetailIntent.LoadDetail(
                    requestId = 491371155L,
                    refCode = "1075558440",
                    requestTypeId = UserRequestTypeIds.DEFERRED_INSTALLMENT,
                    title = "گواهی کسر اقساط معوق",
                    referenceId = "491371155",
                )
            )
            var state = awaitItem()
            while (state.request == null) state = awaitItem()

            assertFalse(state.isLoading)
            assertNotNull(state.request)
            assertEquals("علی محمدی", state.request?.details?.deferredInstallment?.borrowerName)
            assertEquals("491371155", repository.lastReferenceId)
            assertNull(repository.lastRequestId)
        }
    }

    @Test
    fun `load detail falls back to request id when reference id is blank`() = runTest(testDispatcher) {
        repository.showRequestInfoResult = UserRequestDetailsDN()

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(
                UserRequestDetailIntent.LoadDetail(
                    requestId = 491371155L,
                    refCode = "1075558440",
                    requestTypeId = UserRequestTypeIds.DEFERRED_INSTALLMENT,
                    title = "گواهی کسر اقساط معوق",
                    referenceId = "",
                )
            )
            var state = awaitItem()
            while (state.request == null) state = awaitItem()

            assertEquals("491371155", repository.lastReferenceId)
            assertNull(repository.lastRequestId)
        }
    }
}
