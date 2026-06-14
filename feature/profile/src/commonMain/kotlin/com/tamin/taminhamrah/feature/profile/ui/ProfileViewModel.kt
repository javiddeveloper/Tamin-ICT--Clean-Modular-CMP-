package com.tamin.taminhamrah.feature.profile.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileUiState
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileUiState.PartialState
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileIntent
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileMenuItem
import com.tamin.taminhamrah.feature.profile.ui.contract.ProfileEvent
import com.tamin.taminhamrah.mapper.identity.toPresentation
import com.tamin.taminhamrah.mapper.relation.toPresentation
import com.tamin.taminhamrah.repository.TokenStoreManager
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.useCases.user.SendImageRequestUseCase
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
    private val taminRelationUseCase: TaminRelationUseCase,
    private val sendImageRequestUseCase: SendImageRequestUseCase,
) : BaseViewModel<ProfileUiState, PartialState, ProfileEvent, ProfileIntent>(
    initialState = ProfileUiState()
) {

    override fun handleIntent(intent: ProfileIntent): Flow<PartialState> {
        return when (intent) {
            is ProfileIntent.LoadProfile -> handleLoadProfile(intent.userId)
            is ProfileIntent.Logout -> handleLogout()
            is ProfileIntent.OnItemClick -> handleItemClick(intent.item)
            is ProfileIntent.SendImageRequest -> handleSendImageRequest(intent.branchCode, intent.filter)
        }
    }

    private fun handleLoadProfile(providedUserId: String?): Flow<PartialState> {

        val userIdFlow = flow {
            emit(PartialState.Loading(true))
            val userId = providedUserId ?: tokenStoreManager.getUserId()
            emit(PartialState.SetUserId(userId))
        }
        val imageFlow = flow {
            getUserProfileImageUseCase().collect { imageBase64 ->
                emit(PartialState.ProfileImageLoaded(imageBase64))
            }
        }
        val identityFlow = flow {
            identityInfoUseCase().collect { identityInfo ->
                emit(PartialState.IdentityInfoLoaded(identityInfo.toPresentation()))
            }
        }
        val taminRelationFlow = flow {
            taminRelationUseCase().collect { taminRelation ->
                emit(PartialState.TaminRelationLoaded(taminRelation.toPresentation()))
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

    private fun handleItemClick(item: ProfileMenuItem): Flow<PartialState> {
        when (item) {
            ProfileMenuItem.SETTINGS -> sendEvent(ProfileEvent.NavigateToSettings)
            ProfileMenuItem.LOGOUT -> sendIntent(ProfileIntent.Logout)
            ProfileMenuItem.IDENTITY_INFO -> sendEvent(ProfileEvent.NavigateToIdentity)
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
            isLoading = false,
            userId = partialState.userId
        )
        is PartialState.ProfileImageLoaded -> currentState.copy(
            profileImage = partialState.image
        )
        is PartialState.IdentityInfoLoaded -> currentState.copy(
            identityInfo = partialState.info
        )
        is PartialState.TaminRelationLoaded -> currentState.copy(
            taminRelation = partialState.relation
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
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)
}
