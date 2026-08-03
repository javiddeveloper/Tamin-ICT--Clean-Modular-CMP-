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
import androidx.compose.ui.draw.clip
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Lock
import com.tamin.taminhamrah.feature.healthProfile.ui.components.SubmitErrorBanner
import com.tamin.taminhamrah.feature.healthProfile.ui.components.SubmitLoadingDialog
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.SubmitErrorsBottomSheet
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.BottomSheetType
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.findGroup
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileUiState
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taminx.feature.healthprofile.generated.resources.*

@Composable
fun SelfDeclarationReviewScreen(
    state: HealthProfileUiState,
    onIntent: (HealthProfileIntent) -> Unit,
    onBackClicked: () -> Unit,
    onCloseClicked: (() -> Unit)? = null,
    isLoading: Boolean = false,
    openSubmitErrorsBottomSheetTrigger: Boolean = false,
    onResetSubmitErrorsTrigger: () -> Unit = {},
) {
    val selfDecState = state.selfDeclaration
    val illnessGroups = state.illnessGroups
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    var showConfirmDialog by remember { mutableStateOf(false) }
    var showErrorBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(openSubmitErrorsBottomSheetTrigger) {
        if (openSubmitErrorsBottomSheetTrigger) {
            showErrorBottomSheet = true
            onResetSubmitErrorsTrigger()
        }
    }

    Scaffold(
        topBar = {
            HealthTopAppBar(
                title = stringResource(Res.string.health_review_title),
                onBackClicked = onBackClicked,
                onCloseClicked = onCloseClicked,
            )
        },
        bottomBar = {
            HealthIrritateNavigationBar(
                primaryText = stringResource(Res.string.health_confirm_info_btn),
                onPrimaryClick = { showConfirmDialog = true },
                secondaryText = stringResource(Res.string.health_gate_btn_back),
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

                if (selfDecState.submitProblems.isNotEmpty()) {
                    SubmitErrorBanner(
                        errorCount = selfDecState.submitProblems.size,
                        onShowErrorsClick = { showErrorBottomSheet = true }
                    )
                }

                WarningBanner(
                    message = stringResource(Res.string.health_warning_banner_desc)
                )

                // Identity
                ReviewSection(
                    title = stringResource(Res.string.health_identity_section_title),
                    onEdit = {
                        onIntent(
                            HealthProfileIntent.ChangeStep(
                                SelfDeclarationStep.IDENTITY,
                                isEditMode = true
                            )
                        )
                    },
                    icon = painterResource(Res.drawable.ic_identity)
                ) {
                    IdentityRow(
                        label = stringResource(Res.string.health_identity_section_patient_name),
                        value = "${selfDecState.identity.patientName} ${selfDecState.identity.patientFamily}"
                    )
                    IdentityRow(
                        label = stringResource(Res.string.health_identity_section_insurance),
                        value = selfDecState.identity.insuranceNumber,
                        showDivider = false
                    )
                }

                // Personal
                ReviewSection(
                    title = stringResource(Res.string.health_identity_section_personal_title),
                    onEdit = {
                        onIntent(
                            HealthProfileIntent.ChangeStep(
                                SelfDeclarationStep.PERSONAL,
                                isEditMode = true
                            )
                        )
                    },
                    icon = painterResource(Res.drawable.ic_personal)
                ) {
                    IdentityRow(
                        label = stringResource(Res.string.health_identity_section_personal_nationality),
                        value = selfDecState.personal.nationality.ifEmpty { stringResource(Res.string.health_allergy_unknows) })
                    IdentityRow(
                        label = stringResource(Res.string.health_identity_section_personal_marriage),
                        value = selfDecState.personal.maritalStatusLabel.ifEmpty {
                            stringResource(
                                Res.string.health_allergy_unknows
                            )
                        })
                    IdentityRow(
                        label = stringResource(Res.string.health_identity_section_personal_job),
                        value = selfDecState.personal.job.ifEmpty { stringResource(Res.string.health_allergy_unknows) },
                        showDivider = false
                    )
                }

                // Contact
                ReviewSection(
                    title = stringResource(Res.string.health_identity_section_contact_title),
                    onEdit = {
                        onIntent(
                            HealthProfileIntent.ChangeStep(
                                SelfDeclarationStep.CONTACT,
                                isEditMode = true
                            )
                        )
                    },
                    icon = painterResource(Res.drawable.ic_contact)
                ) {
                    IdentityRow(
                        label = stringResource(Res.string.health_identity_section_contact_mobile),
                        value = selfDecState.contact.mobile
                    )
                    IdentityRow(
                        label = stringResource(Res.string.health_identity_section_contact_province_city),
                        value = "${selfDecState.contact.cityLabel} / ${selfDecState.contact.provinceLabel}".ifEmpty {
                            stringResource(
                                Res.string.health_allergy_unknows
                            )
                        })
                    IdentityRow(
                        label = stringResource(Res.string.health_identity_section_contact_postal_code),
                        value = selfDecState.contact.postcode,
                        showDivider = false
                    )
                }

                // Emergency
                ReviewSection(
                    title = stringResource(Res.string.health_identity_section_contact_emergency_title),
                    onEdit = {
                        onIntent(
                            HealthProfileIntent.ChangeStep(
                                SelfDeclarationStep.EMERGENCY,
                                isEditMode = true
                            )
                        )
                    },
                    icon = painterResource(Res.drawable.ic_emergency)
                ) {
                    IdentityRow(
                        label = stringResource(Res.string.health_identity_section_contact_emergency_name),
                        value = "${selfDecState.emergency.emergencyName} ${selfDecState.emergency.emergencyFamily}".trim()
                            .ifEmpty { stringResource(Res.string.health_allergy_unknows) })
                    IdentityRow(
                        label = stringResource(Res.string.health_identity_section_contact_mobile),
                        value = selfDecState.emergency.emergencyMobile.ifEmpty { stringResource(Res.string.health_allergy_unknows) })
                    IdentityRow(
                        label = stringResource(Res.string.health_identity_section_contact_emergency_relation),
                        value = selfDecState.emergency.emergencyRelation.ifEmpty {
                            stringResource(
                                Res.string.health_allergy_unknows
                            )
                        },
                        showDivider = false
                    )
                }

                // Height and Weight
                ReviewSection(
                    title = stringResource(Res.string.health_identity_section_contact_height_weight_title),
                    onEdit = {
                        onIntent(
                            HealthProfileIntent.ChangeStep(
                                SelfDeclarationStep.PHYSICAL,
                                isEditMode = true
                            )
                        )
                    },
                    icon = painterResource(Res.drawable.ic_weight)
                ) {
                    IdentityRow(
                        label = stringResource(Res.string.health_identity_section_contact_height),
                        value = selfDecState.physical.height?.let { "$it ${stringResource(Res.string.health_physical_unit_cm)}" }
                            ?: stringResource(
                                Res.string.health_bmi_unselected_category
                            ))
                    IdentityRow(
                        label = stringResource(Res.string.health_identity_section_contact_weight),
                        value = selfDecState.physical.weight?.let { "$it ${stringResource(Res.string.health_physical_unit_kg)}" } ?: stringResource(
                            Res.string.health_bmi_unselected_category),
                        showDivider = false
                    )
                }

                // Blood
                ReviewSection(
                    title = stringResource(Res.string.health_identity_section_blood_group_title),
                    onEdit = {
                        onIntent(
                            HealthProfileIntent.ChangeStep(
                                SelfDeclarationStep.BLOOD,
                                isEditMode = true
                            )
                        )
                    },
                    icon = painterResource(Res.drawable.ic_blood)
                ) {
                    val unknownText = stringResource(Res.string.health_allergy_unknows)
                    val group =
                        if (selfDecState.bloodGroup.isBloodGroupUnknown) unknownText else "${selfDecState.bloodGroup.selectedBloodGroupLetter ?: ""}${selfDecState.bloodGroup.selectedBloodGroupRh ?: ""}"
                    IdentityRow(
                        label = stringResource(Res.string.health_identity_section_blood_group),
                        value = group.ifEmpty { unknownText },
                        showDivider = false
                    )
                }

                // Lifestyle
                ReviewSection(
                    title = stringResource(Res.string.health_identity_section_lifestyle_title),
                    onEdit = {
                        onIntent(
                            HealthProfileIntent.ChangeStep(
                                SelfDeclarationStep.LIFESTYLE,
                                isEditMode = true
                            )
                        )
                    },
                    icon = painterResource(Res.drawable.ic_lifestyle)
                ) {
                    val yesText = stringResource(Res.string.health_option_yes)
                    val noText = stringResource(Res.string.health_option_no)
                    IdentityRow(
                        label = stringResource(Res.string.health_identity_section_lifestyle_addiction),
                        value = if (selfDecState.lifestyle.hasAddiction == true) "$yesText (${selfDecState.lifestyle.substancePattern ?: ""})" else noText
                    )
                    IdentityRow(
                        label = stringResource(Res.string.health_identity_section_lifestyle_alcohol),
                        value = if (selfDecState.lifestyle.isDrinking == true) "$yesText (${selfDecState.lifestyle.drinkingPattern ?: ""})" else noText
                    )
                    IdentityRow(
                        label = stringResource(Res.string.health_identity_section_lifestyle_exercise),
                        value = if (selfDecState.lifestyle.isExercising == true) "$yesText (${selfDecState.lifestyle.exerciseFrequency ?: ""})" else noText
                    )
                    IdentityRow(
                        label = stringResource(Res.string.health_identity_section_lifestyle_smoking),
                        value = if (selfDecState.lifestyle.isSmoking == true) "$yesText (${selfDecState.lifestyle.smokingPattern ?: ""})" else noText,
                        showDivider = false
                    )
                }

                // Health Questions
                ReviewSection(
                    title = stringResource(Res.string.health_identity_section_diseases_title),
                    onEdit = {
                        onIntent(
                            HealthProfileIntent.ChangeStep(
                                SelfDeclarationStep.DISEASES,
                                isEditMode = true
                            )
                        )
                    },
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

                    val yesText = stringResource(Res.string.health_option_yes)
                    val noText = stringResource(Res.string.health_option_no)
                    val hasText = stringResource(Res.string.health_option_has)
                    val hasNotText = stringResource(Res.string.health_option_has_not)
                    val unknownText = stringResource(Res.string.health_allergy_unknows)

                    IdentityRow(
                        label = stringResource(Res.string.health_identity_section_diseases_risk_factor),
                        value = if (selfDecState.diseases.riskFactorIds.isNotEmpty()) hasText else hasNotText
                    )
                    IdentityRow(
                        label = stringResource(Res.string.health_identity_section_diseases_chronic),
                        value = if (selfDecState.diseases.hasChronicDisease == true) {
                            chronicGroup.ifEmpty { yesText }
                        } else {
                            noText
                        }
                    )
                    IdentityRow(
                        label = stringResource(Res.string.health_identity_section_diseases_mental),
                        value = if (selfDecState.diseases.hasMentalIllness == true) {
                            mental.ifEmpty { yesText }
                        } else if (selfDecState.diseases.hasMentalIllness == false) {
                            noText
                        } else {
                            unknownText
                        }
                    )
                    IdentityRow(
                        label = stringResource(Res.string.health_identity_section_diseases_cancer),
                        value = when (selfDecState.diseases.hasCancer) {
                            true -> {
                                cancer.ifEmpty { yesText }
                            }

                            false -> {
                                noText
                            }

                            else -> {
                                unknownText
                            }
                        },
                        showDivider = false
                    )
                }


                // Family Health
                ReviewSection(
                    title = stringResource(Res.string.health_identity_section_family_title),
                    onEdit = {
                        onIntent(
                            HealthProfileIntent.ChangeStep(
                                SelfDeclarationStep.FAMILY,
                                isEditMode = true
                            )
                        )
                    },
                    icon = painterResource(Res.drawable.ic_family)
                ) {
                    val cancer = illnessGroups.findGroup(
                        BottomSheetType.FAMILY_CANCER,
                        forFamily = true
                    )?.illnesses?.filter { it.id in selfDecState.family.familyCancerIds }
                        ?.joinToString { it.label } ?: ""
                    val hasText = stringResource(Res.string.health_option_has)
                    val hasNotText = stringResource(Res.string.health_option_has_not)
                    val yesText = stringResource(Res.string.health_option_yes)
                    val noText = stringResource(Res.string.health_option_no)
                    val unknownText = stringResource(Res.string.health_allergy_unknows)

                    val group =
                        if (selfDecState.family.familyDiseaseIds.isNotEmpty()) hasText else hasNotText

                    IdentityRow(
                        label = stringResource(Res.string.health_identity_section_diseases_risk_factor),
                        value = group
                    )
                    IdentityRow(
                        label = stringResource(Res.string.health_identity_section_diseases_cancer),
                        value = when (selfDecState.family.familyHasCancer) {
                            true -> {
                                cancer.ifEmpty { yesText }
                            }

                            false -> {
                                noText
                            }

                            else -> {
                                unknownText
                            }
                        },
                        showDivider = false
                    )
                }

                // Allergies
                ReviewSection(
                    title = stringResource(Res.string.health_identity_section_allergy_title),
                    onEdit = {
                        onIntent(
                            HealthProfileIntent.ChangeStep(
                                SelfDeclarationStep.ALLERGY,
                                isEditMode = true
                            )
                        )
                    },
                    icon = painterResource(Res.drawable.ic_allergy)
                ) {
                    val hasNotText = stringResource(Res.string.health_option_has_not)
                    val allergyStr = selfDecState.allergy.allergies.joinToString { it.drugName }
                    IdentityRow(
                        label = stringResource(Res.string.health_identity_section_allergy_drugs),
                        value = allergyStr.ifEmpty { hasNotText },
                        showDivider = false
                    )
                }
                Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
            }
        }
    }

    if (showConfirmDialog) {
        val taminColors = LocalTaminColors.current
        TaminConfirmationDialog(
            title = stringResource(Res.string.health_review_confirm_modal_text),
            description = stringResource(Res.string.health_review_confirm_modal_desc),
            onDismissRequest = { showConfirmDialog = false },
            icon = Icons.Outlined.Lock,
            confirmButton = {
                TaminFilledButton(
                    text = stringResource(Res.string.health_review_confirm_modal_btn_text),
                    icon = Icons.Default.Check,
                    onClick = {
                        showConfirmDialog = false
                        onIntent(HealthProfileIntent.SubmitDeclaration)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    height = 50.dp,
                    shape = RoundedCornerShape(14.dp)
                )
            },
            dismissButton = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .border(1.dp, taminColors.border, RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { showConfirmDialog = false },
                    contentAlignment = Alignment.Center
                ) {
                    TaminText(
                        text = stringResource(Res.string.health_btn_cancel),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        color = taminColors.textSecondary
                    )
                }
            }
        )
    }

    if (selfDecState.isSubmitLoading) {
        SubmitLoadingDialog(message = stringResource(Res.string.health_review_submit_loading_text))
    }

    if (showErrorBottomSheet && selfDecState.submitProblems.isNotEmpty()) {
        SubmitErrorsBottomSheet(
            problems = selfDecState.submitProblems,
            onDismiss = { showErrorBottomSheet = false }
        )
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
                    text = stringResource(Res.string.health_review_edit),
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

@PreviewRtlTheme
@Preview
@Composable
fun SelfDeclarationReviewScreenWithErrorsPreview() {
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
                    ),
                    submitProblems = listOf(
                        com.tamin.taminhamrah.feature.healthProfile.ui.model.HealthProblemPR(
                            code = 1,
                            message = "کد ملی وارد شده در سامانه استعلام یافت نشد."
                        ),
                        com.tamin.taminhamrah.feature.healthProfile.ui.model.HealthProblemPR(
                            code = 2,
                            message = "شمارهٔ موبایل با شمارهٔ ثبت‌شدهٔ بیمه‌شده مطابقت ندارد."
                        ),
                        com.tamin.taminhamrah.feature.healthProfile.ui.model.HealthProblemPR(
                            code = 3,
                            message = "تاریخ تولد با مدارک هویتی ثبت‌شده هم‌خوانی ندارد."
                        )
                    )
                )
            ),
            onIntent = {},
            onBackClicked = {}
        )
    }
}
