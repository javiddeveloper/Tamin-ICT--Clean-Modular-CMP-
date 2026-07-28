package com.tamin.taminhamrah.feature.healthProfile.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.healthProfile.ui.components.*
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.IdentityStepState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taminx.feature.healthprofile.generated.resources.*

@Composable
fun SelfDeclarationIdentityScreen(
    state: IdentityStepState,
    onIntent: (HealthProfileIntent) -> Unit,
    onBackClicked: () -> Unit
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            HealthTopAppBar(
                title = stringResource(Res.string.health_identity_title),
                currentStep = 1,
                totalSteps = 10,
                onBackClicked = onBackClicked
            )
        },
        bottomBar = {
            HealthIrritateNavigationBar(
                primaryText = stringResource(Res.string.health_btn_next_step),
                onPrimaryClick = { onIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.PERSONAL)) },
                secondaryText = stringResource(Res.string.health_btn_cancel),
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
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Info Notice Banner (Non-editable notice with lock icon)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(taminColors.blueBg, RoundedCornerShape(14.dp))
                    .border(1.dp, taminColors.blueText.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_health_identity_lock),
                    contentDescription = null,
                    tint = taminColors.blueText,
                    modifier = Modifier.size(20.dp)
                )
                TaminText(
                    text = stringResource(Res.string.health_identity_notice),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.5.sp,
                        color = taminColors.blueText,
                        lineHeight = 20.sp
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            // Main Patient Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
                border = BorderStroke(1.dp, taminColors.border)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar Icon
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(taminColors.blueBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_health_identity_avatar),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    TaminText(
                        text = "${state.patientName} ${state.patientFamily}".trim(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = taminColors.textPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    TaminText(
                        text = if (state.insuranceNumber.isNotBlank()) "${stringResource(Res.string.health_identity_insurance_number_prefix)} ${state.insuranceNumber}" else "",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 13.sp,
                            color = taminColors.textTertiary
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = taminColors.divider, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    // 2-Column Info Grid:
                    // In RTL layout direction:
                    // - First Column (starts on RIGHT): First Name, Father Name, Birth Date
                    // - Second Column (starts on LEFT): Last Name, Gender, Insurance Number
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            IdentityGridItem(label = stringResource(Res.string.health_label_first_name), value = state.patientName)
                            IdentityGridItem(label = stringResource(Res.string.health_label_father_name), value = state.patientFather)
                            IdentityGridItem(label = stringResource(Res.string.health_label_birth_date), value = state.patientBirthDate)
                        }
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            IdentityGridItem(label = stringResource(Res.string.health_label_last_name), value = state.patientFamily)
                            IdentityGridItem(label = stringResource(Res.string.health_label_gender), value = state.patientGender)
                            IdentityGridItem(label = stringResource(Res.string.health_label_insurance_number), value = state.insuranceNumber)
                        }
                    }
                }
            }

            // Insurance Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
                border = BorderStroke(1.dp, taminColors.border)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_health_identity_heart),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(20.dp)
                        )
                        TaminText(
                            text = stringResource(Res.string.health_identity_insurance_card_title),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = taminColors.textPrimary
                            )
                        )
                    }

                    HorizontalDivider(color = taminColors.divider, thickness = 1.dp)

                    // In RTL layout direction:
                    // - First Column (RIGHT): Insurance Type
                    // - Second Column (LEFT): Last Visit Date
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            IdentityGridItem(label = stringResource(Res.string.health_label_insurance_type), value = state.insuranceType)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            IdentityGridItem(label = stringResource(Res.string.health_label_last_visit), value = state.lastVisitDate)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
        }
    }
}

@Composable
fun IdentityRow(label: String, value: String) {
    val taminColors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TaminText(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = taminColors.textTertiary
        )
        TaminText(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = taminColors.textPrimary
        )
    }
    HorizontalDivider(color = taminColors.divider, thickness = 1.dp)
}

@Composable
private fun IdentityGridItem(label: String, value: String) {
    val taminColors = LocalTaminColors.current
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TaminText(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp,
                color = taminColors.textTertiary
            )
        )
        TaminText(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = taminColors.textPrimary
            )
        )
    }
}

@PreviewRtlTheme
@Preview
@Composable
fun SelfDeclarationIdentityScreenPreview() {
    PreviewRtlThemeContent {
        SelfDeclarationIdentityScreen(
            state = IdentityStepState(),
            onIntent = {},
            onBackClicked = {}
        )
    }
}

