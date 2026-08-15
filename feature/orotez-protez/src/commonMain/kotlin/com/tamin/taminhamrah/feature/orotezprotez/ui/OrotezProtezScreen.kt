package com.tamin.taminhamrah.feature.orotezprotez.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezIntent
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezUiState
import com.tamin.taminhamrah.model.userRequest.UserRequestPR
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.orotez_protez_empty_state
import taminx.core.core_ui.orotez_protez_ref_code_label
import taminx.core.core_ui.orotez_protez_title
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent

@Composable
fun OrotezProtezScreen(
    viewModel: OrotezProtezViewModel = koinViewModel(),
    onBackClicked: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    OrotezProtezContent(
        state = uiState,
        onIntent = viewModel::sendIntent,
        onBackClicked = onBackClicked,
    )
}

@Composable
private fun OrotezProtezContent(
    modifier: Modifier = Modifier,
    state: OrotezProtezUiState,
    onIntent: (OrotezProtezIntent) -> Unit,
    onBackClicked: () -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.orotez_protez_title),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = null,
                        onClick = onBackClicked,
                        bordered = true,
                    )
                },
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {

            if (state.isLoading) {
                LoadingStateOverlay()
            }
        }
    }

    ErrorStateView(
        message = state.error,
        onDismiss = onBackClicked,
        onRetry = { onIntent(OrotezProtezIntent.LoadRequests) },
    )
}


