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
import com.tamin.taminhamrah.useCases.common.GetCitiesByProvinceUseCase
import com.tamin.taminhamrah.useCases.common.GetInsuranceTypesUseCase
import com.tamin.taminhamrah.useCases.common.GetProvincesUseCase
import com.tamin.taminhamrah.useCases.contracts.GetBranchesUseCase
import com.tamin.taminhamrah.useCases.historyObjection.GetHistoryObjectionNotExistRequestsUseCase
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
            getProvincesUseCase = GetProvincesUseCase(cityProvinceRepository),
            getCitiesByProvinceUseCase = GetCitiesByProvinceUseCase(cityProvinceRepository),
            getBranchesUseCase = GetBranchesUseCase(contractsRepository),
            getInsuranceTypesUseCase = GetInsuranceTypesUseCase(commonRepository),
            getHistoryObjectionNotExistRequestsUseCase = GetHistoryObjectionNotExistRequestsUseCase(historyObjectionRepository),
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
    fun citiesLoad_usesFreshNetworkResultNotStaleCache() = runTest(testDispatcher) {
        // Regression test: getCitiesByProvinceUseCase is cache-then-network and emits twice —
        // once with whatever's already cached (possibly stale/incomplete), then again with the
        // fresh network result. The ViewModel must end up on the second emission, not the first.
        val province = ProvinceDN(provinceCode = "27", provinceName = "گلستان", status = "1", statusStartDate = "1")
        val staleCachedCities = listOf(CityDN(cityCode = "1", cityName = "شهر قدیمی", provinceCode = "27"))
        val freshCities = listOf(
            CityDN(cityCode = "1", cityName = "گرگان", provinceCode = "27"),
            CityDN(cityCode = "2", cityName = "گنبد کاووس", provinceCode = "27"),
            CityDN(cityCode = "3", cityName = "علی‌آباد کتول", provinceCode = "27"),
        )
        cityProvinceRepository.provincesResult = listOf(province)
        cityProvinceRepository.staleCitiesByProvinceResult = staleCachedCities
        cityProvinceRepository.citiesByProvinceResult = freshCities

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(HistoryObjectionStepperIntent.Load(null))
            awaitUntil { it.provinces.isNotEmpty() }

            viewModel.sendIntent(
                HistoryObjectionStepperIntent.OnProvinceSelected(
                    com.tamin.taminhamrah.model.common.ProvincePR("27", "گلستان")
                )
            )
            val finalState = awaitUntil { it.cities.size == freshCities.size }
            assertEquals(freshCities.map { it.cityName }, finalState.cities.map { it.cityName })
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
