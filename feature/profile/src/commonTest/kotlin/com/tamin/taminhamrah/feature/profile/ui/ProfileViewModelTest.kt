package com.tamin.taminhamrah.feature.profile.ui

import app.cash.turbine.test
import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileEvent
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileIntent
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.FakeProfileUserRepository
import com.tamin.taminhamrah.model.BaseUrlKey
import com.tamin.taminhamrah.model.DarkThemeConfig
import com.tamin.taminhamrah.model.FontSizeOption
import com.tamin.taminhamrah.model.UserData
import com.tamin.taminhamrah.model.auth.DebugLoginResultDN
import com.tamin.taminhamrah.model.auth.TokenSlot
import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.common.CityListResultDN
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.model.common.ProvinceDN
import com.tamin.taminhamrah.model.payment.PaymentMockMode
import com.tamin.taminhamrah.repository.AuthRepository
import com.tamin.taminhamrah.repository.CityProvinceRepository
import com.tamin.taminhamrah.repository.DeveloperOptionsRepository
import com.tamin.taminhamrah.repository.TokenStoreManager
import com.tamin.taminhamrah.repository.UserPreferencesRepository
import com.tamin.taminhamrah.useCases.auth.GetSignOutUrlUseCase
import com.tamin.taminhamrah.useCases.auth.SignOutUseCase
import com.tamin.taminhamrah.useCases.common.SetThemeUseCase
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.useCases.user.ChangeMobileUseCase
import com.tamin.taminhamrah.useCases.user.GetInsuredActiveBranchUseCase
import com.tamin.taminhamrah.useCases.user.GetRelationTaminAllUseCase
import com.tamin.taminhamrah.useCases.user.SubdominantUseCase
import com.tamin.taminhamrah.useCases.user.TaminRelationUseCase
import com.tamin.taminhamrah.useCases.user.UserProfileImageUseCase
import com.tamin.taminhamrah.useCases.user.VerifyChangeMobileUseCase
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

/** Covers the camera badge's `EDIT_IMAGE` gate: one test per [FeatureStatus] branch. */
@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val featureManager = FakeFeatureManager()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): ProfileViewModel {
        val userRepository = FakeProfileUserRepository()
        return ProfileViewModel(
            tokenStoreManager = UnusedTokenStoreManager,
            identityInfoUseCase = IdentityInfoUseCase(userRepository, UnusedCityProvinceRepository),
            getUserProfileImageUseCase = UserProfileImageUseCase(userRepository),
            taminRelationUseCase = TaminRelationUseCase(userRepository),
            subdominantUseCase = SubdominantUseCase(userRepository),
            signOutUseCase = SignOutUseCase(UnusedAuthRepository),
            getSignOutUrlUseCase = GetSignOutUrlUseCase(UnusedDeveloperOptionsRepository),
            getInsuredActiveBranchUseCase = GetInsuredActiveBranchUseCase(userRepository),
            getRelationTaminAllUseCase = GetRelationTaminAllUseCase(userRepository),
            changeMobileUseCase = ChangeMobileUseCase(userRepository),
            verifyChangeMobileUseCase = VerifyChangeMobileUseCase(userRepository),
            setThemeUseCase = SetThemeUseCase(UnusedUserPreferencesRepository),
            featureManager = featureManager,
        )
    }

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

private class FakeFeatureManager(var status: FeatureStatus = FeatureStatus.Enabled) : FeatureManager {
    var lastFlag: FeatureFlag? = null

    override fun getFeatureStatus(flag: FeatureFlag): Flow<FeatureStatus> {
        lastFlag = flag
        return flowOf(status)
    }

    override suspend fun isFeatureEnabled(flag: FeatureFlag): Boolean = status is FeatureStatus.Enabled
    override suspend fun getDisabledMessage(flag: FeatureFlag): String? = null
}

// The edit-photo gate touches none of the collaborators below; they exist only so the ViewModel
// can be constructed, and fail loudly if a test ever starts reaching them.
private fun unused(): Nothing = error("not used by the edit-photo gate")

private object UnusedTokenStoreManager : TokenStoreManager {
    override fun saveToken(token: String?) = unused()
    override fun getToken(): String = unused()
    override fun saveRefreshToken(refreshToken: String?) = unused()
    override fun getRefreshToken(): String = unused()
    override fun getToken(slot: TokenSlot): String = unused()
    override fun saveToken(slot: TokenSlot, token: String?) = unused()
    override fun getRefreshToken(slot: TokenSlot): String = unused()
    override fun saveRefreshToken(slot: TokenSlot, refreshToken: String?) = unused()
    override fun getActiveSlot(): TokenSlot = unused()
    override fun activeSlotFlow(): Flow<TokenSlot> = unused()
    override suspend fun setActiveSlot(slot: TokenSlot) = unused()
    override fun saveUserId(userId: String?) = unused()
    override fun getUserId(): String = unused()
    override fun saveUserType(userType: String?) = unused()
    override fun getUserType(): String = unused()
    override fun saveCodeVerifier(codeVerifier: String?) = unused()
    override fun getCodeVerifier(): String = unused()
    override fun tokenValidFlow(): Flow<Boolean> = unused()
    override suspend fun setTokenValid(isValid: Boolean) = unused()
    override fun isAuthProcessingFlow(): Flow<Boolean> = unused()
    override fun setAuthProcessing(isProcessing: Boolean) = unused()
}

private object UnusedCityProvinceRepository : CityProvinceRepository {
    override fun getCity(cityId: String): Flow<CityDN> = unused()
    override fun getProvince(provinceId: String): Flow<ProvinceDN> = unused()
    override fun getProvinces(): Flow<List<ProvinceDN>> = unused()
    override fun getCities(cityName: String?, provinceCode: String?): Flow<List<CityDN>> = unused()
    override fun getCitiesByProvince(provinceCode: String): Flow<CityListResultDN> = unused()
}

private object UnusedAuthRepository : AuthRepository {
    override val isLoggedIn: Flow<Boolean> get() = unused()
    override suspend fun getAccessToken(): String = unused()
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
    override suspend fun signOut(token: String): Flow<String> = unused()
    override suspend fun revokeToken(): Boolean = unused()
}

private object UnusedDeveloperOptionsRepository : DeveloperOptionsRepository {
    override fun getEffectiveBaseUrl(key: BaseUrlKey): String = unused()
    override fun observeOverrides(): Flow<Map<BaseUrlKey, String>> = unused()
    override fun setOverride(key: BaseUrlKey, url: String) = unused()
    override fun clearOverride(key: BaseUrlKey) = unused()
    override fun getPaymentMockMode(): PaymentMockMode = unused()
    override fun observePaymentMockMode(): Flow<PaymentMockMode> = unused()
    override fun setPaymentMockMode(mode: PaymentMockMode) = unused()
}

private object UnusedUserPreferencesRepository : UserPreferencesRepository {
    override val userData: StateFlow<UserData> get() = unused()
    override val observeDarkThemeConfig: Flow<DarkThemeConfig> get() = unused()
    override val observeBiometricEnabled: Flow<Boolean> get() = unused()
    override val observeFontSize: Flow<FontSizeOption> get() = unused()
    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) = unused()
    override suspend fun setBiometricEnabled(enabled: Boolean) = unused()
    override suspend fun completeBiometricEnrollmentPrompt(enabled: Boolean) = unused()
    override suspend fun setFontSize(fontSize: FontSizeOption) = unused()
}
