package com.tamin.taminhamrah.feature.historyobjection.ui.stepper

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.historyobjection.fake.FakeCityProvinceRepository
import com.tamin.taminhamrah.feature.historyobjection.fake.FakeCommonRepository
import com.tamin.taminhamrah.feature.historyobjection.fake.FakeContractsRepository
import com.tamin.taminhamrah.feature.historyobjection.fake.FakeHistoryObjectionRepository
import com.tamin.taminhamrah.feature.historyobjection.ui.stepper.contract.HistoryObjectionStepperIntent
import com.tamin.taminhamrah.feature.historyobjection.ui.stepper.contract.HistoryObjectionStepperState
import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.common.InsuranceTypeDN
import com.tamin.taminhamrah.model.common.ProvinceDN
import com.tamin.taminhamrah.model.contracts.BranchDN
import com.tamin.taminhamrah.model.historyObjection.NotExistRequestDN
import com.tamin.taminhamrah.useCases.common.GetCitiesByProvincePageUseCase
import com.tamin.taminhamrah.useCases.common.GetInsuranceTypesUseCase
import com.tamin.taminhamrah.useCases.common.GetProvincesPageUseCase
import com.tamin.taminhamrah.useCases.contracts.GetBranchesUseCase
import com.tamin.taminhamrah.useCases.historyObjection.GetHistoryObjectionNotExistRequestsUseCase
import com.tamin.taminhamrah.useCases.historyObjection.SaveHistoryObjectionNotExistRequestUseCase
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryObjectionStepperViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var cityProvinceRepository: FakeCityProvinceRepository
    private lateinit var contractsRepository: FakeContractsRepository
    private lateinit var commonRepository: FakeCommonRepository
    private lateinit var historyObjectionRepository: FakeHistoryObjectionRepository
    private lateinit var viewModel: HistoryObjectionStepperViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        cityProvinceRepository = FakeCityProvinceRepository()
        contractsRepository = FakeContractsRepository()
        commonRepository = FakeCommonRepository()
        historyObjectionRepository = FakeHistoryObjectionRepository()
        viewModel = HistoryObjectionStepperViewModel(
            getProvincesUseCase = GetProvincesPageUseCase(cityProvinceRepository),
            getCitiesByProvinceUseCase = GetCitiesByProvincePageUseCase(cityProvinceRepository),
            getBranchesUseCase = GetBranchesUseCase(contractsRepository),
            getInsuranceTypesUseCase = GetInsuranceTypesUseCase(commonRepository),
            getHistoryObjectionNotExistRequestsUseCase = GetHistoryObjectionNotExistRequestsUseCase(historyObjectionRepository),
            saveHistoryObjectionNotExistRequestUseCase = SaveHistoryObjectionNotExistRequestUseCase(historyObjectionRepository),
        )
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun selectingProvince_loadsCities_thenSelectingCity_loadsBranches() = runTest(testDispatcher) {
        val province = ProvinceDN(provinceCode = "04", provinceName = "اصفهان", status = "1", statusStartDate = "1")
        val city = CityDN(cityCode = "1158", cityName = "اصفهان", provinceCode = "04")
        val branch = BranchDN(code = "111", name = "شعبه مرکزی", branchAddress = "خیابان اصلی", cityCode = "1158", minCode = null, maxCode = null)
        cityProvinceRepository.provincesResult = listOf(province)
        cityProvinceRepository.citiesByProvinceResult = listOf(city)
        contractsRepository.branchesResult = listOf(branch)

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(HistoryObjectionStepperIntent.Load(null))
            awaitUntil { it.provinces.isNotEmpty() }

            viewModel.sendIntent(HistoryObjectionStepperIntent.OnProvinceSelected(province.let {
                com.tamin.taminhamrah.model.common.ProvincePR(it.provinceCode.orEmpty(), it.provinceName.orEmpty())
            }))
            val afterCityLoad = awaitUntil { it.cities.isNotEmpty() }
            assertEquals("04", cityProvinceRepository.lastCitiesByProvinceCode)
            assertEquals(1, afterCityLoad.cities.size)
            assertNull(afterCityLoad.selectedBranch)

            viewModel.sendIntent(HistoryObjectionStepperIntent.OnCitySelected(afterCityLoad.cities.first()))
            val afterBranchLoad = awaitUntil { it.branches.isNotEmpty() }
            assertEquals("1158", contractsRepository.lastBranchCityCode)
            assertEquals(1, afterBranchLoad.branches.size)
        }
    }

    @Test
    fun insuranceTypesFailure_doesNotCancelProvinceLoad() = runTest(testDispatcher) {
        // Offline: insurance types are network-only and throw, provinces come from the cache.
        val province = ProvinceDN(provinceCode = "04", provinceName = "اصفهان", status = "1", statusStartDate = "1")
        cityProvinceRepository.provincesResult = listOf(province)
        commonRepository.shouldThrowError = true

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(HistoryObjectionStepperIntent.Load(null))
            // StateFlow conflates, so wait for the settled state only: provinces loaded and
            // loading finished. The error must still be there (the modal used to flash and close).
            val finished = awaitUntil { it.provinces.isNotEmpty() && !it.isLoading }
            assertEquals(1, finished.provinces.size)
            assertNotNull(finished.error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun changingProvince_clearsPreviouslySelectedCityAndBranch() = runTest(testDispatcher) {
        val provinceA = ProvinceDN(provinceCode = "04", provinceName = "اصفهان", status = "1", statusStartDate = "1")
        val provinceB = ProvinceDN(provinceCode = "09", provinceName = "خراسان رضوی", status = "1", statusStartDate = "1")
        val city = CityDN(cityCode = "1158", cityName = "اصفهان", provinceCode = "04")
        cityProvinceRepository.provincesResult = listOf(provinceA, provinceB)
        cityProvinceRepository.citiesByProvinceResult = listOf(city)

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(HistoryObjectionStepperIntent.Load(null))
            awaitUntil { it.provinces.isNotEmpty() }

            viewModel.sendIntent(
                HistoryObjectionStepperIntent.OnProvinceSelected(
                    com.tamin.taminhamrah.model.common.ProvincePR("04", "اصفهان")
                )
            )
            val withCity = awaitUntil { it.cities.isNotEmpty() }
            viewModel.sendIntent(HistoryObjectionStepperIntent.OnCitySelected(withCity.cities.first()))
            awaitUntil { it.selectedCity != null }

            viewModel.sendIntent(
                HistoryObjectionStepperIntent.OnProvinceSelected(
                    com.tamin.taminhamrah.model.common.ProvincePR("09", "خراسان رضوی")
                )
            )
            val afterProvinceChange = awaitUntil { it.selectedProvince?.provinceCode == "09" }
            assertNull(afterProvinceChange.selectedCity)
            assertNull(afterProvinceChange.selectedBranch)
        }
    }

    @Test
    fun showCityPicker_withoutProvinceSelected_doesNotOpenSheet() = runTest(testDispatcher) {
        // Guard only depends on selectedProvince, which is already null in the fresh state —
        // no need to wait for Load() to settle first. The guard also sends
        // HistoryObjectionStepperEvent.ShowMessage — verified by code review, not asserted here
        // since the events Channel's timing is not reliably observable via Turbine in this setup.
        viewModel.sendIntent(HistoryObjectionStepperIntent.OnShowCityPicker)
        assertNull(viewModel.uiState.value.bottomSheetConfig)
    }

    @Test
    fun showBranchPicker_withProvinceButNoCitySelected_doesNotOpenSheet() = runTest(testDispatcher) {
        viewModel.sendIntent(
            HistoryObjectionStepperIntent.OnProvinceSelected(
                com.tamin.taminhamrah.model.common.ProvincePR("04", "اصفهان")
            )
        )
        viewModel.sendIntent(HistoryObjectionStepperIntent.OnShowBranchPicker)
        assertNull(viewModel.uiState.value.bottomSheetConfig)
    }

    @Test
    fun editMode_reconstructsFieldsAcrossAllThreeSteps() = runTest(testDispatcher) {
        val existing = NotExistRequestDN(
            requestNumber = "1837710",
            provinceCode = "04",
            provinceName = "اصفهان",
            cityCode = "1158",
            cityName = "اصفهان",
            branchCode = "111",
            branchName = "شعبه مرکزی",
            insuranceType = "01",
            insuranceTypeDesc = "اجباري",
            workshopId = "6393610019",
            workshopName = "کارگاه نمونه",
            workshopManager = "علی رضایی",
            workshopAddress = "تهران، خیابان آزادی",
            startDate = 1000L,
            endDate = 2000L,
            workDays = "90",
        )
        historyObjectionRepository.notExistRequestsResult = listOf(existing)

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(HistoryObjectionStepperIntent.Load("1837710"))
            val loaded = awaitUntil { it.workshopId == "6393610019" }

            assertEquals(true, loaded.isEditMode)
            assertEquals("1837710", loaded.editRequestNumber)
            assertEquals("04", loaded.selectedProvince?.provinceCode)
            assertEquals("1158", loaded.selectedCity?.cityCode)
            assertEquals("111", loaded.selectedBranch?.code)
            assertEquals("01", loaded.selectedInsuranceType?.insuranceTypeCode)
            assertEquals("کارگاه نمونه", loaded.workshopName)
            assertEquals("علی رضایی", loaded.employerName)
            assertEquals("تهران، خیابان آزادی", loaded.workshopAddress)
            assertEquals(1000L, loaded.startDateTimestamp)
            assertEquals(2000L, loaded.endDateTimestamp)
            assertEquals("90", loaded.workDays)
        }
    }

    @Test
    fun editMode_withSharedRequestNumber_usesRowIndexToPickTheRightRow() = runTest(testDispatcher) {
        // One submission (requestNumber) can list several missing periods, each a separate row
        // (rowIndex) — the same composite key `deletenotexist/{requestNumber}/{rowIndex}` already
        // relies on. Editing must not silently fall back to whichever row comes first in the list.
        val firstRow = NotExistRequestDN(
            requestNumber = "1837710",
            rowIndex = "1",
            provinceCode = "04",
            provinceName = "اصفهان",
            cityCode = "1158",
            cityName = "اصفهان",
            branchCode = "111",
            branchName = "شعبه مرکزی",
            insuranceType = "01",
            insuranceTypeDesc = "اجباري",
            workshopId = "6393610019",
            workshopName = "کارگاه اول",
            workshopManager = "علی رضایی",
            workshopAddress = "تهران، خیابان آزادی",
            startDate = 1000L,
            endDate = 2000L,
            workDays = "90",
        )
        val secondRow = NotExistRequestDN(
            requestNumber = "1837710",
            rowIndex = "2",
            provinceCode = "14",
            provinceName = "شهرستانهاي استان تهران",
            cityCode = "1306",
            cityName = "پاکدشت",
            branchCode = "0950",
            branchName = "پاکدشت",
            insuranceType = "02",
            insuranceTypeDesc = "اختياري",
            workshopId = "1111111111",
            workshopName = "کارگاه دوم",
            workshopManager = "رضا احمدی",
            workshopAddress = "پاکدشت، خیابان اصلی",
            startDate = 3000L,
            endDate = 4000L,
            workDays = "45",
        )
        historyObjectionRepository.notExistRequestsResult = listOf(firstRow, secondRow)

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(HistoryObjectionStepperIntent.Load("1837710", "2"))
            val loaded = awaitUntil { it.workshopId == "1111111111" }

            assertEquals("2", loaded.editRowIndex)
            assertEquals("14", loaded.selectedProvince?.provinceCode)
            assertEquals("1306", loaded.selectedCity?.cityCode)
            assertEquals("0950", loaded.selectedBranch?.code)
            assertEquals("02", loaded.selectedInsuranceType?.insuranceTypeCode)
            assertEquals("کارگاه دوم", loaded.workshopName)
            assertEquals("رضا احمدی", loaded.employerName)
            assertEquals("پاکدشت، خیابان اصلی", loaded.workshopAddress)
            assertEquals(3000L, loaded.startDateTimestamp)
            assertEquals(4000L, loaded.endDateTimestamp)
            assertEquals("45", loaded.workDays)
        }
    }

    @Test
    fun confirmClicked_withCompleteData_savesRequestAndMarksSubmitted() = runTest(testDispatcher) {
        val existing = NotExistRequestDN(
            requestNumber = "1837710",
            provinceCode = "14",
            provinceName = "شهرستانهاي استان تهران",
            cityCode = "1306",
            cityName = "پاکدشت",
            branchCode = "0950",
            branchName = "پاکدشت",
            insuranceType = "02",
            insuranceTypeDesc = "اختياري",
            workshopId = "1111111111",
            workshopName = "22333333333333",
            workshopManager = "333333333333",
            workshopAddress = "3333333333333",
            startDate = 1660937400000L,
            endDate = 1724013000000L,
            workDays = "11",
        )
        historyObjectionRepository.notExistRequestsResult = listOf(existing)
        historyObjectionRepository.saveNotExistResult = true

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(HistoryObjectionStepperIntent.Load("1837710"))
            awaitUntil { it.workshopId == "1111111111" }

            viewModel.sendIntent(HistoryObjectionStepperIntent.OnConfirmClicked)
            val submitted = awaitUntil { it.hasSubmitted }

            assertEquals(false, submitted.isSubmitting)
            val request = historyObjectionRepository.lastSaveNotExistRequest
            assertEquals("0950", request?.branchCode)
            assertEquals("1306", request?.cityCode)
            assertEquals("14", request?.provinceCode)
            assertEquals("02", request?.insuranceType)
            assertEquals("1111111111", request?.workshopId)
            assertEquals("22333333333333", request?.workshopName)
            assertEquals("333333333333", request?.workshopManager)
            assertEquals("3333333333333", request?.workshopAddress)
            assertEquals(1660937400000L, request?.startDate)
            assertEquals(1724013000000L, request?.endDate)
            assertEquals("11", request?.workDays)
        }
    }

    @Test
    fun confirmClicked_withIncompleteData_doesNotCallSaveUseCase() = runTest(testDispatcher) {
        viewModel.sendIntent(HistoryObjectionStepperIntent.OnConfirmClicked)

        assertNull(historyObjectionRepository.lastSaveNotExistRequest)
        assertEquals(false, viewModel.uiState.value.hasSubmitted)
    }

    @Test
    fun confirmClicked_whenSaveFails_setsErrorAndDoesNotMarkSubmitted() = runTest(testDispatcher) {
        val existing = NotExistRequestDN(
            requestNumber = "1837710",
            provinceCode = "14",
            provinceName = "شهرستانهاي استان تهران",
            cityCode = "1306",
            cityName = "پاکدشت",
            branchCode = "0950",
            branchName = "پاکدشت",
            insuranceType = "02",
            insuranceTypeDesc = "اختياري",
            workshopId = "1111111111",
            workshopName = "22333333333333",
            workshopManager = "333333333333",
            workshopAddress = "3333333333333",
            startDate = 1660937400000L,
            endDate = 1724013000000L,
            workDays = "11",
        )
        historyObjectionRepository.notExistRequestsResult = listOf(existing)
        historyObjectionRepository.shouldThrowOnSave = true
        historyObjectionRepository.saveError = RuntimeException("network down")

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(HistoryObjectionStepperIntent.Load("1837710"))
            awaitUntil { it.workshopId == "1111111111" }

            viewModel.sendIntent(HistoryObjectionStepperIntent.OnConfirmClicked)
            val errored = awaitUntil { it.error != null }

            assertEquals(false, errored.hasSubmitted)
            assertEquals(false, errored.isSubmitting)
        }
    }

    private suspend fun ReceiveTurbine<HistoryObjectionStepperState>.awaitUntil(
        predicate: (HistoryObjectionStepperState) -> Boolean,
    ): HistoryObjectionStepperState {
        var state = awaitItem()
        while (!predicate(state)) {
            state = awaitItem()
        }
        return state
    }
}
