package com.tamin.taminhamrah.feature.profile.ui.editPhoto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.components.EditProfilePhotoDependantsCard
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.components.EditProfilePhotoHeader
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.components.EditProfilePhotoUserCard
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.contract.EditProfilePhotoEvent
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.contract.EditProfilePhotoIntent
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.contract.EditProfilePhotoUiState
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.contract.PhotoDialogState
import com.tamin.taminhamrah.model.subdominant.SubdominantItemPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.SegmentedInputField
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminSearchableListSheet
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.clip
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.components.EditProfilePhotoGuideDialog
import com.tamin.taminhamrah.ui.theme.CornerRadius
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_info
import taminx.core.core_ui.ic_number
import taminx.core.core_ui.profile_photo_dependants_title
import taminx.core.core_ui.profile_photo_dialog_understood
import taminx.core.core_ui.profile_photo_error_empty_serial
import taminx.core.core_ui.profile_photo_error_title
import taminx.core.core_ui.profile_photo_guide_button
import taminx.core.core_ui.profile_photo_serial_hint
import taminx.core.core_ui.profile_photo_serial_label
import taminx.core.core_ui.profile_photo_serial_placeholder
import taminx.core.core_ui.profile_photo_submit
import taminx.core.core_ui.profile_photo_success_body
import taminx.core.core_ui.profile_photo_success_title

@Composable
fun EditProfilePhotoRoute(
    viewModel: EditProfilePhotoViewModel,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    HandleEditProfilePhotoEvents(
        events = viewModel.events,
        onBackClicked = onBackClicked,
        onNavigateBackOnSuccess = onBackClicked,
        snackbarHostState = snackbarHostState,
    )

    EditProfilePhotoScreen(
        state = state,
        onIntent = viewModel::sendIntent,
        onBackClicked = onBackClicked,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@Composable
private fun HandleEditProfilePhotoEvents(
    events: Flow<EditProfilePhotoEvent>,
    onBackClicked: () -> Unit,
    onNavigateBackOnSuccess: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            is EditProfilePhotoEvent.NavigateBack -> onBackClicked()
            is EditProfilePhotoEvent.NavigateBackOnSuccess -> onNavigateBackOnSuccess()
            is EditProfilePhotoEvent.ShowSnackbar -> {
                val message = getString(event.message)
                snackbarHostState.showSnackbar(message)
            }
        }
    }
}

