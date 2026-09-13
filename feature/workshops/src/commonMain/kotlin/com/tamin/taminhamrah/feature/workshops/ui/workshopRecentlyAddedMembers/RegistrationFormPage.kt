package com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.tamin.taminhamrah.feature.workshops.ui.WorkshopConstants
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopDocumentsPanel
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFieldSlot
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFormBanner
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
import com.tamin.taminhamrah.model.common.isValidIranianNationalId
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.InputRestriction
import com.tamin.taminhamrah.ui.components.SegmentedInputField
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePicker
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.digitsOnly
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.PersianDateFormatter
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.abs_form_banner
import taminx.core.core_ui.abs_form_birth_city
import taminx.core.core_ui.abs_form_birth_date
import taminx.core.core_ui.abs_form_check
import taminx.core.core_ui.abs_form_docs_desc
import taminx.core.core_ui.abs_form_docs_title
import taminx.core.core_ui.abs_form_download
import taminx.core.core_ui.abs_form_err_national_id
import taminx.core.core_ui.abs_form_err_required
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
import taminx.core.core_ui.ic_number
import taminx.core.core_ui.ic_tamin_download
import taminx.core.core_ui.member_national_id
import taminx.core.core_ui.workshop_select_date
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
    var openDatePicker by remember { mutableStateOf<DateField?>(null) }
    val identityLabel = stringResource(Res.string.abs_form_step_identity)
    val placeLabel = stringResource(Res.string.abs_form_step_place)
    val docsLabel = stringResource(Res.string.abs_form_step_docs)
    val steps = remember(form.step, identityLabel, placeLabel, docsLabel) {
        persistentListOf(
            WorkshopFormStep(identityLabel, form.step > 1, form.step == 1),
            WorkshopFormStep(placeLabel, form.step > 2, form.step == 2),
            WorkshopFormStep(docsLabel, false, form.step == 3),
        )
    }

    val onNext = remember(onIntent) {
        { onIntent(WorkshopRecentlyAddedMembersIntent.FormNext) }
    }
    val onPrev = remember(onIntent, form.step > 1) {
        if (form.step > 1) {
            { onIntent(WorkshopRecentlyAddedMembersIntent.FormPrev) }
        } else {
            null
        }
    }
    val onPickBirthDate = remember {
        { openDatePicker = DateField.BIRTH }
    }
    val onPickStartDate = remember {
        { openDatePicker = DateField.START }
    }
    val onDismissPicker = remember(onIntent) {
        { onIntent(WorkshopRecentlyAddedMembersIntent.FormPickerOpened(null)) }
    }
    val onQueryChange = remember(onIntent) {
        { query: String -> onIntent(WorkshopRecentlyAddedMembersIntent.FormPickerQueryChanged(query)) }
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
                        onPickDate = onPickBirthDate,
                    )

                    2 -> PlaceStep(
                        form = form,
                        onIntent = onIntent,
                        onPickDate = onPickStartDate,
                    )

                    else -> DocumentsStep(
                        form = form,
                        workshopName = workshopName,
                        onIntent = onIntent,
                    )
                }

                // Steps one and two mark their own fields, so only what no field owns — a
                // missing document, an unticked declaration — is said here.
                form.error
                    ?.takeIf { form.step == REGISTRATION_FORM_STEPS }
                    ?.takeUnless { form.isDocumentsError }
                    ?.let { WorkshopFormError(text = stringResource(it)) }
            }
        }

        WorkshopFormFooter(
            nextLabel = stringResource(
                if (form.isLastStep) Res.string.abs_form_submit else Res.string.ws_form_next,
            ),
            onNext = onNext,
            onPrev = onPrev,
            isBusy = form.isBusy,
        )
    }

    form.picker?.let { picker ->
        val onSelectOption = remember(onIntent, picker) {
            { option: PickedOption ->
                onIntent(WorkshopRecentlyAddedMembersIntent.FormOptionPicked(picker, option))
            }
        }
        WorkshopLookupSheet(
            title = stringResource(
                when (picker) {
                    RegistrationPicker.BIRTH_CITY -> Res.string.abs_form_birth_city
                    RegistrationPicker.ISSUE_CITY -> Res.string.abs_form_issue_city
                    RegistrationPicker.JOB -> Res.string.abs_form_job
                },
            ),
            query = form.pickerQuery,
            onQueryChange = onQueryChange,
            options = form.pickerOptions,
            isLoading = form.isPickerLoading,
            onDismiss = onDismissPicker,
            onSelect = onSelectOption,
            label = { it.label },
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
                    WorkshopRecentlyAddedMembersIntent.FormFieldChanged(
                        if (field == DateField.BIRTH) {
                            RegistrationField.BIRTH_DATE
                        } else {
                            RegistrationField.START_DATE
                        },
                        date,
                    ),
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
 * Each field states its own verdict, so the red border animates on the field that is actually
 * wrong rather than a single line under the step saying something is. The two names take letters
 * only and the code digits only, so a wrong keyboard is refused as it is typed.
 */
@Composable
private fun IdentityStep(
    form: RegistrationFormState,
    onIntent: (WorkshopRecentlyAddedMembersIntent) -> Unit,
    onPickDate: () -> Unit,
) {
    // Nothing is marked wrong until «مرحلهٔ بعد» has been pressed — telling someone a field is
    // empty before they have reached it is noise.
    val tried = form.hasTriedNext
    val required = stringResource(Res.string.abs_form_err_required)

    // A half-typed code is not yet wrong; a complete one is judged.
    val nationalIdValid = when {
        // Incomplete is not the same as wrong: nothing is said until the tenth digit lands, or
        // until «مرحلهٔ بعد» asks for a code that is not there.
        form.nationalId.length == WorkshopConstants.NATIONAL_ID_LENGTH ->
            isValidIranianNationalId(form.nationalId)

        tried -> false
        else -> null
    }

    val onDownloadDeclaration = remember(onIntent) {
        { onIntent(WorkshopRecentlyAddedMembersIntent.FormDownloadDeclaration) }
    }
    val onFirstNameChange = remember(onIntent) {
        { value: String ->
            onIntent(
                WorkshopRecentlyAddedMembersIntent.FormFieldChanged(
                    RegistrationField.FIRST_NAME,
                    value,
                ),
            )
        }
    }
    val onLastNameChange = remember(onIntent) {
        { value: String ->
            onIntent(
                WorkshopRecentlyAddedMembersIntent.FormFieldChanged(
                    RegistrationField.LAST_NAME,
                    value,
                ),
            )
        }
    }
    val onNationalIdChange = remember(onIntent) {
        { value: String ->
            onIntent(
                WorkshopRecentlyAddedMembersIntent.FormFieldChanged(
                    RegistrationField.NATIONAL_ID,
                    value.digitsOnly(),
                ),
            )
        }
    }

    WorkshopFormBanner(text = stringResource(Res.string.abs_form_banner))
    TaminOutlinedButton(
        text = stringResource(Res.string.abs_form_download),
        onClick = onDownloadDeclaration,
        icon = vectorResource(Res.drawable.ic_tamin_download),
        enabled = !form.isDownloadingDeclaration,
        shape = DeclarationButtonShape,
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
            onValueChange = onFirstNameChange,
            keyboardType = KeyboardType.Text,
            inputRestriction = InputRestriction.LettersOnly,
            isRequired = true,
            isValid = validWhenFilled(tried, form.firstName),
            errorText = required.takeIf { tried && form.firstName.isBlank() },
            modifier = Modifier.weight(1f),
        )
        WorkshopTextField(
            label = stringResource(Res.string.abs_form_last_name),
            value = form.lastName,
            onValueChange = onLastNameChange,
            keyboardType = KeyboardType.Text,
            inputRestriction = InputRestriction.LettersOnly,
            isRequired = true,
            isValid = validWhenFilled(tried, form.lastName),
            errorText = required.takeIf { tried && form.lastName.isBlank() },
            modifier = Modifier.weight(1f),
        )
    }
    // The same ten-slot field addDependent and girlSurvivor collect a national code with — its
    // own animated border is what marks it wrong, so nothing here draws one.
    WorkshopFieldSlot(
        label = stringResource(Res.string.member_national_id),
        isRequired = true,
    ) {
        SegmentedInputField(
            value = form.nationalId,
            onValueChange = onNationalIdChange,
            slotCount = WorkshopConstants.NATIONAL_ID_LENGTH,
            error = nationalIdValid == false,
            errorMessage = when {
                nationalIdValid != false -> null
                form.nationalId.isBlank() -> required
                else -> stringResource(Res.string.abs_form_err_national_id)
            },
            leadingIcon = vectorResource(Res.drawable.ic_number),
            keyboardType = KeyboardType.Number,
        )
    }
    WorkshopPickerField(
        label = stringResource(Res.string.abs_form_birth_date),
        value = form.birthDate.takeIf { it.isNotBlank() },
        onClick = onPickDate,
        placeholder = stringResource(Res.string.workshop_select_date),
        isDate = true,
        isRequired = true,
        isValid = validWhenFilled(tried, form.birthDate),
    )
}

/** Marks a required field wrong only once the step has been attempted, and it is still empty. */
private fun validWhenFilled(tried: Boolean, value: String): Boolean? =
    if (tried && value.isBlank()) false else null

/** Step two: where the person is from, and what they will do here. */
@Composable
private fun PlaceStep(
    form: RegistrationFormState,
    onIntent: (WorkshopRecentlyAddedMembersIntent) -> Unit,
    onPickDate: () -> Unit,
) {
    val tried = form.hasTriedNext
    val onPickBirthCity = remember(onIntent) {
        {
            onIntent(
                WorkshopRecentlyAddedMembersIntent.FormPickerOpened(
                    RegistrationPicker.BIRTH_CITY,
                ),
            )
        }
    }
    val onPickIssueCity = remember(onIntent) {
        {
            onIntent(
                WorkshopRecentlyAddedMembersIntent.FormPickerOpened(
                    RegistrationPicker.ISSUE_CITY,
                ),
            )
        }
    }
    val onPickJob = remember(onIntent) {
        {
            onIntent(
                WorkshopRecentlyAddedMembersIntent.FormPickerOpened(
                    RegistrationPicker.JOB,
                ),
            )
        }
    }

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
            isRequired = true,
            isValid = validWhenFilled(tried, form.birthCity?.label.orEmpty()),
            onClick = onPickBirthCity,
            modifier = Modifier.weight(1f),
        )
        WorkshopPickerField(
            label = stringResource(Res.string.abs_form_issue_city),
            value = form.issueCity?.label,
            isRequired = true,
            isValid = validWhenFilled(tried, form.issueCity?.label.orEmpty()),
            onClick = onPickIssueCity,
            modifier = Modifier.weight(1f),
        )
    }
    WorkshopPickerField(
        label = stringResource(Res.string.abs_form_job),
        value = form.job?.label,
        isRequired = true,
        isValid = validWhenFilled(tried, form.job?.label.orEmpty()),
        onClick = onPickJob,
    )
    WorkshopPickerField(
        label = stringResource(Res.string.abs_form_start_date),
        value = form.startDate.takeIf { it.isNotBlank() },
        onClick = onPickDate,
        placeholder = stringResource(Res.string.workshop_select_date),
        isDate = true,
        isRequired = true,
        isValid = validWhenFilled(tried, form.startDate),
    )
}

