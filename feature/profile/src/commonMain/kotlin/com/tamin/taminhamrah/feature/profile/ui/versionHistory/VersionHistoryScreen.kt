package com.tamin.taminhamrah.feature.profile.ui.versionHistory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.profile.ui.versionHistory.components.VersionHistoryHeader
import com.tamin.taminhamrah.feature.profile.ui.versionHistory.components.VersionHistoryItemCard
import com.tamin.taminhamrah.feature.profile.ui.versionHistory.contract.VersionHistoryEvent
import com.tamin.taminhamrah.feature.profile.ui.versionHistory.contract.VersionHistoryIntent
import com.tamin.taminhamrah.feature.profile.ui.versionHistory.contract.VersionHistoryUiState
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.coroutines.flow.Flow

@Composable
fun VersionHistoryRoute(
    viewModel: VersionHistoryViewModel,
    onBackClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    HandleVersionHistoryEvents(
        events = viewModel.events,
        onBackClicked = onBackClicked
    )

    VersionHistoryScreen(
        state = uiState,
        onIntent = viewModel::sendIntent
    )
}

@Composable
fun HandleVersionHistoryEvents(
    events: Flow<VersionHistoryEvent>,
    onBackClicked: () -> Unit
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            VersionHistoryEvent.NavigateBack -> onBackClicked()
        }
    }
}

@Composable
fun VersionHistoryScreen(
    modifier: Modifier = Modifier,
    state: VersionHistoryUiState,
    onIntent: (VersionHistoryIntent) -> Unit
) {
    val colors = LocalTaminColors.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bgPage)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            VersionHistoryHeader(
                lastUpdatedDate = state.lastUpdatedDate,
                onBackClicked = { onIntent(VersionHistoryIntent.OnBackClicked) }
            )

            if (state.isLoading) {
                LoadingStateOverlay()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    overscrollEffect = rememberJellyOverscroll(),
                    contentPadding = PaddingValues(
                        top = Spacing.lg,
                        bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + Spacing.xxl,
                        start = Spacing.page,
                        end = Spacing.page
                    ),
                    verticalArrangement = Arrangement.spacedBy(Spacing.lg)
                ) {
                    items(
                        items = state.items,
                        key = { it.version }
                    ) { item ->
                        VersionHistoryItemCard(
                            item = item,
                            onToggleExpand = { onIntent(VersionHistoryIntent.ToggleExpand(item.version)) }
                        )
                    }
                }
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewVersionHistoryScreen() {
    PreviewRtlThemeContent {
        VersionHistoryScreen(
            state = VersionHistoryUiState(),
            onIntent = {}
        )
    }
}
