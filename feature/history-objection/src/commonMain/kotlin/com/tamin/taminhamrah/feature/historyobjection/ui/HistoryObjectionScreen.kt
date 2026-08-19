package com.tamin.taminhamrah.feature.historyobjection.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.historyobjection.ui.contract.HistoryObjectionEvent
import com.tamin.taminhamrah.feature.historyobjection.ui.contract.HistoryObjectionIntent
import com.tamin.taminhamrah.feature.historyobjection.ui.contract.HistoryObjectionUiState
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_back
import taminx.core.core_ui.history_objection_title
import taminx.core.core_ui.ic_tamin_chevron_back

private val SuccessIconSize = 72.dp

@Composable
fun HistoryObjectionScreen(
    viewModel: HistoryObjectionViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    HandleHistoryObjectionEvents(
        events = viewModel.events,
        snackbarHostState = snackbarHostState,
        onNavigateBack = onNavigateBack,
    )

    HistoryObjectionContent(
        state = uiState,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::sendIntent,
    )
}

@Composable
private fun HandleHistoryObjectionEvents(
    events: Flow<HistoryObjectionEvent>,
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            else -> {}
        }
    }
}

@Composable
private fun HistoryObjectionContent(
    state: HistoryObjectionUiState,
    snackbarHostState: SnackbarHostState,
    onIntent: (HistoryObjectionIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.history_objection_title),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = stringResource(Res.string.action_back),
                        onClick = { },
                        bordered = true,
                    )
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.bgPage)
                .verticalScroll(rememberScrollState())
                .padding(
                    top = innerPadding.calculateTopPadding() + Spacing.lg,
                    bottom = innerPadding.calculateBottomPadding() + Spacing.xxl,
                    start = Spacing.page,
                    end = Spacing.page,
                ),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {

        }
    }
}

