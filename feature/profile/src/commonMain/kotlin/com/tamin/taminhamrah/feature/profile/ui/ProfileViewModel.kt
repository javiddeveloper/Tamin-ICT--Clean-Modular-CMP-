package com.tamin.taminhamrah.feature.profile.ui

import androidx.lifecycle.viewModelScope
import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileEvent
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileIntent
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileUiState
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileUiState.PartialState
import com.tamin.taminhamrah.feature.profile.ui.model.ProfileMenuItem
import com.tamin.taminhamrah.mapper.identity.toPresentation
import com.tamin.taminhamrah.mapper.relation.toPresentation
import com.tamin.taminhamrah.model.DarkThemeConfig
import com.tamin.taminhamrah.repository.TokenStoreManager
import com.tamin.taminhamrah.useCases.auth.GetSignOutUrlUseCase
import com.tamin.taminhamrah.useCases.auth.SignOutUseCase
import com.tamin.taminhamrah.useCases.common.SetThemeUseCase
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.useCases.user.ChangeMobileUseCase
import com.tamin.taminhamrah.useCases.user.GetInsuredActiveBranchUseCase
import com.tamin.taminhamrah.useCases.user.GetRelationTaminAllUseCase
import com.tamin.taminhamrah.useCases.user.SendImageRequestUseCase
import com.tamin.taminhamrah.useCases.user.SubdominantUseCase
import com.tamin.taminhamrah.useCases.user.TaminRelationUseCase
import com.tamin.taminhamrah.useCases.user.UserProfileImageUseCase
import com.tamin.taminhamrah.useCases.user.VerifyChangeMobileUseCase
import com.tamin.taminhamrah.util.HeaderConstant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val tokenStoreManager: TokenStoreManager,
    private val identityInfoUseCase: IdentityInfoUseCase,
    private val getUserProfileImageUseCase: UserProfileImageUseCase,
    private val taminRelationUseCase: TaminRelationUseCase,
    private val sendImageRequestUseCase: SendImageRequestUseCase,
    private val subdominantUseCase: SubdominantUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getSignOutUrlUseCase: GetSignOutUrlUseCase,
    private val getInsuredActiveBranchUseCase: GetInsuredActiveBranchUseCase,
    private val getRelationTaminAllUseCase: GetRelationTaminAllUseCase,
    private val changeMobileUseCase: ChangeMobileUseCase,
    private val verifyChangeMobileUseCase: VerifyChangeMobileUseCase,
    private val setThemeUseCase: SetThemeUseCase
) : BaseViewModel<ProfileUiState, PartialState, ProfileEvent, ProfileIntent>(
    initialState = ProfileUiState()
) {

    override fun handleIntent(intent: ProfileIntent): Flow<PartialState> {
        return when (intent) {
            is ProfileIntent.LoadProfile -> handleLoadProfile(intent.userId)
            is ProfileIntent.Logout -> handleLogout()
            is ProfileIntent.OnItemClick -> handleItemClick(intent.item)
            is ProfileIntent.SendImageRequest -> handleSendImageRequest(
                intent.branchCode,
                intent.filter
            )
            is ProfileIntent.NavigateToDependentsList -> handleNavigateToDependentsList()
            is ProfileIntent.ToggleTheme -> handleToggleTheme(intent.isDark)
        }
    }

    private fun handleLoadProfile(providedUserId: String?): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))

        val userIdFlow = flow {
            val userId = providedUserId ?: tokenStoreManager.getUserId()
            emit(PartialState.SetUserId(userId))
        }
        val imageFlow = flow {
            getUserProfileImageUseCase().collect { imageBase64 ->
                emit(
                    PartialState.ProfileImageLoaded(
                        imageBase64
                    ))
            }
        }
        val identityFlow = flow {
            identityInfoUseCase().collect { identityInfo ->
                emit(
                    PartialState.IdentityInfoLoaded(
                        identityInfo.toPresentation()
                    ))
            }
        }
        val taminRelationFlow = flow {
            taminRelationUseCase().collect { taminRelation ->
                emit(
                    PartialState.TaminRelationLoaded(taminRelation.toPresentation())
                )
            }
        }
        val dependentsCountFlow = flow {
            subdominantUseCase().collect { subdominant ->
                emit(PartialState.DependentsCountLoaded(subdominant.list?.size ?: 0))
            }
        }

        merge(userIdFlow, imageFlow, identityFlow, taminRelationFlow, dependentsCountFlow).collect {
            emit(it)
        }
    }

    private fun handleLogout(): Flow<PartialState> = flow {
        val token = tokenStoreManager.getToken()
        if (token != null) {
            signOutUseCase.invoke(HeaderConstant.AUTHORIZATION_TYPE + token).collect {
                com.tamin.taminhamrah.util.Logger.d("handleLogout", "Sign out response: $it")
            }
        } else {
            // If no token, just perform local logout
            tokenStoreManager.saveToken(null)
            tokenStoreManager.saveRefreshToken(null)
            tokenStoreManager.saveUserId(null)
            tokenStoreManager.setTokenValid(isValid = false)
        }

        val signOutUrl = getSignOutUrlUseCase()
        sendEvent(ProfileEvent.OpenUrl(signOutUrl))
        sendEvent(ProfileEvent.NavigateBack)
    }

    private fun handleItemClick(item: ProfileMenuItem): Flow<PartialState> {
        when (item) {
            ProfileMenuItem.SETTINGS -> sendEvent(ProfileEvent.NavigateToSettings)
            ProfileMenuItem.LOGOUT -> sendIntent(ProfileIntent.Logout)
            ProfileMenuItem.IDENTITY_INFO -> sendEvent(ProfileEvent.NavigateToIdentity)
            ProfileMenuItem.ELECTRONIC_FILE -> sendEvent(ProfileEvent.NavigateToElectronicFile)
            ProfileMenuItem.VERSION_HISTORY -> sendEvent(ProfileEvent.NavigateToVersionHistory)
            ProfileMenuItem.ACTIVE_RELATION -> sendEvent(ProfileEvent.NavigateToActiveRelation)
            ProfileMenuItem.CHANGE_MOBILE -> sendEvent(ProfileEvent.NavigateToChangeMobile)
            ProfileMenuItem.BANK_ACCOUNTS -> sendEvent(ProfileEvent.NavigateToBankAccount)
            ProfileMenuItem.CONTACT_ME -> sendEvent(ProfileEvent.NavigateToContactUs)
            ProfileMenuItem.PERSONAL_INBOX -> sendEvent(ProfileEvent.NavigateToMyInbox)
            ProfileMenuItem.SECURITY -> sendEvent(ProfileEvent.NavigateToSecurity)
            ProfileMenuItem.SHARE -> sendEvent(ProfileEvent.ShareAppLink("https://hamrah.tamin.ir/"))
            ProfileMenuItem.SUPPORT -> sendEvent(ProfileEvent.Support("1420"))
            ProfileMenuItem.REQUESTS -> sendEvent(ProfileEvent.NavigateToUserContracts)
            else -> sendEvent(ProfileEvent.ShowToast("به زودی: ${item.name}"))
        }
        return emptyFlow()
    }

    private fun handleSendImageRequest(branchCode: String, filter: String): Flow<PartialState> = flow {
        emit(PartialState.ImageRequestLoading(true))
        sendImageRequestUseCase(branchCode, filter)
            .collect { result ->
                emit(PartialState.ImageRequestResult(result))
            }
    }


    private fun handleNavigateToDependentsList(): Flow<PartialState> {
        return flow {
            sendEvent(ProfileEvent.NavigateToDependentsList)
        }
    }

    private fun handleGetInsuranceActiveBranch(): Flow<PartialState> {
        return flow {
            emit(PartialState.ScreenStateChanged.Loading)
            getInsuredActiveBranchUseCase.invoke().collect {
                emit(PartialState.ScreenStateChanged.Success)
            }
        }
    }

    private fun handleGetRelationTaminAll(): Flow<PartialState> {
        return flow {
            emit(PartialState.ScreenStateChanged.Loading)
            getRelationTaminAllUseCase.invoke().collect {
                emit(PartialState.ScreenStateChanged.Success)
            }
        }
    }

    private fun handleToggleTheme(isDark: Boolean): Flow<PartialState> {
        viewModelScope.launch {
            val config = if (isDark) DarkThemeConfig.DARK else DarkThemeConfig.LIGHT
            setThemeUseCase(config)
        }
        return emptyFlow()
    }


    override fun reduceState(
        currentState: ProfileUiState,
        partialState: PartialState
    ): ProfileUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(
            isLoading = partialState.isLoading
        )
        is PartialState.Error -> currentState.copy(
            isLoading = false,
            error = partialState.message
        )

        is PartialState.SetUserId -> currentState.copy(
            userId = partialState.userId
        )

        is PartialState.ProfileImageLoaded -> currentState.copy(
            isProfileImageLoading = false,
            profileImage = partialState.image
        )
        is PartialState.IdentityInfoLoaded -> currentState.copy(
            isLoading = false,
            identityInfo = partialState.info
        )

        is PartialState.TaminRelationLoaded -> currentState.copy(
            taminRelation = partialState.relation
        )

        is PartialState.DependentsCountLoaded -> currentState.copy(
            dependentsCount = partialState.count
        )

        is PartialState.ImageRequestLoading -> currentState.copy(
            isImageRequestLoading = partialState.isLoading,
            imageRequestError = null
        )

        is PartialState.ImageRequestResult -> currentState.copy(
            isImageRequestLoading = false,
            imageRequestResult = partialState.result
        )
        is PartialState.ImageRequestError -> currentState.copy(
            isImageRequestLoading = false,
            imageRequestError = partialState.message
        )

        is PartialState.ScreenStateChanged -> when (partialState) {
            is PartialState.ScreenStateChanged.Loading -> currentState.copy(isLoading = true)
            is PartialState.ScreenStateChanged.Success -> currentState.copy(isLoading = false)
            is PartialState.ScreenStateChanged.Error -> currentState.copy(isLoading = false, error = partialState.message)
        }
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)

}
