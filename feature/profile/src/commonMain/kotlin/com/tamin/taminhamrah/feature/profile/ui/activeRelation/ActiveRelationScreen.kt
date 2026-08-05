package com.tamin.taminhamrah.feature.profile.ui.activeRelation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.components.ActiveRelationHeader
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.components.ActiveRelationItemCard
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.contract.ActiveRelationEvent
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.contract.ActiveRelationIntent
import com.tamin.taminhamrah.feature.profile.ui.activeRelation.contract.ActiveRelationUiState
import com.tamin.taminhamrah.model.activeRelation.ActiveRelationPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow

@Composable
internal fun ActiveRelationRoute(
    viewModel: ActiveRelationViewModel,
    onBackClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    ActiveRelationEvents(
        events = viewModel.events,
        onBackClicked = onBackClicked
    )

    ActiveRelationScreen(
        uiState = uiState,
        onIntent = viewModel::sendIntent
    )
}

@Composable
fun ActiveRelationEvents(
    events: Flow<ActiveRelationEvent>,
    onBackClicked: () -> Unit
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            ActiveRelationEvent.NavigateBack -> onBackClicked()
            is ActiveRelationEvent.ShowToast -> {}
        }
    }
}

@Composable
internal fun ActiveRelationScreen(
    uiState: ActiveRelationUiState,
    onIntent: (ActiveRelationIntent) -> Unit
) {
    val taminColors = LocalTaminColors.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ActiveRelationHeader(
                activeCount = uiState.activeCount,
                inactiveCount = uiState.inactiveCount,
                lastCheckTime = uiState.lastCheckTime,
                onBackClicked = { onIntent(ActiveRelationIntent.OnBackClicked) }
            )
        },
        containerColor = taminColors.bgPage
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(taminColors.bgPage)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.lg)
            ) {
                items(uiState.items, key = { it.id }) { item ->
                    ActiveRelationItemCard(
                        item = item,
                        onSendCertificateClicked = {
                            // TODO: Implement send certificate action
                        }
                    )
                }
            }

            if (uiState.isLoading) {
                LoadingStateOverlay()
            }
        }
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
            onIntent = {}
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
            onIntent = {}
        )
    }
}

private val PreviewMockActiveRelations = persistentListOf(
    ActiveRelationPR(
        id = 2,
        organizationName = "شعبه شهرری",
        insuranceId = "۰۰۲۲۱۶۶۱۳۱",
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
        relationStatus = "کارفرما - عدم بیمه‌پرداز",
        startDate = "۱۴۰۰/۱۱/۲۰",
        endDate = "۱۴۰۵/۰۵/۱۴",
        isActive = true,
        isVerified = true
    )
)