/** Step three: what was entered, the evidence for it, and the declaration. */
@Composable
private fun DocumentsStep(
    form: RegistrationFormState,
    workshopName: String,
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

    val summaryRows = remember(
        form.fullName,
        form.nationalId,
        form.birthDate,
        form.birthCity?.label,
        form.issueCity?.label,
        form.job?.label,
        form.startDate,
        workshopName,
        fullNameLabel,
        nationalIdLabel,
        birthDateLabel,
        birthCityLabel,
        issueCityLabel,
        jobLabel,
        startDateLabel,
        workshopLabel,
    ) {
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

    val onSummaryToggle = remember(onIntent, form.isSummaryOpen) {
        {
            onIntent(
                WorkshopRecentlyAddedMembersIntent.FormSummaryToggled(!form.isSummaryOpen),
            )
        }
    }
    val onSummaryEdit = remember(onIntent) {
        {
            onIntent(WorkshopRecentlyAddedMembersIntent.FormStepRequested(FirstStep))
        }
    }
    val onAddDocument = remember(onIntent) {
        { fileName: String, bytes: ByteArray, typeCode: String ->
            onIntent(
                WorkshopRecentlyAddedMembersIntent.FormAddDocument(fileName, bytes, typeCode),
            )
        }
    }
    val onRemoveDocument = remember(onIntent) {
        { index: Int ->
            onIntent(WorkshopRecentlyAddedMembersIntent.FormRemoveDocument(index))
        }
    }
    val onConfirmToggle = remember(onIntent, form.isConfirmed) {
        {
            onIntent(
                WorkshopRecentlyAddedMembersIntent.FormConfirmedChanged(!form.isConfirmed),
            )
        }
    }

    WorkshopFormSection(
        title = stringResource(Res.string.abs_form_docs_title),
        description = stringResource(Res.string.abs_form_docs_desc),
    )
    WorkshopReviewGroup(
        title = stringResource(Res.string.abs_form_summary),
        rows = summaryRows,
        isOpen = form.isSummaryOpen,
        onToggle = onSummaryToggle,
        onEdit = onSummaryEdit,
    )
    WorkshopDocumentsPanel(
        attachments = form.attachments,
        types = RegistrationDocumentTypes,
        capacity = REGISTRATION_MAX_DOCUMENTS,
        onAdd = onAddDocument,
        onRemove = onRemoveDocument,
        isUploading = form.isUploading,
        isError = form.isDocumentsError,
    )
    WorkshopFormNote(text = stringResource(Res.string.abs_form_note_dependants))
    WorkshopFormNote(text = stringResource(Res.string.abs_form_note_changes))
    WorkshopFormCheck(
        label = stringResource(Res.string.abs_form_check),
        isChecked = form.isConfirmed,
        onToggle = onConfirmToggle,
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

/** The step «ویرایش اطلاعات» jumps back to. */
private const val FirstStep = 1
private val DeclarationButtonShape = RoundedCornerShape(CornerRadius.chip)
