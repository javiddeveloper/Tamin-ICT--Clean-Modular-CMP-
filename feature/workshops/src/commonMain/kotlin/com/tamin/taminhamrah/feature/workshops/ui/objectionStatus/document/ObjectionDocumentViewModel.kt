package com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.document

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.document.ObjectionDocumentUiState.PartialState
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionType
import com.tamin.taminhamrah.useCases.workshops.GetArticleSixteenReportPdfUseCase
import com.tamin.taminhamrah.useCases.workshops.GetDebitObjectionPdfUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

/**
 * سند اعتراض — downloads the result PDF of one filed objection.
 *
 * Which endpoint answers depends on [ObjectionDocumentUiState.objectionType]: ماده ۱۶ requests get
 * their committee report from a different endpoint than an estimated-debt or primary-vote
 * objection's own document — both already exist and are just called from here.
 */
class ObjectionDocumentViewModel(
    private val getDebitObjectionPdf: GetDebitObjectionPdfUseCase,
    private val getArticleSixteenReportPdf: GetArticleSixteenReportPdfUseCase,
) : BaseViewModel<ObjectionDocumentUiState, PartialState, ObjectionDocumentEvent, ObjectionDocumentIntent>(
    initialState = ObjectionDocumentUiState()
) {

    override fun handleIntent(intent: ObjectionDocumentIntent): Flow<PartialState> = when (intent) {
        is ObjectionDocumentIntent.Open -> flow {
            emit(
                PartialState.Opened(
                    seqNo = intent.seqNo,
                    debitNumber = intent.debitNumber,
                    workshopId = intent.workshopId,
                    objectionDate = intent.objectionDate,
                    objectionType = intent.objectionType,
                    objectionStatus = intent.objectionStatus,
                )
            )
        }

        ObjectionDocumentIntent.DownloadFile -> downloadFile()
    }

    /** Guarded so two taps before the first request resolves can't fire two downloads at once. */
    private fun downloadFile(): Flow<PartialState> = flow {
        val state = uiState.value
        if (state.isDownloading) return@flow
        emit(PartialState.Downloading)
        val pdf = if (state.objectionType == WorkShopObjectionType.ARTICLE_SIXTEEN) {
            getArticleSixteenReportPdf(state.seqNo)
        } else {
            getDebitObjectionPdf(state.seqNo)
        }
        sendEvent(ObjectionDocumentEvent.DownloadSucceeded(pdf.toPresentation()))
        emit(PartialState.DownloadFinished)
    }.catch {
        sendEvent(ObjectionDocumentEvent.DownloadFailed)
        emit(PartialState.DownloadFinished)
    }

    override fun reduceState(
        currentState: ObjectionDocumentUiState,
        partialState: PartialState,
    ): ObjectionDocumentUiState = when (partialState) {
        is PartialState.Opened -> currentState.copy(
            seqNo = partialState.seqNo,
            debitNumber = partialState.debitNumber,
            workshopId = partialState.workshopId,
            objectionDate = partialState.objectionDate,
            objectionType = partialState.objectionType,
            objectionStatus = partialState.objectionStatus,
        )
        PartialState.Downloading -> currentState.copy(isDownloading = true)
        PartialState.DownloadFinished -> currentState.copy(isDownloading = false)
    }

    override fun createErrorState(message: String): PartialState = PartialState.DownloadFinished
}
