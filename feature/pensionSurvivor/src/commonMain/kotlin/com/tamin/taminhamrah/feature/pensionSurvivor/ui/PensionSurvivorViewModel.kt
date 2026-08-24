package com.tamin.taminhamrah.feature.pensionSurvivor.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorEvent
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorIntent
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorStep
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorUiState
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorUiState.PartialState
import com.tamin.taminhamrah.mapper.personal.toPresentation
import com.tamin.taminhamrah.model.personal.SubmitFinalSurvivorPensionDN
import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoPR
import com.tamin.taminhamrah.model.personal.survivorDependent.SurvivorDependentPR
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.personal.GetAgeUseCase
import com.tamin.taminhamrah.useCases.personal.GetConfirmSurvivorsListUseCase
import com.tamin.taminhamrah.useCases.personal.GetDeceasedInfoUseCase
import com.tamin.taminhamrah.useCases.personal.GetFinalSurvivorPensionPDFUseCase
import com.tamin.taminhamrah.useCases.personal.GetPersonalInfoUseCase
import com.tamin.taminhamrah.useCases.personal.GetSurvivorListUseCase
import com.tamin.taminhamrah.useCases.personal.SubmitFinalSurvivorPensionUseCase
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.resources.getString
import taminx.core.core_ui.Res
import taminx.core.core_ui.pension_survivor_final_request_unavailable

class PensionSurvivorViewModel(
    private val getPersonalInfoUseCase: GetPersonalInfoUseCase,
    private val getDeceasedInfoUseCase: GetDeceasedInfoUseCase,
    private val getAgeUseCase: GetAgeUseCase,
    private val getSurvivorListUseCase: GetSurvivorListUseCase,
    private val getConfirmSurvivorsListUseCase: GetConfirmSurvivorsListUseCase,
    private val getFinalSurvivorPensionPDFUseCase: GetFinalSurvivorPensionPDFUseCase,
    private val submitFinalSurvivorPensionUseCase: SubmitFinalSurvivorPensionUseCase,
) : BaseViewModel<
    PensionSurvivorUiState,
    PartialState,
    PensionSurvivorEvent,
    PensionSurvivorIntent,
