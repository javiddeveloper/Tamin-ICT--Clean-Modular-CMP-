package com.tamin.taminhamrah.feature.pensionInquiry.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.pensionInquiry.ui.contract.PensionInquiryUiState
import com.tamin.taminhamrah.feature.pensionInquiry.ui.contract.PensionInquiryUiState.PartialState
import com.tamin.taminhamrah.feature.pensionInquiry.ui.contract.PensionInquiryIntent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.contract.PensionInquiryEvent
import com.tamin.taminhamrah.mapper.common.toPresentation
import com.tamin.taminhamrah.mapper.pension.toPresentation
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.useCases.common.GetBeneficiaryUseCase
import com.tamin.taminhamrah.useCases.pension.GetEdictPensionerUseCase
import com.tamin.taminhamrah.useCases.common.GetRecipientListUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionInquiryUseCase
import com.tamin.taminhamrah.useCases.pension.GetPensionerIdUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PensionInquiryViewModel(
    private val getPensionInquiryUseCase: GetPensionInquiryUseCase,
    private val getRecipientListUseCase: GetRecipientListUseCase,
    private val getPensionerIdUseCase: GetPensionerIdUseCase,
    private val getEdictPensionerUseCase: GetEdictPensionerUseCase,
    private val getBeneficiaryUseCase: GetBeneficiaryUseCase,
) : BaseViewModel<PensionInquiryUiState, PartialState, PensionInquiryEvent, PensionInquiryIntent>(
    initialState = PensionInquiryUiState()
) {

    override fun handleIntent(intent: PensionInquiryIntent): Flow<PartialState> {
        return when (intent) {
            is PensionInquiryIntent.LoadPensionInquiry -> handleLoadPensionInquiry()
            is PensionInquiryIntent.LoadPensionerIds -> handleLoadPensionerIds()
            is PensionInquiryIntent.LoadEdict -> handleLoadEdict(intent.pensionerId)
            is PensionInquiryIntent.LoadRecipients -> handleLoadRecipients()
            is PensionInquiryIntent.LoadBeneficiaryList -> handleLoadBeneficiaryList()
        }
    }

    private fun handleLoadRecipients(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            getRecipientListUseCase().collect { list ->
                emit(PartialState.RecipientsLoaded(list.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    private fun handleLoadPensionInquiry(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            getPensionInquiryUseCase().collect { list ->
                emit(PartialState.PensionListLoaded(list.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    private fun handleLoadPensionerIds(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            getPensionerIdUseCase().collect { list ->
                val presentationList = list.toPresentation()
                emit(PartialState.PensionerIdsLoaded(presentationList))
                if (presentationList.isNotEmpty()) {
                    sendIntent(PensionInquiryIntent.LoadEdict(presentationList.first().pensionerId))
                }
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    private fun handleLoadBeneficiaryList(): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            getBeneficiaryUseCase().collect { list ->
                val presentationList = list.toPresentation()
                emit(PartialState.BeneficiaryListLoaded(presentationList))
                if (presentationList.isNotEmpty()){
                    sendIntent(PensionInquiryIntent.LoadPensionInquiry)
                }
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    private fun handleLoadEdict(pensionerId: String): Flow<PartialState> = flow {
        emit(PartialState.Loading(true))
        try {
            val query = ApiQueryParamDN(
                filters = listOf(
                    ApiFilterDN(FilterProperty.START_DATE, "14000101", FilterOperator.EQUAL),
                    ApiFilterDN(FilterProperty.PENSIONER_ID, pensionerId, FilterOperator.EQUAL)
                )
            )
            getEdictPensionerUseCase(query).collect { edict ->
                emit(PartialState.EdictLoaded(edict?.toPresentation()))
            }
        } catch (e: Exception) {
            emit(PartialState.Error(e.message))
        }
    }

    override fun reduceState(
        currentState: PensionInquiryUiState,
        partialState: PartialState
    ): PensionInquiryUiState = when (partialState) {
        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)
        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
        is PartialState.PensionListLoaded -> currentState.copy(
            isLoading = false,
            pensionList = partialState.list
        )

        is PartialState.PensionerIdsLoaded -> currentState.copy(
            isLoading = false,
            pensionerIds = partialState.list
        )

        is PartialState.RecipientsLoaded -> currentState.copy(
            isLoading = false,
            recipients = partialState.list
        )

        is PartialState.EdictLoaded -> currentState.copy(
            isLoading = false,
            edictPensioner = partialState.edict
        )
        is PartialState.BeneficiaryListLoaded -> currentState.copy(
            isLoading = false,
            beneficiaryList = partialState.list
        )
    }

    override fun createErrorState(message: String): PartialState =
        PartialState.Error(message)
}
