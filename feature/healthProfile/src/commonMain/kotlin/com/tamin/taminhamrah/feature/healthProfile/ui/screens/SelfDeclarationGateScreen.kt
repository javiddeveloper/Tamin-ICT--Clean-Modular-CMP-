package com.tamin.taminhamrah.feature.healthProfile.ui.screens

import com.tamin.taminhamrah.ui.components.TaminText

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import com.tamin.taminhamrah.feature.healthProfile.ui.components.HealthIrritateNavigationBar
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.topbars.TaminTopAppBar
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import androidx.compose.ui.tooling.preview.Preview
import com.tamin.taminhamrah.feature.healthProfile.ui.components.HealthNavigationBar
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep
import org.jetbrains.compose.resources.stringResource
import taminx.feature.healthprofile.generated.resources.*

/**
 * Gate screen of the Self-Declaration Flow (locked notice page).
 */
@Composable
fun SelfDeclarationGateScreen(
    onIntent: (SelfDeclarationIntent) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    SelfDeclarationGateContent(
        onIntent = onIntent,
        onBackClicked = onBackClicked,
        modifier = modifier
    )
}

@Composable
fun SelfDeclarationGateContent(
    onIntent: (SelfDeclarationIntent) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()


    Box(
        modifier = modifier
            .fillMaxSize()
            .background(taminColors.bgPage)
    ) {
        // 1. Static Blue Header (drawn behind the top portion)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(taminColors.heroGradient)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(bottom = 30.dp)
            ) {
                // Top Bar using custom TaminTopAppBar
                TaminTopAppBar(
                    title = {
                        TaminText(
                            text = stringResource(Res.string.health_gate_title),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                        )
                    },
                    navigationIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.health_gate_btn_back),
                            tint = Color.White
                        )
                    },
                    onNavigationClick = onBackClicked,
                    backgroundColor = Color.Transparent,
                    contentColor = Color.White
                )
            }
        }

        // 2. Content Scaffold
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            bottomBar = {
                HealthNavigationBar(
                    primaryText = stringResource(Res.string.health_gate_btn_start),
                    onPrimaryClick = { onIntent(SelfDeclarationIntent.ChangeStep(SelfDeclarationStep.INTRO)) },
                    secondaryText = stringResource(Res.string.health_gate_btn_back),
                    onSecondaryClick = onBackClicked
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Space for TopAppBar
                Spacer(
                    modifier = Modifier
                        .statusBarsPadding()
                        .height(56.dp)
                )

                // Scrollable Cards List
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(horizontal = 18.dp)
                            .padding(bottom = 18.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(10.dp))

                        // 1. Info Card "پرونده سلامت من"
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = (-10).dp),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
                            border = BorderStroke(1.dp, taminColors.border),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 22.dp, vertical = 26.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Shield Icon
                                Box(
                                    modifier = Modifier
                                        .size(84.dp)
                                        .background(taminColors.blueBg, RoundedCornerShape(26.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = taminColors.blueText,
                                        modifier = Modifier.size(46.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                TaminText(
                                    text = stringResource(Res.string.health_gate_heading),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = taminColors.textPrimary
                                    )
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                TaminText(
                                    text = stringResource(Res.string.health_gate_desc),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 13.5.sp,
                                        color = taminColors.textTertiary,
                                        lineHeight = 26.sp,
                                        textAlign = TextAlign.Center
                                    )
                                )
                            }
                        }

                        // 2. Lock / Access Denied Notice
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(taminColors.orangeBg, RoundedCornerShape(16.dp))
                                .border(
                                    1.dp,
                                    taminColors.orangeText.copy(alpha = 0.3f),
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = taminColors.orangeText,
                                modifier = Modifier
                                    .size(22.dp)
                                    .padding(top = 1.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                TaminText(
                                    text = stringResource(Res.string.health_gate_locked_title),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = taminColors.orangeText
                                    )
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                TaminText(
                                    text = stringResource(Res.string.health_gate_locked_desc),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 12.5.sp,
                                        color = taminColors.orangeText.copy(alpha = 0.8f),
                                        lineHeight = 22.sp
                                    )
                                )
                            }
                        }

                        // 3. Step indicators list
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
                            border = BorderStroke(1.dp, taminColors.border)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                StepIndicatorRow(
                                    stepNumber = "۱",
                                    title = stringResource(Res.string.health_gate_step1),
                                    isLast = false
                                )
                                StepIndicatorRow(
                                    stepNumber = "۲",
                                    title = stringResource(Res.string.health_gate_step2),
                                    isLast = false
                                )
                                StepIndicatorRow(
                                    stepNumber = "۳",
                                    title = stringResource(Res.string.health_gate_step3),
                                    isLast = true
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}

@Composable
private fun StepIndicatorRow(
    stepNumber: String,
    title: String,
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
                .size(30.dp)
                .background(taminColors.blueBg, RoundedCornerShape(9.dp)),
            contentAlignment = Alignment.Center
        ) {
            TaminText(
                text = stepNumber,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = taminColors.blueText
                )
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        TaminText(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.5.sp,
                color = taminColors.textPrimary,
                fontWeight = FontWeight.Medium
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
fun SelfDeclarationGateScreenPreview() {
    PreviewRtlThemeContent {
        SelfDeclarationGateContent(
            onIntent = {},
            onBackClicked = {}
        )
    }
}
