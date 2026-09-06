package com.tamin.taminhamrah.feature.contracts.ui.affairs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.contracts.ui.affairs.components.ContractAffairsHeader
import com.tamin.taminhamrah.feature.contracts.ui.affairs.contract.ContractAffairsEvent
import com.tamin.taminhamrah.feature.contracts.ui.affairs.contract.ContractAffairsIntent
import com.tamin.taminhamrah.feature.contracts.ui.affairs.contract.ContractAffairsUiState
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.contracts.ContractPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.rememberCollapsingHeaderState
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.components.reservedHeight
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow

private val HeaderCollapseDistance = 160.dp

/**
 * امور قراردادها و پرداخت.
 *
 * Built up step by step — the collapsing gradient header is in place; the real body (search sheet,
 * contract cards, انعقاد قرارداد جدید button, امور قرارداد bottom sheet, cancel flow,
 * payment-history list, PDF viewer) lands in later steps. Everything the body needs already lives
 * on [ContractAffairsViewModel] / [ContractAffairsUiState].
 */
@Composable
fun ContractAffairsRoute(
    viewModel: ContractAffairsViewModel,
    onBackClicked: () -> Unit,
    onNavigateToService: (FeatureFlag) -> Unit,
    onOpenUrl: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ContractAffairsEvents(
        events = viewModel.events,
        onNavigateToService = onNavigateToService,
        onNavigateToWeb = onOpenUrl,
    )

    ContractAffairsScreen(
        uiState = uiState,
        onIntent = viewModel::sendIntent,
        onBackClicked = onBackClicked,
    )
}

@Composable
private fun ContractAffairsEvents(
    events: Flow<ContractAffairsEvent>,
    onNavigateToService: (FeatureFlag) -> Unit,
    onNavigateToWeb: (String) -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            is ContractAffairsEvent.NavigateToService -> onNavigateToService(event.flag)
            is ContractAffairsEvent.NavigateToWeb -> onNavigateToWeb(event.url)
            is ContractAffairsEvent.NavigateToPremiumPayment -> Unit // TODO(ui): SEP payment flow
            is ContractAffairsEvent.NavigateToEditContract -> Unit // TODO(ui): edit-contract flow
            is ContractAffairsEvent.ContractCancelled -> Unit // TODO(ui): success confirmation
            is ContractAffairsEvent.ShowToast -> Unit // TODO(ui): snackbar
            is ContractAffairsEvent.ShowError -> Unit // TODO(ui): error snackbar
        }
    }
}

@Composable
internal fun ContractAffairsScreen(
    uiState: ContractAffairsUiState,
    onIntent: (ContractAffairsIntent) -> Unit,
    onBackClicked: () -> Unit,
) {
    val taminColors = LocalTaminColors.current

    val collapse = rememberCollapsingHeaderState(HeaderCollapseDistance)
    var headerHeightPx by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(taminColors.bgPage),
    ) {
        LazyColumn(
            overscrollEffect = rememberJellyOverscroll(),
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(collapse.nestedScrollConnection),
            contentPadding = PaddingValues(
                bottom = WindowInsets.navigationBars.asPaddingValues()
                    .calculateBottomPadding() + Spacing.lg,
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            item {
                Spacer(modifier = Modifier.reservedHeight { headerHeightPx })
            }

            when {
                uiState.isLoading && uiState.contracts.isEmpty() -> item {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(320.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.contracts.isEmpty() -> item {
                    EmptyStateMessage(
                        icon = Icons.Outlined.Description,
                        title = "قراردادی یافت نشد",
                        subtitle = "قراردادی برای شما ثبت نشده است",
                        showIconTile = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                            .padding(horizontal = Spacing.xlg),
                    )
                }

                else -> items(uiState.contracts, key = { it.contractNumber }) { contract ->
                    // TODO(ui): replace with the real ContractAffairsItemCard in a later step.
                    TaminText(
                        text = "${contract.contractNumber} — ${contract.statusDesc}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.lg),
                    )
                }
            }
        }

        ContractAffairsHeader(
            collapseProgress = collapse.progressProvider,
            onBackClicked = onBackClicked,
            onSearchClicked = { /* TODO(ui): open جستجوی قرارداد sheet in a later step */ },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .onSizeChanged { headerHeightPx = it.height },
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewContractAffairsScreenLight() {
    PreviewRtlThemeContent {
        ContractAffairsScreen(
            uiState = ContractAffairsUiState(contracts = PreviewMockContracts),
            onIntent = {},
            onBackClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewContractAffairsScreenDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        ContractAffairsScreen(
            uiState = ContractAffairsUiState(contracts = PreviewMockContracts),
            onIntent = {},
            onBackClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewContractAffairsScreenEmpty() {
    PreviewRtlThemeContent {
        ContractAffairsScreen(
            uiState = ContractAffairsUiState(contracts = persistentListOf()),
            onIntent = {},
            onBackClicked = {},
        )
    }
}

private val PreviewMockContracts = listOf(
    ContractPR(
        contractNumber = "4832222686",
        statusDesc = "فعال",
        isActive = true,
        requestDate = "۱۴۰۵/۰۴/۰۱",
        insuranceType = "بیمهٔ اختیاری",
        monthlyPremiumLabel = "۲٬۳۵۰٬۰۰۰ ریال",
        monthlyIncome = "۷٬۰۰۰٬۰۰۰ ریال",
        treatmentSupportText = "با احتساب درمان",
        hasTreatmentSupport = true,
        jobTitle = "کارگر ساختمانی",
        premiumTypeCode = "02",
        statusCode = 1,
        freeJobCode = "",
    ),
    ContractPR(
        contractNumber = "4832221501",
        statusDesc = "خاتمه‌یافته",
        isActive = false,
        requestDate = "۱۴۰۳/۱۱/۱۵",
        insuranceType = "بیمهٔ حرف و مشاغل آزاد",
        monthlyPremiumLabel = "۱٬۹۸۰٬۰۰۰ ریال",
        monthlyIncome = "۶٬۰۰۰٬۰۰۰ ریال",
        treatmentSupportText = "بدون درمان",
        hasTreatmentSupport = false,
        jobTitle = "رانندهٔ درون‌شهری",
        premiumTypeCode = "01",
        statusCode = 0,
        freeJobCode = "",
    ),
).toImmutableList()
