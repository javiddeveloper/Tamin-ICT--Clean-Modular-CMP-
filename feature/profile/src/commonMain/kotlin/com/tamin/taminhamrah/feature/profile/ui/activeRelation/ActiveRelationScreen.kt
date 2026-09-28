package com.tamin.taminhamrah.feature.profile.ui.activeRelation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.components.ActiveRelationHeader
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.components.ActiveRelationItemCard
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.components.ActiveRelationItemCardSkeleton
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.components.CertificateBottomSheet
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.components.CertificateSuccessDialog
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.components.RecipientsBottomSheet
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.contract.ActiveRelationEvent
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.contract.ActiveRelationIntent
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.contract.ActiveRelationUiState
import com.tamin.taminhamrah.model.activeRelation.ActiveRelationPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.components.rememberStaggeredEntranceState
import com.tamin.taminhamrah.ui.components.staggeredItemEntrance
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.ToasterState
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerCardList
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme
import com.tamin.taminhamrah.ui.toparea.driveTopArea
import com.tamin.taminhamrah.ui.toparea.rememberMeasuredTopAreaState
import com.tamin.taminhamrah.ui.toparea.reportTopAreaHeight
import com.tamin.taminhamrah.ui.toparea.topAreaContentPadding
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow

/** What the list stands in with while it loads — shaped like [ActiveRelationItemCard]. */
private const val LoadingPlaceholderCards = 3
private val LoadingPlaceholderCardHeight = 180.dp

@Composable
internal fun ActiveRelationRoute(
    viewModel: ActiveRelationViewModel,
    onBackClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val toaster = LocalToaster.current

    ActiveRelationEvents(
        events = viewModel.events,
        onBackClicked = onBackClicked,
        toaster = toaster
    )

    ActiveRelationScreen(
        uiState = uiState,
        onIntent = viewModel::sendIntent,
    )
}

@Composable
fun ActiveRelationEvents(
    events: Flow<ActiveRelationEvent>,
    onBackClicked: () -> Unit,
    toaster: ToasterState,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            ActiveRelationEvent.NavigateBack -> onBackClicked()
            is ActiveRelationEvent.ShowToast -> {
                toaster.error(event.message)
            }
        }
    }
}

