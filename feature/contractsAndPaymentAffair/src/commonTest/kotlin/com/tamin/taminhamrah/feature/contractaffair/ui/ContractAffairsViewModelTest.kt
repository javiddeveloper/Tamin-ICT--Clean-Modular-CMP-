package com.tamin.taminhamrah.feature.contractaffair.ui

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.contractaffair.fake.FakeCommonRepository
import com.tamin.taminhamrah.feature.contractaffair.fake.FakeContractAffairRepository
import com.tamin.taminhamrah.feature.contractaffair.fake.FakeFeatureManager
import com.tamin.taminhamrah.feature.contractaffair.ui.contract.ContractAffairsIntent
import com.tamin.taminhamrah.feature.contractaffair.ui.contract.ContractAffairsUiState
import com.tamin.taminhamrah.feature.contractaffair.ui.contract.ContractOperation
import com.tamin.taminhamrah.feature.contractaffair.ui.contract.ContractSearchFilter
import com.tamin.taminhamrah.model.contractAffair.ContractPR
import com.tamin.taminhamrah.model.contractAffair.ContractStateDN
import com.tamin.taminhamrah.model.contractAffair.ContractStatePR
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.useCases.common.GetMainMenuUseCase
import com.tamin.taminhamrah.useCases.contractAffair.CancelContractUseCase
import com.tamin.taminhamrah.useCases.contractAffair.DownloadContractReportUseCase
import com.tamin.taminhamrah.useCases.contractAffair.GetContractStatesUseCase
import com.tamin.taminhamrah.useCases.contractAffair.GetContractsPageUseCase
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ContractAffairsViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeContractAffairRepository
    private lateinit var commonRepository: FakeCommonRepository
    private lateinit var featureManager: FakeFeatureManager

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = FakeContractAffairRepository()
        commonRepository = FakeCommonRepository()
        featureManager = FakeFeatureManager()
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(): ContractAffairsViewModel = ContractAffairsViewModel(
        getContractsPageUseCase = GetContractsPageUseCase(repository),
        getContractStatesUseCase = GetContractStatesUseCase(repository),
        cancelContractUseCase = CancelContractUseCase(repository),
        downloadContractReportUseCase = DownloadContractReportUseCase(repository),
        getMainMenuUseCase = GetMainMenuUseCase(commonRepository),
        featureManager = featureManager,
    )

    @Test
    fun `the list loads on init and an empty page reaches its end`() = runTest(dispatcher) {
        repository.contractsPageResult = PageDN(items = emptyList(), total = 0)

        viewModel().uiState.test {
            val state = awaitUntil { !it.isLoading && it.endReached }

            assertTrue(state.contracts.isEmpty())
            assertNull(state.paginationError)
            assertNotNull(repository.lastPageQuery)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ApplySearch records the criteria and refreshes with a contract-number filter`() =
        runTest(dispatcher) {
            val vm = viewModel()

            vm.uiState.test {
                awaitUntil { !it.isLoading }

                vm.sendIntent(ContractAffairsIntent.ApplySearch("123456", ContractSearchFilter.OPTIONAL))
                val state = awaitUntil { it.isSearchActive }

                assertEquals("123456", state.searchContractNumber)
                assertEquals(ContractSearchFilter.OPTIONAL, state.searchFilter)
                assertTrue(
                    repository.lastPageQuery?.filters?.any { it.value == "123456" } == true,
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `ClearSearch resets the criteria`() = runTest(dispatcher) {
        val vm = viewModel()

        vm.uiState.test {
            awaitUntil { !it.isLoading }
            vm.sendIntent(ContractAffairsIntent.ApplySearch("123456", ContractSearchFilter.FREELANCE))
            awaitUntil { it.isSearchActive }

            vm.sendIntent(ContractAffairsIntent.ClearSearch)
            val state = awaitUntil { !it.isSearchActive }

            assertEquals("", state.searchContractNumber)
            assertEquals(ContractSearchFilter.ALL, state.searchFilter)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `an active freelance contract exposes every operation`() = runTest(dispatcher) {
        val vm = viewModel()
        val contract = contractPr(statusCode = 1, premiumTypeCode = "01", freeJobCode = "555")

        vm.uiState.test {
            awaitUntil { !it.isLoading }

            vm.sendIntent(ContractAffairsIntent.ShowContractOperations(contract))
            val state = awaitUntil { it.operationsContract != null }

            assertEquals(
                listOf(
                    ContractOperation.PAY_PREMIUM,
                    ContractOperation.VIEW_PAYMENTS,
                    ContractOperation.EDIT_CONTRACT,
                    ContractOperation.VIEW_CONTRACT,
                    ContractOperation.DEACTIVATE,
                ),
                state.operations.toList(),
            )

            vm.sendIntent(ContractAffairsIntent.DismissContractOperations)
            assertNull(awaitUntil { it.operationsContract == null }.operationsContract)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `an inactive contract only offers مشاهده قرارداد`() = runTest(dispatcher) {
        val vm = viewModel()
        val contract = contractPr(statusCode = 9, premiumTypeCode = "01", freeJobCode = "555")

        vm.uiState.test {
            awaitUntil { !it.isLoading }
            vm.sendIntent(ContractAffairsIntent.ShowContractOperations(contract))
            val state = awaitUntil { it.operationsContract != null }

            assertEquals(listOf(ContractOperation.VIEW_CONTRACT), state.operations.toList())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `the deactivate flow loads reasons and succeeds`() = runTest(dispatcher) {
        repository.contractStatesResult = listOf(
            ContractStateDN(code = 5, description = "ابطال به درخواست بیمه‌شده"),
        )
        val vm = viewModel()
        val contract = contractPr(statusCode = 1, premiumTypeCode = "01", freeJobCode = "555")

        vm.uiState.test {
            awaitUntil { !it.isLoading }

            vm.sendIntent(ContractAffairsIntent.OnOperationClick(contract, ContractOperation.DEACTIVATE))
            val opened = awaitUntil { it.showCancelSheet && it.cancelReasons.isNotEmpty() }
            assertEquals(5, opened.cancelReasons.first().code)

            vm.sendIntent(
                ContractAffairsIntent.OnCancelReasonSelected(
                    ContractStatePR(code = 5, title = "ابطال به درخواست بیمه‌شده"),
                ),
            )
            vm.sendIntent(ContractAffairsIntent.OnCancelDescriptionChanged("دیگر نیازی نیست"))
            awaitUntil { it.selectedCancelReason != null && it.cancelDescription.isNotEmpty() }

            vm.sendIntent(ContractAffairsIntent.ConfirmCancelContract)
            val done = awaitUntil { it.showCancelSuccess }

            assertFalse(done.showCancelSheet)
            assertTrue(repository.cancelContractCalled)
            assertEquals(5, repository.lastCancelParams?.stateCode)
            assertEquals("دیگر نیازی نیست", repository.lastCancelParams?.description)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private suspend fun ReceiveTurbine<ContractAffairsUiState>.awaitUntil(
        predicate: (ContractAffairsUiState) -> Boolean,
    ): ContractAffairsUiState {
        var state = awaitItem()
        while (!predicate(state)) state = awaitItem()
        return state
    }
}

private fun contractPr(
    statusCode: Int?,
    premiumTypeCode: String,
    freeJobCode: String,
): ContractPR = ContractPR(
    contractNumber = "987654",
    statusDesc = "قرارداد فعال",
    isActive = true,
    requestDate = "1405/06/01",
    insuranceType = "حرف و مشاغل آزاد",
    monthlyPremiumLabel = "",
    monthlyIncome = "100000000",
    treatmentSupportText = "حمایت درمان دارد",
    hasTreatmentSupport = true,
    jobTitle = "رانندهٔ تاکسی",
    premiumTypeCode = premiumTypeCode,
    statusCode = statusCode,
    freeJobCode = freeJobCode,
)
