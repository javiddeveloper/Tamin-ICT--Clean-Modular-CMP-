package com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopDocumentBox
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopDocumentTypeSheet
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFormCheck
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFormError
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFormFooter
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFormNote
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFormSection
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFormStep
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopLookupSheet
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopPickerField
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopReviewGroup
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopReviewRow
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopScreenShell
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopStepper
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopTextField
import com.tamin.taminhamrah.feature.workshops.ui.model.REGISTRATION_MAX_DOCUMENTS
import com.tamin.taminhamrah.feature.workshops.ui.model.RegistrationDocumentTypes
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.feature.workshops.ui.WorkshopConstants
import com.tamin.taminhamrah.ui.components.InputRestriction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.shape.RoundedCornerShape
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFormBanner
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.abs_form_banner
import taminx.core.core_ui.abs_form_download
import taminx.core.core_ui.ic_tamin_download
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePicker
import com.tamin.taminhamrah.ui.digitsOnly
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.PersianDateFormatter
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.abs_form_birth_city
import taminx.core.core_ui.abs_form_birth_date
import taminx.core.core_ui.abs_form_check
import taminx.core.core_ui.abs_form_err_national_id
import taminx.core.core_ui.abs_form_docs_desc
import taminx.core.core_ui.abs_form_docs_title
import taminx.core.core_ui.abs_form_first_name
import taminx.core.core_ui.abs_form_full_name
import taminx.core.core_ui.abs_form_identity_desc
import taminx.core.core_ui.abs_form_identity_title
import taminx.core.core_ui.abs_form_issue_city
import taminx.core.core_ui.abs_form_job
import taminx.core.core_ui.abs_form_last_name
import taminx.core.core_ui.abs_form_note_changes
import taminx.core.core_ui.abs_form_note_dependants
import taminx.core.core_ui.abs_form_place_desc
import taminx.core.core_ui.abs_form_place_title
import taminx.core.core_ui.abs_form_start_date
import taminx.core.core_ui.abs_form_step_docs
import taminx.core.core_ui.abs_form_step_identity
import taminx.core.core_ui.abs_form_step_place
import taminx.core.core_ui.abs_form_submit
import taminx.core.core_ui.abs_form_summary
import taminx.core.core_ui.abs_form_title
import taminx.core.core_ui.abs_form_workshop
import taminx.core.core_ui.member_national_id
import taminx.core.core_ui.workshop_select_date
import taminx.core.core_ui.workshop_ten_digits
import taminx.core.core_ui.ws_form_next

/**
 * نام‌نویسی غیرحضوری — who the person is, where they are from and what they will do, then the
 * documents that evidence it.
 *
 * A page of نام‌نویسی غیرحضوری بیمه‌شده rather than a route: the registration is created against
 * the workshop that list is already showing.
 */
