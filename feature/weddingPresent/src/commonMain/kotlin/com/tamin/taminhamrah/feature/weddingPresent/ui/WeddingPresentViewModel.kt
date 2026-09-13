package com.tamin.taminhamrah.feature.weddingPresent.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.weddingPresent.ui.contract.WeddingPresentEvent
import com.tamin.taminhamrah.feature.weddingPresent.ui.contract.WeddingPresentIntent
import com.tamin.taminhamrah.feature.weddingPresent.ui.contract.WeddingPresentUiState
import com.tamin.taminhamrah.feature.weddingPresent.ui.contract.WeddingPresentUiState.PartialState
import com.tamin.taminhamrah.mapper.weddingPresent.toPresentation
import com.tamin.taminhamrah.model.payment.isValidNationalCode
import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentInfoDN
import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentSubmitRequestDN
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.weddingPresent.GetWeddingPresentInfoUseCase
import com.tamin.taminhamrah.useCases.weddingPresent.SubmitWeddingPresentUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.resources.StringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.error_select_check_box
import taminx.core.core_ui.error_not_valid_national_id
import taminx.core.core_ui.error_updating_infos
import taminx.core.core_ui.message_select_marriage_date

private const val NATIONAL_CODE_LENGTH = 10

class WeddingPresentViewModel(
    private val getWeddingPresentInfoUseCase: GetWeddingPresentInfoUseCase,
    private val submitWeddingPresentUseCase: SubmitWeddingPresentUseCase,
) : BaseViewModel<WeddingPresentUiState, PartialState, WeddingPresentEvent, WeddingPresentIntent>(
    initialState = WeddingPresentUiState(),
) {
    private var loadedInfo: WeddingPresentInfoDN? = null

    init {
        sendIntent(WeddingPresentIntent.Load)
    }

    override fun handleIntent(intent: WeddingPresentIntent): Flow<PartialState> =
        handleIntentInternal(intent).catch { e ->
            sendEvent(WeddingPresentEvent.ShowToast(e.toSingleLineMessage()))
            emit(PartialState.Submitting(false))
            emit(PartialState.Loading(false))
            // Load failures need ErrorStateView + retry; submit failures stay on the form (toast only).
            if (intent is WeddingPresentIntent.Load) {
                emit(createErrorState(e.toSingleLineMessage()))
            }
        }

    private fun handleIntentInternal(intent: WeddingPresentIntent): Flow<PartialState> = flow {
        when (intent) {
            WeddingPresentIntent.Load -> loadInfo()
            WeddingPresentIntent.ToggleDetails ->
                emit(PartialState.DetailsExpanded(!uiState.value.isDetailsExpanded))
            WeddingPresentIntent.OpenDatePicker -> emit(PartialState.ShowDatePicker(true))
            WeddingPresentIntent.DismissDatePicker -> emit(PartialState.ShowDatePicker(false))
            is WeddingPresentIntent.MarriageDatePicked -> {
                emit(PartialState.MarriageDateChanged(intent.label, intent.millis))
                emit(PartialState.ShowDatePicker(false))
                emit(
                    PartialState.FieldErrors(
                        marriageDateError = null,
                        partnerNationalCodeError = uiState.value.partnerNationalCodeError,
                    ),
                )
            }
            is WeddingPresentIntent.PartnerNationalCodeChanged -> {
                val filtered = intent.value.toAsciiDigitsOnly().take(NATIONAL_CODE_LENGTH)
                emit(PartialState.PartnerNationalCodeChanged(filtered))
                if (filtered.length == NATIONAL_CODE_LENGTH && isValidNationalCode(filtered)) {
                    emit(
                        PartialState.FieldErrors(
                            marriageDateError = uiState.value.marriageDateError,
                            partnerNationalCodeError = null,
                        ),
                    )
                }
            }
            is WeddingPresentIntent.CommitmentChecked ->
                emit(PartialState.CommitmentChecked(intent.checked))
            WeddingPresentIntent.Submit -> submit()
            WeddingPresentIntent.DismissSuccessDialog -> {
                emit(PartialState.ShowSuccessDialog(false))
                sendEvent(WeddingPresentEvent.NavigateBack)
            }
            WeddingPresentIntent.OpenCalculate ->
                sendEvent(WeddingPresentEvent.NavigateToCalculate)
        }
    }

    private suspend fun FlowCollector<PartialState>.loadInfo() {
        emit(PartialState.Loading(true))
        val info = getWeddingPresentInfoUseCase().first()
        loadedInfo = info
        emit(PartialState.InfoLoaded(info.toPresentation()))
        emit(PartialState.Loading(false))
    }

    private suspend fun FlowCollector<PartialState>.submit() {
        if (uiState.value.isSubmitting) return

        val dateMillis = uiState.value.marriageDateMillis
        val partnerCode = uiState.value.partnerNationalCode
        val info = loadedInfo

        var dateError: StringResource? = null
        var codeError: StringResource? = null

        if (dateMillis == null || dateMillis == 0L) {
            dateError = Res.string.message_select_marriage_date
        }
        if (partnerCode.length != NATIONAL_CODE_LENGTH || !isValidNationalCode(partnerCode)) {
            codeError = Res.string.error_not_valid_national_id
        }
        if (dateError != null || codeError != null) {
            emit(PartialState.FieldErrors(dateError, codeError))
            return
        }
        if (!uiState.value.isCommitmentChecked) {
            sendEvent(WeddingPresentEvent.ShowToastRes(Res.string.error_select_check_box))
            return
        }
        if (info == null) {
            emit(PartialState.ErrorRes(Res.string.error_updating_infos))
            return
        }

        emit(PartialState.Submitting(true))
        submitWeddingPresentUseCase(
            WeddingPresentSubmitRequestDN(
                partnerNationalId = partnerCode,
                weddingDateTimeStamp = dateMillis!!,
                info = info,
            ),
        ).first()
        emit(PartialState.Submitting(false))
        emit(PartialState.ShowSuccessDialog(true))
    }

    override fun reduceState(
        currentState: WeddingPresentUiState,
        partialState: PartialState,
    ): WeddingPresentUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(
            isLoading = partialState.isLoading,
            error = null,
            errorRes = null,
        )
        is PartialState.Submitting -> currentState.copy(isSubmitting = partialState.isSubmitting)
        is PartialState.InfoLoaded -> currentState.copy(
            info = partialState.info,
            error = null,
            errorRes = null,
        )
        is PartialState.DetailsExpanded -> currentState.copy(isDetailsExpanded = partialState.expanded)
        is PartialState.MarriageDateChanged -> currentState.copy(
            marriageDateLabel = partialState.label,
            marriageDateMillis = partialState.millis,
        )
        is PartialState.PartnerNationalCodeChanged ->
            currentState.copy(partnerNationalCode = partialState.value)
        is PartialState.CommitmentChecked ->
            currentState.copy(isCommitmentChecked = partialState.checked)
        is PartialState.ShowDatePicker -> currentState.copy(showDatePicker = partialState.show)
        is PartialState.ShowSuccessDialog -> currentState.copy(showSuccessDialog = partialState.show)
        is PartialState.FieldErrors -> currentState.copy(
            marriageDateError = partialState.marriageDateError,
            partnerNationalCodeError = partialState.partnerNationalCodeError,
        )
        is PartialState.Error -> currentState.copy(
            isLoading = false,
            isSubmitting = false,
            error = partialState.message,
            errorRes = null,
        )
        is PartialState.ErrorRes -> currentState.copy(
            isLoading = false,
            isSubmitting = false,
            error = null,
            errorRes = partialState.message,
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}

private fun String.toAsciiDigitsOnly(): String = mapNotNull { char ->
    when (char) {
        in '0'..'9' -> char
        in '۰'..'۹' -> '0' + (char - '۰')
        else -> null
    }
}.joinToString("")
