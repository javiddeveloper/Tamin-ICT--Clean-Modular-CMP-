package com.tamin.taminhamrah.feature.profile.ui

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.feature.profile.fake.FakeProfileUserRepository
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileEvent
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileIntent
import com.tamin.taminhamrah.feature.profile.ui.model.ProfileMenuItem
import com.tamin.taminhamrah.model.BaseUrlKey
import com.tamin.taminhamrah.model.DarkThemeConfig
import com.tamin.taminhamrah.model.FontSizeOption
import com.tamin.taminhamrah.model.UserData
import com.tamin.taminhamrah.model.activeRelation.ActiveRelationDN
import com.tamin.taminhamrah.model.agent.AgentMockMode
import com.tamin.taminhamrah.model.auth.DebugLoginResultDN
import com.tamin.taminhamrah.model.auth.TokenSlot
import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.model.common.ProvinceDN
import com.tamin.taminhamrah.model.identity.IdentityInfoDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.payment.PaymentMockMode
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.AuthRepository
import com.tamin.taminhamrah.repository.CityProvinceRepository
import com.tamin.taminhamrah.repository.DeveloperOptionsRepository
import com.tamin.taminhamrah.repository.TokenStoreManager
import com.tamin.taminhamrah.repository.UserPreferencesRepository
import com.tamin.taminhamrah.useCases.auth.GetSignOutUrlUseCase
import com.tamin.taminhamrah.useCases.auth.SignOutUseCase
import com.tamin.taminhamrah.useCases.common.SetThemeUseCase
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.useCases.user.GetRelationTaminAllUseCase
import com.tamin.taminhamrah.useCases.user.SubdominantUseCase
import com.tamin.taminhamrah.useCases.user.TaminRelationUseCase
import com.tamin.taminhamrah.useCases.user.UserProfileImageUseCase
import com.tamin.taminhamrah.util.HeaderConstant
import com.tamin.taminhamrah.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
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
import kotlin.test.assertIs
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val featureManager = FakeFeatureManager()
    private val userRepository = FakeProfileUserRepository()
    private val tokenStore = FakeTokenStoreManager()
    private val authRepository = FakeAuthRepository()
    private val developerOptions = FakeDeveloperOptionsRepository()
    private val preferences = FakeUserPreferencesRepository()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        // Logout logs the server's answer, and android.util.Log throws in a JVM unit test.
        Logger.enabled = false
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        Logger.enabled = true
    }

    private fun createViewModel() = ProfileViewModel(
        tokenStoreManager = tokenStore,
        identityInfoUseCase = IdentityInfoUseCase(userRepository, UnusedCityProvinceRepository),
        getUserProfileImageUseCase = UserProfileImageUseCase(userRepository),
        taminRelationUseCase = TaminRelationUseCase(userRepository),
        subdominantUseCase = SubdominantUseCase(userRepository),
        signOutUseCase = SignOutUseCase(authRepository),
        getSignOutUrlUseCase = GetSignOutUrlUseCase(developerOptions),
        getRelationTaminAllUseCase = GetRelationTaminAllUseCase(userRepository),
        setThemeUseCase = SetThemeUseCase(preferences),
        featureManager = featureManager,
    )

    // --- Loading the profile -------------------------------------------------------------------

    @Test
    fun loadProfile_fillsEveryCardFromItsSource() = runTest(testDispatcher) {
        tokenStore.storedUserId = "stored-user"
        userRepository.profileImage = "base64-image"
        userRepository.identityInfo = identity(firstName = "سعید", lastName = "نامی")
        userRepository.activeRelations = listOf(
            relation(id = 1, relationDescription = "شاغل"),
            relation(id = 2, relationDescription = "شاغل"),
            relation(id = 3, relationDescription = null),
        )
        val viewModel = createViewModel()

        viewModel.sendIntent(ProfileIntent.LoadProfile())

        val state = viewModel.uiState.value
        assertEquals("stored-user", state.userId)
        assertEquals("base64-image", state.profileImage)
        assertFalse(state.isProfileImageLoading)
        assertEquals("سعید نامی", state.identityInfo?.fullName)
        assertEquals("0020939111", state.taminRelation?.nationalId)
        assertEquals(2, state.dependentsCount)
        assertEquals(2, state.activeRelationCount)
        assertEquals(1, state.inactiveRelationCount)
        assertFalse(state.isActiveRelationLoading)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun loadProfile_prefersTheProvidedUserIdOverTheStoredOne() = runTest(testDispatcher) {
        tokenStore.storedUserId = "stored-user"
        val viewModel = createViewModel()

        viewModel.sendIntent(ProfileIntent.LoadProfile(userId = "provided-user"))

        assertEquals("provided-user", viewModel.uiState.value.userId)
    }

    @Test
    fun loadProfile_identityFailure_endsLoadingWithItsMessage() = runTest(testDispatcher) {
        userRepository.identityError = RuntimeException("سرویس هویت در دسترس نیست")
        val viewModel = createViewModel()

        viewModel.events.test {
            viewModel.sendIntent(ProfileIntent.LoadProfile())

            // A failed section toasts once and falls back instead of parking an error in state.
            assertIs<ProfileEvent.ShowToast>(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertFalse(viewModel.uiState.value.isLoading)
    }

    // --- Logout --------------------------------------------------------------------------------

    @Test
    fun logout_withToken_signsOutOnTheServer_thenOpensSignOutPageAndLeaves() = runTest(testDispatcher) {
        tokenStore.storedToken = "access-token"
        val viewModel = createViewModel()

        viewModel.events.test {
            viewModel.sendIntent(ProfileIntent.Logout)

            assertEquals(ProfileEvent.OpenUrl(GetSignOutUrlUseCase(developerOptions)()), awaitItem())
            assertEquals(ProfileEvent.NavigateBack, awaitItem())
            expectNoEvents()
        }
        assertEquals(HeaderConstant.AUTHORIZATION_TYPE + "access-token", authRepository.signedOutWith)
    }

    @Test
    fun logout_withoutToken_clearsTheSessionLocally_andSkipsTheServer() = runTest(testDispatcher) {
        tokenStore.storedToken = null
        tokenStore.storedRefreshToken = "refresh"
        tokenStore.storedUserId = "stored-user"
        tokenStore.tokenValid = true
        val viewModel = createViewModel()

        viewModel.events.test {
            viewModel.sendIntent(ProfileIntent.Logout)

            assertEquals(ProfileEvent.OpenUrl(GetSignOutUrlUseCase(developerOptions)()), awaitItem())
            assertEquals(ProfileEvent.NavigateBack, awaitItem())
            expectNoEvents()
        }
        assertNull(authRepository.signedOutWith)
        assertNull(tokenStore.storedRefreshToken)
        assertNull(tokenStore.storedUserId)
        assertFalse(tokenStore.tokenValid)
    }

    @Test
    fun logoutMenuItem_runsTheLogout() = runTest(testDispatcher) {
        tokenStore.storedToken = "access-token"
        val viewModel = createViewModel()

        viewModel.events.test {
            viewModel.sendIntent(ProfileIntent.OnItemClick(ProfileMenuItem.LOGOUT))

            assertEquals(ProfileEvent.OpenUrl(GetSignOutUrlUseCase(developerOptions)()), awaitItem())
            assertEquals(ProfileEvent.NavigateBack, awaitItem())
        }
        assertEquals(HeaderConstant.AUTHORIZATION_TYPE + "access-token", authRepository.signedOutWith)
    }

    // --- Menu ----------------------------------------------------------------------------------

    @Test
    fun menuItems_eachSendTheirOwnEvent() = runTest(testDispatcher) {
        val expected = mapOf(
            ProfileMenuItem.SETTINGS to ProfileEvent.NavigateToSettings,
            ProfileMenuItem.IDENTITY_INFO to ProfileEvent.NavigateToIdentity,
            ProfileMenuItem.ELECTRONIC_FILE to ProfileEvent.NavigateToElectronicFile,
            ProfileMenuItem.VERSION_HISTORY to ProfileEvent.NavigateToVersionHistory,
            ProfileMenuItem.ACTIVE_RELATION to ProfileEvent.NavigateToActiveRelation,
            ProfileMenuItem.CHANGE_MOBILE to ProfileEvent.NavigateToChangeMobile,
            ProfileMenuItem.BANK_ACCOUNTS to ProfileEvent.NavigateToBankAccount,
            ProfileMenuItem.CONTACT_ME to ProfileEvent.NavigateToContactUs,
            ProfileMenuItem.PERSONAL_INBOX to ProfileEvent.NavigateToMyInbox,
            ProfileMenuItem.SECURITY to ProfileEvent.NavigateToSecurity,
            ProfileMenuItem.DEVELOPER_OPTIONS to ProfileEvent.NavigateToDeveloperOptions,
            ProfileMenuItem.SHARE to ProfileEvent.ShareAppLink("https://hamrah.tamin.ir/"),
            ProfileMenuItem.SUPPORT to ProfileEvent.Support("1420"),
            ProfileMenuItem.REQUESTS to ProfileEvent.NavigateToUserContracts,
            ProfileMenuItem.SAVE_EVENTS to ProfileEvent.NavigateToSaveEvents,
        )
        val viewModel = createViewModel()

        viewModel.events.test {
            expected.forEach { (item, event) ->
                viewModel.sendIntent(ProfileIntent.OnItemClick(item))
                assertEquals(event, awaitItem(), "menu item $item")
            }
            expectNoEvents()
        }
    }

    @Test
    fun unmappedMenuItem_showsComingSoon() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.events.test {
            viewModel.sendIntent(ProfileIntent.OnItemClick(ProfileMenuItem.DEPENDENTS))

            assertEquals(ProfileEvent.ShowToast("به زودی: DEPENDENTS"), awaitItem())
        }
    }

    @Test
    fun dependentsShortcut_navigatesToTheList() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.events.test {
            viewModel.sendIntent(ProfileIntent.NavigateToDependentsList)

            assertEquals(ProfileEvent.NavigateToDependentsList, awaitItem())
        }
    }

    // --- Theme ---------------------------------------------------------------------------------

    @Test
    fun toggleTheme_savesDarkOrLight() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.sendIntent(ProfileIntent.ToggleTheme(isDark = true))
        assertEquals(DarkThemeConfig.DARK, preferences.savedTheme)

        viewModel.sendIntent(ProfileIntent.ToggleTheme(isDark = false))
        assertEquals(DarkThemeConfig.LIGHT, preferences.savedTheme)
    }

    // --- Camera badge: the EDIT_IMAGE gate, one test per FeatureStatus branch ------------------

    @Test
    fun editPhoto_enabled_navigatesToTheForm() = runTest(testDispatcher) {
        featureManager.status = FeatureStatus.Enabled
        val viewModel = createViewModel()

        viewModel.events.test {
            viewModel.sendIntent(ProfileIntent.EditPhotoClicked)

            assertEquals(ProfileEvent.NavigateToEditProfilePhoto, awaitItem())
            expectNoEvents()
        }
        assertEquals(FeatureFlag.EDIT_IMAGE, featureManager.lastFlag)
    }

    @Test
    fun editPhoto_disabled_showsTheMessage_andStaysOnProfile() = runTest(testDispatcher) {
        featureManager.status = FeatureStatus.Disabled("سرویس غیرفعال است")
        val viewModel = createViewModel()

        viewModel.events.test {
            viewModel.sendIntent(ProfileIntent.EditPhotoClicked)

            assertEquals(ProfileEvent.ShowToast("سرویس غیرفعال است"), awaitItem())
            expectNoEvents()
        }
    }

    @Test
    fun editPhoto_disabledWithoutMessage_doesNothing() = runTest(testDispatcher) {
        featureManager.status = FeatureStatus.Disabled(null)
        val viewModel = createViewModel()

        viewModel.events.test {
            viewModel.sendIntent(ProfileIntent.EditPhotoClicked)

            expectNoEvents()
        }
    }

    @Test
    fun editPhoto_temporaryDisabled_showsTheMessage_andStaysOnProfile() = runTest(testDispatcher) {
        featureManager.status = FeatureStatus.TemporaryDisabled("موقتاً در دسترس نیست")
        val viewModel = createViewModel()

        viewModel.events.test {
            viewModel.sendIntent(ProfileIntent.EditPhotoClicked)

            assertEquals(ProfileEvent.ShowToast("موقتاً در دسترس نیست"), awaitItem())
            expectNoEvents()
        }
    }

    @Test
    fun editPhoto_enabledWithError_showsTheMessage_thenStillNavigates() = runTest(testDispatcher) {
        featureManager.status = FeatureStatus.EnabledWithError("ممکن است با اختلال همراه باشد")
        val viewModel = createViewModel()

        viewModel.events.test {
            viewModel.sendIntent(ProfileIntent.EditPhotoClicked)

            assertEquals(ProfileEvent.ShowToast("ممکن است با اختلال همراه باشد"), awaitItem())
            assertEquals(ProfileEvent.NavigateToEditProfilePhoto, awaitItem())
            expectNoEvents()
        }
    }

    @Test
    fun editPhoto_webView_opensTheUrlInsteadOfTheForm() = runTest(testDispatcher) {
        featureManager.status = FeatureStatus.WebView("https://es.tamin.ir/edit-image")
        val viewModel = createViewModel()

        viewModel.events.test {
            viewModel.sendIntent(ProfileIntent.EditPhotoClicked)

            assertEquals(ProfileEvent.OpenUrl("https://es.tamin.ir/edit-image"), awaitItem())
            expectNoEvents()
        }
    }
}

