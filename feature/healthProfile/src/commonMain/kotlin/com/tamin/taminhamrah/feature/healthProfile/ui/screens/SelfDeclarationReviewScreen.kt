package com.tamin.taminhamrah.feature.healthProfile.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.healthProfile.ui.components.*
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationUiState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.PersonalStepState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.ContactStepState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.EmergencyStepState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.PhysicalStepState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.BloodGroupStepState
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import androidx.compose.ui.tooling.preview.Preview
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.BottomSheetType
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.findGroup

import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taminx.feature.healthprofile.generated.resources.Res
import taminx.feature.healthprofile.generated.resources.health_emergency_desc
import taminx.feature.healthprofile.generated.resources.ic_allergy
import taminx.feature.healthprofile.generated.resources.ic_blood
import taminx.feature.healthprofile.generated.resources.ic_contact
import taminx.feature.healthprofile.generated.resources.ic_emergency
import taminx.feature.healthprofile.generated.resources.ic_family
import taminx.feature.healthprofile.generated.resources.ic_health_pill
import taminx.feature.healthprofile.generated.resources.ic_health_question
import taminx.feature.healthprofile.generated.resources.ic_identity
import taminx.feature.healthprofile.generated.resources.ic_lifestyle
import taminx.feature.healthprofile.generated.resources.ic_personal
import taminx.feature.healthprofile.generated.resources.ic_weight

