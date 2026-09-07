package com.tamin.taminhamrah.feature.contracts.ui.affairs.premiumPayment

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.contracts.ui.affairs.premiumPayment.components.ContractDebitResultCard
import com.tamin.taminhamrah.feature.contracts.ui.affairs.premiumPayment.components.ContractPremiumPaymentHeader
import com.tamin.taminhamrah.feature.contracts.ui.affairs.premiumPayment.components.DashedDivider
import com.tamin.taminhamrah.feature.contracts.ui.affairs.premiumPayment.components.MonthStepper
import com.tamin.taminhamrah.feature.contracts.ui.affairs.premiumPayment.components.PremiumPaymentInfoCard
import com.tamin.taminhamrah.feature.contracts.ui.affairs.premiumPayment.contract.ContractPremiumPaymentEvent
import com.tamin.taminhamrah.feature.contracts.ui.affairs.premiumPayment.contract.ContractPremiumPaymentIntent
import com.tamin.taminhamrah.feature.contracts.ui.affairs.premiumPayment.contract.ContractPremiumPaymentUiState
import com.tamin.taminhamrah.model.contracts.ContractDebitPR
import com.tamin.taminhamrah.model.contracts.ContractLastPaymentPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.components.toast.AppToastHost
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.components.toast.warning
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.GradientGreenEnd
import com.tamin.taminhamrah.ui.theme.GradientGreenStart
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_affairs_pay_premium
import taminx.core.core_ui.contract_premium_payment_calculate

@Composable
fun ContractPremiumPaymentRoute(
    viewModel: ContractPremiumPaymentViewModel,
    contractNumber: String,
    premiumTypeCode: String,
    insuranceType: String,
    onBackClicked: () -> Unit,
    onNavigateToPaymentDetails: (premiumTypeCode: String, startDate: Long, endDate: Long) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(contractNumber, premiumTypeCode, insuranceType) {
        viewModel.sendIntent(
            ContractPremiumPaymentIntent.Load(contractNumber, premiumTypeCode, insuranceType),
        )
    }

    ContractPremiumPaymentEvents(
        events = viewModel.events,
        onBackClicked = onBackClicked,
        onNavigateToPaymentDetails = onNavigateToPaymentDetails,
    )

    ContractPremiumPaymentScreen(uiState = uiState, onIntent = viewModel::sendIntent)
}

@Composable
private fun ContractPremiumPaymentEvents(
    events: Flow<ContractPremiumPaymentEvent>,
    onBackClicked: () -> Unit,
    onNavigateToPaymentDetails: (premiumTypeCode: String, startDate: Long, endDate: Long) -> Unit,
) {
    val toaster = LocalToaster.current
    events.collectWithLifecycleAware { event ->
        when (event) {
            ContractPremiumPaymentEvent.NavigateBack -> onBackClicked()
            is ContractPremiumPaymentEvent.ShowError -> toaster.error(event.message)
            is ContractPremiumPaymentEvent.ShowWarning -> toaster.warning(event.message)
            is ContractPremiumPaymentEvent.NavigateToPaymentDetails ->
                onNavigateToPaymentDetails(event.premiumTypeCode, event.startDate, event.endDate)
        }
    }
}

@Composable
internal fun ContractPremiumPaymentScreen(
    uiState: ContractPremiumPaymentUiState,
    onIntent: (ContractPremiumPaymentIntent) -> Unit,
) {
    val colors = LocalTaminColors.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgPage),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ContractPremiumPaymentHeader(
                insuranceType = uiState.insuranceType,
                contractNumber = uiState.contractNumber,
                onBackClicked = { onIntent(ContractPremiumPaymentIntent.OnBackClicked) },
            )

            Box(
                modifier = Modifier.weight(1f)
                    .padding(Spacing.sm)
                    .background(
                        shape = RoundedCornerShape(CornerRadius.lg),
                        color = colors.bgSurface
                    )
                    .border(
                        width = 1.dp,
                        shape = RoundedCornerShape(CornerRadius.lg),
                        color = colors.border
                    )
            ) {
                if (uiState.isInitLoading && uiState.lastPayment == null) {
                    LoadingStateOverlay()
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(Spacing.page),
                        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
                    ) {
                        PremiumPaymentInfoCard(
                            isFreelance = uiState.isFreelance,
                            paidUntilLabel = uiState.lastPayment?.paidUntilLabel.orEmpty(),
                        )

                        DashedDivider(color = colors.divider)

                        MonthStepper(
                            months = uiState.months,
                            canDecrement = uiState.canDecrement,
                            canIncrement = uiState.canIncrement,
                            onDecrement = { onIntent(ContractPremiumPaymentIntent.DecrementMonths) },
                            onIncrement = { onIntent(ContractPremiumPaymentIntent.IncrementMonths) },
                        )

                        TaminPrimaryButton(
                            background = Brush.linearGradient(
                                listOf(
                                    GradientGreenStart,
                                    GradientGreenEnd
                                )
                            ),
                            text = stringResource(Res.string.contract_premium_payment_calculate),
                            onClick = { onIntent(ContractPremiumPaymentIntent.Calculate) },
                        )

                        uiState.debit?.let { debit ->
                            ContractDebitResultCard(
                                debit = debit,
                                months = uiState.months,
                                onOpenDetails = {
                                    onIntent(ContractPremiumPaymentIntent.OpenPaymentDetails)
                                },
                            )
                        }
                    }
                }
            }

            PremiumPaymentBottomBar(
                enabled = uiState.canPay,
                onPay = { onIntent(ContractPremiumPaymentIntent.Pay) },
            )
        }
    }
}

