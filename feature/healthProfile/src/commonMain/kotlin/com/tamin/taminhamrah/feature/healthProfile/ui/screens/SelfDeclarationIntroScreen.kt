package com.tamin.taminhamrah.feature.healthProfile.ui.screens

import com.tamin.taminhamrah.ui.components.TaminText

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Color
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import androidx.compose.ui.tooling.preview.Preview
import com.tamin.taminhamrah.feature.healthProfile.ui.components.HealthNavigationBar
import com.tamin.taminhamrah.feature.healthProfile.ui.components.HealthTopAppBar
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taminx.feature.healthprofile.generated.resources.*

/**
 * Intro screen of the Self-Declaration Flow (instructions page).
 */
@Composable
fun SelfDeclarationIntroScreen(
    onIntent: (HealthProfileIntent) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    SelfDeclarationIntroContent(
        onIntent = onIntent,
        onBackClicked = onBackClicked,
        modifier = modifier
    )
}

@Composable
fun SelfDeclarationIntroContent(
    onIntent: (HealthProfileIntent) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                HealthTopAppBar(
                    title = stringResource(Res.string.health_intro_title),
                    onBackClicked = onBackClicked
                )
            },
            bottomBar = {
                HealthNavigationBar(
                    primaryText = stringResource(Res.string.health_intro_btn_next),
                    primaryIconPainter = painterResource(Res.drawable.ic_health_gate_button),
                    onPrimaryClick = { onIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.IDENTITY)) },
                    secondaryText = stringResource(Res.string.health_gate_btn_back),
                    onSecondaryClick = onBackClicked
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
                    .background(taminColors.bgPage)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 18.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(6.dp))

                // Hero clipboard checked icon
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .background(taminColors.blueBg, RoundedCornerShape(28.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_health_intro_hero),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Heading & Description
                TaminText(
                    text = stringResource(Res.string.health_intro_heading),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = taminColors.textPrimary
                    )
                )

                TaminText(
                    text = stringResource(Res.string.health_intro_desc),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.5.sp,
                        color = taminColors.textTertiary,
                        lineHeight = 26.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Instruction features list (grouped inside a card, like StepIndicatorRow)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
                    border = BorderStroke(1.dp, taminColors.border)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        InstructionFeatureItem(
                            painter = painterResource(Res.drawable.ic_health_intro_fast),
                            message = stringResource(Res.string.health_intro_fast),
                            iconBgColor = taminColors.blueBg,
                            iconColor = taminColors.blueText,
                            isLast = false
                        )
                        HorizontalDivider(color = taminColors.divider, thickness = 1.dp)
                        InstructionFeatureItem(
                            painter = painterResource(Res.drawable.ic_health_intro_update),
                            message = stringResource(Res.string.health_intro_update),
                            iconBgColor = taminColors.orangeBg,
                            iconColor = taminColors.orangeText,
                            isLast = false
                        )
                        HorizontalDivider(color = taminColors.divider, thickness = 1.dp)
                        InstructionFeatureItem(
                            painter = painterResource(Res.drawable.ic_health_intro_privacy),
                            message = stringResource(Res.string.health_intro_privacy),
                            iconBgColor = taminColors.greenBg,
                            iconColor = taminColors.greenText,
                            isLast = true
                        )
                    }
                }
                Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
            }
        }
    }
}

@Composable
private fun InstructionFeatureItem(
    painter: androidx.compose.ui.graphics.painter.Painter,
    message: String,
    iconBgColor: androidx.compose.ui.graphics.Color,
    iconColor: androidx.compose.ui.graphics.Color,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(iconBgColor, RoundedCornerShape(11.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painter,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(19.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        TaminText(
            text = message,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.5.sp,
                color = taminColors.textTertiary,
                fontWeight = FontWeight.Medium,
                lineHeight = 22.sp
            ),
            modifier = Modifier.weight(1f)
        )
    }

    if (!isLast) {
        HorizontalDivider(color = taminColors.divider, thickness = 1.dp)
    }
}

@PreviewRtlTheme
@Preview
@Composable
fun SelfDeclarationIntroScreenPreview() {
    PreviewRtlThemeContent {
        SelfDeclarationIntroContent(
            onIntent = {},
            onBackClicked = {}
        )
    }
}