@Composable
fun SelfDeclarationReviewScreen(
    state: HealthProfileUiState,
    onIntent: (HealthProfileIntent) -> Unit,
    onBackClicked: () -> Unit,
    isLoading: Boolean = false
) {
    val selfDecState = state.selfDeclaration
    val illnessGroups = state.illnessGroups
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            HealthTopAppBar(title = "بررسی نهایی پروندهٔ سلامت", onBackClicked = onBackClicked)
        },
        bottomBar = {
            HealthIrritateNavigationBar(
                primaryText = "تأیید و ثبت نهایی اطلاعات",
                onPrimaryClick = { onIntent(HealthProfileIntent.SubmitDeclaration) },
                secondaryText = "بازگشت",
                onSecondaryClick = onBackClicked
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(taminColors.bgPage)
            ) {
                CardsListShimmerSkeleton()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
                    .background(taminColors.bgPage)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

            WarningBanner(
                message = "لطفاً صحت اطلاعات وارد شده را بررسی و تأیید کنید."
            )

            // Identity
            ReviewSection(
                title = "مشخصات هویتی",
                onEdit = { onIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.IDENTITY)) },
                icon = painterResource(Res.drawable.ic_identity)
            ) {
                IdentityRow(
                    label = "نام بیمار:",
                    value = "${selfDecState.identity.patientName} ${selfDecState.identity.patientFamily}"
                )
                IdentityRow(
                    label = "بیمه:",
                    value = selfDecState.identity.insuranceNumber
                )
            }

            // Personal
            ReviewSection(
                title = "اطلاعات تکمیلی فردی",
                onEdit = { onIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.PERSONAL)) },
                icon = painterResource(Res.drawable.ic_personal)
            ) {
                IdentityRow(
                    label = "ملیت:",
                    value = selfDecState.personal.nationality.ifEmpty { "نامشخص" })
                IdentityRow(
                    label = "تاهل:",
                    value = selfDecState.personal.maritalStatusLabel.ifEmpty { "نامشخص" })
                IdentityRow(
                    label = "شغل:",
                    value = selfDecState.personal.job.ifEmpty { "نامشخص" })
            }

            // Contact
            ReviewSection(
                title = "تماس و سکونت",
                onEdit = { onIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.CONTACT)) },
                icon = painterResource(Res.drawable.ic_contact)
            ) {
                IdentityRow(label = "موبایل:", value = selfDecState.contact.mobile)
                IdentityRow(
                    label = "شهر / استان:",
                    value = "${selfDecState.contact.cityLabel} / ${selfDecState.contact.provinceLabel}".ifEmpty { "نامشخص" })
                IdentityRow(label = "کد پستی:", value = selfDecState.contact.postcode)
            }

            // Emergency
            ReviewSection(
                title = "تماس اضطراری",
                onEdit = { onIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.EMERGENCY)) },
                icon = painterResource(Res.drawable.ic_emergency)
            ) {
                IdentityRow(
                    label = "نام:",
                    value = "${selfDecState.emergency.emergencyName} ${selfDecState.emergency.emergencyFamily}".trim()
                        .ifEmpty { "نامشخص" })
                IdentityRow(
                    label = "موبایل:",
                    value = selfDecState.emergency.emergencyMobile.ifEmpty { "نامشخص" })
                IdentityRow(
                    label = "نسبت:",
                    value = selfDecState.emergency.emergencyRelation.ifEmpty { "نامشخص" })
            }

            // Height and Weight
            ReviewSection(
                title = "قد و وزن",
                onEdit = { onIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.PHYSICAL)) },
                icon = painterResource(Res.drawable.ic_weight)
            ) {
                IdentityRow(
                    label = "قد:",
                    value = selfDecState.physical.height?.let { "$it سانتی‌متر" } ?: "ثبت نشده")
                IdentityRow(
                    label = "وزن:",
                    value = selfDecState.physical.weight?.let { "$it کیلوگرم" } ?: "ثبت نشده")
            }

            // Blood
            ReviewSection(
                title = "گروه خونی",
                onEdit = { onIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.BLOOD)) },
                icon = painterResource(Res.drawable.ic_blood)
            ) {
                val group =
                    if (selfDecState.bloodGroup.isBloodGroupUnknown) "نامشخص" else "${selfDecState.bloodGroup.selectedBloodGroupLetter ?: ""}${selfDecState.bloodGroup.selectedBloodGroupRh ?: ""}"
                IdentityRow(label = "گروه خونی:", value = group.ifEmpty { "نامشخص" })
            }

            // Lifestyle
            ReviewSection(
                title = "سبک زندگی",
                onEdit = { onIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.LIFESTYLE)) },
                icon = painterResource(Res.drawable.ic_lifestyle)
            ) {
                IdentityRow(
                    label = "اعتیاد:",
                    value = if (selfDecState.lifestyle.hasAddiction == true) "بله (${selfDecState.lifestyle.substancePattern ?: ""})" else "خیر"
                )
                IdentityRow(
                    label = "الکل:",
                    value = if (selfDecState.lifestyle.isDrinking == true) "بله (${selfDecState.lifestyle.drinkingPattern ?: ""})" else "خیر"
                )
                IdentityRow(
                    label = "ورزش:",
                    value = if (selfDecState.lifestyle.isExercising == true) "بله (${selfDecState.lifestyle.exerciseFrequency ?: ""})" else "خیر"
                )
                IdentityRow(
                    label = "دخانیات:",
                    value = if (selfDecState.lifestyle.isSmoking == true) "بله (${selfDecState.lifestyle.smokingPattern ?: ""})" else "خیر"
                )
            }

            // Health Questions
            ReviewSection(
                title = "سوالات سلامت",
                onEdit = { onIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.DISEASES)) },
                icon = painterResource(Res.drawable.ic_health_question)
            ) {
                val highRik =
                    illnessGroups.findGroup(BottomSheetType.RISK_FACTOR)?.illnesses?.filter { it.id in selfDecState.diseases.riskFactorIds }
                        ?.joinToString { it.label } ?: ""
                val chronicGroup =
                    illnessGroups.findGroup(BottomSheetType.ILLNESS_HISTORY)?.illnesses?.filter { it.id in selfDecState.diseases.chronicDiseaseIds }
                        ?.joinToString { it.label } ?: ""
                val mental =
                    illnessGroups.findGroup(BottomSheetType.MENTAL)?.illnesses?.filter { it.id in selfDecState.diseases.mentalIllnessIds }
                        ?.joinToString { it.label } ?: ""
                val cancer =
                    illnessGroups.findGroup(BottomSheetType.CANCER)?.illnesses?.filter { it.id in selfDecState.diseases.cancerIds }
                        ?.joinToString { it.label } ?: ""

                IdentityRow(
                    label = "سابقهٔ فشار/قند/چربی:",
                    value = if (selfDecState.diseases.riskFactorIds.isNotEmpty()) "دارد" else "ندارد"
                )
                IdentityRow(
                    label = "ابتلا به بیماری:",
                    value = if (selfDecState.diseases.hasChronicDisease == true) {
                        chronicGroup.ifEmpty { "بله" }
                    } else {
                        "خیر"
                    }
                )
                IdentityRow(
                    label = "اعصاب و روان:",
                    value = if (selfDecState.diseases.hasMentalIllness == true) {
                        mental.ifEmpty { "بله" }
                    } else if (selfDecState.diseases.hasMentalIllness == false) {
                        "خیر"
                    } else {
                        "نامشخص"
                    }
                )
                IdentityRow(
                    label = "سابقهٔ سرطان:",
                    value = when (selfDecState.diseases.hasCancer) {
                        true -> {
                            cancer.ifEmpty { "بله" }
                        }
                        false -> {
                            "خیر"
                        }
                        else -> {
                            "نامشخص"
                        }
                    }
                )
            }


            // Family Health
            ReviewSection(
                title = "سلامتی خانواده درجه یک",
                onEdit = { onIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.FAMILY)) },
                icon = painterResource(Res.drawable.ic_family)
            ) {
                val cancer = illnessGroups.findGroup(BottomSheetType.FAMILY_CANCER, forFamily = true)?.illnesses?.filter { it.id in selfDecState.family.familyCancerIds }?.joinToString { it.label } ?: ""
                val group = if (selfDecState.family.familyDiseaseIds.isNotEmpty()) "دارد" else "ندارد"

                IdentityRow(label = "سابقهٔ فشار/قند/چربی:", value = group)
                IdentityRow(
                    label = "سابقهٔ سرطان:",
                    value = when (selfDecState.family.familyHasCancer) {
                        true -> {
                            cancer.ifEmpty { "بله" }
                        }
                        false -> {
                            "خیر"
                        }
                        else -> {
                            "نامشخص"
                        }
                    }
                )
            }

            // Allergies
            ReviewSection(
                title = "حساسیت‌های دارویی",
                onEdit = { onIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.ALLERGY)) },
                icon = painterResource(Res.drawable.ic_allergy)
            ) {
                val allergyStr = selfDecState.allergy.allergies.joinToString { it.drugName }
                IdentityRow(label = "داروهای آلرژیک:", value = allergyStr.ifEmpty { "ندارد" })
            }
            Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
            }
        }
    }
}

@Composable
private fun ReviewSection(
    title: String,
    icon: Painter,
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        modifier = Modifier.size(20.dp),
                        painter = icon,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(4.dp))
                    TaminText(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = taminColors.textPrimary
                    )
                }
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
            state = HealthProfileUiState(
                selfDeclaration = SelfDeclarationUiState(
                    personal = PersonalStepState(maritalStatusLabel = "متاهل", job = "کارمند"),
                    contact = ContactStepState(
                        cityLabel = "تهران",
                        provinceLabel = "تهران",
                        address = "میدان ونک"
                    ),
                    emergency = EmergencyStepState(
                        emergencyName = "محمد",
                        emergencyRelation = "پدر"
                    ),
                    physical = PhysicalStepState(height = 180, weight = 80),
                    bloodGroup = BloodGroupStepState(
                        selectedBloodGroupLetter = "AB",
                        selectedBloodGroupRh = "+"
                    )
                )
            ),
            onIntent = {},
            onBackClicked = {}
        )
    }
}
