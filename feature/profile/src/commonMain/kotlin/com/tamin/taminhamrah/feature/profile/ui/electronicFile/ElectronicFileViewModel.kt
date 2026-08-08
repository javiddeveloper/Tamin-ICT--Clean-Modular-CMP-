package com.tamin.taminhamrah.feature.profile.ui.electronicFile

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.contract.ElectronicFileEvent
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.contract.ElectronicFileIntent
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.contract.ElectronicFileUiState
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.contract.ElectronicFileUiState.PartialState
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.model.DocumentTarget
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.model.ELECTRONIC_FILE_PAGE_SIZE
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.model.ElectronicFilePagingSource
import com.tamin.taminhamrah.feature.profile.ui.electronicFile.model.documentTarget
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.model.erecords.ElectronicFilePR
import com.tamin.taminhamrah.useCases.agent.GetCurrentUserNationalCodeUseCase
import com.tamin.taminhamrah.useCases.file.DownloadDocumentUseCase
import com.tamin.taminhamrah.useCases.file.GetElectronicFilePageUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ElectronicFileViewModel(
    private val getElectronicFilePageUseCase: GetElectronicFilePageUseCase,
    private val downloadDocumentUseCase: DownloadDocumentUseCase,
    private val getCurrentUserNationalCodeUseCase: GetCurrentUserNationalCodeUseCase,
) : BaseViewModel<ElectronicFileUiState, PartialState, ElectronicFileEvent, ElectronicFileIntent>(
    initialState = ElectronicFileUiState(),
) {

    /**
     * The document list, kept out of [uiState] on purpose.
     *
     * `cachedIn` means opening a document and coming back does not refetch the pages already
     * loaded, which is the difference between a smooth back gesture and a full reload.
     * Compatible with both Android and iOS targets using androidx.paging KMP.
     */
    val documents: Flow<PagingData<ElectronicFilePR>> =
        Pager(PagingConfig(pageSize = ELECTRONIC_FILE_PAGE_SIZE, enablePlaceholders = false)) {
            ElectronicFilePagingSource(getElectronicFilePageUseCase::invoke)
        }.flow.cachedIn(viewModelScope)

    override fun handleIntent(intent: ElectronicFileIntent): Flow<PartialState> = flow {
        when (intent) {
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

        is PartialState.Error -> currentState.copy(downloadFailed = true)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
