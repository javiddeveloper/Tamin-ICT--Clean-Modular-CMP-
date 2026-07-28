package com.tamin.taminhamrah.feature.healthProfile.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.healthProfile.ui.components.*
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationUiState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.PersonalStepState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.ContactStepState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.EmergencyStepState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.PhysicalStepState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.BloodGroupStepState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.LifestyleStepState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.AllergyStepState
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
        topBar = {
            HealthTopAppBar(title = "بررسی نهایی پروندهٔ سلامت", onBackClicked = onBackClicked)
        },
        bottomBar = {
            HealthIrritateNavigationBar(
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
                .padding(top = paddingValues.calculateTopPadding())
                .background(taminColors.bgPage)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 16.dp),
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
                IdentityRow(label = "نام بیمار:", value = "${state.identity.patientName} ${state.identity.patientFamily}")
                IdentityRow(label = "کد ملی / شماره بیمه:", value = state.identity.insuranceNumber)
            }

            // Personal
            ReviewSection(
                title = "اطلاعات تکمیلی فردی",
                onEdit = { onIntent(SelfDeclarationIntent.ChangeStep(SelfDeclarationStep.PERSONAL)) }
            ) {
                IdentityRow(label = "وضعیت تاهل:", value = state.personal.maritalStatus.ifEmpty { "نامشخص" })
                IdentityRow(label = "شغل فعلی:", value = state.personal.job.ifEmpty { "نامشخص" })
            }

            // Contact
            ReviewSection(
                title = "تماس و سکونت",
                onEdit = { onIntent(SelfDeclarationIntent.ChangeStep(SelfDeclarationStep.CONTACT)) }
            ) {
                IdentityRow(label = "شماره همراه:", value = state.contact.mobile)
                IdentityRow(label = "شهر / استان:", value = "${state.contact.city} / ${state.contact.province}".replace(" / ", "").ifEmpty { "نامشخص" })
            }

            // Emergency
            ReviewSection(
                title = "تماس اضطراری",
                onEdit = { onIntent(SelfDeclarationIntent.ChangeStep(SelfDeclarationStep.EMERGENCY)) }
            ) {
                IdentityRow(label = "مخاطب اضطراری:", value = "${state.emergency.emergencyName} ${state.emergency.emergencyFamily}".trim().ifEmpty { "نامشخص" })
                IdentityRow(label = "نسبت:", value = state.emergency.emergencyRelation.ifEmpty { "نامشخص" })
            }

            // Height and Weight
            ReviewSection(
                title = "قد و وزن",
                onEdit = { onIntent(SelfDeclarationIntent.ChangeStep(SelfDeclarationStep.PHYSICAL)) }
            ) {
                IdentityRow(label = "قد:", value = "${state.physical.height} سانتی‌متر")
                IdentityRow(label = "وزن:", value = "${state.physical.weight} کیلوگرم")
            }

            // Blood
            ReviewSection(
                title = "گروه خونی",
                onEdit = { onIntent(SelfDeclarationIntent.ChangeStep(SelfDeclarationStep.BLOOD)) }
            ) {
                val group = if (state.bloodGroup.isBloodGroupUnknown) "نامشخص" else "${state.bloodGroup.selectedBloodGroupLetter ?: ""}${state.bloodGroup.selectedBloodGroupRh ?: ""}"
                IdentityRow(label = "گروه خونی:", value = group.ifEmpty { "نامشخص" })
            }

            // Lifestyle
            ReviewSection(
                title = "سبک زندگی",
                onEdit = { onIntent(SelfDeclarationIntent.ChangeStep(SelfDeclarationStep.LIFESTYLE)) }
            ) {
                IdentityRow(label = "مصرف سیگار / دخانیات:", value = if (state.lifestyle.isSmoking == true) "بله (${state.lifestyle.smokingPattern ?: ""})" else "خیر")
                IdentityRow(label = "فعالیت ورزشی:", value = if (state.lifestyle.isExercising == true) "بله (${state.lifestyle.exerciseFrequency ?: ""})" else "خیر")
            }

            // Allergies
            ReviewSection(
                title = "حساسیت‌های دارویی",
                onEdit = { onIntent(SelfDeclarationIntent.ChangeStep(SelfDeclarationStep.ALLERGY)) }
            ) {
                val allergyStr = state.allergy.allergies.joinToString { it.drugName }
                IdentityRow(label = "داروهای آلرژیک:", value = allergyStr.ifEmpty { "ندارد" })
            }
            Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
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
                personal = PersonalStepState(maritalStatus = "متاهل", job = "کارمند"),
                contact = ContactStepState(city = "تهران", province = "تهران", address = "میدان ونک"),
                emergency = EmergencyStepState(emergencyName = "محمد", emergencyRelation = "پدر"),
                physical = PhysicalStepState(height = 180, weight = 80),
                bloodGroup = BloodGroupStepState(selectedBloodGroupLetter = "AB", selectedBloodGroupRh = "+")
            ),
            onIntent = {},
            onBackClicked = {}
        )
    }
}

