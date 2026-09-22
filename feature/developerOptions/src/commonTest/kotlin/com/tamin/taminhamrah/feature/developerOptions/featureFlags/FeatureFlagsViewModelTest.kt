package com.tamin.taminhamrah.feature.developerOptions.featureFlags

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.feature.developerOptions.featureFlags.contract.FeatureFlagsIntent
import com.tamin.taminhamrah.feature.developerOptions.featureFlags.contract.OverrideKind
import com.tamin.taminhamrah.model.common.BeneficiaryDN
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.model.common.InsuranceTypeDN
import com.tamin.taminhamrah.model.common.JobTitleDN
import com.tamin.taminhamrah.model.common.JobTitleListDN
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.common.RoleDN
import com.tamin.taminhamrah.model.common.UserType
import com.tamin.taminhamrah.model.common.UserTypeInfoDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.common.CommonRepository
import com.tamin.taminhamrah.repository.feature.FeatureFlagOverrideRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Stands in for [com.tamin.taminhamrah.data.feature.FeatureManagerImpl]'s real contract: a flag's
 * status is [FeatureStatus.Enabled] unless [overrideRepository] says otherwise — the exact merge the
 * real implementation does against the menu, so this test exercises the same live-update shape.
 */
private class FakeFeatureManager(private val overrideRepository: FeatureFlagOverrideRepository) : FeatureManager {
    override fun getFeatureStatus(flag: FeatureFlag): Flow<FeatureStatus> =
        overrideRepository.observeOverrides().map { it[flag] ?: FeatureStatus.Enabled }

    override suspend fun isFeatureEnabled(flag: FeatureFlag) = true
    override suspend fun getDisabledMessage(flag: FeatureFlag): String? = null
}

private class FakeFeatureFlagOverrideRepository : FeatureFlagOverrideRepository {
    val overrides = MutableStateFlow<Map<FeatureFlag, FeatureStatus>>(emptyMap())

    override fun observeOverrides(): Flow<Map<FeatureFlag, FeatureStatus>> = overrides.asStateFlow()

    override fun setOverride(flag: FeatureFlag, status: FeatureStatus) {
        overrides.value += (flag to status)
    }

    override fun clearOverride(flag: FeatureFlag) {
        overrides.value -= flag
    }

    override fun clearAllOverrides() {
        overrides.value = emptyMap()
    }
}

/**
 * A live, never-completing menu source (like the real DAO-backed one) with everything but
 * [getMainMenu] left as trivial stubs — this test only exercises how names are read off the menu.
 */
private class FakeCommonRepository : CommonRepository {
    val menu = MutableStateFlow<List<MainServiceDN>>(emptyList())

    override fun getMainMenu(versionCode: String, forceUpdate: Boolean): Flow<List<MainServiceDN>> = menu.asStateFlow()

    override fun getBeneficiary(filters: List<ApiFilterDN>): Flow<List<BeneficiaryDN>> = flow { emit(emptyList()) }
    override fun getRegistrationDeclarationForm(): Flow<ByteArray> = flow { emit(byteArrayOf()) }
    override fun getJobTitle(query: ApiQueryParamDN): Flow<JobTitleListDN?> = flow { emit(null) }
    override fun getJobTitlePage(query: ApiQueryParamDN): Flow<PageDN<JobTitleDN>> = flow {
        emit(PageDN(items = emptyList(), total = 0))
    }
    override fun getRoles(): Flow<List<RoleDN>> = flow { emit(emptyList()) }
    override fun getInsuranceTypes(searchText: String?): Flow<List<InsuranceTypeDN>> = flow { emit(emptyList()) }
    override fun checkUserType(): Flow<UserTypeInfoDN> = flow { emit(UserTypeInfoDN(userType = UserType.INSURED)) }
}

