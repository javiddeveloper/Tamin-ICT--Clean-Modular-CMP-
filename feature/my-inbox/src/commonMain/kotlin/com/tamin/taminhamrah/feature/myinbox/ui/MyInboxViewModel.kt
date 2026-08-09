package com.tamin.taminhamrah.feature.myinbox.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.myinbox.ui.contract.MyInboxEvent
import com.tamin.taminhamrah.feature.myinbox.ui.contract.MyInboxEvent.*
import com.tamin.taminhamrah.feature.myinbox.ui.contract.MyInboxIntent
import com.tamin.taminhamrah.feature.myinbox.ui.contract.MyInboxUiState
import com.tamin.taminhamrah.feature.myinbox.ui.contract.MyInboxUiState.PartialState
import com.tamin.taminhamrah.mapper.inbox.toPresentation
import com.tamin.taminhamrah.model.inbox.PermitDurationPR
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamPR
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import com.tamin.taminhamrah.useCases.personalInbox.DeleteMyRequestUseCase
import com.tamin.taminhamrah.useCases.personalInbox.GetMyRequestPdfUseCase
import com.tamin.taminhamrah.useCases.personalInbox.GetPersonalInboxItemsUseCase
import com.tamin.taminhamrah.useCases.personalInbox.GetPersonalInboxSizeUseCase
import io.ktor.util.decodeBase64Bytes
import io.ktor.utils.io.ByteReadChannel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.permit_duration_one_day
import taminx.core.core_ui.permit_duration_one_month
import taminx.core.core_ui.permit_duration_one_week
import taminx.core.core_ui.permit_duration_one_year