private fun identity(firstName: String, lastName: String) = IdentityInfoDN(
    cityOfBirthId = null,
    cityOfIssueId = null,
    countryId = null,
    dateOfBirth = null,
    fatherName = null,
    firstName = firstName,
    gender = null,
    id = 1,
    idCardNumber = null,
    idCardSerial1 = null,
    idCardSerial2 = null,
    lastName = lastName,
    nationalId = "0020939111",
    ssn = null,
)

/** A relation counts as active when it has a description — see `ActiveRelationDN.toUiModel`. */
private fun relation(id: Int, relationDescription: String?) = ActiveRelationDN(
    id = id,
    firstName = null,
    lastName = null,
    nationalId = null,
    insuranceId = null,
    birthDate = null,
    relationWithTaminId = null,
    startDate = null,
    endDate = null,
    workshopId = null,
    workshopName = null,
    organizationId = null,
    organizationName = null,
    relationDescription = relationDescription,
)

private class FakeFeatureManager(var status: FeatureStatus = FeatureStatus.Enabled) : FeatureManager {
    var lastFlag: FeatureFlag? = null

    override fun getFeatureStatus(flag: FeatureFlag): Flow<FeatureStatus> {
        lastFlag = flag
        return flowOf(status)
    }

    override suspend fun isFeatureEnabled(flag: FeatureFlag): Boolean = status is FeatureStatus.Enabled
    override suspend fun getDisabledMessage(flag: FeatureFlag): String? = null
}

