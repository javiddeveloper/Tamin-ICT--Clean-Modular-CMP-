package com.tamin.taminhamrah.feature.userRequest.ui.screens

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.userRequest.ui.screens.contract.UserRequestDetailIntent
import com.tamin.taminhamrah.model.userRequest.DeferredInstallmentDetailDN
import com.tamin.taminhamrah.model.userRequest.UserRequestDN
import com.tamin.taminhamrah.model.userRequest.UserRequestDetailsDN
import com.tamin.taminhamrah.model.userRequest.UserRequestStatusDN
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeDN
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeIds
import com.tamin.taminhamrah.useCases.userRequest.GetShowRequestInfoUseCase
import com.tamin.taminhamrah.useCases.userRequest.GetUserRequestDetailUseCase
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
            getUserRequestDetailUseCase = GetUserRequestDetailUseCase(repository),
            getShowRequestInfoUseCase = GetShowRequestInfoUseCase(repository),
        )
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `load detail overlays show request info onto header`() = runTest(testDispatcher) {
        repository.userRequestDetailResult = UserRequestDN(
            id = 491371155L,
            refCode = "1075558440",
            title = "گواهی کسر اقساط معوق",
            comment = null,
            creationTime = 1785215695428L,
            createByName = "سیدرحمت اله میرفضلی",
            status = UserRequestStatusDN("0018", "تایید نهایی"),
            requestType = UserRequestTypeDN(22L, "گواهی کسر اقساط معوق", null),
            referenceId = "req-22",
            requestDetails = null,
        )
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
                )
            )
            var state = awaitItem()
            while (state.request == null) state = awaitItem()

            assertFalse(state.isLoading)
            assertNotNull(state.request)
            assertEquals("علی محمدی", state.request?.details?.deferredInstallment?.borrowerName)
            assertEquals("req-22", repository.lastReferenceId)
        }
    }
}