@Composable
internal fun ActiveRelationScreen(
    uiState: ActiveRelationUiState,
    onIntent: (ActiveRelationIntent) -> Unit,
) {
    val taminColors = LocalTaminColors.current

    val showListShimmer = uiState.isLoading && uiState.items.isEmpty()
    val topArea = rememberMeasuredTopAreaState { state ->
        ActiveRelationHeader(
            activeCount = uiState.activeCount,
            inactiveCount = uiState.inactiveCount,
            lastCheckTime = uiState.lastCheckTime,
            isLoading = showListShimmer,
            topAreaState = state,
            onBackClicked = {},
        )
    }
    val listState = rememberLazyListState()

    val staggerState = rememberStaggeredEntranceState(key = uiState.items.size)


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(taminColors.bgPage)
    ) {
        LazyColumn(
            state = listState,

            overscrollEffect = rememberJellyOverscroll(),
            modifier = Modifier
                .fillMaxSize()
                .driveTopArea(topArea, listState)
                .padding(top= Spacing.lg),
            contentPadding = topAreaContentPadding(
                state = topArea,
                rest = PaddingValues(
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + Spacing.lg
                )
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            if (showListShimmer) {
                items(3) {
                    ActiveRelationItemCardSkeleton(
                        modifier = Modifier.padding(horizontal = Spacing.lg)
                    )
                }
            } else {
                itemsIndexed(uiState.items, key = { _, item -> item.id }) { index, item ->
                    ActiveRelationItemCard(
                        item = item,
                        modifier = Modifier
                            .padding(horizontal = Spacing.lg)
                            .animateItem(
                                fadeInSpec = null,
                                fadeOutSpec = tween(100),
                                placementSpec = spring(stiffness = Spring.StiffnessLow)
                            )
                            .staggeredItemEntrance(index = index, key = item.id, state = staggerState),
                        onSendCertificateClicked = {
                            onIntent(ActiveRelationIntent.OnSendCertificateClicked(item))
                        }
                    )
                }
            }
        }

        // Drawn before the header so the header still floats over it, and inset by the same
        // top-area padding as the list, so the cards land where the placeholders were.
        if (uiState.isLoading) {
            ShimmerCardList(
                count = LoadingPlaceholderCards,
                cardHeight = LoadingPlaceholderCardHeight,
                contentPadding = topAreaContentPadding(
                    state = topArea,
                    rest = PaddingValues(horizontal = Spacing.lg)
                )
            )
        }

        // The header floats on top so the list passes underneath it as it scrolls away.
        ActiveRelationHeader(
            activeCount = uiState.activeCount,
            inactiveCount = uiState.inactiveCount,
            lastCheckTime = uiState.lastCheckTime,
            isLoading = showListShimmer,
            topAreaState = topArea,
            onBackClicked = { onIntent(ActiveRelationIntent.OnBackClicked) },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .reportTopAreaHeight(topArea)
        )
    }

    if (uiState.showCertificateSheet && uiState.selectedItem != null) {
        CertificateBottomSheet(
            state = uiState,
            onRecipientClick = { onIntent(ActiveRelationIntent.OnSelectRecipientClicked) },
            onBranchNameChange = { onIntent(ActiveRelationIntent.OnBranchNameChanged(it)) },
            onIssueClick = { onIntent(ActiveRelationIntent.OnIssueCertificateClicked(uiState.selectedItem)) },
            onDismiss = { onIntent(ActiveRelationIntent.OnDismissCertificateSheet) }
        )
    }

    if (uiState.showRecipientsSheet) {
        RecipientsBottomSheet(
            state = uiState,
            onSearchQueryChange = { onIntent(ActiveRelationIntent.OnSearchRecipients(it)) },
            onRecipientSelected = { onIntent(ActiveRelationIntent.OnRecipientSelected(it)) },
            onDismiss = { onIntent(ActiveRelationIntent.OnDismissRecipientsSheet) }
        )
    }

    if (uiState.showSuccessDialog) {
        CertificateSuccessDialog(
            onDismiss = { onIntent(ActiveRelationIntent.OnDismissSuccessDialog) }
        )
    }

    // Without it a failed load read as "no active relation" under a zero count.
    ErrorStateView(
        message = uiState.error,
        onDismiss = { onIntent(ActiveRelationIntent.OnBackClicked) },
        onRetry = { onIntent(ActiveRelationIntent.LoadActiveRelations) },
    )
}

@PreviewRtlTheme
@Composable
private fun PreviewActiveRelationScreenLight() {
    PreviewRtlThemeContent {
        ActiveRelationScreen(
            uiState = ActiveRelationUiState(
                items = PreviewMockActiveRelations,
                activeCount = 1,
                inactiveCount = 1,
                lastCheckTime = "۱۰:۲۴"
            ),
            onIntent = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewActiveRelationScreenLoading() {
    PreviewRtlThemeContent {
        ActiveRelationScreen(
            uiState = ActiveRelationUiState(isLoading = true),
            onIntent = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewActiveRelationScreenError() {
    PreviewRtlThemeContent {
        ActiveRelationScreen(
            uiState = ActiveRelationUiState(error = "اتصال اینترنت برقرار نیست"),
            onIntent = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewActiveRelationScreenDark() {
    TaminHamrahTheme(darkTheme = true) {
        ActiveRelationScreen(
            uiState = ActiveRelationUiState(
                items = PreviewMockActiveRelations,
                activeCount = 1,
                inactiveCount = 1,
                lastCheckTime = "۱۰:۲۴"
            ),
            onIntent = {},
        )
    }
}

private val PreviewMockActiveRelations = persistentListOf(
    ActiveRelationPR(
        id = 2,
        organizationName = "شعبه شهرری",
        insuranceId = "۰۰۲۲۱۶۶۱۳۱",
        branchCode = "5750",
        relationStatus = "فاقد ارتباط فعال",
        startDate = "—",
        endDate = null,
        isActive = false,
        isVerified = true
    ),
    ActiveRelationPR(
        id = 4,
        organizationName = "شعبه لاهیجان",
        insuranceId = "۰۰۱۵۷۴۲۳۷۱",
        branchCode = "5751",
        relationStatus = "کارفرما - عدم بیمه‌پرداز",
        startDate = "۱۴۰۰/۱۱/۲۰",
        endDate = "۱۴۰۵/۰۵/۱۴",
        isActive = true,
        isVerified = true
    )
)