@Composable
fun RegistrationFormPage(
    form: RegistrationFormState,
    workshopName: String,
    workshopCode: String?,
    onIntent: (WorkshopRecentlyAddedMembersIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isTypeSheetOpen by remember { mutableStateOf(false) }
    var pendingTypeCode by remember { mutableStateOf<String?>(null) }
    var openDatePicker by remember { mutableStateOf<DateField?>(null) }
    val scope = rememberCoroutineScope()

    val filePicker = rememberFilePickerLauncher(type = FileKitType.Image) { file ->
        val typeCode = pendingTypeCode
        pendingTypeCode = null
        if (file == null || typeCode == null) return@rememberFilePickerLauncher
        scope.launch {
            onIntent(
                WorkshopRecentlyAddedMembersIntent.FormAddDocument(
                    fileName = file.name,
                    bytes = file.readBytes(),
                    typeCode = typeCode,
                ),
            )
        }
    }

    val identityLabel = stringResource(Res.string.abs_form_step_identity)
    val placeLabel = stringResource(Res.string.abs_form_step_place)
    val docsLabel = stringResource(Res.string.abs_form_step_docs)
    val steps = remember(form.step, identityLabel) {
        persistentListOf(
            WorkshopFormStep(identityLabel, form.step > 1, form.step == 1),
            WorkshopFormStep(placeLabel, form.step > 2, form.step == 2),
            WorkshopFormStep(docsLabel, false, form.step == 3),
        )
    }

    WorkshopScreenShell(
        title = stringResource(Res.string.abs_form_title),
        onBack = onBack,
        workshopName = workshopName.takeIf { it.isNotBlank() },
        workshopCode = workshopCode,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        ) {
            WorkshopStepper(steps = steps)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.page)
                    .padding(top = Spacing.md, bottom = Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                when (form.step) {
                    1 -> IdentityStep(
                        form = form,
                        onIntent = onIntent,
                        onPickDate = { openDatePicker = DateField.BIRTH },
                    )

                    2 -> PlaceStep(
                        form = form,
                        onIntent = onIntent,
                        onPickDate = { openDatePicker = DateField.START },
                    )

                    else -> DocumentsStep(
                        form = form,
                        workshopName = workshopName,
                        onAdd = { isTypeSheetOpen = true },
                        onIntent = onIntent,
                    )
                }

                // The national id states its own verdict on the field; anything else the step
                // is missing is said once, here.
                form.error
                    ?.takeIf { it != Res.string.abs_form_err_national_id }
                    ?.let { WorkshopFormError(text = stringResource(it)) }
            }
        }

        WorkshopFormFooter(
            nextLabel = stringResource(
                if (form.isLastStep) Res.string.abs_form_submit else Res.string.ws_form_next,
            ),
            onNext = { onIntent(WorkshopRecentlyAddedMembersIntent.FormNext) },
            onPrev = if (form.step > 1) {
                { onIntent(WorkshopRecentlyAddedMembersIntent.FormPrev) }
            } else {
                null
            },
        )
    }

    if (isTypeSheetOpen) {
        WorkshopDocumentTypeSheet(
            types = RegistrationDocumentTypes,
            onDismiss = { isTypeSheetOpen = false },
            onSelect = { type ->
                isTypeSheetOpen = false
                pendingTypeCode = type.code
                filePicker.launch()
            },
        )
    }

    form.picker?.let { picker ->
        WorkshopLookupSheet(
            title = stringResource(
                when (picker) {
                    RegistrationPicker.BIRTH_CITY -> Res.string.abs_form_birth_city
                    RegistrationPicker.ISSUE_CITY -> Res.string.abs_form_issue_city
                    RegistrationPicker.JOB -> Res.string.abs_form_job
                },
            ),
            query = form.pickerQuery,
            onQueryChange = {
                onIntent(WorkshopRecentlyAddedMembersIntent.FormPickerQueryChanged(it))
            },
            options = form.pickerOptions,
            isLoading = form.isPickerLoading,
            onDismiss = { onIntent(WorkshopRecentlyAddedMembersIntent.FormPickerOpened(null)) },
            onSelect = { option ->
                onIntent(WorkshopRecentlyAddedMembersIntent.FormOptionPicked(picker, option))
            },
        )
    }

    openDatePicker?.let { field ->
        TaminJalaliDatePicker(
            title = stringResource(
                if (field == DateField.BIRTH) {
                    Res.string.abs_form_birth_date
                } else {
                    Res.string.abs_form_start_date
                },
            ),
            onDismiss = { openDatePicker = null },
            onConfirm = { year, month, day ->
                val date = PersianDateFormatter.format(year, month, day)
                onIntent(
                    WorkshopRecentlyAddedMembersIntent.FormFieldChanged {
                        if (field == DateField.BIRTH) copy(birthDate = date) else copy(startDate = date)
                    },
                )
                openDatePicker = null
            },
        )
    }
}

/** Which of the two dates a picker was opened for. */
private enum class DateField { BIRTH, START }

/**
 * Step one: who the person is, exactly as their documents spell it.
 *
 * The two names take letters only and the code digits only, so a wrong keyboard is refused as it
 * is typed rather than at submit. The code's own verdict shows on the field, which is where the
 * user is looking when they mistype it.
 */