@OptIn(ExperimentalCoroutinesApi::class)
class FeatureFlagsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var overrideRepository: FakeFeatureFlagOverrideRepository
    private lateinit var commonRepository: FakeCommonRepository
    private lateinit var viewModel: FeatureFlagsViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        overrideRepository = FakeFeatureFlagOverrideRepository()
        commonRepository = FakeCommonRepository()
        viewModel = FeatureFlagsViewModel(FakeFeatureManager(overrideRepository), overrideRepository, commonRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun everyFlagStartsEnabledAndNotOverridden() = runTest(testDispatcher) {
        viewModel.uiState.test {
            var state = awaitItem()
            while (state.rows.isEmpty()) state = awaitItem()

            assertEquals(FeatureFlag.entries.size, state.rows.size)
            val contractsRow = state.rows.first { it.flag == FeatureFlag.CONTRACTS }
            assertEquals(FeatureStatus.Enabled, contractsRow.status)
            assertTrue(!contractsRow.isOverridden)
            cancelAndIgnoreRemainingEvents()
        }
    }

    /** The row's name is read off the real menu by id — never authored in the app. */
    @Test
    fun aRowsNameComesStraightOffTheServersMenu() = runTest(testDispatcher) {
        commonRepository.menu.value = listOf(
            MainServiceDN(id = FeatureFlag.PRESCRIPTION.id, name = "هرچی سرور اسمش رو گذاشته")
        )

        val row = viewModel.uiState.value.rows.first { it.flag == FeatureFlag.PRESCRIPTION }
        assertEquals("هرچی سرور اسمش رو گذاشته", row.serverName)
    }

    /** A flag this account's menu carries no row for at all shows no name — never a guess. */
    @Test
    fun aFlagMissingFromTheMenuHasNoServerName() = runTest(testDispatcher) {
        val row = viewModel.uiState.value.rows.first { it.flag == FeatureFlag.PRESCRIPTION }
        assertNull(row.serverName)
    }

    @Test
    fun longPressOpensTheEditorForThatFlag() = runTest(testDispatcher) {
        viewModel.sendIntent(FeatureFlagsIntent.OnRowLongPressed(FeatureFlag.PRESCRIPTION))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(FeatureFlag.PRESCRIPTION, viewModel.uiState.value.editingFlag)
    }

    @Test
    fun confirmingAnOverrideWritesItAndClosesTheEditor() = runTest(testDispatcher) {
        viewModel.sendIntent(FeatureFlagsIntent.OnRowLongPressed(FeatureFlag.PRESCRIPTION))

        viewModel.sendIntent(
            FeatureFlagsIntent.OnOverrideConfirmed(
                FeatureFlag.PRESCRIPTION,
                OverrideKind.TEMPORARY_DISABLED,
                "در حال بروزرسانی",
            )
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.uiState.value.editingFlag)
        val override = overrideRepository.overrides.value.getValue(FeatureFlag.PRESCRIPTION)
        assertEquals(FeatureStatus.TemporaryDisabled("در حال بروزرسانی"), override)

        val row = viewModel.uiState.value.rows.first { it.flag == FeatureFlag.PRESCRIPTION }
        assertTrue(row.isOverridden)
        assertEquals(FeatureStatus.TemporaryDisabled("در حال بروزرسانی"), row.status)
    }

    @Test
    fun aBlankMessageIsStoredAsNoMessageAtAll() = runTest(testDispatcher) {
        viewModel.sendIntent(
            FeatureFlagsIntent.OnOverrideConfirmed(FeatureFlag.CONTRACTS, OverrideKind.DISABLED, "   ")
        )
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(FeatureStatus.Disabled(null), overrideRepository.overrides.value.getValue(FeatureFlag.CONTRACTS))
    }

    @Test
    fun clearingAnOverrideFallsBackToTheRealStatus() = runTest(testDispatcher) {
        viewModel.sendIntent(
            FeatureFlagsIntent.OnOverrideConfirmed(FeatureFlag.CONTRACTS, OverrideKind.DISABLED, "x")
        )
        viewModel.sendIntent(FeatureFlagsIntent.OnOverrideCleared(FeatureFlag.CONTRACTS))
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(!overrideRepository.overrides.value.containsKey(FeatureFlag.CONTRACTS))
        val row = viewModel.uiState.value.rows.first { it.flag == FeatureFlag.CONTRACTS }
        assertTrue(!row.isOverridden)
        assertEquals(FeatureStatus.Enabled, row.status)
    }

    @Test
    fun clearAllDropsEveryOverride() = runTest(testDispatcher) {
        viewModel.sendIntent(FeatureFlagsIntent.OnOverrideConfirmed(FeatureFlag.CONTRACTS, OverrideKind.DISABLED, "x"))
        viewModel.sendIntent(FeatureFlagsIntent.OnOverrideConfirmed(FeatureFlag.PRESCRIPTION, OverrideKind.ENABLED, ""))

        viewModel.sendIntent(FeatureFlagsIntent.OnClearAllClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(overrideRepository.overrides.value.isEmpty())
    }
}
