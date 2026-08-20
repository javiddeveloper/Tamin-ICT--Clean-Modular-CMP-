package com.tamin.taminhamrah.feature.taminServices.occurrence.components.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.InfoBanner
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceErrorWrapper
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceNavigationBar
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceTopAppBar
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.Step1PersonInfoShimmerSkeleton
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.StyledTextField
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceIntent
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceStep
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceUiState
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.PersonInfoStepState
import com.tamin.taminhamrah.feature.taminServices.occurrence.model.Gender
import com.tamin.taminhamrah.feature.taminServices.occurrence.model.OccurrencePersonalInfoPR
import com.tamin.taminhamrah.feature.taminServices.occurrence.model.UserInfoPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePicker
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePickerBottomSheet
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.PersianDateFormatter
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.edict_insurance_id
import taminx.core.core_ui.ic_privacy
import taminx.core.core_ui.ic_tamin_calendar
import taminx.core.core_ui.ic_tamin_user
import taminx.core.core_ui.identity_field_birth_date
import taminx.core.core_ui.identity_field_father_name
import taminx.core.core_ui.identity_field_first_name
import taminx.core.core_ui.identity_field_last_name
import taminx.core.core_ui.occurrence_field_birth_date
import taminx.core.core_ui.occurrence_field_gender
import taminx.core.core_ui.occurrence_field_handling_branch
import taminx.core.core_ui.occurrence_field_national_code
import taminx.core.core_ui.occurrence_next_step
import taminx.core.core_ui.occurrence_prev_step
import taminx.core.core_ui.occurrence_step1_readonly_hint
import taminx.core.core_ui.occurrence_step1_title

@Composable
internal fun Step1PersonInfoStep(
    uiState: OccurrenceUiState,
    onIntent: (OccurrenceIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
) {
    val taminColors = LocalTaminColors.current
    val step = uiState.personInfo
    val nationalCode = step.userInfo?.nationalID?.takeIf { it.isNotBlank() }
        ?: step.personalInfo?.nationalCode.orEmpty()

    if (uiState.dialogs.showBirthDatePicker) {
        TaminJalaliDatePickerBottomSheet(
            title = stringResource(Res.string.occurrence_field_birth_date),
            onDismiss = { onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(showBirthDatePicker = false))) },
            onConfirm = { year, month, day ->
                onIntent(
                    OccurrenceIntent.UpdatePersonInfo(
                        step.copy(
                            birthDate = PersianDateFormatter.format(year, month, day),
                            birthDateTimestamp = PersianDateFormatter.toEpochMillis(year, month, day),
                        )
                    )
                )
                onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(showBirthDatePicker = false)))
            },
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            OccurrenceTopAppBar(
                title = stringResource(Res.string.occurrence_step1_title),
                onBackClicked = onBack,
                currentStep = uiState.stepNumber,
                totalSteps = OccurrenceStep.entries.size,
            )
        },
        bottomBar = {
            OccurrenceNavigationBar(
                primaryText = stringResource(Res.string.occurrence_next_step),
                primaryEnabled = uiState.isStep1Valid && !uiState.isLoading && !uiState.isSubmitting,
                onPrimaryClick = { onIntent(OccurrenceIntent.GoToNextStep) },
                secondaryText = stringResource(Res.string.occurrence_prev_step),
                onSecondaryClick = onBack,
            )
        },
        contentWindowInsets = WindowInsets(0),
    ) { padding ->
        OccurrenceErrorWrapper(
            isLoading = uiState.isLoading,
            error = error,
            onRetry = { onIntent(OccurrenceIntent.LoadInitialData) },
            modifier = Modifier.padding(padding),
            shimmerContent = { Step1PersonInfoShimmerSkeleton(modifier = Modifier.padding(padding)) },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = padding.calculateTopPadding())
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.lg),
            ) {
                Spacer(modifier = Modifier.height(Spacing.md))

                InfoBanner(message = stringResource(Res.string.occurrence_step1_readonly_hint))

                Spacer(Modifier.height(Spacing.smd))

                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(13.dp))
                        .background(color = taminColors.bgSurface)
                        .border(
                            width = 1.dp,
                            shape = RoundedCornerShape(13.dp),
                            color = taminColors.border
                        )
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TaminText(
                        stringResource(Res.string.occurrence_field_national_code),
                        fontSize = 13.5.sp,
                        color = taminColors.textMuted,
                        fontWeight = FontWeight.Normal
                    )
                    TaminText(nationalCode)
                }

                Spacer(modifier = Modifier.height(Spacing.md))

                StyledTextField(
                    value = step.birthDate,
                    onValueChange = {},
                    label = "تاریخ تولد فرد حادثه دیده",
                    placeholder = "انتخاب تاریخ تولد",
                    trailingIcon = vectorResource(Res.drawable.ic_tamin_calendar),
                    readOnly = true,
                    onClick = { onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(showBirthDatePicker = true))) },
                )

                Spacer(modifier = Modifier.height(Spacing.lg))

                if (step.birthDate.isNotBlank()) {
                    step.userInfo?.let { info ->
                        PersonInfoCard(
                            info = info,
                            birthDate = step.birthDate,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.lg))
                Spacer(modifier = Modifier.height(padding.calculateBottomPadding()))
            }
        }
    }
}