@Composable
private fun IdentityStep(
    form: RegistrationFormState,
    onIntent: (WorkshopRecentlyAddedMembersIntent) -> Unit,
    onPickDate: () -> Unit,
) {
    // Only judged once there are ten digits to judge — a half-typed code is not yet wrong.
    val isNationalIdValid = when {
        form.nationalId.length < WorkshopConstants.NATIONAL_ID_LENGTH -> null
        else -> isValidIranianNationalId(form.nationalId)
    }

    // The design opens step one with why the declaration is needed, and the form itself.
    WorkshopFormBanner(text = stringResource(Res.string.abs_form_banner))
    TaminOutlinedButton(
        text = stringResource(Res.string.abs_form_download),
        onClick = { onIntent(WorkshopRecentlyAddedMembersIntent.FormDownloadDeclaration) },
        icon = vectorResource(Res.drawable.ic_tamin_download),
        enabled = !form.isDownloadingDeclaration,
        shape = RoundedCornerShape(CornerRadius.chip),
        height = WorkshopDimens.panelButtonHeight,
        borderWidth = WorkshopDimens.panelButtonBorderWidth,
        borderColor = LocalTaminColors.current.blueBorder,
        containerColor = LocalTaminColors.current.bgSurface,
        contentColor = LocalTaminColors.current.blueText,
    )

    WorkshopFormSection(
        title = stringResource(Res.string.abs_form_identity_title),
        description = stringResource(Res.string.abs_form_identity_desc),
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(WorkshopDimens.fieldGap),
    ) {
        WorkshopTextField(
            label = stringResource(Res.string.abs_form_first_name),
            value = form.firstName,
            onValueChange = { value ->
                onIntent(
                    WorkshopRecentlyAddedMembersIntent.FormFieldChanged { copy(firstName = value) },
                )
            },
            keyboardType = KeyboardType.Text,
            inputRestriction = InputRestriction.LettersOnly,
            isRequired = true,
            modifier = Modifier.weight(1f),
        )
        WorkshopTextField(
            label = stringResource(Res.string.abs_form_last_name),
            value = form.lastName,
            onValueChange = { value ->
                onIntent(
                    WorkshopRecentlyAddedMembersIntent.FormFieldChanged { copy(lastName = value) },
                )
            },
            keyboardType = KeyboardType.Text,
            inputRestriction = InputRestriction.LettersOnly,
            isRequired = true,
            modifier = Modifier.weight(1f),
        )
    }
    WorkshopTextField(
        label = stringResource(Res.string.member_national_id),
        value = form.nationalId,
        onValueChange = { value ->
            onIntent(
                WorkshopRecentlyAddedMembersIntent.FormFieldChanged {
                    copy(nationalId = value.digitsOnly())
                },
            )
        },
        placeholder = stringResource(Res.string.workshop_ten_digits),
        maxLength = WorkshopConstants.NATIONAL_ID_LENGTH,
        isRequired = true,
        isValid = isNationalIdValid,
        errorText = stringResource(Res.string.abs_form_err_national_id)
            .takeIf { isNationalIdValid == false },
    )
    WorkshopPickerField(
        label = stringResource(Res.string.abs_form_birth_date),
        value = form.birthDate.takeIf { it.isNotBlank() },
        onClick = onPickDate,
        placeholder = stringResource(Res.string.workshop_select_date),
        isDate = true,
    )
}

/** Step two: where the person is from, and what they will do here. */
@Composable
private fun PlaceStep(
    form: RegistrationFormState,
    onIntent: (WorkshopRecentlyAddedMembersIntent) -> Unit,
    onPickDate: () -> Unit,
) {
    WorkshopFormSection(
        title = stringResource(Res.string.abs_form_place_title),
        description = stringResource(Res.string.abs_form_place_desc),
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(WorkshopDimens.fieldGap),
    ) {
        WorkshopPickerField(
            label = stringResource(Res.string.abs_form_birth_city),
            value = form.birthCity?.label,
            onClick = {
                onIntent(
                    WorkshopRecentlyAddedMembersIntent.FormPickerOpened(
                        RegistrationPicker.BIRTH_CITY,
                    ),
                )
            },
            modifier = Modifier.weight(1f),
        )
        WorkshopPickerField(
            label = stringResource(Res.string.abs_form_issue_city),
            value = form.issueCity?.label,
            onClick = {
                onIntent(
                    WorkshopRecentlyAddedMembersIntent.FormPickerOpened(
                        RegistrationPicker.ISSUE_CITY,
                    ),
                )
            },
            modifier = Modifier.weight(1f),
        )
    }
    WorkshopPickerField(
        label = stringResource(Res.string.abs_form_job),
        value = form.job?.label,
        onClick = {
            onIntent(WorkshopRecentlyAddedMembersIntent.FormPickerOpened(RegistrationPicker.JOB))
        },
    )
    WorkshopPickerField(
        label = stringResource(Res.string.abs_form_start_date),
        value = form.startDate.takeIf { it.isNotBlank() },
        onClick = onPickDate,
        placeholder = stringResource(Res.string.workshop_select_date),
        isDate = true,
    )
}

