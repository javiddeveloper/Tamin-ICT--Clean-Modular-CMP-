package com.tamin.taminhamrah.feature.healthProfile.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
fun SelfDeclarationReviewScreen(
    state: SelfDeclarationUiState,
    onIntent: (SelfDeclarationIntent) -> Unit,
    onBackClicked: () -> Unit
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = {
            HealthNavigationBar(
                primaryText = "تأیید و ثبت نهایی اطلاعات",
                onPrimaryClick = { onIntent(SelfDeclarationIntent.ChangeStep(SelfDeclarationStep.SUCCESS)) },
                secondaryText = "بازگشت",
                onSecondaryClick = onBackClicked
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(taminColors.bgPage)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TaminText(
                text = "بررسی نهایی پروندهٔ سلامت",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = taminColors.textPrimary
                )
            )

            TaminText(
                text = "لطفاً صحت تمامی اطلاعات وارد شده در بخش‌های زیر را بررسی نموده و در صورت تایید نهایی دکمه ثبت را کلیک کنید.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = taminColors.textTertiary,
                    lineHeight = 22.sp
                )
            )

            // Identity
            ReviewSection(
                title = "اطلاعات هویتی",
                onEdit = { onIntent(SelfDeclarationIntent.ChangeStep(SelfDeclarationStep.IDENTITY)) }
            ) {
                IdentityRow(label = "نام بیمار:", value = "${state.patientName} ${state.patientFamily}")
                IdentityRow(label = "کد ملی / شماره بیمه:", value = state.insuranceNumber)
            }

            // Personal
            ReviewSection(
                title = "اطلاعات تکمیلی فردی",
                onEdit = { onIntent(SelfDeclarationIntent.ChangeStep(SelfDeclarationStep.PERSONAL)) }
            ) {
                IdentityRow(label = "وضعیت تاهل:", value = state.maritalStatus.ifEmpty { "نامشخص" })
                IdentityRow(label = "شغل فعلی:", value = state.job.ifEmpty { "نامشخص" })
            }

            // Contact
            ReviewSection(
                title = "تماس و سکونت",
                onEdit = { onIntent(SelfDeclarationIntent.ChangeStep(SelfDeclarationStep.CONTACT)) }
            ) {
                IdentityRow(label = "شماره همراه:", value = state.mobile)
                IdentityRow(label = "شهر / استان:", value = "${state.city} / ${state.province}".replace(" / ", "").ifEmpty { "نامشخص" })
            }

            // Emergency
            ReviewSection(
                title = "تماس اضطراری",
                onEdit = { onIntent(SelfDeclarationIntent.ChangeStep(SelfDeclarationStep.EMERGENCY)) }
            ) {
                IdentityRow(label = "مخاطب اضطراری:", value = "${state.emergencyName} ${state.emergencyFamily}".trim().ifEmpty { "نامشخص" })
                IdentityRow(label = "نسبت:", value = state.emergencyRelation.ifEmpty { "نامشخص" })
            }

            // Height and Weight
            ReviewSection(
                title = "قد و وزن",
                onEdit = { onIntent(SelfDeclarationIntent.ChangeStep(SelfDeclarationStep.PHYSICAL)) }
            ) {
                IdentityRow(label = "قد:", value = "${state.height} سانتی‌متر")
                IdentityRow(label = "وزن:", value = "${state.weight} کیلوگرم")
            }

            // Blood
            ReviewSection(
                title = "گروه خونی",
                onEdit = { onIntent(SelfDeclarationIntent.ChangeStep(SelfDeclarationStep.BLOOD)) }
            ) {
                val group = if (state.isBloodGroupUnknown) "نامشخص" else "${state.selectedBloodGroupLetter ?: ""}${state.selectedBloodGroupRh ?: ""}"
                IdentityRow(label = "گروه خونی:", value = group.ifEmpty { "نامشخص" })
            }

            // Lifestyle
            ReviewSection(
                title = "سبک زندگی",
                onEdit = { onIntent(SelfDeclarationIntent.ChangeStep(SelfDeclarationStep.LIFESTYLE)) }
            ) {
                IdentityRow(label = "مصرف سیگار / دخانیات:", value = if (state.isSmoking == true) "بله (${state.smokingPattern ?: ""})" else "خیر")
                IdentityRow(label = "فعالیت ورزشی:", value = if (state.isExercising == true) "بله (${state.exerciseFrequency ?: ""})" else "خیر")
            }

            // Allergies
            ReviewSection(
                title = "حساسیت‌های دارویی",
                onEdit = { onIntent(SelfDeclarationIntent.ChangeStep(SelfDeclarationStep.ALLERGY)) }
            ) {
                val allergyStr = state.allergies.joinToString { it.drugName }
                IdentityRow(label = "داروهای آلرژیک:", value = allergyStr.ifEmpty { "ندارد" })
            }
        }
    }
}

@Composable
private fun ReviewSection(
    title: String,
    onEdit: () -> Unit,
    content: @Composable () -> Unit
) {
    val taminColors = LocalTaminColors.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
        border = BorderStroke(1.dp, taminColors.border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TaminText(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = taminColors.textPrimary
                )
                TaminText(
                    text = "ویرایش",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = taminColors.blueText,
                    modifier = Modifier.clickable { onEdit() }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@PreviewRtlTheme
@Preview
@Composable
fun SelfDeclarationReviewScreenPreview() {
    PreviewRtlThemeContent {
        SelfDeclarationReviewScreen(
            state = SelfDeclarationUiState(
                maritalStatus = "متاهل",
                job = "کارمند",
                city = "تهران",
                province = "تهران",
                address = "میدان ونک",
                emergencyName = "محمد",
                emergencyRelation = "پدر",
                height = 180,
                weight = 80,
                selectedBloodGroupLetter = "AB",
                selectedBloodGroupRh = "+"
            ),
            onIntent = {},
            onBackClicked = {}
        )
    }
}