class MyInboxViewModel(
    private val getPersonalInboxItemsUseCase: GetPersonalInboxItemsUseCase,
    private val getPersonalInboxSizeUseCase: GetPersonalInboxSizeUseCase,
    private val getMyRequestPdfUseCase: GetMyRequestPdfUseCase,
    private val deleteMyRequestUseCase: DeleteMyRequestUseCase,
    private val inboxInquiryLicenseUseCase: com.tamin.taminhamrah.useCases.personalInbox.InboxInquiryLicenseUseCase,
) : BaseViewModel<MyInboxUiState, PartialState, MyInboxEvent, MyInboxIntent>(
    initialState = MyInboxUiState()
) {

    init {
        sendIntent(MyInboxIntent.LoadDurations)
        sendIntent(MyInboxIntent.LoadInbox)
    }

    override fun handleIntent(intent: MyInboxIntent): Flow<PartialState> {
        return when (intent) {
            is MyInboxIntent.LoadInbox -> handleLoadInbox()
            is MyInboxIntent.LoadDurations -> handleLoadDurations()
            is MyInboxIntent.OnBackClicked -> {
                sendEvent(NavigateBack)
                emptyFlow()
            }

            is MyInboxIntent.OnCopyClicked -> {
                sendEvent(CopyToClipboard(intent.id))
                emptyFlow()
            }

            is MyInboxIntent.OnItemActionClicked -> {
                if (intent.actionValue == "ISSUE_LICENSE") {
                    flow { emit(PartialState.ShowInquiryPermitSheet(intent.id)) }
                } else if (intent.actionValue == "CANCEL_LICENSE") {
                    flow { emit(PartialState.ShowCancelLicenseConfirmation(intent.id)) }
                } else if (intent.actionValue == "CORRESPONDENCE") {
                    flow { emit(PartialState.ShowPdfViewer(intent.id)) }
                } else if (intent.actionValue == "DELETE") {
                    flow { emit(PartialState.ShowDeleteConfirmation(intent.id)) }
                } else {
                    emptyFlow()
                }
            }

            is MyInboxIntent.ShowDeleteConfirmation -> flow {
                emit(PartialState.ShowDeleteConfirmation(intent.id))
            }

            is MyInboxIntent.DismissDeleteConfirmation -> flow {
                emit(PartialState.HideDeleteConfirmation)
            }

            is MyInboxIntent.ConfirmDeleteRequest -> handleDelete(intent.id)

            is MyInboxIntent.ShowCancelLicenseConfirmation -> flow {
                emit(PartialState.ShowCancelLicenseConfirmation(intent.id))
            }

            is MyInboxIntent.DismissCancelLicenseConfirmation -> flow {
                emit(PartialState.HideCancelLicenseConfirmation)
            }

            is MyInboxIntent.ConfirmCancelLicense -> handleCancelLicense(intent.id)

            is MyInboxIntent.RequestPdfDownload -> downloadPdf(intent.id)

            is MyInboxIntent.DismissPdfViewer -> flow {
                emit(PartialState.HidePdfViewer)
            }

            is MyInboxIntent.ShowInquiryPermit -> flow {
                emit(PartialState.ShowInquiryPermitSheet(intent.id))
            }

            is MyInboxIntent.DismissInquiryPermit -> flow {
                emit(PartialState.HideInquiryPermitSheet)
            }

            is MyInboxIntent.ConfirmInquiryPermit -> handleIssueLicense(intent.id, intent.duration)

            is MyInboxIntent.CancelInquiryPermit -> handleCancelLicense(intent.id)
        }
    }

    private fun handleLoadInbox(): Flow<PartialState> = merge(
        loadInboxItems(),
        loadInboxSize(),
    )

    private fun handleLoadDurations(): Flow<PartialState> = flow {
        val durations = persistentListOf(
            PermitDurationPR(label = getString(Res.string.permit_duration_one_day), valueInDays = 1),
            PermitDurationPR(label = getString(Res.string.permit_duration_one_week), valueInDays = 7),
            PermitDurationPR(label = getString(Res.string.permit_duration_one_month), valueInDays = 30),
            PermitDurationPR(label = getString(Res.string.permit_duration_one_year), valueInDays = 365)
        )
        emit(PartialState.DurationsLoaded(durations))
    }

    private fun loadInboxItems(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            getPersonalInboxItemsUseCase().collect { items ->
                emit(PartialState.ItemsLoaded(items.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message ?: "Unknown Error"))
        }
    }

    private fun loadInboxSize(): Flow<PartialState> = flow {
        try {
            getPersonalInboxSizeUseCase().collect { size ->
                emit(PartialState.SizeLoaded(size.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message ?: "Unknown Error"))
        }
    }

    private fun downloadPdf(requestId: Long): Flow<PartialState> = flow {
        try {
            val response = getMyRequestPdfUseCase(requestId.toString()).first()
            val base64String = response.pdf
            if (!base64String.isNullOrEmpty()) {
                val bytes = base64String.decodeBase64Bytes()
                val channel = ByteReadChannel(bytes)
                val pdfDownload = PdfDownloadPR(InputStreamPR(channel))
                emit(PartialState.PdfLoaded(pdfDownload))
            } else {
                emit(PartialState.PdfDownloadError(true))
            }
        } catch (e: Exception) {
            emit(PartialState.PdfDownloadError(true))
        }
    }

    private fun handleDelete(requestId: Long): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            deleteMyRequestUseCase(requestId.toString()).collect()
            emit(PartialState.HideDeleteConfirmation)
            // Refresh inbox after deletion
            handleLoadInbox().collect { emit(it) }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message ?: "حذف پیام با خطا مواجه شد"))
        } finally {
            emit(PartialState.Loading(false))
        }
    }

    private fun handleCancelLicense(requestId: Long): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            inboxInquiryLicenseUseCase(
                requestId = requestId.toString(),
                operation = "cancel"
            ).collect()
            emit(PartialState.HideCancelLicenseConfirmation)
            // Refresh inbox after cancellation
            handleLoadInbox().collect { emit(it) }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message ?: "لغو مجوز با خطا مواجه شد"))
        } finally {
            emit(PartialState.Loading(false))
        }
    }

    private fun handleIssueLicense(requestId: Long, duration: PermitDurationPR): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            inboxInquiryLicenseUseCase(
                requestId = requestId.toString(),
                operation = "ok",
                duration = duration.valueInDays.toString()
            ).collect()
            emit(PartialState.HideInquiryPermitSheet)
            // Refresh inbox after issuing
            handleLoadInbox().collect { emit(it) }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message ?: "صدور مجوز با خطا مواجه شد"))
        } finally {
            emit(PartialState.Loading(false))
        }
    }

    override fun reduceState(
        currentState: MyInboxUiState,
        partialState: PartialState
    ): MyInboxUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(
            isLoading = partialState.isLoading
        )

        is PartialState.Error -> currentState.copy(
            isLoading = false,
            error = partialState.message
        )

        is PartialState.ItemsLoaded -> currentState.copy(
            isLoading = false,
            items = partialState.items
        )

        is PartialState.SizeLoaded -> currentState.copy(
            size = partialState.size
        )

        is PartialState.ShowInquiryPermitSheet -> currentState.copy(
            showInquiryPermitSheet = true,
            selectedItemIdForPermit = partialState.itemId
        )

        is PartialState.HideInquiryPermitSheet -> currentState.copy(
            showInquiryPermitSheet = false,
            selectedItemIdForPermit = null
        )

        is PartialState.DurationsLoaded -> currentState.copy(
            permitDurations = partialState.durations
        )

        is PartialState.ShowPdfViewer -> currentState.copy(
            showPdfViewer = true,
            selectedPdfId = partialState.id,
            pdfDownload = null,
            pdfDownloadFailed = false
        )

        is PartialState.HidePdfViewer -> currentState.copy(
            showPdfViewer = false,
            selectedPdfId = null,
            pdfDownload = null,
            pdfDownloadFailed = false
        )

        is PartialState.PdfLoaded -> currentState.copy(
            pdfDownload = partialState.pdf,
            pdfDownloadFailed = false
        )

        is PartialState.PdfDownloadError -> currentState.copy(
            pdfDownloadFailed = partialState.failed
        )

        is PartialState.ShowDeleteConfirmation -> currentState.copy(
            showDeleteConfirmation = true,
            selectedItemIdForDelete = partialState.id
        )

        is PartialState.HideDeleteConfirmation -> currentState.copy(
            showDeleteConfirmation = false,
            selectedItemIdForDelete = null
        )

        is PartialState.ShowCancelLicenseConfirmation -> currentState.copy(
            showCancelLicenseConfirmation = true,
            selectedItemIdForCancelLicense = partialState.id
        )

        is PartialState.HideCancelLicenseConfirmation -> currentState.copy(
            showCancelLicenseConfirmation = false,
            selectedItemIdForCancelLicense = null
        )
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)
}
