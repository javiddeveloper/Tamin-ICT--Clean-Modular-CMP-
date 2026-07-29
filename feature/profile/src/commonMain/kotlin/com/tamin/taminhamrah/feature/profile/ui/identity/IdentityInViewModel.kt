package com.tamin.taminhamrah.feature.profile.ui.identity

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.profile.ui.identity.contract.IdentityInEvent
import com.tamin.taminhamrah.feature.profile.ui.identity.contract.IdentityInIntent
import com.tamin.taminhamrah.feature.profile.ui.identity.contract.IdentityInUiState
import com.tamin.taminhamrah.feature.profile.ui.identity.contract.IdentityInUiState.PartialState
import com.tamin.taminhamrah.mapper.identity.toPresentation
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.identity.IdentityInfoUseCase
import com.tamin.taminhamrah.useCases.user.GetUserProfileUseCase
import com.tamin.taminhamrah.useCases.user.UserProfileImageUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

class IdentityInViewModel(
    private val identityInfoUseCase: IdentityInfoUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val userProfileImageUseCase: UserProfileImageUseCase,
) : BaseViewModel<IdentityInUiState, PartialState, IdentityInEvent, IdentityInIntent>(
    initialState = IdentityInUiState()
) {

    override fun handleIntent(intent: IdentityInIntent): Flow<PartialState> {
        return when (intent) {
            is IdentityInIntent.LoadIdentity -> loadIdentity()
            is IdentityInIntent.OnBackClicked -> flow { sendEvent(IdentityInEvent.NavigateBack) }
        }
    }

    /**
     * The screen is fed by three calls, merged rather than chained so the record paints as soon as
     * it lands instead of waiting on the photo.
     *
     * Only the identity call can fail the screen: the contact details and the photo are extras, so
     * a failure there leaves their rows reading as absent rather than blanking the page. The
     * previous app did the same — it filled mobile and email from the stored user profile, because
     * the identity endpoint has never returned them.
     */
    private fun loadIdentity(): Flow<PartialState> {
        val identityFlow = flow {
            emit(PartialState.Loading(true))
            identityInfoUseCase()
                .map { PartialState.IdentityLoaded(it.toPresentation()) as PartialState }
                .catch { emit(PartialState.Error(it.toSingleLineMessage())) }
                .collect { emit(it) }
        }

        val contactFlow = flow {
            getUserProfileUseCase()
                .map { PartialState.ContactLoaded(it.mobile, it.email) as PartialState }
                .catch { emit(PartialState.ContactLoaded(null, null)) }
                .collect { emit(it) }
        }

        val imageFlow = flow {
            userProfileImageUseCase()
                .map { PartialState.ProfileImageLoaded(it) as PartialState }
                .catch { emit(PartialState.ProfileImageLoaded(null)) }
                .collect { emit(it) }
        }

        return merge(identityFlow, contactFlow, imageFlow)
    }

    override fun reduceState(
        currentState: IdentityInUiState,
        partialState: PartialState
    ): IdentityInUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, error = null)
        is PartialState.IdentityLoaded -> currentState.copy(
            isLoading = false,
            identityInfo = partialState.info,
            error = null
        )
        is PartialState.ContactLoaded -> currentState.copy(
            mobile = partialState.mobile,
            email = partialState.email,
        )
        is PartialState.ProfileImageLoaded -> currentState.copy(profileImage = partialState.image)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