// Members ProfileViewModel never reaches fail loudly, so a test that starts relying on one says so.
private fun unused(): Nothing = error("not used by ProfileViewModel")

/** Only the single-slot session members the ViewModel reads and clears are real. */
private class FakeTokenStoreManager : TokenStoreManager {
    var storedToken: String? = null
    var storedRefreshToken: String? = null
    var storedUserId: String? = null
    var tokenValid = false

    override fun saveToken(token: String?) { storedToken = token }
    override fun getToken(): String? = storedToken
    override fun saveRefreshToken(refreshToken: String?) { storedRefreshToken = refreshToken }
    override fun getRefreshToken(): String? = storedRefreshToken
    override fun saveUserId(userId: String?) { storedUserId = userId }
    override fun getUserId(): String? = storedUserId
    override suspend fun setTokenValid(isValid: Boolean) { tokenValid = isValid }

    override fun getToken(slot: TokenSlot): String? = unused()
    override fun saveToken(slot: TokenSlot, token: String?) = unused()
    override fun getRefreshToken(slot: TokenSlot): String? = unused()
    override fun saveRefreshToken(slot: TokenSlot, refreshToken: String?) = unused()
    override fun getActiveSlot(): TokenSlot = unused()
    override fun activeSlotFlow(): Flow<TokenSlot> = unused()
    override suspend fun setActiveSlot(slot: TokenSlot) = unused()
    override fun saveUserType(userType: String?) = unused()
    override fun getUserType(): String? = unused()
    override fun saveCodeVerifier(codeVerifier: String?) = unused()
    override fun getCodeVerifier(): String? = unused()
    override fun tokenValidFlow(): Flow<Boolean> = unused()
    override fun isAuthProcessingFlow(): Flow<Boolean> = unused()
    override fun setAuthProcessing(isProcessing: Boolean) = unused()
}

