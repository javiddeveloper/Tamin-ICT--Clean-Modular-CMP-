package com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.ContractRowsUiState
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.EmployerOnlineServicesErrorSource
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.EmployerOnlineServicesEvent
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.EmployerOnlineServicesIntent
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.EmployerOnlineServicesUiState
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.EmployerOnlineServicesUiState.PartialState
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.EmployerOnlineServicesScreen
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.mapper.toIdentityCardPR
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.mapper.toRowPR
import com.tamin.taminhamrah.mapper.workshop.toPresentation
import com.tamin.taminhamrah.model.workshop.WorkshopListQuery
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.user.GetUserProfileUseCase
import com.tamin.taminhamrah.useCases.workshops.GetEmployerAgreementsUseCase
import com.tamin.taminhamrah.useCases.workshops.GetWorkshopContractRowsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

/**
 * The one ViewModel behind the whole خدمات غیرحضوری کارفرمایان flow.
 *
 * On entry it fires identity ([GetUserProfileUseCase]) and the registered agreements list
 * ([GetEmployerAgreementsUseCase]) together; each failure blocks only its own part of the landing
 * screen with a fatal, retryable error tagged to that call — the same per-source error map the
 * inspection wizard uses. A card's "ردیف‌های پیمان" tap switches [EmployerOnlineServicesUiState.currentScreen]
 * to [EmployerOnlineServicesScreen.CONTRACT_ROWS] and loads that workshop's rows
 * ([GetWorkshopContractRowsUseCase]). Wizard steps for "ثبت درخواست تعهدنامه" attach to this same
 * ViewModel/contract when their design lands (see `TODO(step 2+)`).
 */
class EmployerOnlineServicesViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getEmployerAgreementsUseCase: GetEmployerAgreementsUseCase,
    private val getWorkshopContractRowsUseCase: GetWorkshopContractRowsUseCase,
) : BaseViewModel<EmployerOnlineServicesUiState, PartialState, EmployerOnlineServicesEvent, EmployerOnlineServicesIntent>(
    initialState = EmployerOnlineServicesUiState(),
) {

    init {
        sendIntent(EmployerOnlineServicesIntent.Load)
    }

    override fun handleIntent(intent: EmployerOnlineServicesIntent): Flow<PartialState> = when (intent) {
        EmployerOnlineServicesIntent.Load -> merge(loadIdentity(), loadAgreements())
            .onStart { emit(PartialState.Loading(true)) }
            .onCompletion { emit(PartialState.Loading(false)) }

        is EmployerOnlineServicesIntent.RetrySource -> retry(intent.source)
            .onStart {
                emit(PartialState.ErrorCleared(intent.source))
                emit(PartialState.Loading(true))
            }
            .onCompletion { emit(PartialState.Loading(false)) }

        // TODO(step 2+): replace with the agreement-request wizard once its design is available.
        EmployerOnlineServicesIntent.OpenAgreementRequest ->
            flow<PartialState> { sendEvent(EmployerOnlineServicesEvent.ShowComingSoon) }

        is EmployerOnlineServicesIntent.OpenContractRows -> flow<PartialState> {
            val row = intent.row
            emit(
                PartialState.ContractRowsTarget(
                    workshopName = row.workshopName,
                    workshopCodeLabel = row.workshopCodeLabel,
                    workshopId = row.workshopId,
                    branchCode = row.branchCode,
                ),
            )
            emit(PartialState.ScreenChanged(EmployerOnlineServicesScreen.CONTRACT_ROWS))
            emitAll(loadContractRows(row.workshopId, row.branchCode))
        }
            .onStart { emit(PartialState.Loading(true)) }
            .onCompletion { emit(PartialState.Loading(false)) }

        EmployerOnlineServicesIntent.CloseContractRows ->
            flow<PartialState> { emit(PartialState.ScreenChanged(EmployerOnlineServicesScreen.AGREEMENTS_LIST)) }
    }

    private fun retry(source: EmployerOnlineServicesErrorSource): Flow<PartialState> = when (source) {
        EmployerOnlineServicesErrorSource.IDENTITY -> loadIdentity()
        EmployerOnlineServicesErrorSource.AGREEMENTS -> loadAgreements()
        EmployerOnlineServicesErrorSource.CONTRACT_ROWS -> uiState.value.contractRows.let {
            loadContractRows(it.workshopId, it.branchCode)
        }
    }

    private fun loadIdentity(): Flow<PartialState> = flow {
        emitAll(
            getUserProfileUseCase().map { profile ->
                PartialState.IdentityLoaded(profile.toIdentityCardPR()) as PartialState
            },
        )
    }.catch { e ->
        emit(PartialState.Error(e.toSingleLineMessage(), EmployerOnlineServicesErrorSource.IDENTITY))
    }

    private fun loadAgreements(): Flow<PartialState> = flow<PartialState> {
        val page = getEmployerAgreementsUseCase(WorkshopListQuery(page = 0))
        emit(PartialState.AgreementsLoaded(page.items.map { it.toRowPR() }, page.total))
    }.catch { e ->
        emit(PartialState.Error(e.toSingleLineMessage(), EmployerOnlineServicesErrorSource.AGREEMENTS))
    }

    private fun loadContractRows(workshopId: String, branchCode: String): Flow<PartialState> = flow<PartialState> {
        val page = getWorkshopContractRowsUseCase(workshopId, branchCode, page = 0)
        emit(PartialState.ContractRowsLoaded(page.items.map { it.toPresentation() }))
    }.catch { e ->
        emit(PartialState.Error(e.toSingleLineMessage(), EmployerOnlineServicesErrorSource.CONTRACT_ROWS))
    }

    override fun reduceState(
        currentState: EmployerOnlineServicesUiState,
        partialState: PartialState,
    ): EmployerOnlineServicesUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)

        is PartialState.ScreenChanged -> currentState.copy(currentScreen = partialState.screen)

        is PartialState.ContractRowsTarget -> currentState.copy(
            contractRows = ContractRowsUiState(
                workshopName = partialState.workshopName,
                workshopCodeLabel = partialState.workshopCodeLabel,
                workshopId = partialState.workshopId,
                branchCode = partialState.branchCode,
            ),
            errors = currentState.errors - EmployerOnlineServicesErrorSource.CONTRACT_ROWS,
        )

        is PartialState.ContractRowsLoaded -> currentState.copy(
            contractRows = currentState.contractRows.copy(rows = partialState.rows),
            errors = currentState.errors - EmployerOnlineServicesErrorSource.CONTRACT_ROWS,
        )

        is PartialState.IdentityLoaded -> currentState.copy(
            agreementsList = currentState.agreementsList.copy(identity = partialState.identity),
            errors = currentState.errors - EmployerOnlineServicesErrorSource.IDENTITY,
        )

        is PartialState.AgreementsLoaded -> currentState.copy(
            agreementsList = currentState.agreementsList.copy(
                agreements = partialState.agreements,
                agreementCount = partialState.total,
            ),
            errors = currentState.errors - EmployerOnlineServicesErrorSource.AGREEMENTS,
        )

        is PartialState.Error -> currentState.copy(
            errors = currentState.errors + (partialState.source to partialState.message),
        )

        is PartialState.ErrorCleared -> currentState.copy(
            errors = currentState.errors - partialState.source,
        )
    }

    override fun createErrorState(message: String): PartialState {
        sendEvent(EmployerOnlineServicesEvent.ShowToast(message))
        return PartialState.Loading(false)
    }
}