@Composable
private fun PersonInfoCard(
    info: UserInfoPR,
    birthDate: String,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface()
            .padding(Spacing.lg),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(IconSize.tile)
                    .background(taminColors.blueBg, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_user),
                    contentDescription = null,
                    tint = taminColors.blueText,
                    modifier = Modifier.size(IconSize.tileInner),
                )
            }
            Spacer(modifier = Modifier.width(Spacing.md))
            Column {
                Text(
                    text = info.firstName + " " + info.lastName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = taminColors.textPrimary,
                )
                Spacer(modifier = Modifier.height(Spacing.xxs))
                Text(
                    text = stringResource(
                        Res.string.edict_insurance_id,
                        info.insuranceNumber.ifBlank { "-" }
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = taminColors.textTertiary,
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.lg))
        TaminDivider()
        Spacer(modifier = Modifier.height(Spacing.md))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.lg),
            ) {
                PersonInfoGridItem(
                    label = stringResource(Res.string.identity_field_first_name),
                    value = info.firstName
                )
                PersonInfoGridItem(
                    label = stringResource(Res.string.identity_field_father_name),
                    value = info.fatherName
                )
                PersonInfoGridItem(
                    label = stringResource(Res.string.identity_field_birth_date),
                    value = birthDate,
                    numeric = true
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.lg),
            ) {
                PersonInfoGridItem(
                    label = stringResource(Res.string.identity_field_last_name),
                    value = info.lastName
                )
                PersonInfoGridItem(
                    label = stringResource(Res.string.occurrence_field_gender),
                    value = Gender.fromCode(info.genderCode)?.displayName?:""
//                    value = info.genderCode
                )

                PersonInfoGridItem(
                    label = stringResource(Res.string.occurrence_field_handling_branch),
                    value = "-",
                    numeric = true
                )

            }
        }
    }
}

@Composable
private fun PersonInfoGridItem(label: String, value: String, numeric: Boolean = false) {
    val taminColors = LocalTaminColors.current
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = taminColors.textMuted,
        )
        if (numeric) {
            NumericText(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = taminColors.textPrimary
            )
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = taminColors.textPrimary
            )
        }
    }
}

@PreviewRtlTheme
@Preview
@Composable
private fun Step1PersonInfoStepPreview() {
    PreviewRtlThemeContent {
        Step1PersonInfoStep(
            uiState = OccurrenceUiState(
                currentStep = OccurrenceStep.PERSON_INFO,
                personInfo = PersonInfoStepState(
                    personalInfo = OccurrencePersonalInfoPR(
                        nationalCode = "0012345678",
                        firstName = "علی",
                        lastName = "محمدی",
                        fatherName = "رضا",
                        gender = "مرد",
                        birthDate = "1370/01/15",
                        insuranceNumber = "12345",
                        branchCode = "001",
                        nationality = "ایرانی",
                        insuranceType = "اجباری",
                    ),
                    birthDate = "1370/01/15",
                ),
            ),
            onIntent = {},
            onBack = {},
        )
    }
}

@PreviewRtlTheme
@Preview
@Composable
private fun Step1PersonInfoStepEmptyPreview() {
    PreviewRtlThemeContent {
        Step1PersonInfoStep(uiState = OccurrenceUiState( currentStep = OccurrenceStep.PERSON_INFO,), onIntent = {}, onBack = {})
    }
}
