package com.tamin.taminhamrah.feature.profile.ui.editPhoto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.components.EditProfilePhotoDependantsCard
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.components.EditProfilePhotoDialog
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.components.EditProfilePhotoHeader
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.components.EditProfilePhotoSerialField
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.components.EditProfilePhotoUserCard
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.components.pickerLabel
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.contract.EditProfilePhotoEvent
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.contract.EditProfilePhotoIntent
import com.tamin.taminhamrah.feature.profile.ui.editPhoto.contract.EditProfilePhotoUiState
import com.tamin.taminhamrah.model.subdominant.SubdominantItemPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectAsStateWithLifecycle
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminSearchableListSheet
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.profile_photo_dependants_placeholder
import taminx.core.core_ui.profile_photo_submit

@Composable
fun EditProfilePhotoRoute(
    viewModel: EditProfilePhotoViewModel,
    onBackClicked: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    HandleEditProfilePhotoEvents(
        events = viewModel.events,
        onBackClicked = onBackClicked,
    )

    EditProfilePhotoScreen(
        state = state,
        onIntent = viewModel::sendIntent,
    )
}

@Composable
private fun HandleEditProfilePhotoEvents(
    events: Flow<EditProfilePhotoEvent>,
    onBackClicked: () -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            EditProfilePhotoEvent.NavigateBack -> onBackClicked()
        }
    }
}

@Composable
fun EditProfilePhotoScreen(
    state: EditProfilePhotoUiState,
    onIntent: (EditProfilePhotoIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = LocalTaminColors.current.bgPage,
        topBar = { EditProfilePhotoHeader(onBack = { onIntent(EditProfilePhotoIntent.OnBackClicked) }) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.page, vertical = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            EditProfilePhotoUserCard(
                userName = state.userName,
                nationalCode = state.nationalCode,
                insuranceNumber = state.insuranceNumber,
            )
            EditProfilePhotoSerialField(
                serial = state.serialNumber,
                isError = state.isSerialError,
                onSerialChange = { onIntent(EditProfilePhotoIntent.SerialNumberChanged(it)) },
                onOpenGuide = { onIntent(EditProfilePhotoIntent.OpenGuide) },
            )
            EditProfilePhotoDependantsCard(
                isDependantMode = state.isDependantMode,
                selectedDependant = state.selectedDependant,
                isError = state.isDependantError,
                onToggle = { onIntent(EditProfilePhotoIntent.DependantModeToggled(it)) },
                onOpenPicker = { onIntent(EditProfilePhotoIntent.DependantPickerVisibilityChanged(true)) },
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            LoadingButton(
                text = stringResource(Res.string.profile_photo_submit),
                onClick = { onIntent(EditProfilePhotoIntent.Submit) },
                isLoading = state.isSubmitting,
            )
        }
    }

    if (state.isDependantPickerOpen) {
        // The design's sheet has no search, and neither did the old app's.
        TaminSearchableListSheet(
            title = stringResource(Res.string.profile_photo_dependants_placeholder),
            items = state.dependants,
            itemLabel = { it.pickerLabel() },
            itemKey = { "${it.id}_${it.nationalCode}" },
            onItemSelected = { onIntent(EditProfilePhotoIntent.DependantSelected(it)) },
            onDismiss = { onIntent(EditProfilePhotoIntent.DependantPickerVisibilityChanged(false)) },
            showSearch = false,
            isLoading = state.isDependantsLoading,
        )
    }

    state.dialogState?.let { dialogState ->
        EditProfilePhotoDialog(
            dialogState = dialogState,
            onDismiss = { onIntent(EditProfilePhotoIntent.DismissDialog) },
        )
    }
}

private val PreviewDependant = SubdominantItemPR(
    id = 1L,
    firstName = "زهره",
    lastName = "تابانی",
    fullName = "زهره تابانی",
    nationalCode = "0061777943",
    relationDescription = "همسر",
)

private val PreviewState = EditProfilePhotoUiState(
    userName = "سعید نامی",
    nationalCode = "0020939111",
    insuranceNumber = "0823456789",
    branchCode = "0010",
    dependants = persistentListOf(PreviewDependant),
    isDependantsLoading = false,
)

@PreviewRtlTheme
@Composable
private fun EditProfilePhotoScreenEmptyPreview() {
    PreviewRtlThemeContent {
        EditProfilePhotoScreen(state = PreviewState, onIntent = {})
    }
}

@PreviewRtlTheme
@Composable
private fun EditProfilePhotoScreenDependantPreview() {
    PreviewRtlThemeContent {
        EditProfilePhotoScreen(
            state = PreviewState.copy(
                serialNumber = "1G50497996",
                isDependantMode = true,
                selectedDependant = PreviewDependant,
            ),
            onIntent = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun EditProfilePhotoScreenErrorPreview() {
    PreviewRtlThemeContent {
        EditProfilePhotoScreen(
            state = PreviewState.copy(
                isDependantMode = true,
                isSerialError = true,
                isDependantError = true,
            ),
            onIntent = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun EditProfilePhotoScreenSubmittingPreview() {
    PreviewRtlThemeContent {
        EditProfilePhotoScreen(
            state = PreviewState.copy(serialNumber = "1G50497996", isSubmitting = true),
            onIntent = {},
        )
    }
}
