package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries.ui

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries.contract.BeneficiariesEvent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries.contract.BeneficiariesIntent
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries.contract.BeneficiariesUiState
import com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries.contract.BeneficiariesUiState.PartialState
import com.tamin.taminhamrah.mapper.toPR
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.model.request.FilterOperator
import com.tamin.taminhamrah.model.request.FilterProperty
import com.tamin.taminhamrah.paging.Paginator
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.useCases.constructionInsurance.GetBeneficiariesWorkshopPageUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

class BeneficiariesViewModel(
    private val getBeneficiariesWorkshopPageUseCase: GetBeneficiariesWorkshopPageUseCase,
) : BaseViewModel<BeneficiariesUiState, PartialState, BeneficiariesEvent, BeneficiariesIntent>(
    initialState = BeneficiariesUiState()
) {

    private var requestNumber: Long? = null
    private var fileNumber: Long? = null
    private var requestDate: String? = null
    private var workshopId: String? = null
    private var branchCode: String? = null
    private var hasLoaded = false

    private val paginator = Paginator(
        loadPage = { query -> getBeneficiariesWorkshopPageUseCase(query).first() },
    )

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
                    workshopId = intent.workshopId
                    branchCode = intent.branchCode
                    merge(
                        flow {
                            emit(
                                PartialState.HeaderSeeded(
                                    requestNumber,
                                    fileNumber,
                                    requestDate,
                                    workshopId,
                                    branchCode,
                                )
                            )
                        },
                        observePaging(),
                        flow { paginator.refresh(query = buildQuery()) },
                    )
                }
            }

            BeneficiariesIntent.LoadNextPage -> flow { paginator.loadNext() }

            BeneficiariesIntent.RetryNextPage -> flow { paginator.retry() }

            BeneficiariesIntent.OnBackClicked -> {
                sendEvent(BeneficiariesEvent.NavigateBack)
                emptyFlow()
            }
        }

    /** [requestNumber]/[fileNumber] of `0` mean "not provided", same as the old app's convention. */
    private fun buildQuery(): ApiQueryParamDN {
        val filters = mutableListOf<ApiFilterDN>()
        requestNumber?.takeIf { it != 0L }?.let {
            filters.add(ApiFilterDN(FilterProperty.REQ_NO, it.toString(), FilterOperator.EQ))
        }
        fileNumber?.takeIf { it != 0L }?.let {
            filters.add(ApiFilterDN(FilterProperty.FILE_NO, it.toString(), FilterOperator.EQ))
        }
        requestDate?.takeIf { it.isNotBlank() }?.let {
            filters.add(ApiFilterDN(FilterProperty.BUILDING_REQUEST_DATE, it, FilterOperator.EQ))
        }
        return ApiQueryParamDN(filters = filters)
    }

    private fun observePaging(): Flow<PartialState> = paginator.state.map { paging ->
        val errorMessage = paging.error?.toSingleLineMessage()
        if (errorMessage != null && paging.items.isEmpty()) {
            sendEvent(BeneficiariesEvent.ShowError(errorMessage))
        }
        PartialState.PagingChanged(
            items = paging.items.map { it.toPR() }.toImmutableList(),
            isLoadingFirstPage = paging.isLoadingFirstPage,
            isLoadingNextPage = paging.isLoadingNextPage,
            endReached = paging.endReached,
            error = errorMessage,
        )
    }

    override fun reduceState(
        currentState: BeneficiariesUiState,
        partialState: PartialState,
    ): BeneficiariesUiState = when (partialState) {
        is PartialState.HeaderSeeded -> currentState.copy(
            requestNumber = partialState.requestNumber,
            fileNumber = partialState.fileNumber,
            requestDate = partialState.requestDate,
            workshopId = partialState.workshopId,
            branchCode = partialState.branchCode,
        )

        is PartialState.PagingChanged -> currentState.copy(
            items = partialState.items,
            isLoading = partialState.isLoadingFirstPage,
            isLoadingNextPage = partialState.isLoadingNextPage,
            endReached = partialState.endReached,
            error = null,
            paginationError = partialState.error,
        )

        is PartialState.Error -> currentState.copy(isLoading = false, error = partialState.message)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
