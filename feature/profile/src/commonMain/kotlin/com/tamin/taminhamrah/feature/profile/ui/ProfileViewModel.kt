package com.tamin.taminhamrah.feature.profile.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.profile.ui.contract.AsyncState
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileUiState
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileUiState.PartialState
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileIntent
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileEvent
import com.tamin.taminhamrah.repository.TokenStoreManager
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.useCases.user.TaminRelationUseCase
import com.tamin.taminhamrah.useCases.user.UserProfileImageUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge

class ProfileViewModel(
    private val tokenStoreManager: TokenStoreManager,
    private val identityInfoUseCase: IdentityInfoUseCase,
    private val getUserProfileImageUseCase: UserProfileImageUseCase,
    private val taminRelationUseCase: TaminRelationUseCase
) : BaseViewModel<ProfileUiState, PartialState, ProfileEvent, ProfileIntent>(
    initialState = ProfileUiState()
) {

    override fun handleIntent(intent: ProfileIntent): Flow<PartialState> {
        return when (intent) {
            is ProfileIntent.LoadProfile -> handleLoadProfile(intent.userId)
            is ProfileIntent.Logout -> handleLogout()
            is ProfileIntent.OnItemClick -> handleItemClick(intent.title)
        }
    }

    private fun handleLoadProfile(providedUserId: String?): Flow<PartialState> {

        val userIdFlow = flow {
            emit(PartialState.ScreenStateChanged(AsyncState.Loading))
            val userId = providedUserId ?: tokenStoreManager.getUserId()
            emit(PartialState.SetUserId(userId))
        }
        val imageFlow = flow {
            emit(PartialState.ProfileImageChanged(AsyncState.Loading))
            val imageBase64 = getUserProfileImageUseCase()
            emit(PartialState.ProfileImageChanged(
                imageBase64.let { AsyncState.Success(it) }
            ))
        }
        val identityFlow = flow {
            emit(PartialState.IdentityInfoChanged(AsyncState.Loading))
            identityInfoUseCase().collect { identityInfo ->
                emit(PartialState.IdentityInfoChanged(
                    identityInfo.let { AsyncState.Success(it) }
                ))
            }
        }
        val taminRelationFlow = flow {
            emit(PartialState.TaminRelationChanged(AsyncState.Loading))
            taminRelationUseCase().collect { taminRelation ->
                emit(PartialState.TaminRelationChanged(
                    AsyncState.Success(taminRelation)
                ))
            }
        }

        return merge(userIdFlow, imageFlow, identityFlow, taminRelationFlow)
    }

    private fun handleLogout(): Flow<PartialState> = flow {
        tokenStoreManager.saveToken(null)
        tokenStoreManager.saveRefreshToken(null)
        tokenStoreManager.setTokenValid(isValid = false)
        sendEvent(ProfileEvent.NavigateBack)
    }

    private fun handleItemClick(title: String): Flow<PartialState> {
        when (title) {
            "تنظیمات" -> sendEvent(ProfileEvent.NavigateToSettings)
            "خروج از حساب کاربری" -> sendIntent(ProfileIntent.Logout)
            else -> sendEvent(ProfileEvent.ShowToast("کلیک بر روی: $title"))
        }
        return emptyFlow()
    }

    override fun reduceState(
        currentState: ProfileUiState,
        partialState: PartialState
    ): ProfileUiState = when (partialState) {
        is PartialState.ScreenStateChanged -> currentState.copy(
            screenState = partialState.state
        )
        is PartialState.SetUserId -> currentState.copy(
            screenState = AsyncState.Success(Unit),
            userId = partialState.userId
        )
        is PartialState.ProfileImageChanged -> currentState.copy(
            profileImageState = partialState.state
        )
        is PartialState.IdentityInfoChanged -> currentState.copy(
            identityInfoState = partialState.state
        )
        is PartialState.TaminRelationChanged -> currentState.copy(
            taminRelationState = partialState.state
        )
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.ScreenStateChanged(AsyncState.Error(message))
}
