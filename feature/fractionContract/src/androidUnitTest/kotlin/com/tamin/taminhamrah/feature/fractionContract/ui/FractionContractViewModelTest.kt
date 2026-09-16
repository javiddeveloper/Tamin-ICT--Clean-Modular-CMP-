package com.tamin.taminhamrah.feature.fractionContract.ui

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.tamin.taminhamrah.feature.fractionContract.fake.FakeFractionCityProvinceRepository
import com.tamin.taminhamrah.feature.fractionContract.fake.FakeFractionContractRepository
import com.tamin.taminhamrah.feature.fractionContract.fake.FakeFractionContractsRepository
import com.tamin.taminhamrah.feature.fractionContract.fake.FakeFractionUserRepository
import com.tamin.taminhamrah.feature.fractionContract.fake.FractionContractViewModelTestData
import com.tamin.taminhamrah.feature.fractionContract.ui.contract.FractionContractIntent
import com.tamin.taminhamrah.feature.fractionContract.ui.contract.FractionContractState
import com.tamin.taminhamrah.feature.fractionContract.ui.contract.FractionContractStep
import com.tamin.taminhamrah.useCases.contracts.GetRegistrationInfoUseCase
import com.tamin.taminhamrah.useCases.contracts.SaveContactUseCase
import com.tamin.taminhamrah.useCases.fractionContract.CheckFractionAgeAndHistoryUseCase
import com.tamin.taminhamrah.useCases.fractionContract.MakeFractionContractUseCase
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class FractionContractViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var contractsRepository: FakeFractionContractsRepository
    private lateinit var fractionRepository: FakeFractionContractRepository
    private lateinit var cityProvinceRepository: FakeFractionCityProvinceRepository
    private lateinit var viewModel: FractionContractViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        contractsRepository = FakeFractionContractsRepository()
        fractionRepository = FakeFractionContractRepository()
        cityProvinceRepository = FakeFractionCityProvinceRepository()
        viewModel = createViewModel()
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private fun createViewModel(): FractionContractViewModel = FractionContractViewModel(
        getRegistrationInfoUseCase = GetRegistrationInfoUseCase(contractsRepository),
        checkFractionAgeAndHistoryUseCase = CheckFractionAgeAndHistoryUseCase(fractionRepository),
        identityInfoUseCase = IdentityInfoUseCase(
            userRepository = FakeFractionUserRepository(),
            cityProvinceRepository = cityProvinceRepository,
        ),
        saveContactUseCase = SaveContactUseCase(contractsRepository),
        makeFractionContractUseCase = MakeFractionContractUseCase(fractionRepository),
    )

    @Test
    fun initData_withUnder18_setsBlockingErrorAndIsNotEligible() = runTest(testDispatcher) {
        fractionRepository.eligibilityResult = FractionContractViewModelTestData.under18Eligibility

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(FractionContractIntent.InitData)
            val state = awaitUntil { it.blockingErrorMessage != null }

            assertNotNull(state.blockingErrorMessage)
            assertFalse(state.isEligible)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun goNext_onEligibility_whenStatusNotEligible_doesNotAdvance() = runTest(testDispatcher) {
        fractionRepository.eligibilityResult = FractionContractViewModelTestData.ineligibleStatusEligibility

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(FractionContractIntent.InitData)
            awaitUntil { !it.isLoading }

            viewModel.sendIntent(FractionContractIntent.OnNextStepClicked)
            assertEquals(FractionContractStep.Eligibility, viewModel.uiState.value.currentStep)
            assertFalse(viewModel.uiState.value.isEligible)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun goNext_onTerms_withoutRulesConfirmed_doesNotAdvance() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitItem()
            advanceToTermsStep()

            viewModel.sendIntent(FractionContractIntent.OnNextStepClicked)
            assertEquals(FractionContractStep.Terms, viewModel.uiState.value.currentStep)
            assertFalse(viewModel.uiState.value.isRulesConfirmed)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun goNext_onUserInfo_whenIncomplete_doesNotAdvance() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitItem()
            advanceToUserInfoStep()

            viewModel.sendIntent(
                FractionContractIntent.UpdateUserInfo(
                    viewModel.uiState.value.userInfo.copy(zipCode = "123"),
                ),
            )
            awaitUntil { it.userInfo.zipCode == "123" }

            viewModel.sendIntent(FractionContractIntent.OnNextStepClicked)
            assertEquals(FractionContractStep.UserInfo, viewModel.uiState.value.currentStep)
            assertFalse(viewModel.uiState.value.isUserInfoComplete)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun goNext_onUserInfo_whenContactChanged_triggersSaveBeforeAdvance() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitItem()
            advanceToUserInfoStep()

            viewModel.sendIntent(
                FractionContractIntent.UpdateUserInfo(
                    viewModel.uiState.value.userInfo.copy(address = "آدرس جدید"),
                ),
            )
            awaitUntil { it.userInfo.address == "آدرس جدید" }

            viewModel.sendIntent(FractionContractIntent.OnNextStepClicked)
            val state = awaitUntil { it.showContactSavedDialog }

            assertEquals(1, contractsRepository.saveContactCallCount)
            assertNotNull(contractsRepository.lastSaveContactRequest)
            assertEquals(FractionContractStep.UserInfo, state.currentStep)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun goNext_onUserInfo_whenContactUnchanged_advancesToSubmit() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitItem()
            advanceToUserInfoStep()

            viewModel.sendIntent(FractionContractIntent.OnNextStepClicked)
            val state = awaitUntil { it.currentStep == FractionContractStep.Submit }

            assertEquals(0, contractsRepository.saveContactCallCount)
            assertEquals(FractionContractStep.Submit, state.currentStep)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun submitContract_whenAlreadySubmitted_isIdempotent() = runTest(testDispatcher) {
        viewModel.uiState.test {
            awaitItem()
            advanceToSubmitStep()

            viewModel.sendIntent(FractionContractIntent.SetFinalConfirmed(true))
            awaitUntil { it.isFinalConfirmed }

            viewModel.sendIntent(FractionContractIntent.SubmitContract)
            awaitUntil { it.submittedContract != null }
            assertEquals(1, fractionRepository.makeContractCallCount)

            viewModel.sendIntent(FractionContractIntent.SubmitContract)
            assertEquals(1, fractionRepository.makeContractCallCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun isEligible_falseWhenBlockingErrorEvenWithEligibleStatus() = runTest(testDispatcher) {
        fractionRepository.eligibilityResult = FractionContractViewModelTestData.under18Eligibility

        viewModel.uiState.test {
            awaitItem()
            viewModel.sendIntent(FractionContractIntent.InitData)
            val state = awaitUntil { it.blockingErrorMessage != null }

            assertEquals(2, state.eligibility?.eligibilityStatus)
            assertFalse(state.isEligible)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private suspend fun ReceiveTurbine<FractionContractState>.advanceToTermsStep() {
        viewModel.sendIntent(FractionContractIntent.InitData)
        awaitUntil { !it.isLoading && it.isEligible }
        viewModel.sendIntent(FractionContractIntent.OnNextStepClicked)
        awaitUntil { it.currentStep == FractionContractStep.Terms }
    }

    private suspend fun ReceiveTurbine<FractionContractState>.advanceToUserInfoStep() {
        advanceToTermsStep()
        viewModel.sendIntent(FractionContractIntent.SetRulesConfirmed(true))
        awaitUntil { it.isRulesConfirmed }
        viewModel.sendIntent(FractionContractIntent.OnNextStepClicked)
        awaitUntil { it.currentStep == FractionContractStep.UserInfo }
        ensureCompleteUserInfo()
    }

    private suspend fun ReceiveTurbine<FractionContractState>.ensureCompleteUserInfo() {
        if (viewModel.uiState.value.isUserInfoComplete) return
        viewModel.sendIntent(
            FractionContractIntent.UpdateUserInfo(
                viewModel.uiState.value.userInfo.copy(
                    cityName = "تهران",
                    address = "تهران، خیابان فاطمی",
                    zipCode = "1414657771",
                    phoneNumber = "02188974532",
                ),
            ),
        )
        awaitUntil { it.isUserInfoComplete }
    }

    private suspend fun ReceiveTurbine<FractionContractState>.advanceToSubmitStep() {
        advanceToUserInfoStep()
        viewModel.sendIntent(FractionContractIntent.OnNextStepClicked)
        awaitUntil { it.currentStep == FractionContractStep.Submit }
    }

    private suspend fun ReceiveTurbine<FractionContractState>.awaitUntil(
        predicate: (FractionContractState) -> Boolean,
    ): FractionContractState {
        var state = awaitItem()
        while (!predicate(state)) {
            state = awaitItem()
        }
        return state
    }
}