>(
    initialState = PensionSurvivorUiState(),
) {
    init {
        sendIntent(PensionSurvivorIntent.Init)
    }

    override fun handleIntent(intent: PensionSurvivorIntent): Flow<PartialState> =
        handleIntentInternal(intent).catch { e ->
            sendEvent(PensionSurvivorEvent.ShowToast(e.toSingleLineMessage()))
            emit(createErrorState(e.toSingleLineMessage()))
        }

    private fun handleIntentInternal(intent: PensionSurvivorIntent): Flow<PartialState> = flow {
        when (intent) {
            PensionSurvivorIntent.Init -> loadApplicantProfile()
            is PensionSurvivorIntent.CommitmentChanged -> {
                emit(PartialState.CommitmentChanged(intent.accepted))
            }
            PensionSurvivorIntent.ViewRules -> {
                sendEvent(PensionSurvivorEvent.OpenRulesDocument)
            }
            PensionSurvivorIntent.NextStep -> goToNextStep()
            PensionSurvivorIntent.PreviousStep -> goToPreviousStep()
            is PensionSurvivorIntent.DeceasedNationalIdChanged -> {
                emit(
                    PartialState.DeceasedNationalIdChanged(
                        intent.value.filter(Char::isDigit).take(MAX_NATIONAL_ID_LENGTH),
                    ),
                )
            }
            PensionSurvivorIntent.SearchDeceased -> searchDeceased()
            is PensionSurvivorIntent.OpenSurvivor -> {
                if (intent.item.nationalId.isNotBlank()) {
                    sendEvent(
                        PensionSurvivorEvent.NavigateToSurvivorInfo(
                            survivor = intent.item,
                            deceasedNationalId = uiState.value.deceasedNationalId,
                        ),
                    )
                }
            }
            PensionSurvivorIntent.RefreshSurvivors -> loadSurvivors()
            PensionSurvivorIntent.DownloadFinalPdf -> downloadFinalPdf()
            PensionSurvivorIntent.RetryPdfDownload -> downloadFinalPdf()
            PensionSurvivorIntent.DismissPdfViewer -> {
                emit(PartialState.ViewerPdfChanged(null))
            }
            is PensionSurvivorIntent.PdfConfirmedChanged -> {
                emit(PartialState.PdfConfirmedChanged(intent.confirmed))
            }
            PensionSurvivorIntent.SubmitFinal -> submitFinalRequest()
            PensionSurvivorIntent.DismissSuccessDialog -> {
                emit(PartialState.ShowSuccessDialog(false))
                sendEvent(PensionSurvivorEvent.NavigateBack)
            }
            PensionSurvivorIntent.OnBack -> {
                sendEvent(PensionSurvivorEvent.NavigateBack)
            }
        }
    }

    override fun reduceState(
        currentState: PensionSurvivorUiState,
        partialState: PartialState,
    ): PensionSurvivorUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(
            isLoading = partialState.isLoading,
            viewerDownloadFailed = false,
        )
        is PartialState.ProfileLoading -> currentState.copy(isProfileLoading = partialState.isProfileLoading)
        is PartialState.ApplicantLoaded -> currentState.copy(
            applicantFullName = partialState.fullName,
            applicantNationalId = partialState.nationalId,
            isProfileLoading = false,
            isLoading = false,
        )
        is PartialState.StepChanged -> currentState.copy(
            currentStep = partialState.step,
            isLoading = false,
        )
        is PartialState.CommitmentChanged -> currentState.copy(commitmentAccepted = partialState.accepted)
        is PartialState.DeceasedNationalIdChanged -> currentState.copy(
            deceasedNationalId = partialState.value,
            deceasedInfo = null,
            survivors = persistentListOf(),
            requestId = null,
            viewerPdf = null,
            isPdfConfirmed = false,
            showSuccessDialog = false,
        )
        is PartialState.DeceasedLoaded -> currentState.copy(
            deceasedInfo = partialState.info,
            isLoading = false,
        )
        is PartialState.SurvivorsLoaded -> currentState.copy(
            survivors = partialState.items,
            isLoading = false,
        )
        is PartialState.RequestIdLoaded -> currentState.copy(
            requestId = partialState.requestId,
            isLoading = false,
        )
        is PartialState.ViewerPdfChanged -> currentState.copy(
            viewerPdf = partialState.pdf,
            isLoading = false,
            viewerDownloadFailed = false,
            isPdfConfirmed = if (partialState.pdf != null) false else currentState.isPdfConfirmed,
        )
        PartialState.ViewerDownloadFailed -> currentState.copy(
            isLoading = false,
            viewerDownloadFailed = true,
        )
        is PartialState.PdfConfirmedChanged -> currentState.copy(isPdfConfirmed = partialState.confirmed)
        is PartialState.ShowSuccessDialog -> currentState.copy(
            showSuccessDialog = partialState.show,
            isLoading = false,
        )
        is PartialState.Error -> currentState.copy(
            isProfileLoading = false,
            isLoading = false,
        )
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)

    private suspend fun FlowCollector<PartialState>.loadApplicantProfile() {
        emit(PartialState.ProfileLoading(true))
        getPersonalInfoUseCase(refreshRemote = true).collect { info ->
            val personal = info?.personal
            val fullName = listOfNotNull(personal?.firstName, personal?.lastName)
                .joinToString(" ")
                .trim()

            emit(
                PartialState.ApplicantLoaded(
                    fullName = fullName,
                    nationalId = personal?.nationalId.orEmpty(),
                ),
            )
        }
    }

    private suspend fun FlowCollector<PartialState>.goToNextStep() {
        val state = uiState.value
        if (state.isLoading) return

        when (state.currentStep) {
            PensionSurvivorStep.Rules -> {
                if (state.commitmentAccepted) {
                    emit(PartialState.StepChanged(PensionSurvivorStep.Deceased))
                }
            }

            PensionSurvivorStep.Deceased -> {
                if (state.deceasedInfo == null) return
                emit(PartialState.StepChanged(PensionSurvivorStep.Survivors))
                loadSurvivors()
            }

            PensionSurvivorStep.Survivors -> {
                emit(PartialState.StepChanged(PensionSurvivorStep.Final))
                loadRequestId()
            }

            PensionSurvivorStep.Final -> Unit
        }
    }

    private suspend fun FlowCollector<PartialState>.goToPreviousStep() {
        when (uiState.value.currentStep) {
            PensionSurvivorStep.Rules -> Unit
            PensionSurvivorStep.Deceased -> emit(PartialState.StepChanged(PensionSurvivorStep.Rules))
            PensionSurvivorStep.Survivors -> emit(PartialState.StepChanged(PensionSurvivorStep.Deceased))
            PensionSurvivorStep.Final -> emit(PartialState.StepChanged(PensionSurvivorStep.Survivors))
        }
    }

    private suspend fun FlowCollector<PartialState>.searchDeceased() {
        val state = uiState.value
        if (state.isLoading || state.deceasedNationalId.isBlank()) return

        emit(PartialState.Loading(true))
        getDeceasedInfoUseCase(state.deceasedNationalId).collect { deceasedInfo ->
            emit(PartialState.DeceasedLoaded(deceasedInfo.toPresentationWithResolvedAge()))
        }
    }

    private suspend fun FlowCollector<PartialState>.loadSurvivors() {
        val state = uiState.value
        if (state.isLoading || state.deceasedNationalId.isBlank()) return

        emit(PartialState.Loading(true))
        getSurvivorListUseCase(state.deceasedNationalId).collect { survivors ->
            emit(PartialState.SurvivorsLoaded(survivors.toPresentation().toImmutableList()))
        }
    }

    private suspend fun FlowCollector<PartialState>.loadRequestId() {
        if (uiState.value.isLoading) return

        emit(PartialState.Loading(true))
        getConfirmSurvivorsListUseCase(emptyList()).collect { confirmedItems ->
            val requestId = confirmedItems.firstNotNullOfOrNull { item -> item.request?.id }
            emit(
                PartialState.RequestIdLoaded(
                    requestId = requestId,
                ),
            )
            if (requestId == null) {
                sendEvent(
                    PensionSurvivorEvent.ShowToast(
                        getString(Res.string.pension_survivor_final_request_unavailable),
                    ),
                )
            }
        }
    }

    private suspend fun FlowCollector<PartialState>.downloadFinalPdf() {
        if (uiState.value.isLoading) return

        emit(PartialState.Loading(true))
        emit(PartialState.ViewerPdfChanged(null))
        try {
            getFinalSurvivorPensionPDFUseCase().collect { pdf ->
                emit(PartialState.ViewerPdfChanged(pdf.toPresentation()))
                sendEvent(PensionSurvivorEvent.OpenPdfViewer)
            }
        } catch (e: Exception) {
            emit(PartialState.ViewerDownloadFailed)
            sendEvent(PensionSurvivorEvent.ShowToast(e.toSingleLineMessage()))
        }
    }

    private suspend fun FlowCollector<PartialState>.submitFinalRequest() {
        val state = uiState.value
        val requestId = state.requestId ?: return
        if (state.isLoading || !state.isPdfConfirmed) return

        emit(PartialState.Loading(true))
        submitFinalSurvivorPensionUseCase(
            requestId = requestId,
            body = SubmitFinalSurvivorPensionDN(id = requestId),
        ).collect {
            emit(PartialState.ShowSuccessDialog(true))
        }
    }

    private suspend fun toPresentationAgeParts(birthDate: Long?): AgeParts {
        if (birthDate == null) return AgeParts()

        var resolved = AgeParts()
        getAgeUseCase(birthDate).collect { age ->
            resolved = age.age.toAgeParts()
        }
        return resolved
    }

    private suspend fun com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoDN.toPresentationWithResolvedAge(): DeceasedInfoPR {
        val presentation = toPresentation()
        val ageParts = toPresentationAgeParts(personal?.dateOfBirth)
        return presentation.copy(
            yearsAge = ageParts.years ?: presentation.yearsAge,
            monthsAge = ageParts.months ?: presentation.monthsAge,
            daysAge = ageParts.days ?: presentation.daysAge,
        )
    }

    private fun String?.toAgeParts(): AgeParts {
        val values = this
            ?.split(",")
            ?.map { it.trim() }
            .orEmpty()

        return AgeParts(
            years = values.getOrNull(0)?.takeIf { it.isNotBlank() },
            months = values.getOrNull(1)?.takeIf { it.isNotBlank() },
            days = values.getOrNull(2)?.takeIf { it.isNotBlank() },
        )
    }

    private data class AgeParts(
        val years: String? = null,
        val months: String? = null,
        val days: String? = null,
    )

    private companion object {
        const val MAX_NATIONAL_ID_LENGTH = 10
    }
}
