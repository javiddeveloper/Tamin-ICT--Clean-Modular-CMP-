package com.tamin.taminhamrah.feature.contracts.flow.ui

import com.tamin.taminhamrah.feature.contracts.flow.config.StudentContractFlowConfig
import com.tamin.taminhamrah.feature.contracts.flow.ui.contract.ContractFlowIntent
import com.tamin.taminhamrah.feature.contracts.flow.ui.fake.ContractFlowViewModelTestData
import com.tamin.taminhamrah.feature.contracts.flow.ui.fake.FakeContractFlowCityProvinceRepository
import com.tamin.taminhamrah.feature.contracts.flow.ui.fake.FakeContractFlowRepository
import com.tamin.taminhamrah.feature.contracts.flow.ui.fake.FakeContractFlowUserRepository
import com.tamin.taminhamrah.useCases.contracts.CalculateFreelanceSalaryUseCase
import com.tamin.taminhamrah.useCases.contracts.CalculateOptionalSalaryUseCase
import com.tamin.taminhamrah.useCases.contracts.CheckMedicalStudentUseCase
import com.tamin.taminhamrah.useCases.contracts.CheckRedCrossStatusUseCase
import com.tamin.taminhamrah.useCases.contracts.GetBranchesUseCase
import com.tamin.taminhamrah.useCases.contracts.GetContractsUseCase
import com.tamin.taminhamrah.useCases.contracts.GetFreeJobWagesUseCase
import com.tamin.taminhamrah.useCases.contracts.GetFreelancePremiumRangeUseCase
import com.tamin.taminhamrah.useCases.contracts.GetOptionalPremiumRangeUseCase
import com.tamin.taminhamrah.useCases.contracts.GetRegistrationInfoUseCase
import com.tamin.taminhamrah.useCases.contracts.GetSpcPremiumRatesUseCase
import com.tamin.taminhamrah.useCases.contracts.MakeContractUseCase
import com.tamin.taminhamrah.useCases.contracts.MakeFreelanceContractByGuardianUseCase
import com.tamin.taminhamrah.useCases.contracts.MakeOptionalContractByGuardianUseCase
import com.tamin.taminhamrah.useCases.contracts.SaveContactUseCase
import com.tamin.taminhamrah.useCases.contracts.UpdateContractUseCase
import com.tamin.taminhamrah.useCases.contracts.UpdateFreelanceContractByGuardianUseCase
import com.tamin.taminhamrah.useCases.contracts.UpdateOptionalContractByGuardianUseCase
import com.tamin.taminhamrah.useCases.contracts.UploadImageUseCase
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.useCases.user.SubdominantUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.jetbrains.compose.resources.StringResource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Compose [org.jetbrains.compose.resources.getString] is unavailable in JVM unit tests.
 * Tests inject a stub [resolveString] so the registration gate can run.
 *
 * A cache hit must not enable «بعدی» before both contract refreshes finish, a failed
 * all-contracts refresh must still close the gate, and a stale preflight error must
 * clear once a later check finds nothing blocking.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ContractFlowViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val stubResolveString: suspend (StringResource) -> String = { "stub" }

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun cachedEligibility_doesNotEnableNext_untilBothRefreshesComplete() = runTest(testDispatcher) {
        val repository = FakeContractFlowRepository().apply { holdRefresh = true }
        val viewModel = createViewModel(repository)

        viewModel.sendIntent(ContractFlowIntent.LoadInitialData)
        advanceUntilIdle()

        val cached = viewModel.uiState.value
        assertTrue(cached.eligibility?.isEligible == true)
        assertTrue(cached.isRegistrationGateLoading)
        assertFalse(cached.canGoNext)
        assertNull(cached.preflightGateError)

        repository.releaseTypedRefresh.complete(Unit)
        repository.releaseAllRefresh.complete(Unit)
        advanceUntilIdle()

        val refreshed = viewModel.uiState.value
        assertFalse(refreshed.isRegistrationGateLoading)
        assertNull(refreshed.preflightGateError)
        assertTrue(refreshed.canGoNext)
    }

    @Test
    fun failedAllContractsRefresh_completesGateWithFailureMessage() = runTest(testDispatcher) {
        val repository = FakeContractFlowRepository().apply {
            failAllContracts = true
            typedContracts = listOf(ContractFlowViewModelTestData.eligibleContract())
        }
        val viewModel = createViewModel(repository)

        viewModel.sendIntent(ContractFlowIntent.LoadInitialData)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isRegistrationGateLoading)
        assertEquals("stub", state.preflightGateError)
        assertFalse(state.canGoNext)
    }

    @Test
    fun preflightGateError_clearsWhenLaterCheckFindsNoBlock() = runTest(testDispatcher) {
        val repository = FakeContractFlowRepository().apply {
            typedContracts = listOf(ContractFlowViewModelTestData.activeContract())
        }
        val viewModel = createViewModel(repository)

        viewModel.sendIntent(ContractFlowIntent.LoadInitialData)
        advanceUntilIdle()

        assertEquals("stub", viewModel.uiState.value.preflightGateError)
        assertFalse(viewModel.uiState.value.canGoNext)

        repository.typedContracts = listOf(ContractFlowViewModelTestData.eligibleContract())
        viewModel.sendIntent(ContractFlowIntent.LoadInitialData)
        advanceUntilIdle()

        val cleared = viewModel.uiState.value
        assertFalse(cleared.isRegistrationGateLoading)
        assertNull(cleared.preflightGateError)
        assertTrue(cleared.canGoNext)
    }

    @Test
    fun reload_resetsGateToLoading_untilNewRefreshesComplete() = runTest(testDispatcher) {
        val repository = FakeContractFlowRepository()
        val viewModel = createViewModel(repository)

        viewModel.sendIntent(ContractFlowIntent.LoadInitialData)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isRegistrationGateLoading)

        repository.holdRefresh = true
        viewModel.sendIntent(ContractFlowIntent.LoadInitialData)
        advanceUntilIdle()

        val reloading = viewModel.uiState.value
        assertTrue(reloading.isRegistrationGateLoading)
        assertFalse(reloading.canGoNext)

        repository.releaseTypedRefresh.complete(Unit)
        repository.releaseAllRefresh.complete(Unit)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isRegistrationGateLoading)
    }

    private fun createViewModel(repository: FakeContractFlowRepository): ContractFlowViewModel {
        val userRepository = FakeContractFlowUserRepository()
        return ContractFlowViewModel(
            config = StudentContractFlowConfig(),
            getRegistrationInfoUseCase = GetRegistrationInfoUseCase(repository),
            getContractsUseCase = GetContractsUseCase(repository),
            identityInfoUseCase = IdentityInfoUseCase(
                userRepository = userRepository,
                cityProvinceRepository = FakeContractFlowCityProvinceRepository(),
            ),
            getBranchesUseCase = GetBranchesUseCase(repository),
            getSpcPremiumRatesUseCase = GetSpcPremiumRatesUseCase(repository),
            getFreelancePremiumRangeUseCase = GetFreelancePremiumRangeUseCase(repository),
            getOptionalPremiumRangeUseCase = GetOptionalPremiumRangeUseCase(repository),
            calculateFreelanceSalaryUseCase = CalculateFreelanceSalaryUseCase(repository),
            calculateOptionalSalaryUseCase = CalculateOptionalSalaryUseCase(repository),
            getFreeJobWagesUseCase = GetFreeJobWagesUseCase(repository),
            checkRedCrossStatusUseCase = CheckRedCrossStatusUseCase(repository),
            checkMedicalStudentUseCase = CheckMedicalStudentUseCase(repository),
            makeContractUseCase = MakeContractUseCase(repository),
            makeOptionalContractByGuardianUseCase = MakeOptionalContractByGuardianUseCase(repository),
            makeFreelanceContractByGuardianUseCase = MakeFreelanceContractByGuardianUseCase(repository),
            updateContractUseCase = UpdateContractUseCase(repository),
            updateOptionalContractByGuardianUseCase = UpdateOptionalContractByGuardianUseCase(repository),
            updateFreelanceContractByGuardianUseCase = UpdateFreelanceContractByGuardianUseCase(repository),
            saveContactUseCase = SaveContactUseCase(repository),
            uploadImageUseCase = UploadImageUseCase(repository),
            subdominantUseCase = SubdominantUseCase(userRepository),
            resolveString = stubResolveString,
        )
    }
}