private class FakeAuthRepository : AuthRepository {
    var signedOutWith: String? = null

    override suspend fun signOut(token: String): Flow<String> {
        signedOutWith = token
        return flowOf("OK")
    }

    override val isLoggedIn: Flow<Boolean> get() = unused()
    override suspend fun getAccessToken(): String? = unused()
    override suspend fun exchangeCodeForTokens(
        code: String,
        codeVerifier: String,
        audience: String,
        redirectUri: String,
        clientId: String,
    ): Boolean = unused()
    override suspend fun debugClientCredentialsLogin(clientId: String, clientSecret: String): DebugLoginResultDN = unused()
    override suspend fun refreshToken(): Boolean = unused()
    override suspend fun refreshTokenSlot(slot: TokenSlot): Boolean = unused()
    override suspend fun switchTokenSlot(slot: TokenSlot) = unused()
    override suspend fun logout() = unused()
    override suspend fun revokeToken(): Boolean = unused()
}

private class FakeDeveloperOptionsRepository : DeveloperOptionsRepository {
    override fun getEffectiveBaseUrl(key: BaseUrlKey): String = "https://account.test/"

    override fun observeOverrides(): Flow<Map<BaseUrlKey, String>> = unused()
    override fun setOverride(key: BaseUrlKey, url: String) = unused()
    override fun clearOverride(key: BaseUrlKey) = unused()
    override fun getPaymentMockMode(): PaymentMockMode = unused()
    override fun observePaymentMockMode(): Flow<PaymentMockMode> = unused()
    override fun setPaymentMockMode(mode: PaymentMockMode) = unused()
    override fun getAgentMockMode(): AgentMockMode = unused()
    override fun observeAgentMockMode(): Flow<AgentMockMode> = unused()
    override fun setAgentMockMode(mode: AgentMockMode) = unused()
}

