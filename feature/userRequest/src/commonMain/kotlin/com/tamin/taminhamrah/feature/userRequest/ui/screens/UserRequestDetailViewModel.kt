package com.tamin.taminhamrah.feature.userRequest.ui.screens

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.userRequest.ui.screens.contract.DocumentPreview
import com.tamin.taminhamrah.feature.userRequest.ui.screens.contract.UserRequestDetailEvent
import com.tamin.taminhamrah.feature.userRequest.ui.screens.contract.UserRequestDetailIntent
import com.tamin.taminhamrah.feature.userRequest.ui.screens.contract.UserRequestDetailState
import com.tamin.taminhamrah.feature.userRequest.ui.screens.contract.UserRequestDetailState.PartialState
import com.tamin.taminhamrah.mapper.userRequest.toPresentation
import com.tamin.taminhamrah.model.userRequest.UserRequestDN
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeDN
import com.tamin.taminhamrah.useCases.userRequest.DownloadUserRequestDocumentUseCase
import com.tamin.taminhamrah.useCases.userRequest.GetShowRequestInfoUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.resources.getString
import taminx.feature.userrequest.generated.resources.Res
import taminx.feature.userrequest.generated.resources.user_request_detail_load_error
import taminx.feature.userrequest.generated.resources.user_request_document_download_error

class UserRequestDetailViewModel(
    private val getShowRequestInfoUseCase: GetShowRequestInfoUseCase,
    private val downloadUserRequestDocumentUseCase: DownloadUserRequestDocumentUseCase,
) : BaseViewModel<UserRequestDetailState, PartialState, UserRequestDetailEvent, UserRequestDetailIntent>(
    initialState = UserRequestDetailState()
) {

    override fun handleIntent(intent: UserRequestDetailIntent): Flow<PartialState> {
        return when (intent) {
            is UserRequestDetailIntent.NavigateBack -> flow {
                sendEvent(UserRequestDetailEvent.NavigateBack)
            }
            is UserRequestDetailIntent.LoadDetail -> handleLoadDetail(intent)
            is UserRequestDetailIntent.DownloadDocument -> handleDownloadDocument(intent)
            is UserRequestDetailIntent.DismissDocumentPreview -> flow {
                emit(PartialState.DocumentPreviewDismissed)
            }
        }
    }

    private fun handleLoadDetail(intent: UserRequestDetailIntent.LoadDetail): Flow<PartialState> = flow {
        if (uiState.value.isLoading) return@flow
        emit(PartialState.Loading(true))

        // Same key as my-tamin-droid MyRequestListFragment.createBundle.
        val referenceId = intent.referenceId.takeIf { it.isNotBlank() }
            ?: intent.requestId.takeIf { it > 0L }?.toString().orEmpty()

        try {
            val showInfo = getShowRequestInfoUseCase(referenceId, intent.requestTypeId)
            emit(
                PartialState.Loaded(
                    fallbackRequest(intent, referenceId).copy(details = showInfo).toPresentation()
                )
            )
        } catch (e: Exception) {
            // A failed detail payload must surface to the user, not silently render a bare
            // fallback summary (matches old_android's error_recive_data behaviour).
            val message = e.message ?: runCatching { getString(Res.string.user_request_detail_load_error) }
                .getOrElse { Res.string.user_request_detail_load_error.toString() }
            emit(PartialState.Error(message))
        }
    }

    private fun handleDownloadDocument(
        intent: UserRequestDetailIntent.DownloadDocument,
    ): Flow<PartialState> = flow {
        if (uiState.value.downloadingDocumentGuid != null) return@flow
        emit(PartialState.DocumentDownloading(intent.guid))
        try {
            val imageData = downloadUserRequestDocumentUseCase(intent.guid)
            if (imageData.isBlank()) {
                val message = runCatching { getString(Res.string.user_request_document_download_error) }
                    .getOrElse { Res.string.user_request_document_download_error.toString() }
                sendEvent(UserRequestDetailEvent.ShowToast(message))
            } else {
                emit(
                    PartialState.DocumentPreviewReady(
                        DocumentPreview(title = intent.title, imageData = imageData)
                    )
                )
            }
        } catch (e: Exception) {
            val message = e.message ?: runCatching { getString(Res.string.user_request_document_download_error) }
                .getOrElse { Res.string.user_request_document_download_error.toString() }
            sendEvent(
                UserRequestDetailEvent.ShowToast(
                    message
                )
            )
        } finally {
            emit(PartialState.DocumentDownloading(null))
        }
    }

    private fun fallbackRequest(
        intent: UserRequestDetailIntent.LoadDetail,
        referenceId: String,
    ): UserRequestDN {
        return UserRequestDN(
            id = intent.requestId,
            refCode = intent.refCode,
            title = intent.title,
            comment = null,
            creationTime = null,
            createByName = null,
            status = null,
            requestType = UserRequestTypeDN(
                id = intent.requestTypeId,
                title = intent.title,
                description = null,
            ),
            referenceId = referenceId,
            requestDetails = null,
        )
    }

    override fun reduceState(
        currentState: UserRequestDetailState,
        partialState: PartialState
    ): UserRequestDetailState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading, error = null)
        is PartialState.Loaded -> currentState.copy(
            isLoading = false,
            request = partialState.request,
            error = null,
        )
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PartialState.DocumentDownloading -> currentState.copy(downloadingDocumentGuid = partialState.guid)
        is PartialState.DocumentPreviewReady -> currentState.copy(documentPreview = partialState.preview)
        is PartialState.DocumentPreviewDismissed -> currentState.copy(documentPreview = null)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
