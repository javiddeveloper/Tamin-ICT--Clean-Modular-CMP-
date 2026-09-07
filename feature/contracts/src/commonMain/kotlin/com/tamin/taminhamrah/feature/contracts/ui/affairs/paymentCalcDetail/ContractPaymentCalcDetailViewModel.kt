package com.tamin.taminhamrah.feature.contracts.ui.affairs.paymentCalcDetail

import com.tamin.taminhamrah.base.BaseViewModel
import com.tamin.taminhamrah.feature.contracts.ui.affairs.paymentCalcDetail.contract.ContractPaymentCalcDetailEvent
import com.tamin.taminhamrah.feature.contracts.ui.affairs.paymentCalcDetail.contract.ContractPaymentCalcDetailIntent
import com.tamin.taminhamrah.feature.contracts.ui.affairs.paymentCalcDetail.contract.ContractPaymentCalcDetailUiState
import com.tamin.taminhamrah.feature.contracts.ui.affairs.paymentCalcDetail.contract.ContractPaymentCalcDetailUiState.PartialState
import com.tamin.taminhamrah.mapper.contracts.toMonthPresentation
import com.tamin.taminhamrah.model.contracts.ContractPremiumType
import com.tamin.taminhamrah.tools.errorHandling.toSingleLineMessage
import com.tamin.taminhamrah.ui.toRialAmount
import com.tamin.taminhamrah.useCases.contracts.GetPaymentCalculationDetailsUseCase
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

/**
 * جزئیات برگ پرداخت — ریز محاسبه for a calculated payment period. Ported from `old_android`'s
 * `PaymentCalculationDetailFragment` (`getPaymentCalculationDetailList` /
 * `getOptionalInsurancePaymentCalculationDetailList`), fetched as a single page.
 */
class ContractPaymentCalcDetailViewModel(
    private val getPaymentCalculationDetailsUseCase: GetPaymentCalculationDetailsUseCase,
) : BaseViewModel<ContractPaymentCalcDetailUiState, PartialState, ContractPaymentCalcDetailEvent, ContractPaymentCalcDetailIntent>(
    initialState = ContractPaymentCalcDetailUiState(),
) {

    private var premiumType: ContractPremiumType = ContractPremiumType.FREELANCE
    private var startDate: Long = 0L
    private var endDate: Long = 0L

    override fun handleIntent(
        intent: ContractPaymentCalcDetailIntent,
    ): Flow<PartialState> = when (intent) {
        is ContractPaymentCalcDetailIntent.Load -> {
            premiumType = ContractPremiumType.fromCode(intent.premiumTypeCode)
                ?: ContractPremiumType.FREELANCE
            startDate = intent.startDate
            endDate = intent.endDate
            load(seedHeader = true)
        }

        ContractPaymentCalcDetailIntent.Retry -> load(seedHeader = false)

        ContractPaymentCalcDetailIntent.OnBackClicked -> {
            sendEvent(ContractPaymentCalcDetailEvent.NavigateBack)
            emptyFlow()
        }
    }

    private fun load(seedHeader: Boolean): Flow<PartialState> = flow {
        if (seedHeader) {
            emit(
                PartialState.HeaderSeeded(
                    startLabel = PersianDateFormatter.formatTimestamp(startDate),
                    endLabel = PersianDateFormatter.formatTimestamp(endDate),
                ),
            )
        }
        emit(PartialState.Loading(true))
        try {
            // One card per month, keeping every line (حق بیمه + کمک دولت). جمع کل is the net sum.
            val months = getPaymentCalculationDetailsUseCase(premiumType, startDate, endDate)
                .first()
                .toMonthPresentation()
                .toImmutableList()
            val total = months.sumOf { it.netAmountRaw }
            emit(
                PartialState.Loaded(
                    rows = months,
                    monthCountLabel = months.size.toString().toPersianDigits(),
                    totalLabel = total.toString().toRialAmount(fallback = ""),
                ),
            )
        } catch (e: Exception) {
            emit(PartialState.Error(e.toSingleLineMessage()))
        } finally {
            emit(PartialState.Loading(false))
        }
    }

    override fun reduceState(
        currentState: ContractPaymentCalcDetailUiState,
        partialState: PartialState,
    ): ContractPaymentCalcDetailUiState = when (partialState) {
        is PartialState.HeaderSeeded -> currentState.copy(
            startLabel = partialState.startLabel,
            endLabel = partialState.endLabel,
        )

        is PartialState.Loading -> currentState.copy(isLoading = partialState.isLoading)

        is PartialState.Loaded -> currentState.copy(
            rows = partialState.rows,
            monthCountLabel = partialState.monthCountLabel,
            totalLabel = partialState.totalLabel,
            error = null,
        )

        is PartialState.Error -> currentState.copy(error = partialState.message)
    }

    override fun createErrorState(message: String): PartialState = PartialState.Error(message)
}
