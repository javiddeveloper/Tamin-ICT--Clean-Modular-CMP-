package com.tamin.taminhamrah.feature.addDependent.ui

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.addDependent.fake.FakeAddDependentRepository
import com.tamin.taminhamrah.feature.addDependent.ui.contract.AddDependentIntent
import com.tamin.taminhamrah.feature.addDependent.ui.contract.AddDependentState
import com.tamin.taminhamrah.feature.addDependent.ui.contract.STEP_DOCUMENTS
import com.tamin.taminhamrah.feature.addDependent.ui.contract.STEP_VERIFICATION
import com.tamin.taminhamrah.feature.addDependent.ui.contract.StepperMode
import com.tamin.taminhamrah.feature.addDependent.ui.model.FamilyRelationshipPR
import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.common.ProvinceDN
import com.tamin.taminhamrah.repository.CityProvinceRepository
import com.tamin.taminhamrah.useCases.addDependent.AddNewDependentUseCase
import com.tamin.taminhamrah.useCases.addDependent.GetActiveBranchesUseCase
import com.tamin.taminhamrah.useCases.addDependent.GetFamilyRelationshipsFromProxyUseCase
import com.tamin.taminhamrah.useCases.addDependent.InquiryEducationCodeUseCase
import com.tamin.taminhamrah.useCases.addDependent.InquiryRegistryUseCase
import com.tamin.taminhamrah.useCases.addDependent.UploadDependentImageUseCase
import com.tamin.taminhamrah.useCases.common.GetCitiesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(ExperimentalCoroutinesApi::class)
class AddDependentViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeAddDependentRepository
    private lateinit var viewModel: AddDependentViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeAddDependentRepository()
        viewModel = AddDependentViewModel(
            getActiveBranchesUseCase = GetActiveBranchesUseCase(repository),
            getFamilyRelationshipsFromProxyUseCase = GetFamilyRelationshipsFromProxyUseCase(repository),
            inquiryRegistryUseCase = InquiryRegistryUseCase(repository),
            inquiryEducationCodeUseCase = InquiryEducationCodeUseCase(repository),
            uploadDependentImageUseCase = UploadDependentImageUseCase(repository),
            addNewDependentUseCase = AddNewDependentUseCase(repository),
            getCitiesUseCase = GetCitiesUseCase(EmptyCityProvinceRepository)
        )
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun sonMode_afterEducationInquiry_nextAdvancesToDocuments() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitItem()

            viewModel.sendIntent(AddDependentIntent.InitData)
            awaitUntil { it.selectedBranch != null }

            viewModel.sendIntent(AddDependentIntent.OnNationalIdChanged("0012345678"))
            viewModel.sendIntent(
                AddDependentIntent.OnBirthDateSelected(
                    persianDate = "1380/01/01",
                    gregorianDate = "2001-03-21",
                    timestamp = "985132800000"
                )
            )
            viewModel.sendIntent(
                AddDependentIntent.OnRelationshipSelected(
                    FamilyRelationshipPR(id = 2, relationCode = "02", relationDesc = "پسر")
                )
            )
            awaitUntil { it.selectedRelationship?.relationCode == "02" }

            viewModel.sendIntent(AddDependentIntent.SubmitInquiryRegistry)
            awaitUntil { it.currentStep == STEP_VERIFICATION && it.stepperMode == StepperMode.SON_MODE }

            val city = CityPR(cityCode = "1", cityName = "تهران")
            viewModel.sendIntent(AddDependentIntent.OnCityBirthSelected(city))
            viewModel.sendIntent(AddDependentIntent.OnCityIssuanceSelected(city))
            awaitUntil { it.selectedCityBirth != null && it.selectedCityIssuance != null }

            viewModel.sendIntent(AddDependentIntent.OnEducationCodeChanged("12345"))
            viewModel.sendIntent(AddDependentIntent.SubmitInquiryEducation)
            awaitUntil { !it.needCallInquiryEducation && it.universityName.isNotBlank() }

            viewModel.sendIntent(AddDependentIntent.OnNextStepClicked)
            val state = awaitUntil { it.currentStep == STEP_DOCUMENTS }

            assertEquals(STEP_DOCUMENTS, state.currentStep)
            assertFalse(state.needCallInquiryEducation)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private suspend fun ReceiveTurbine<AddDependentState>.awaitUntil(
        predicate: (AddDependentState) -> Boolean
    ): AddDependentState {
        var state = awaitItem()
        while (!predicate(state)) {
            state = awaitItem()
        }
        return state
    }
}

private object EmptyCityProvinceRepository : CityProvinceRepository {
    override fun getCity(cityId: String): Flow<CityDN> = flowOf()
    override fun getProvince(provinceId: String): Flow<ProvinceDN> = flowOf()
    override fun getProvinces(): Flow<List<ProvinceDN>> = flowOf(emptyList())
    override fun getCities(cityName: String?, provinceCode: String?): Flow<List<CityDN>> =
        flowOf(emptyList())
    override fun getCitiesByProvince(provinceCode: String): Flow<List<CityDN>> = flowOf(emptyList())
}
