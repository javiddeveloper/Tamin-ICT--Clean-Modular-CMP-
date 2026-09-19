package com.tamin.taminhamrah.feature.pensionStatusInquiry.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.tamin.taminhamrah.feature.pensionStatusInquiry.ui.components.PensionStatusCard
import com.tamin.taminhamrah.feature.pensionStatusInquiry.ui.components.PensionStatusListSkeleton
import com.tamin.taminhamrah.feature.pensionStatusInquiry.ui.contract.PensionStatusInquiryEvent
import com.tamin.taminhamrah.feature.pensionStatusInquiry.ui.contract.PensionStatusInquiryIntent
import com.tamin.taminhamrah.feature.pensionStatusInquiry.ui.contract.PensionStatusInquiryUiState
import com.tamin.taminhamrah.model.pension.PensionInquiryPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectAsStateWithLifecycle
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.HeaderDecoration
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_back
import taminx.core.core_ui.btn_understood
import taminx.core.core_ui.ic_tamin_check_circle
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_search
import taminx.core.core_ui.pension_status_empty_subtitle
import taminx.core.core_ui.pension_status_empty_title
import taminx.core.core_ui.pension_status_inquiry_title
import taminx.core.core_ui.pension_status_send_success_desc
import taminx.core.core_ui.pension_status_send_success_title

@Composable
fun PensionStatusInquiryRoute(
    viewModel: PensionStatusInquiryViewModel = koinViewModel(),
    onBackClicked: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.sendIntent(PensionStatusInquiryIntent.Load)
    }

    HandlePensionStatusInquiryEvents(
        events = viewModel.events,
        onBackClicked = onBackClicked,
        snackbarHostState = snackbarHostState,
    )

    PensionStatusInquiryContent(
        state = state,
        onBackClicked = onBackClicked,
        onIntent = viewModel::sendIntent,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
private fun HandlePensionStatusInquiryEvents(
    events: Flow<PensionStatusInquiryEvent>,
    onBackClicked: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            PensionStatusInquiryEvent.NavigateBack -> onBackClicked()
            is PensionStatusInquiryEvent.ShowToast -> snackbarHostState.showSnackbar(event.message)
        }
    }
}

@Composable
fun PensionStatusInquiryContent(
    state: PensionStatusInquiryUiState,
    onBackClicked: () -> Unit,
    onIntent: (PensionStatusInquiryIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val profileGradientBrush = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }
    val blockingError = state.error.takeIf { state.pensionList.isEmpty() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.pension_status_inquiry_title),
                background = profileGradientBrush,
                bottomPadding = Spacing.xl,
                shape = RoundedCornerShape(bottomStart = CornerRadius.x3l, bottomEnd = CornerRadius.x3l),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = stringResource(Res.string.action_back),
                        onClick = onBackClicked,
                        bordered = true,
                    )
                },
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    DecorativeBackgroundCircle(
                        size = HeaderDecoration.circleSize,
                        xOffset = HeaderDecoration.circleXOffset,
                        yOffset = HeaderDecoration.circleYOffset,
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        AnimatedRingHeaderIcon(
                            icon = vectorResource(Res.drawable.ic_tamin_check_circle),
                        )
                        Spacer(modifier = Modifier.height(Spacing.md))
                        Text(
                            text = stringResource(Res.string.pension_status_inquiry_title),
                            style = MaterialTheme.typography.labelLarge,
                            color = taminColors.textHeaderSubtitle,
                        )
                    }
                }
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when {
                state.isLoading && state.pensionList.isEmpty() -> {
                    PensionStatusListSkeleton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.page),
                    )
                }

                blockingError != null -> {
                    ErrorStateView(
                        message = blockingError,
                        onRetry = { onIntent(PensionStatusInquiryIntent.OnRetry) },
                        onDismiss = { onIntent(PensionStatusInquiryIntent.OnBackClicked) },
                    )
                }

                state.pensionList.isEmpty() -> {
                    EmptyStateMessage(
                        icon = vectorResource(Res.drawable.ic_tamin_search),
                        title = stringResource(Res.string.pension_status_empty_title),
                        subtitle = stringResource(Res.string.pension_status_empty_subtitle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.page)
                            .align(Alignment.Center),
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(Spacing.page),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md),
                        overscrollEffect = rememberJellyOverscroll(),
                    ) {
                        items(
                            items = state.pensionList,
                            key = { "${it.pensionerRisUid}-${it.insuranceNumber}-${it.branchCode}" },
                        ) { item ->
                            PensionStatusCard(
                                item = item,
                                isSendingCertificate = state.isSendingCertificate,
                                onSendCertificateClicked = {
                                    onIntent(PensionStatusInquiryIntent.OnSendCertificateClicked(item))
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    state.successMessage?.let {
        TaminConfirmationDialog(
            title = stringResource(Res.string.pension_status_send_success_title),
            description = stringResource(Res.string.pension_status_send_success_desc),
            icon = vectorResource(Res.drawable.ic_tamin_check_circle),
            iconTint = taminColors.greenText,
            iconBackground = taminColors.greenBg,
            confirmButton = {
                TaminFilledButton(
                    text = stringResource(Res.string.btn_understood),
                    onClick = { onIntent(PensionStatusInquiryIntent.DismissSuccess) },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            dismissButton = {},
            onDismissRequest = { onIntent(PensionStatusInquiryIntent.DismissSuccess) },
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewPensionStatusInquiryLoaded() {
    PreviewRtlThemeContent {
        PensionStatusInquiryContent(
            state = PensionStatusInquiryUiState(pensionList = PreviewPensionList),
            onBackClicked = {},
            onIntent = {},
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewPensionStatusInquiryLoading() {
    PreviewRtlThemeContent {
        PensionStatusInquiryContent(
            state = PensionStatusInquiryUiState(isLoading = true),
            onBackClicked = {},
            onIntent = {},
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewPensionStatusInquiryEmpty() {
    PreviewRtlThemeContent {
        PensionStatusInquiryContent(
            state = PensionStatusInquiryUiState(),
            onBackClicked = {},
            onIntent = {},
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewPensionStatusInquiryError() {
    PreviewRtlThemeContent {
        PensionStatusInquiryContent(
            state = PensionStatusInquiryUiState(error = "خطا در دریافت اطلاعات"),
            onBackClicked = {},
            onIntent = {},
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}

private val PreviewPensionList = listOf(
    PensionInquiryPR(
        branchCode = "5750",
        insuranceNumber = "0043007196",
        pensionerRisUid = "1003406938",
        pensionerType = "بازنشستگی",
        paymentDate = "14050530",
        pensionerBaseDate = "13881201",
        fullName = "سیدرحمت اله میرفضلی",
        statusDesc = "01",
        isActive = true,
        sexDesc = "",
        branchName = "یک کرج",
        pensionEndDate = "",
        nationalId = "6319889391",
        paymentAmount = "0",
    ),
    PensionInquiryPR(
        branchCode = "0100",
        insuranceNumber = "0043007196",
        pensionerRisUid = "1003406939",
        pensionerType = "ازکارافتادگی",
        paymentDate = "14040101",
        pensionerBaseDate = "13900101",
        fullName = "سیدرحمت اله میرفضلی",
        statusDesc = "02",
        isActive = false,
        sexDesc = "",
        branchName = "تهران مرکزی",
        pensionEndDate = "",
        nationalId = "6319889391",
        paymentAmount = "0",
    ),
)