/** Step three: what was entered, the evidence for it, and the declaration. */
@Composable
private fun DocumentsStep(
    form: RegistrationFormState,
    workshopName: String,
    onAdd: () -> Unit,
    onIntent: (WorkshopRecentlyAddedMembersIntent) -> Unit,
) {
    val fullNameLabel = stringResource(Res.string.abs_form_full_name)
    val nationalIdLabel = stringResource(Res.string.member_national_id)
    val birthDateLabel = stringResource(Res.string.abs_form_birth_date)
    val birthCityLabel = stringResource(Res.string.abs_form_birth_city)
    val issueCityLabel = stringResource(Res.string.abs_form_issue_city)
    val jobLabel = stringResource(Res.string.abs_form_job)
    val startDateLabel = stringResource(Res.string.abs_form_start_date)
    val workshopLabel = stringResource(Res.string.abs_form_workshop)
    val summaryRows = remember(form, fullNameLabel) {
        persistentListOf(
            WorkshopReviewRow(fullNameLabel, form.fullName, isNumeric = false),
            WorkshopReviewRow(nationalIdLabel, form.nationalId),
            WorkshopReviewRow(birthDateLabel, form.birthDate),
            WorkshopReviewRow(birthCityLabel, form.birthCity?.label.orEmpty(), isNumeric = false),
            WorkshopReviewRow(issueCityLabel, form.issueCity?.label.orEmpty(), isNumeric = false),
            WorkshopReviewRow(jobLabel, form.job?.label.orEmpty(), isNumeric = false),
            WorkshopReviewRow(startDateLabel, form.startDate),
            WorkshopReviewRow(workshopLabel, workshopName, isNumeric = false),
        )
    }

    WorkshopFormSection(
        title = stringResource(Res.string.abs_form_docs_title),
        description = stringResource(Res.string.abs_form_docs_desc),
    )
    WorkshopReviewGroup(
        title = stringResource(Res.string.abs_form_summary),
        rows = summaryRows,
        isOpen = form.isSummaryOpen,
        onToggle = {
            onIntent(
                WorkshopRecentlyAddedMembersIntent.FormFieldChanged {
                    copy(isSummaryOpen = !isSummaryOpen)
                },
            )
        },
        onEdit = {
            onIntent(WorkshopRecentlyAddedMembersIntent.FormFieldChanged { copy(step = 1) })
        },
    )
    WorkshopDocumentBox(
        documents = form.documents,
        capacity = REGISTRATION_MAX_DOCUMENTS,
        onAdd = onAdd,
        onRemove = { index ->
            onIntent(WorkshopRecentlyAddedMembersIntent.FormRemoveDocument(index))
        },
    )
    WorkshopFormNote(text = stringResource(Res.string.abs_form_note_dependants))
    WorkshopFormNote(text = stringResource(Res.string.abs_form_note_changes))
    WorkshopFormCheck(
        label = stringResource(Res.string.abs_form_check),
        isChecked = form.isConfirmed,
        onToggle = {
            onIntent(
                WorkshopRecentlyAddedMembersIntent.FormFieldChanged {
                    copy(isConfirmed = !isConfirmed)
                },
            )
        },
    )
}

@PreviewRtlTheme
@Composable
private fun RegistrationFormIdentityPreview() {
    PreviewRtlThemeContent {
        RegistrationFormPage(
            form = RegistrationFormState(firstName = "احمد", lastName = "احمدی"),
            workshopName = "آموزشگاه کامپیوتر توکلی-ایمیل",
            workshopCode = "۰۹۶۸۲۱۰۱۷۰",
            onIntent = {},
            onBack = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun RegistrationFormDocumentsPreview() {
    PreviewRtlThemeContent {
        RegistrationFormPage(
            form = RegistrationFormState(
                step = 3,
                firstName = "احمد",
                lastName = "احمدی",
                nationalId = "۲۷۴۱۸۸۰۲۹۸",
                birthDate = "۱۳۷۸/۰۵/۲۶",
                startDate = "۱۴۰۵/۰۵/۰۱",
            ),
            workshopName = "آموزشگاه کامپیوتر توکلی-ایمیل",
            workshopCode = "۰۹۶۸۲۱۰۱۷۰",
            onIntent = {},
            onBack = {},
        )
    }
}
