package com.tamin.taminhamrah.feature.workshops.ui

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsIntent
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsUiState
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.common.ProvincePR
import com.tamin.taminhamrah.model.contracts.BranchPR
import com.tamin.taminhamrah.useCases.common.GetCitiesUseCase
import com.tamin.taminhamrah.useCases.common.GetProvincesUseCase
import com.tamin.taminhamrah.useCases.contracts.GetBranchesUseCase
import com.tamin.taminhamrah.useCases.workshops.GetAllEmployerAgreementByNationalIdUseCase
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
 * The استان → شهر → شعبه cascade only filters the workshop list through the branch code it
 * ends at, so a stale code left behind by an earlier selection would filter the list by a
 * branch the user can no longer see selected. These tests pin the clearing behavior.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WorkshopsCascadeResetTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var cityProvinceRepository: FakeCascadeCityProvinceRepository
    private lateinit var contractsRepository: FakeCascadeContractsRepository
    private lateinit var viewModel: WorkshopsViewModel

    private val tehran = ProvincePR(provinceCode = "07", provinceName = "تهران")
    private val esfahan = ProvincePR(provinceCode = "04", provinceName = "اصفهان")
    private val tehranCity = CityPR(cityCode = "0701", cityName = "تهران", provinceCode = "07")
    private val branchOne = BranchPR(code = "123", name = "شعبه ۱ تهران")

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        cityProvinceRepository = FakeCascadeCityProvinceRepository()
        contractsRepository = FakeCascadeContractsRepository()
        viewModel = WorkshopsViewModel(
            getAllEmployerAgreementUseCase = GetAllEmployerAgreementByNationalIdUseCase(
                FakeCascadeWorkShopsRepository(),
            ),
            getProvincesUseCase = GetProvincesUseCase(cityProvinceRepository),
            getCitiesUseCase = GetCitiesUseCase(cityProvinceRepository),
            getBranchesUseCase = GetBranchesUseCase(contractsRepository),
        )
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun selectingAProvinceLoadsItsCitiesAndLeavesCityAndBranchEmpty() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitItem()

            viewModel.sendIntent(WorkshopsIntent.SelectProvince(tehran))
            val state = awaitUntil { it.cities.isNotEmpty() }

            assertEquals("07", state.branchSelection.provinceCode)
            assertEquals("تهران", state.branchSelection.provinceName)
            assertEquals("", state.branchSelection.cityCode)
            assertEquals("", state.branchSelection.branchCode)
            assertEquals("07", cityProvinceRepository.lastRequestedProvinceCode)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun selectingACityLoadsItsBranchesAndClearsAnyPreviousBranch() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitItem()

            viewModel.sendIntent(WorkshopsIntent.SelectProvince(tehran))
            awaitUntil { it.cities.isNotEmpty() }
            viewModel.sendIntent(WorkshopsIntent.SelectCity(tehranCity))
            val state = awaitUntil { it.branches.isNotEmpty() }

            assertEquals("0701", state.branchSelection.cityCode)
            assertEquals("", state.branchSelection.branchCode)
            assertEquals("0701", contractsRepository.lastRequestedCityCode)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun reselectingAProvinceClearsTheCityAndBranchChosenUnderTheOldOne() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitItem()

            // Walk the cascade all the way down so there is a branch to lose.
            viewModel.sendIntent(WorkshopsIntent.SelectProvince(tehran))
            awaitUntil { it.cities.isNotEmpty() }
            viewModel.sendIntent(WorkshopsIntent.SelectCity(tehranCity))
            awaitUntil { it.branches.isNotEmpty() }
            viewModel.sendIntent(WorkshopsIntent.SelectBranch(branchOne))
            val selected = awaitUntil { it.branchSelection.branchCode == "123" }
            assertTrue(selected.branchSelection.isValid)

            viewModel.sendIntent(WorkshopsIntent.SelectProvince(esfahan))
            val state = awaitUntil { it.branchSelection.provinceCode == "04" }

            assertEquals("", state.branchSelection.cityCode)
            assertEquals("", state.branchSelection.cityName)
            assertEquals("", state.branchSelection.branchCode)
            assertEquals("", state.branchSelection.branchName)
            assertTrue(state.branches.isEmpty(), "stale branch options must not survive")
            cancelAndIgnoreRemainingEvents()
        }
    }

    private suspend fun ReceiveTurbine<WorkshopsUiState>.awaitUntil(
        predicate: (WorkshopsUiState) -> Boolean
    ): WorkshopsUiState {
        var state = awaitItem()
        while (!predicate(state)) {
            state = awaitItem()
        }
        return state
    }
}