@Composable
fun EditProfilePhotoScreen(
    state: EditProfilePhotoUiState,
    onIntent: (EditProfilePhotoIntent) -> Unit,
    onBackClicked: () -> Unit = {},
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    val onBack = remember(onIntent) {
        { onIntent(EditProfilePhotoIntent.OnBackClicked) }
    }
    val onSerialChanged = remember(onIntent) {
        { serial: String -> onIntent(EditProfilePhotoIntent.SerialNumberChanged(serial)) }
    }
    val onToggleDependant = remember(onIntent) {
        { enabled: Boolean -> onIntent(EditProfilePhotoIntent.DependantModeToggled(enabled)) }
    }
    val onOpenPicker = remember(onIntent) {
        { onIntent(EditProfilePhotoIntent.DependantPickerVisibilityChanged(true)) }
    }
    val onClosePicker = remember(onIntent) {
        { onIntent(EditProfilePhotoIntent.DependantPickerVisibilityChanged(false)) }
    }
    val onSelectDependant = remember(onIntent) {
        { dep: SubdominantItemPR -> onIntent(EditProfilePhotoIntent.DependantSelected(dep)) }
    }
    val onSubmit = remember(onIntent) {
        { onIntent(EditProfilePhotoIntent.Submit) }
    }
    val onOpenGuide = remember(onIntent) {
        { onIntent(EditProfilePhotoIntent.OpenGuide) }
    }
    val onDismissDialog = remember(onIntent) {
        { onIntent(EditProfilePhotoIntent.DismissDialog) }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = taminColors.bgPage,
        topBar = {
            EditProfilePhotoHeader(onBack = onBack)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .navigationBarsPadding()
                .verticalScroll(scrollState)
                .padding(horizontal = Spacing.page, vertical = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            EditProfilePhotoUserCard(
                userName = state.userName,
                nationalCode = state.nationalCode,
                insuranceNumber = state.insuranceNumber,
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${stringResource(Res.string.profile_photo_serial_label)} *",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = taminColors.textSecondary,
                    )
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(CornerRadius.xs))
                            .clickable(onClick = onOpenGuide)
                            .padding(horizontal = Spacing.xs, vertical = Spacing.xxs),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.ic_info),
                            contentDescription = null,
                            tint = taminColors.blueText,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = stringResource(Res.string.profile_photo_guide_button),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = taminColors.blueText,
                        )
                    }
                }

                SegmentedInputField(
                    value = state.serialNumber,
                    onValueChange = onSerialChanged,
                    slotCount = 10,
                    error = state.isSerialError,
                    errorMessage = if (state.isSerialError) stringResource(Res.string.profile_photo_error_empty_serial) else null,
                    placeholderText = stringResource(Res.string.profile_photo_serial_placeholder),
                    leadingIcon = vectorResource(Res.drawable.ic_number),
                    keyboardType = KeyboardType.Ascii,
                    keyboardCapitalization = KeyboardCapitalization.Characters,
                    formatAsPersianDigits = false,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Spacing.xxs),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(taminColors.orangeText, CircleShape),
                    )
                    Text(
                        text = stringResource(Res.string.profile_photo_serial_hint),
                        style = MaterialTheme.typography.labelSmall,
                        color = taminColors.textTertiary,
                    )
                }
            }

            EditProfilePhotoDependantsCard(
                isDependantMode = state.isDependantMode,
                onToggle = onToggleDependant,
                selectedDependant = state.selectedDependant,
                onOpenPicker = onOpenPicker,
                isError = state.isDependantError,
            )

            Spacer(modifier = Modifier.height(Spacing.xs))

            LoadingButton(
                text = stringResource(Res.string.profile_photo_submit),
                onClick = onSubmit,
                isLoading = state.isSubmitting,
                enabled = !state.isSubmitting,
            )

            Spacer(modifier = Modifier.height(Spacing.xl))
        }

        if (state.isDependantPickerOpen) {
            TaminSearchableListSheet(
                title = stringResource(Res.string.profile_photo_dependants_title),
                items = state.dependants,
                itemLabel = { "${it.fullName} (${it.relationDescription})" },
                itemKey = { it.id },
                onItemSelected = {
                    onSelectDependant(it)
                    onClosePicker()
                },
                onDismiss = onClosePicker,
            )
        }

        when (val dialog = state.dialogState) {
            is PhotoDialogState.Guide -> {
                EditProfilePhotoGuideDialog(onDismiss = onDismissDialog)
            }
            is PhotoDialogState.Success -> {
                val descriptionText = dialog.message?.takeIf { it.isNotBlank() }
                    ?: stringResource(Res.string.profile_photo_success_body)
                TaminConfirmationDialog(
                    title = stringResource(Res.string.profile_photo_success_title),
                    description = descriptionText,
                    icon = Icons.Default.Check,
                    iconTint = taminColors.springGreenText,
                    iconBackground = taminColors.greenBg,
                    confirmButton = {
                        TaminFilledButton(
                            text = stringResource(Res.string.profile_photo_dialog_understood),
                            onClick = onDismissDialog,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    },
                    dismissButton = {},
                    onDismissRequest = onDismissDialog,
                )
            }
            is PhotoDialogState.ValidationError -> {
                TaminConfirmationDialog(
                    title = stringResource(Res.string.profile_photo_error_title),
                    description = stringResource(dialog.message),
                    icon = Icons.Default.Warning,
                    iconTint = taminColors.dangerText,
                    iconBackground = taminColors.dangerBg,
                    confirmButton = {
                        TaminFilledButton(
                            text = stringResource(Res.string.profile_photo_dialog_understood),
                            onClick = onDismissDialog,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    },
                    dismissButton = {},
                    onDismissRequest = onDismissDialog,
                )
            }
            is PhotoDialogState.ServerError -> {
                TaminConfirmationDialog(
                    title = stringResource(Res.string.profile_photo_error_title),
                    description = dialog.message,
                    icon = Icons.Default.Warning,
                    iconTint = taminColors.dangerText,
                    iconBackground = taminColors.dangerBg,
                    confirmButton = {
                        TaminFilledButton(
                            text = stringResource(Res.string.profile_photo_dialog_understood),
                            onClick = onDismissDialog,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    },
                    dismissButton = {},
                    onDismissRequest = onDismissDialog,
                )
            }
            null -> Unit
        }
    }
}

@PreviewRtlTheme
@Composable
private fun EditProfilePhotoScreenPreview() {
    PreviewRtlThemeContent {
        EditProfilePhotoScreen(
            state = EditProfilePhotoUiState(
                userName = "سعید نامی",
                nationalCode = "۰۰۲۰۹۳۹۱۱۱",
                insuranceNumber = "۰۸۲۳۴۵۶۷۸۹",
                serialNumber = "۱۲۳۴۵۶۷۸۹",
                isDependantMode = true,
                selectedDependant = SubdominantItemPR(
                    id = 1L,
                    firstName = "زهره",
                    lastName = "تابانی",
                    fullName = "زهره تابانی",
                    nationalCode = "۰۰۶۱۷۷۷۹۴۳",
                    relationDescription = "همسر",
                ),
                dependants = persistentListOf(
                    SubdominantItemPR(
                        id = 1L,
                        firstName = "زهره",
                        lastName = "تابانی",
                        fullName = "زهره تابانی",
                        nationalCode = "۰۰۶۱۷۷۷۹۴۳",
                        relationDescription = "همسر",
                    ),
                    SubdominantItemPR(
                        id = 2L,
                        firstName = "آرش",
                        lastName = "تابانی",
                        fullName = "آرش تابانی",
                        nationalCode = "۰۰۲۴۵۵۱۹۰۲",
                        relationDescription = "فرزند",
                    ),
                ),
            ),
            onIntent = {},
        )
    }
}
