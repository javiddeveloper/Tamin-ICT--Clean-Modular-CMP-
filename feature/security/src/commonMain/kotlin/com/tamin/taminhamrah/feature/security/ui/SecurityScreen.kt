package com.tamin.taminhamrah.feature.security.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.security.ui.contract.SecurityEvent
import com.tamin.taminhamrah.feature.security.ui.contract.SecurityIntent
import com.tamin.taminhamrah.feature.security.ui.contract.SecurityUiState
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.ListGroupView
import com.tamin.taminhamrah.ui.components.ListItemBadge
import com.tamin.taminhamrah.ui.components.ListItemColors
import com.tamin.taminhamrah.ui.components.ListItemData
import com.tamin.taminhamrah.ui.components.SectionHeaderTitle
import com.tamin.taminhamrah.ui.components.TaminSwitchButton
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.DarkTaminColors
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.util.BiometricAuthResult
import com.tamin.taminhamrah.ui.util.BiometricAvailability
import com.tamin.taminhamrah.ui.util.rememberBiometricAuthenticator
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_back
import taminx.core.core_ui.action_cancel
import taminx.core.core_ui.biometric_prompt_subtitle
import taminx.core.core_ui.biometric_prompt_title
import taminx.core.core_ui.ic_pattern
import taminx.core.core_ui.ic_privacy
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_shield_check
import taminx.core.core_ui.profile_security
import taminx.core.core_ui.security_2fa
import taminx.core.core_ui.security_account_title
import taminx.core.core_ui.security_change_password
import taminx.core.core_ui.security_fingerprint
import taminx.core.core_ui.security_pattern

@Composable
fun SecurityScreen(
    viewModel: SecurityViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    HandleSecurityEvents(
        events = viewModel.events,
        onNavigateBack = onNavigateBack
    )

    SecurityContent(
        state = uiState,
        onIntent = viewModel::sendIntent,
        onNavigateBack = onNavigateBack
    )
}

@Composable
private fun HandleSecurityEvents(
    events: Flow<SecurityEvent>,
    onNavigateBack: () -> Unit
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            is SecurityEvent.NavigateBack -> onNavigateBack()
        }
    }
}

@Composable
private fun SecurityContent(
    modifier: Modifier = Modifier,
    state: SecurityUiState,
    onIntent: (SecurityIntent) -> Unit,
    onNavigateBack: () -> Unit
) {
    val taminColors = LocalTaminColors.current
    val isDark = taminColors == DarkTaminColors
    val topBarGradient = remember(isDark) { Brush.horizontalGradient(taminColors.profileGradientStops) }
    val defaultBorder = remember(taminColors) { BorderStroke(1.dp, taminColors.border) }
    var patternEnabled by remember { mutableStateOf(false) }

    val biometricAuthenticator = rememberBiometricAuthenticator()
    val scope = rememberCoroutineScope()
    val biometricPromptTitle = stringResource(Res.string.biometric_prompt_title)
    val biometricPromptSubtitle = stringResource(Res.string.biometric_prompt_subtitle)
    val biometricPromptNegativeButton = stringResource(Res.string.action_cancel)

    LaunchedEffect(Unit) {
        val available = biometricAuthenticator.availability() == BiometricAvailability.AVAILABLE
        onIntent(SecurityIntent.UpdateBiometricAvailability(available))
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TaminTopAppBar(
                background = topBarGradient,
                title = stringResource(Res.string.profile_security),
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
                ),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.page)
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                SectionHeaderTitle(title = stringResource(Res.string.security_account_title))
                ListGroupView(
                    containerBorder = defaultBorder,
                    items = persistentListOf(
                        ListItemData(
                            title = stringResource(Res.string.security_change_password),
                            leadingIconPainter = painterResource(Res.drawable.ic_privacy),
                            colors = ListItemColors(
                                leadingIconBackgroundGradient = taminColors.iconGradientNeutral,
                                leadingIconTintColor = Color.White
                            ),
                            showArrow = true,
                            onClick = { /* Navigate to change password */ }
                        ),
                        ListItemData(
                            title = stringResource(Res.string.security_2fa),
                            leadingIconPainter = painterResource(Res.drawable.ic_tamin_shield_check),
                            colors = ListItemColors(
                                leadingIconBackgroundGradient = taminColors.iconGradientNeutral,
                                leadingIconTintColor = Color.White
                            ),
                            badge = ListItemBadge(
                                text = "تست",
                                backgroundColor = taminColors.greenBg,
                                textColor = taminColors.greenText
                            ),
                            showArrow = true,
                            onClick = { /* Navigate to 2FA */ }
                        ),
                        ListItemData(
                            title = stringResource(Res.string.security_fingerprint),
                            leadingIconPainter = rememberVectorPainter(Icons.Rounded.Fingerprint),
                            colors = ListItemColors(
                                leadingIconBackgroundColor = if (isDark) taminColors.blueBg else Color(0xFFEEF2FB),
                                leadingIconTintColor = if (isDark) taminColors.textPrimary else Color(0xFF5E7392)
                            ),
                            showArrow = false,
                            customTrailingContent = {
                                TaminSwitchButton(
                                    checked = state.isBiometricEnabled,
                                    enabled = state.isBiometricAvailable,
                                    onCheckedChange = { newValue ->
                                        scope.launch {
                                            val result = biometricAuthenticator.authenticate(
                                                title = biometricPromptTitle,
                                                subtitle = biometricPromptSubtitle,
                                                negativeButtonText = biometricPromptNegativeButton
                                            )
                                            if (result is BiometricAuthResult.Success) {
                                                onIntent(SecurityIntent.SetBiometricEnabled(newValue))
                                            }
                                        }
                                    }
                                )
                            }
                        )
                    )
                )
            }
        }
    }
}
