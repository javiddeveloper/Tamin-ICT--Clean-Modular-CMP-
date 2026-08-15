package com.tamin.taminhamrah.feature.pensionInquiry.ui.edict

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.contract.*
import com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.contract.EdictUiState.PartialState
import com.tamin.taminhamrah.mapper.pension.toPresentation
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.useCases.pension.GetEdictPensionerUseCase
import com.tamin.taminhamrah.useCases.pension.GetEdictReportPDFUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionerIdUseCase
import com.tamin.taminhamrah.useCases.pension.SendEdictPensionerToMyInboxUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class EdictViewModel(
    private val getEdictPensionerUseCase: GetEdictPensionerUseCase,
    private val getPensionerIdUseCase: GetPensionerIdUseCase,
    private val sendEdictPensionerToMyInboxUseCase: SendEdictPensionerToMyInboxUseCase,
    private val getEdictReportPDFUseCase: GetEdictReportPDFUseCase,
) : BaseViewModel<EdictUiState, PartialState, EdictEvent, EdictIntent>(
    initialState = EdictUiState(
        searchYear = PersianDateFormatter.currentJalaliYear().toString(),
    )
) {

    init {
        sendIntent(EdictIntent.LoadPensionerIds)
    }

    override fun handleIntent(intent: EdictIntent): Flow<PartialState> = flow {
        when (intent) {
            is EdictIntent.LoadPensionerIds -> {
                emit(PartialState.Loading(true))
                try {
                    getPensionerIdUseCase().collect { list ->
                        val presentationList = list.toPresentation()
                        emit(PartialState.PensionerIdsLoaded(presentationList))
                        if (presentationList.isNotEmpty()) {
                            val pensionerId = presentationList.first().pensionerId
                            emit(PartialState.SelectedPensionerIdChanged(pensionerId))

                            // Auto-load the most recent edict (فروردین or مرداد of the current year).
                            val defaultDate = defaultEdictStartDate()
                            emit(PartialState.StartDateChanged(defaultDate))
                            try {
                                val query = ApiQueryParamDN(filters = edictFilters(pensionerId, defaultDate))
                                getEdictPensionerUseCase(query).collect { edict ->
                                    emit(PartialState.EdictLoaded(edict?.toPresentation()))
                                }
                            } catch (e: Exception) {
                                emit(PartialState.Error(e.message))
                            }
                        }
                    }
                } catch (e: Exception) {
                    emit(PartialState.Error(e.message))
                }
            }
            is EdictIntent.ChangeSelectedPensionerId -> {
                emit(PartialState.SelectedPensionerIdChanged(intent.id))
                emit(PartialState.ShowPensionerSheet(false))
            }
            is EdictIntent.ChangeStartDate -> {
                emit(PartialState.StartDateChanged(intent.date))
            }
            is EdictIntent.LoadEdict -> {
                val state = uiState.value
                val pensionerId = state.selectedPensionerId
                if (pensionerId.isNullOrEmpty()) {
                    emit(PartialState.Error("شناسه مستمری‌بگیر یافت نشد"))
                    return@flow
                }
                emit(PartialState.Loading(true))
                try {
                    val query = ApiQueryParamDN(filters = edictFilters(pensionerId, state.startDate))
                    getEdictPensionerUseCase(query).collect { edict ->
                        emit(PartialState.EdictLoaded(edict?.toPresentation()))
                    }
                } catch (e: Exception) {
                    emit(PartialState.Error(e.message))
                }
            }
            is EdictIntent.RequestSendToInbox -> {
                val state = uiState.value
                val pensionerId = state.selectedPensionerId
                if (pensionerId.isNullOrEmpty()) return@flow
                emit(PartialState.SendingToInbox(true))
                try {
                    val filters = listOf(
                        ApiFilterDN(FilterProperty.PENSIONER_ID, pensionerId, FilterOperator.EQUAL),
                        ApiFilterDN(FilterProperty.START_DATE, state.startDate + "01", FilterOperator.EQUAL)
                    )
                    sendEdictPensionerToMyInboxUseCase(filters).collect {
                        emit(PartialState.SendingToInbox(false))
                        emit(PartialState.ShowSendSuccess(true))
                    }
                } catch (e: Exception) {
                    emit(PartialState.SendingToInbox(false))
                    emit(PartialState.Error(e.message))
                }
            }
            is EdictIntent.DismissSendSuccess -> {
                emit(PartialState.ShowSendSuccess(false))
            }
            is EdictIntent.DownloadPdf -> {
                val state = uiState.value
                val pensionerId = state.selectedPensionerId
                if (pensionerId.isNullOrEmpty()) return@flow
                emit(PartialState.Loading(true))
                emit(PartialState.ViewerPdfChanged(null))
                try {
                    val filters = listOf(
                        ApiFilterDN(FilterProperty.START_DATE, state.startDate + "01", FilterOperator.EQUAL),
                        ApiFilterDN(FilterProperty.PENSIONER_ID, pensionerId, FilterOperator.EQUAL)
                    )
                    getEdictReportPDFUseCase(filters).collect { pdf ->
                        emit(PartialState.ViewerPdfChanged(pdf.toPresentation()))
                    }
                } catch (e: Exception) {
                    emit(PartialState.Error(e.message))
                    emit(PartialState.ViewerDownloadFailed)
                }
            }
            is EdictIntent.DismissPdfViewer -> {
                emit(PartialState.ViewerPdfChanged(null))
            }
            is EdictIntent.ShowPensionerSheet -> {
                emit(PartialState.ShowPensionerSheet(true))
            }
            is EdictIntent.DismissPensionerSheet -> {
                emit(PartialState.ShowPensionerSheet(false))
            }
            is EdictIntent.ShowSearchSheet -> {
                emit(PartialState.ShowSearchSheet(true))
            }
            is EdictIntent.DismissSearchSheet -> {
                emit(PartialState.ShowSearchSheet(false))
            }
            is EdictIntent.ChangeSearchYear -> {
                emit(PartialState.SearchYearChanged(intent.year))
            }
            is EdictIntent.ChangeSearchMonth -> {
                emit(PartialState.SearchMonthChanged(intent.month))
            }
            is EdictIntent.ApplySearch -> {
                val state = uiState.value
                val month = if (state.searchMonth.isEmpty()) "01" else state.searchMonth
                val newDate = "${state.searchYear}${month}"
                emit(PartialState.ShowSearchSheet(false))
                emit(PartialState.StartDateChanged(newDate))
                emit(PartialState.DateFilteredBySearch(true))
                val pensionerId = state.selectedPensionerId
                if (pensionerId.isNullOrEmpty()) return@flow
                emit(PartialState.Loading(true))
                try {
                    val query = ApiQueryParamDN(filters = edictFilters(pensionerId, newDate))
                    getEdictPensionerUseCase(query).collect { edict ->
                        emit(PartialState.EdictLoaded(edict?.toPresentation()))
                    }
                } catch (e: Exception) {
                    emit(PartialState.Error(e.message))
                }
            }
            is EdictIntent.ClearDateFilter -> {
                val state = uiState.value
                val pensionerId = state.selectedPensionerId
                emit(PartialState.ShowSearchSheet(false))
                emit(PartialState.DateFilteredBySearch(false))
                val defaultDate = defaultEdictStartDate()
                emit(PartialState.StartDateChanged(defaultDate))
                if (pensionerId.isNullOrEmpty()) return@flow
                emit(PartialState.Loading(true))
                try {
                    val query = ApiQueryParamDN(filters = edictFilters(pensionerId, defaultDate))
                    getEdictPensionerUseCase(query).collect { edict ->
                        emit(PartialState.EdictLoaded(edict?.toPresentation()))
                    }
                } catch (e: Exception) {
                    emit(PartialState.Error(e.message))
                }
            }
        }
    }

    override fun reduceState(
        currentState: EdictUiState,
        partialState: PartialState
    ): EdictUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.SendingToInbox -> currentState.copy(isSendingToInbox = partialState.isSending)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PartialState.PensionerIdsLoaded -> currentState.copy(
            isLoading = false,
            pensionerIds = partialState.list
        )
        is PartialState.SelectedPensionerIdChanged -> currentState.copy(
            selectedPensionerId = partialState.id
        )
        is PartialState.StartDateChanged -> currentState.copy(
            startDate = partialState.date
        )
        is PartialState.EdictLoaded -> currentState.copy(
            isLoading = false,
            edictPensioner = partialState.edict
        )
        is PartialState.ShowPensionerSheet -> currentState.copy(showPensionerSheet = partialState.show)
        is PartialState.ShowSearchSheet -> currentState.copy(showSearchSheet = partialState.show)
        is PartialState.SearchYearChanged -> currentState.copy(searchYear = partialState.year)
        is PartialState.SearchMonthChanged -> currentState.copy(searchMonth = partialState.month)
        is PartialState.DateFilteredBySearch -> currentState.copy(isDateFilteredBySearch = partialState.filtered)
        is PartialState.ShowSendSuccess -> currentState.copy(showSendSuccess = partialState.show)
        is PartialState.ViewerPdfChanged -> currentState.copy(
            isLoading = false,
            viewerPdf = partialState.pdf,
            viewerDownloadFailed = false,
        )
        is PartialState.ViewerDownloadFailed -> currentState.copy(
            isLoading = false,
            viewerDownloadFailed = true,
        )
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)

    /**
     * Builds the edict filter list for a given pensioner and start date.
     * START_DATE is always included; END_DATE is added for فروردین/مرداد start months.
     * endDate is always the CURRENT Jalali year's opposite edict month, so the query
     * spans from the selected startDate up to the latest edict period:
     *   startDate month فروردین (01)  →  endDate = currentYear + مرداد (05)
     *   startDate month مرداد  (05)   →  endDate = currentYear + فروردین (01)
     */
    private fun edictFilters(pensionerId: String, startDateYYYYMM: String): List<ApiFilterDN> = buildList {
        add(ApiFilterDN(FilterProperty.PENSIONER_ID, pensionerId, FilterOperator.EQUAL))
        add(ApiFilterDN(FilterProperty.START_DATE, startDateYYYYMM + "01", FilterOperator.EQUAL))
        computeEdictEndDate(startDateYYYYMM)?.let { endDate ->
            add(ApiFilterDN(FilterProperty.END_DATE, endDate, FilterOperator.EQUAL))
        }
    }

    /** Returns فروردین of the current Jalali year as a YYYYMM string (e.g. "140501"). */
    private fun defaultEdictStartDate(): String =
        "${PersianDateFormatter.currentJalaliYear()}01"

    private fun computeEdictEndDate(startDateYYYYMM: String): String? {
        if (startDateYYYYMM.length < 6) return null
        val currentYear = PersianDateFormatter.currentJalaliYear()
        return when (startDateYYYYMM.drop(4).take(2)) {
            "01" -> "${currentYear}0501"
            "05" -> "${currentYear}0101"
            else -> null
        }
    }
}
