package com.tamin.taminhamrah.feature.profile.ui.electronicFile

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.contract.ElectronicFileEvent
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.contract.ElectronicFileIntent
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.contract.ElectronicFileUiState
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.contract.ElectronicFileUiState.PartialState
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.model.DocumentTarget
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.model.documentTarget
import com.tamin.taminhamrah.mapper.erecords.toPresentation
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.useCases.agent.GetCurrentUserNationalCodeUseCase
import com.tamin.taminhamrah.useCases.file.DownloadDocumentUseCase
import com.tamin.taminhamrah.useCases.file.GetElectronicFileUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ElectronicFileViewModel(
    private val getElectronicFileUseCase: GetElectronicFileUseCase,
    private val downloadDocumentUseCase: DownloadDocumentUseCase,
    private val getCurrentUserNationalCodeUseCase: GetCurrentUserNationalCodeUseCase,
) : BaseViewModel<ElectronicFileUiState, PartialState, ElectronicFileEvent, ElectronicFileIntent>(
    initialState = ElectronicFileUiState(),
) {

    override fun handleIntent(intent: ElectronicFileIntent): Flow<PartialState> = flow {
        when (intent) {
            // One ordinary request for the whole list — no pages, no cache.
            ElectronicFileIntent.LoadDocuments -> {
                emit(PartialState.Loading)
                emitAll(
                    getElectronicFileUseCase()
                        .map { documents ->
                            PartialState.DocumentsLoaded(
                                documents.toPresentation().toImmutableList(),
                            )
                        }
                        .catch { error -> emit(PartialState.Error(error.message.orEmpty())) },
                )
            }

            ElectronicFileIntent.LoadNationalCode -> {
                val code = runCatching { getCurrentUserNationalCodeUseCase() }.getOrNull()
                if (!code.isNullOrBlank()) {
                    emit(PartialState.NationalCodeLoaded(code))
                }
            }

            is ElectronicFileIntent.OpenDocument -> {
                val target = intent.document.documentTarget() ?: return@flow
                emit(PartialState.TargetOpened(target))
                if (target is DocumentTarget.Pdf) {
                    runCatching { downloadDocumentUseCase(target.url).toPresentation() }
                        .onSuccess { emit(PartialState.PdfLoaded(it)) }
                        .onFailure { emit(PartialState.DownloadFailed) }
                }
            }

            is ElectronicFileIntent.DownloadPdf -> {
                runCatching { downloadDocumentUseCase(intent.url).toPresentation() }
                    .onSuccess { emit(PartialState.PdfLoaded(it)) }
                    .onFailure { emit(PartialState.DownloadFailed) }
            }

            ElectronicFileIntent.DismissViewer -> emit(PartialState.TargetOpened(null))

            ElectronicFileIntent.OnBackClicked -> sendEvent(ElectronicFileEvent.NavigateBack)
        }
    }

    override fun reduceState(
        currentState: ElectronicFileUiState,
        partialState: PartialState,
    ): ElectronicFileUiState = when (partialState) {
        PartialState.Loading -> currentState.copy(isLoading = true, errorMessage = null)

        is PartialState.DocumentsLoaded -> currentState.copy(
            documents = partialState.documents,
            isLoading = false,
            errorMessage = null,
        )

        is PartialState.TargetOpened -> currentState.copy(
            openTarget = partialState.target,
            pdfPR = null,
            downloadFailed = false,
        )

        is PartialState.PdfLoaded -> currentState.copy(
            pdfPR = partialState.pdf,
            downloadFailed = false,
        )

        is PartialState.NationalCodeLoaded -> currentState.copy(
            nationalCode = partialState.nationalCode,
        )

        PartialState.DownloadFailed -> currentState.copy(downloadFailed = true)

        // The viewer reports its own failures through downloadFailed; a list failure is the only
        // thing that can leave the grid with nothing to show, so it is the one that surfaces here.
        is PartialState.Error -> if (currentState.openTarget != null) {
            currentState.copy(downloadFailed = true)
        } else {
            currentState.copy(isLoading = false, errorMessage = partialState.message)
        }
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