private class FakeUserPreferencesRepository : UserPreferencesRepository {
    var savedTheme: DarkThemeConfig? = null

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) { savedTheme = darkThemeConfig }

    override val userData: StateFlow<UserData> get() = unused()
    override val observeDarkThemeConfig: Flow<DarkThemeConfig> get() = unused()
    override val observeBiometricEnabled: Flow<Boolean> get() = unused()
    override val observeFontSize: Flow<FontSizeOption> get() = unused()
    override suspend fun setBiometricEnabled(enabled: Boolean) = unused()
    override suspend fun completeBiometricEnrollmentPrompt(enabled: Boolean) = unused()
    override suspend fun setFontSize(fontSize: FontSizeOption) = unused()
}

private object UnusedCityProvinceRepository : CityProvinceRepository {
    override fun getCity(cityId: String): Flow<CityDN> = unused()
    override fun getProvince(provinceId: String): Flow<ProvinceDN> = unused()
    override fun getProvincesPage(query: ApiQueryParamDN): Flow<PageDN<ProvinceDN>> = unused()
    override fun getCitiesPage(query: ApiQueryParamDN): Flow<PageDN<CityDN>> = unused()
    override fun getCitiesByProvincePage(provinceCode: String, query: ApiQueryParamDN): Flow<PageDN<CityDN>> = unused()
}