@Composable
private fun PremiumPaymentBottomBar(
    enabled: Boolean,
    onPay: () -> Unit,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.bgPage),
    ) {
        TaminDivider()
        TaminPrimaryButton(
            background = Brush.linearGradient(listOf(GradientGreenStart, GradientGreenEnd)),
            iconAtStart = true,
            icon = Icons.Outlined.Payment,
            text = stringResource(Res.string.contract_affairs_pay_premium),
            onClick = { if (enabled) onPay() },
            modifier = Modifier
                .padding(
                    start = Spacing.page,
                    end = Spacing.page,
                    top = Spacing.md,
                    bottom = WindowInsets.navigationBars.asPaddingValues()
                        .calculateBottomPadding() + Spacing.md,
                )
                .alpha(if (enabled) 1f else 0.45f),
        )
    }
}

// ---- previews ----

private val PreviewLastPayment = ContractLastPaymentPR(
    paidUntilLabel = "۱۴۰۵/۰۶/۳۱",
    hasHistory = true,
    warningMessage = null,
)

private val PreviewDebit = ContractDebitPR(
    payableAmount = "53866782",
    periodPremiumAmount = "53866782",
    pastDebtAmount = "0",
    periodStartLabel = "۱۴۰۵/۰۷/۰۱",
    periodEndLabel = "۱۴۰۵/۰۷/۳۰",
    deadlineLabel = "۱۴۰۵/۱۰/۰۱",
    hasPastDebt = false,
    infoMessage = null,
    startDate = 0L,
    endDate = 0L,
)

@PreviewRtlTheme
@Composable
private fun ContractPremiumPaymentScreenPreviewLight() {
    PreviewRtlThemeContent {
        AppToastHost {
            ContractPremiumPaymentScreen(
                uiState = ContractPremiumPaymentUiState(
                    contractNumber = "۴۸۳۲۲۲۲۶۸۶",
                    insuranceType = "بیمهٔ اختیاری",
                    isFreelance = false,
                    lastPayment = PreviewLastPayment,
                ),
                onIntent = {},
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ContractPremiumPaymentScreenPreviewCalculatedDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        AppToastHost {
            ContractPremiumPaymentScreen(
                uiState = ContractPremiumPaymentUiState(
                    contractNumber = "۴۸۱۱۹۰۷۴۳۲",
                    insuranceType = "حرف و مشاغل آزاد",
                    isFreelance = true,
                    lastPayment = PreviewLastPayment,
                    months = 1,
                    debit = PreviewDebit,
                ),
                onIntent = {},
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ContractPremiumPaymentScreenPreviewCalculatedLight() {
    PreviewRtlThemeContent() {
        AppToastHost {
            ContractPremiumPaymentScreen(
                uiState = ContractPremiumPaymentUiState(
                    contractNumber = "۴۸۱۱۹۰۷۴۳۲",
                    insuranceType = "حرف و مشاغل آزاد",
                    isFreelance = true,
                    lastPayment = PreviewLastPayment,
                    months = 1,
                    debit = PreviewDebit,
                ),
                onIntent = {},
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ContractPremiumPaymentScreenPreviewLoading() {
    PreviewRtlThemeContent {
        AppToastHost {
            ContractPremiumPaymentScreen(
                uiState = ContractPremiumPaymentUiState(
                    contractNumber = "۴۸۳۲۲۲۲۶۸۶",
                    insuranceType = "بیمهٔ اختیاری",
                    isInitLoading = true,
                ),
                onIntent = {},
            )
        }
    }
}
