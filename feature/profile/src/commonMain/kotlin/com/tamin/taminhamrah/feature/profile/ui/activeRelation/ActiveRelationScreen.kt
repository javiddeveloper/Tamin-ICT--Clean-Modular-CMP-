package com.tamin.taminhamrah.feature.profile.ui.activeRelation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.components.ActiveRelationHeader
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.components.ActiveRelationItemCard
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
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.rememberCollapsingHeaderState
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.components.rememberStaggeredEntranceState
import com.tamin.taminhamrah.ui.components.reservedHeight
import com.tamin.taminhamrah.ui.components.staggeredItemEntrance
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.ToasterState
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow

/** How much drag folds the header from open to collapsed. */
private val HeaderCollapseDistance = 160.dp

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

    // Folds the header from the list's drag, snapping on release. Read only inside the
    // header's layout/draw lambdas, so the fold never recomposes the screen.
    val collapse = rememberCollapsingHeaderState(HeaderCollapseDistance)
    var headerHeightPx by remember { mutableIntStateOf(0) }
    // Remembers completed entrance animation keys across recompositions so items don't
    // re-play their entrance every time the list is redrawn.
    val staggerState = rememberStaggeredEntranceState(key = uiState.items.size)

    // Overlaid rather than wrapped in a Scaffold so the header keeps its edge-to-edge draw
    // behind the status bar — a Scaffold's own content padding would double up with
    // TaminTopAppBar's inset handling and push the bar down, leaving a page-colored gap.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(taminColors.bgPage)
    ) {
        LazyColumn(
            // The drag folds the header first, then scrolls the list, and only what neither
            // wanted reaches the rubber band — so the fold always wins over the bounce.
            overscrollEffect = rememberJellyOverscroll(),
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(collapse.nestedScrollConnection),
            contentPadding = PaddingValues(
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + Spacing.lg
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            item {
                // Stands in for the floating header, which is measured rather than fixed.
                Spacer(modifier = Modifier.reservedHeight { headerHeightPx })
            }

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

        // The header floats on top so the list passes underneath it as it scrolls away.
        ActiveRelationHeader(
            activeCount = uiState.activeCount,
            inactiveCount = uiState.inactiveCount,
            lastCheckTime = uiState.lastCheckTime,
            collapseProgress = collapse.progressProvider,
            onBackClicked = { onIntent(ActiveRelationIntent.OnBackClicked) },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .onSizeChanged { headerHeightPx = it.height }
        )

        if (uiState.isLoading) {
            LoadingStateOverlay()
        }
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
