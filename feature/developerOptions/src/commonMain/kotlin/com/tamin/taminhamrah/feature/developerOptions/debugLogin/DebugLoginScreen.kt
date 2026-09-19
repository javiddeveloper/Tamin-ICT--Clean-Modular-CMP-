package com.tamin.taminhamrah.feature.developerOptions.debugLogin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.developerOptions.debugLogin.contract.DebugLoginEvent
import com.tamin.taminhamrah.feature.developerOptions.debugLogin.contract.DebugLoginIntent
import com.tamin.taminhamrah.feature.developerOptions.debugLogin.contract.DebugLoginUiState
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminTextField
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.debug_login_client_id_hint
import taminx.core.core_ui.debug_login_client_secret_hint
import taminx.core.core_ui.debug_login_submit
import taminx.core.core_ui.debug_login_title
import taminx.core.core_ui.error_generic
import taminx.core.core_ui.ic_tamin_chevron_back

@Composable
fun DebugLoginScreen(
    viewModel: DebugLoginViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    viewModel.events.collectWithLifecycleAware { event: DebugLoginEvent ->
        when (event) {
            DebugLoginEvent.NavigateBack -> onNavigateBack()
        }
    }

    DebugLoginContent(
        state = uiState,
        onIntent = viewModel::sendIntent,
        onNavigateBack = onNavigateBack
    )
}

@Composable
private fun DebugLoginContent(
    state: DebugLoginUiState,
    onIntent: (DebugLoginIntent) -> Unit,
    onNavigateBack: () -> Unit
) {
    val taminColors = LocalTaminColors.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.debug_login_title),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = null,
                        onClick = onNavigateBack,
                        bordered = true
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    top = innerPadding.calculateTopPadding() + Spacing.lg,
                    bottom = innerPadding.calculateBottomPadding() + Spacing.xxl
                )
                .padding(horizontal = Spacing.page),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            TaminTextField(
                value = state.clientId,
                onValueChange = { onIntent(DebugLoginIntent.OnClientIdChanged(it)) },
                label = stringResource(Res.string.debug_login_client_id_hint),
                enabled = !state.isLoading
            )
            TaminTextField(
                value = state.clientSecret,
                onValueChange = { onIntent(DebugLoginIntent.OnClientSecretChanged(it)) },
                label = stringResource(Res.string.debug_login_client_secret_hint),
                enabled = !state.isLoading
            )

            TaminFilledButton(
                text = stringResource(Res.string.debug_login_submit),
                onClick = { onIntent(DebugLoginIntent.OnLoginClicked) },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth(),
                height = 50.dp
            )

            if (state.isLoading) {
                CircularProgressIndicator()
            }

            state.statusText?.let { statusText ->
                val displayText = if (state.isError) {
                    stringResource(Res.string.error_generic, statusText)
                } else {
                    statusText
                }
                SelectionContainer {
                    Text(
                        text = displayText,
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                        color = taminColors.textSecondary
                    )
                }
            }
        }
    }
}
