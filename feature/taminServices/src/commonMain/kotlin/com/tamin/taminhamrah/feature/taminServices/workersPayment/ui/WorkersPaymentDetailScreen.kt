package com.tamin.taminhamrah.feature.taminServices.workersPayment.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import com.tamin.taminhamrah.feature.taminServices.workersPayment.contract.WorkersPaymentIntent
import com.tamin.taminhamrah.feature.taminServices.workersPayment.model.WorkersPaymentInfoPR
import com.tamin.taminhamrah.feature.taminServices.workersPayment.ui.components.WorkersPaymentDetailHeader
import com.tamin.taminhamrah.feature.taminServices.workersPayment.ui.components.WorkersPaymentGatewayBar
import com.tamin.taminhamrah.feature.taminServices.workersPayment.ui.components.WorkersPaymentGatewayNoticeBanner
import com.tamin.taminhamrah.feature.taminServices.workersPayment.ui.components.WorkersPaymentInfoBottomSheet
import com.tamin.taminhamrah.feature.taminServices.workersPayment.ui.components.WorkersPaymentStatementCard
import com.tamin.taminhamrah.feature.taminServices.workersPayment.ui.components.workersPaymentPreviewItem
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.reservedHeight
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing

/**
 * Screen 2 — the single-month confirmation before the bank gateway. The header is copied from
 * screen 1's (pinned gradient bar + ripple-ring icon + subtitle, with the month recap card riding
 * up into its rounded bottom edge), only non-collapsing. Body is the صورت‌حساب breakdown + gateway
 * notice, with one sticky action that fires [WorkersPaymentIntent.PayItem]. Shares the ViewModel
 * with the list screen.
 */
@Composable
internal fun WorkersPaymentDetailScreen(
    item: WorkersPaymentInfoPR,
    isProcessing: Boolean,
    onIntent: (WorkersPaymentIntent) -> Unit,
    onBack: () -> Unit,
) {
    val colors = LocalTaminColors.current
    var showInfoSheet by remember { mutableStateOf(false) }
    var headerHeightPx by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgPage),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                // Stands in for the pinned header (top bar + recap card) so the scrolling body
                // starts below it — same trick as screen 1's list.
                Spacer(modifier = Modifier.reservedHeight { headerHeightPx })

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.lg)
                        .padding(top = Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.lg),
                ) {
                    WorkersPaymentStatementCard(item = item)

                    WorkersPaymentGatewayNoticeBanner()

                    Spacer(Modifier.height(Spacing.lg))
                }
            }

            WorkersPaymentGatewayBar(
                enabled = !isProcessing,
                onPay = { onIntent(WorkersPaymentIntent.PayItem(item)) },
            )
        }

        WorkersPaymentDetailHeader(
            item = item,
            onBack = onBack,
            onInfoClicked = { showInfoSheet = true },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .onSizeChanged { headerHeightPx = it.height },
        )

        if (isProcessing) {
            LoadingStateOverlay()
        }
    }

    if (showInfoSheet) {
        WorkersPaymentInfoBottomSheet(onDismiss = { showInfoSheet = false })
    }
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@PreviewRtlTheme
@Composable
private fun WorkersPaymentDetailScreenPreviewLight() {
    PreviewRtlThemeContent {
        WorkersPaymentDetailScreen(
            item = workersPaymentPreviewItem(),
            isProcessing = false,
            onIntent = {},
            onBack = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun WorkersPaymentDetailScreenPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        WorkersPaymentDetailScreen(
            item = workersPaymentPreviewItem(),
            isProcessing = false,
            onIntent = {},
            onBack = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun WorkersPaymentDetailScreenPreviewWithFine() {
    PreviewRtlThemeContent {
        WorkersPaymentDetailScreen(
            item = workersPaymentPreviewItem(amountFines = 1341000),
            isProcessing = false,
            onIntent = {},
            onBack = {},
        )
    }
}

