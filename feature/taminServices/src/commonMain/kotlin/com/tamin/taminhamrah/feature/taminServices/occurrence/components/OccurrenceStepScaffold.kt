package com.tamin.taminhamrah.feature.taminServices.occurrence.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.ui.components.TaminBottomActionBar
import com.tamin.taminhamrah.ui.components.topbars.TaminStepTopAppBar

@Composable
internal fun OccurrenceStepScaffold(
    title: String,
    stepNumber: Int,
    totalSteps: Int,
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
        modifier = modifier.fillMaxSize().imePadding(),
        topBar = {
            TaminStepTopAppBar(
                title = title,
                onBackClicked = onBackClicked,
                onCloseClicked = onCloseClicked,
                currentStep = stepNumber,
                totalSteps = totalSteps,
            )
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
