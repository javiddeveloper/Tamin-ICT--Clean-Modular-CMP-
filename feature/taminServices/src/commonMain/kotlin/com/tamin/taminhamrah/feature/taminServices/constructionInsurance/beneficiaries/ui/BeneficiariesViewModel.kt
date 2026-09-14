package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries.contract.BeneficiariesEvent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries.contract.BeneficiariesIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries.contract.BeneficiariesUiState
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries.contract.BeneficiariesUiState.PartialState
import com.tamin.taminhamrah.mapper.toPR
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.constructionInsurance.GetBeneficiariesWorkshopUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class BeneficiariesViewModel(
    private val getBeneficiariesWorkshopUseCase: GetBeneficiariesWorkshopUseCase,
) : BaseViewModel<BeneficiariesUiState, PartialState, BeneficiariesEvent, BeneficiariesIntent>(
    initialState = BeneficiariesUiState()
) {

    private var requestNumber: Long? = null
    private var fileNumber: Long? = null
    private var requestDate: String? = null
    private var hasLoaded = false

    override fun handleIntent(intent: BeneficiariesIntent): Flow<PartialState> =
        when (intent) {
            is BeneficiariesIntent.Load -> {
                if (hasLoaded) {
                    emptyFlow()
                } else {
                    hasLoaded = true
                    requestNumber = intent.requestNumber
                    fileNumber = intent.fileNumber
                    requestDate = intent.requestDate
                    loadBeneficiaries(seed = intent)
                }
            }

            BeneficiariesIntent.Retry -> loadBeneficiaries(seed = null)

            BeneficiariesIntent.OnBackClicked -> {
                sendEvent(BeneficiariesEvent.NavigateBack)
                emptyFlow()
            }
        }

    private fun loadBeneficiaries(seed: BeneficiariesIntent.Load?): Flow<PartialState> = flow {
        seed?.let {
            emit(PartialState.HeaderSeeded(it.requestNumber, it.fileNumber, it.requestDate))
        }
        emit(PartialState.Loading(true))
        emitAll(
            getBeneficiariesWorkshopUseCase(requestNumber, fileNumber, requestDate)
                .map { list -> PartialState.Loaded(list.map { it.toPR() }.toImmutableList()) as PartialState }
                .catch { e -> emit(PartialState.Error(e.toSingleLineMessage())) }
        )
        emit(PartialState.Loading(false))
    }

    override fun reduceState(
        currentState: BeneficiariesUiState,
        partialState: PartialState,
    ): BeneficiariesUiState = when (partialState) {
        is PartialState.HeaderSeeded -> currentState.copy(
            requestNumber = partialState.requestNumber,
            fileNumber = partialState.fileNumber,
            requestDate = partialState.requestDate,
        )

        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)

        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)

        is PartialState.Loaded -> currentState.copy(
            isLoading = false,
            items = partialState.items,
            error = null,
        )
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
