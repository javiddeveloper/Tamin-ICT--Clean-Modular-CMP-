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
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.AllergyStepState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import androidx.compose.ui.tooling.preview.Preview
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.BottomSheetConfig
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.BottomSheetItem
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.BottomSheetType
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.HealthBottomSheet
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.model.DrugAllergyItemPR
import com.tamin.taminhamrah.ui.components.IconBox
import org.jetbrains.compose.resources.painterResource
import taminx.feature.healthprofile.generated.resources.Res
import taminx.feature.healthprofile.generated.resources.ic_health_pill
import taminx.feature.healthprofile.generated.resources.ic_health_tobacco

@Composable
fun SelfDeclarationAllergyScreen(
    state: AllergyStepState,
    onIntent: (HealthProfileIntent) -> Unit,
    onBackClicked: () -> Unit
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    var showBottomsheet by remember { mutableStateOf(false) }

    var selectedDrugIndex by remember { mutableStateOf(-1) }
    var allergyDesc by remember { mutableStateOf("") }
    val drugList = listOf("پنی‌سیلین", "استامینوفن", "آسپیرین", "ایبوپروفن", "آموکسی‌سیلین", "سفکسیم", "سفالکسین", "مترونیدازول")

    Scaffold(
        topBar = {
            HealthTopAppBar(
                title = "حساسیت‌ دارویی",
                currentStep = 10,
                totalSteps = 10,
                onBackClicked = onBackClicked
            )
        },
        bottomBar = {
            HealthIrritateNavigationBar(
                primaryText = "مرحلهٔ بعدی (بررسی نهایی)",
                onPrimaryClick = { onIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.REVIEW)) },
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconBox(
                    painter = painterResource(Res.drawable.ic_health_pill),
                    backgroundColor = taminColors.greenBg,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(8.dp))
                TaminText(
                    "انتخاب دارو",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = taminColors.textPrimary
                )
            }

            TaminText(
                text = "در این قسمت می\u200Cتوانید داروهایی که به آن\u200Cها حساسیت دارید را اضافه کنید.",
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
                    showBottomsheet = true
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
                            onIntent(HealthProfileIntent.UpdateAllergy(
                                state.copy(allergies = state.allergies.filterIndexed { i, _ -> i != idx })
                            ))
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
        }

        if (showBottomsheet) {
            HealthBottomSheet(
                config = BottomSheetConfig(
                    title = "انتخاب دارو",
                    subtitle = "دارویی که به آن حساسیت دارید را انتخاب کنید.",
                    description = "",
                    type = BottomSheetType.CUSTOM,
                    singleSelection = true,
                    submitText = "افزودن",
                    cancelText = "انصراف",
                    items = drugList.mapIndexed { index, drug ->
                        BottomSheetItem(
                            id = index,
                            title = drug,
                            isSelected = index == selectedDrugIndex
                        )
                    }
                ),
                onDismissRequest = { showBottomsheet = false },
                onSubmit = { result ->
                    val selectedId = result.selectedItemIds.firstOrNull()
                    if (selectedId != null) {
                        val allergy = DrugAllergyItemPR(
                            drugId = selectedId,
                            drugName = drugList[selectedId],
                            allergyComments = result.description?.takeIf { it.isNotBlank() } ?: "فاقد توضیحات عارضه"
                        )
                        onIntent(
                            HealthProfileIntent.UpdateAllergy(
                                state.copy(allergies = state.allergies + allergy)
                            )
                        )
                    }
                    showBottomsheet = false
                }
            )
        }
    }
}

@PreviewRtlTheme
@Preview
@Composable
fun SelfDeclarationAllergyScreenPreview() {
    PreviewRtlThemeContent {
        SelfDeclarationAllergyScreen(
            state = AllergyStepState(
                allergies = listOf(
//                    PatientDrugAllergyMock(1, "پنی‌سیلین", "راش پوستی")
                )
            ),
            onIntent = {},
            onBackClicked = {}
        )
    }
}

