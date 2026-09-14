package com.tamin.taminhamrah.feature.taminServices.funeralAllowance.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.contract.FuneralAllowanceStep
import com.tamin.taminhamrah.ui.components.StepIndicator
import com.tamin.taminhamrah.ui.components.StepIndicatorModel
import com.tamin.taminhamrah.ui.components.StepState
import com.tamin.taminhamrah.ui.components.TaminBottomActionBar
import com.tamin.taminhamrah.ui.components.topbars.TaminStepTopAppBar
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.funeral_allowance_applicant_info
import taminx.core.core_ui.funeral_allowance_deceased_info

@Composable
internal fun FuneralAllowanceStepScaffold(
    title: String,
    currentStep: FuneralAllowanceStep,
    onBackClicked: () -> Unit,
    primaryText: String,
    primaryEnabled: Boolean,
    onPrimaryClick: () -> Unit,
    secondaryText: String,
    onSecondaryClick: () -> Unit,
    onCloseClicked: (() -> Unit)? = null,
    isPrimaryLoading: Boolean? = null,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Surface(color = LocalTaminColors.current.bgSurface) {
                Column {
                    TaminStepTopAppBar(
                        title = title,
                        onBackClicked = onBackClicked,
                        onCloseClicked = onCloseClicked,
                    )
                    FuneralAllowanceStepIndicator(
                        currentStep = currentStep,
                        modifier = Modifier.padding(
                            horizontal = Spacing.lg,
                        ).padding(bottom = Spacing.md),
                    )
                }
            }
        },
        bottomBar = {
            TaminBottomActionBar(
                primaryText = primaryText,
                primaryEnabled = primaryEnabled,
                isPrimaryLoading = isPrimaryLoading,
                onPrimaryClick = onPrimaryClick,
                secondaryText = secondaryText,
                onSecondaryClick = onSecondaryClick,
            )
        },
        contentWindowInsets = WindowInsets(0),
        content = content,
    )
}

/**
 * Two-step progress header ([FuneralAllowanceStep.APPLICANT_INFO] then
 * [FuneralAllowanceStep.DECEASED_AND_BANK_INFO]) rendered with the shared
 * [StepIndicator] — the same component orotez-protez uses. There is no third step.
 */
@Composable
private fun FuneralAllowanceStepIndicator(
    currentStep: FuneralAllowanceStep,
    modifier: Modifier = Modifier,
) {
    val currentIndex = currentStep.ordinal

    StepIndicator(
        modifier = modifier,
        steps = persistentListOf(
            StepIndicatorModel(
                title = stringResource(Res.string.funeral_allowance_applicant_info),
                stepNumber = "1",
                state = if (currentIndex == 0) StepState.Active else StepState.Completed,
            ),
            StepIndicatorModel(
                title = stringResource(Res.string.funeral_allowance_deceased_info),
                stepNumber = "2",
                state = if (currentIndex >= 1) StepState.Active else StepState.Inactive,
            ),
        ),
    )
}
