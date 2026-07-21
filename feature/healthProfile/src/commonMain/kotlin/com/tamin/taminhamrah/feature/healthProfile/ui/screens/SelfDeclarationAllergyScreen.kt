package com.tamin.taminhamrah.feature.healthProfile.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.tamin.taminhamrah.feature.healthProfile.ui.components.*
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationUiState
import com.tamin.taminhamrah.feature.healthProfile.ui.model.PatientDrugAllergyMock
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SelfDeclarationAllergyScreen(
    state: SelfDeclarationUiState,
    onIntent: (SelfDeclarationIntent) -> Unit,
    onBackClicked: () -> Unit
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    var showAddDialog by remember { mutableStateOf(false) }

    // Dialog state
    var selectedDrugIndex by remember { mutableStateOf(-1) }
    var allergyDesc by remember { mutableStateOf("") }
    val drugList = listOf("پنی‌سیلین", "استامینوفن", "آسپیرین", "ایبوپروفن", "آموکسی‌سیلین", "سفکسیم", "سفالکسین", "مترونیدازول")

    Scaffold(
        topBar = {
            HealthTopAppBar(onBackClicked = onBackClicked)
        },
        bottomBar = {
            HealthIrritateNavigationBar(
                primaryText = "مرحلهٔ بعدی (بررسی نهایی)",
                onPrimaryClick = { onIntent(SelfDeclarationIntent.ChangeStep(SelfDeclarationStep.REVIEW)) },
                secondaryText = "مرحلهٔ قبلی",
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
            HealthProgressBar(currentStep = 10, totalSteps = 10)

            TaminText(
                text = "حساسیت‌های دارویی",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = taminColors.textPrimary
                )
            )

            TaminText(
                text = "در این قسمت می‌توانید داروهایی که به آن‌ها حساسیت دارید را با ذکر جزئیات یا عوارض ایجاد شده اضافه کنید.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = taminColors.textTertiary,
                    lineHeight = 22.sp
                )
            )

            DashedAddButton(
                label = "افزودن حساسیت دارویی جدید",
                onClick = {
                    selectedDrugIndex = -1
                    allergyDesc = ""
                    showAddDialog = true
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (state.allergies.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TaminText(
                        text = "هیچ حساسیت دارویی ثبت نشده است.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = taminColors.textMuted
                    )
                }
            } else {
                state.allergies.forEachIndexed { idx, allergy ->
                    DynamicItemCard(
                        title = allergy.drugName,
                        description = allergy.allergyComments,
                        onDelete = {
                            onIntent(SelfDeclarationIntent.UpdateState {
                                copy(allergies = allergies.filterIndexed { i, _ -> i != idx })
                            })
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
        }

        // Add Allergy Dialog
        if (showAddDialog) {
            Dialog(onDismissRequest = { showAddDialog = false }) {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
                    border = BorderStroke(1.dp, taminColors.border),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(22.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        TaminText(
                            text = "انتخاب داروی حساسیت",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = taminColors.textPrimary
                        )

                        TaminText(
                            text = "دارویی که به آن حساسیت دارید را انتخاب کنید:",
                            fontSize = 12.sp,
                            color = taminColors.textTertiary
                        )

                        InteractiveChoiceChips(
                            options = drugList,
                            selectedIndices = if (selectedDrugIndex >= 0) setOf(selectedDrugIndex) else emptySet(),
                            onSelectionChanged = { idxs ->
                                selectedDrugIndex = idxs.firstOrNull() ?: -1
                            }
                        )

                        StyledTextField(
                            value = allergyDesc,
                            onValueChange = { allergyDesc = it },
                            label = "توضیحات عارضه یا حساسیت",
                            placeholder = "مثلاً خارش، تنگی نفس و..."
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (selectedDrugIndex >= 0) {
                                        val allergy = PatientDrugAllergyMock(
                                            drugId = (state.allergies.size + 1).toLong(),
                                            drugName = drugList[selectedDrugIndex],
                                            allergyComments = allergyDesc.ifEmpty { "فاقد توضیحات عارضه" }
                                        )
                                        onIntent(SelfDeclarationIntent.UpdateState {
                                            copy(allergies = allergies + allergy)
                                        })
                                    }
                                    showAddDialog = false
                                },
                                modifier = Modifier.weight(1f),
                                enabled = selectedDrugIndex >= 0,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = taminColors.blueText,
                                    contentColor = Color.White
                                )
                            ) {
                                TaminText("افزودن", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { showAddDialog = false },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, taminColors.border),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = taminColors.textTertiary)
                            ) {
                                TaminText("انصراف", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@PreviewRtlTheme
@Preview
@Composable
fun SelfDeclarationAllergyScreenPreview() {
    PreviewRtlThemeContent {
        SelfDeclarationAllergyScreen(
            state = SelfDeclarationUiState(
                allergies = listOf(
                    PatientDrugAllergyMock(1, "پنی‌سیلین", "راش پوستی")
                )
            ),
            onIntent = {},
            onBackClicked = {}
        )
    }
}
