package com.tamin.taminhamrah.feature.profile.ui.contactUs

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.profile.ui.contactUs.contract.ContactUsEvent
import com.tamin.taminhamrah.feature.profile.ui.contactUs.contract.ContactUsIntent
import com.tamin.taminhamrah.feature.profile.ui.contactUs.contract.ContactUsUiState
import com.tamin.taminhamrah.feature.profile.ui.contactUs.contract.ContactUsUiState.PartialState
import com.tamin.taminhamrah.mapper.contactUs.toPresentation
import com.tamin.taminhamrah.useCases.contactUs.GetContactUsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

class ContactUsViewModel(
    private val getContactUsUseCase: GetContactUsUseCase
) : BaseViewModel<ContactUsUiState, PartialState, ContactUsEvent, ContactUsIntent>(
    initialState = ContactUsUiState()
) {

    override fun handleIntent(intent: ContactUsIntent): Flow<PartialState> = when (intent) {
        ContactUsIntent.LoadContactUs -> handleLoadContactUs()

        ContactUsIntent.OnCallHotline -> flow {
            val dialNumber = uiState.value.contactInfo?.hotline?.dialNumber ?: "1420"
            sendEvent(ContactUsEvent.OpenUrl("tel:$dialNumber"))
        }

        is ContactUsIntent.OnSocialChannelClick -> flow {
            sendEvent(ContactUsEvent.OpenUrl(intent.channel.actionUrl))
        }

        is ContactUsIntent.OnContactDetailClick -> flow {
            val actionUrl = intent.detail.actionUrl
            if (!actionUrl.isNullOrBlank()) {
                sendEvent(ContactUsEvent.OpenUrl(actionUrl))
            } else if (intent.detail.canCopy) {
                sendEvent(ContactUsEvent.CopyToClipboard(intent.detail.value, intent.detail.title))
            }
        }

        is ContactUsIntent.OnCopyText -> flow {
            sendEvent(ContactUsEvent.CopyToClipboard(intent.text, intent.label))
        }

        ContactUsIntent.OnBackClicked -> flow {
            sendEvent(ContactUsEvent.NavigateBack)
        }
    }

    private fun handleLoadContactUs(): Flow<PartialState> =
        getContactUsUseCase()
            .map { dn -> PartialState.SetContactInfo(dn.toPresentation()) as PartialState }
            .onStart { emit(PartialState.SetLoading(true)) }
            .catch { e ->
                emit(PartialState.SetError(e.message))
            }
            .onCompletion { emit(PartialState.SetLoading(false)) }

    override fun reduceState(
        currentState: ContactUsUiState,
        partialState: PartialState
    ): ContactUsUiState = when (partialState) {
        is PartialState.SetLoading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.SetContactInfo -> currentState.copy(
            isLoading = false,
            contactInfo = partialState.info,
            error = null
        )
        is PartialState.SetError -> currentState.copy(
            isLoading = false,
            error = partialState.message
        )
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.SetError(message)
}
