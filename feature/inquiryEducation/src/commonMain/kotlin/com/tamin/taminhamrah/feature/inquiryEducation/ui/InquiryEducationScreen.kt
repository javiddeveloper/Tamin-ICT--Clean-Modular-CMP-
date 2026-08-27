package com.tamin.taminhamrah.feature.inquiryEducation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import com.tamin.taminhamrah.feature.inquiryEducation.ui.components.InquiryEducationFailureStep
import com.tamin.taminhamrah.feature.inquiryEducation.ui.components.InquiryEducationFormStep
import com.tamin.taminhamrah.feature.inquiryEducation.ui.components.InquiryEducationSuccessStep
import com.tamin.taminhamrah.feature.inquiryEducation.ui.contract.InquiryEducationEvent
import com.tamin.taminhamrah.feature.inquiryEducation.ui.contract.InquiryEducationIntent
import com.tamin.taminhamrah.feature.inquiryEducation.ui.contract.InquiryEducationStep
import com.tamin.taminhamrah.feature.inquiryEducation.ui.contract.InquiryEducationUiState
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.BackHandler
import com.tamin.taminhamrah.ui.components.GlassIconTile
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.TaminBottomBar
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.inquiry_education_another
import taminx.core.core_ui.inquiry_education_back_to_services
import taminx.core.core_ui.inquiry_education_retry
import taminx.core.core_ui.inquiry_education_submit
import taminx.core.core_ui.inquiry_education_subtitle
import taminx.core.core_ui.inquiry_education_title
import taminx.core.core_ui.ic_request
import taminx.core.core_ui.ic_tamin_chevron_back

@Composable
fun InquiryEducationScreen(
    onBack: () -> Unit,
    viewModel: InquiryEducationViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    viewModel.events.collectWithLifecycleAware { event ->
        when (event) {
            InquiryEducationEvent.NavigateBack -> onBack()
        }
    }

    val handleBack: () -> Unit = {
        when (state.step) {
            InquiryEducationStep.Form -> onBack()
            InquiryEducationStep.Success,
            InquiryEducationStep.Failure,
            -> viewModel.sendIntent(InquiryEducationIntent.BackToServices)
        }
    }

    if (state.step == InquiryEducationStep.Success || state.step == InquiryEducationStep.Failure) {
        BackHandler(onBack = handleBack)
    }

    InquiryEducationContent(
        state = state,
        onBack = handleBack,
        onIntent = viewModel::sendIntent,
    )
}

@Composable
private fun InquiryEducationContent(
    state: InquiryEducationUiState,
    onBack: () -> Unit,
    onIntent: (InquiryEducationIntent) -> Unit,
) {
    val colors = LocalTaminColors.current
    val headerBrush = remember(colors.profileGradientStops) {
        Brush.horizontalGradient(colors.profileGradientStops)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.bgPage,
        topBar = {
            InquiryEducationHeader(
                headerBrush = headerBrush,
                onBack = onBack,
            )
        },
        bottomBar = {
            if (!(state.isLoading && state.step == InquiryEducationStep.Form)) {
                InquiryEducationBottomBar(
                    state = state,
                    onIntent = onIntent,
                )
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding).padding(top = Spacing.sm),
        ) {
            when {
                state.isLoading && state.step == InquiryEducationStep.Form -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = colors.blueText,
                    )
                }
                state.step == InquiryEducationStep.Form -> {
                    InquiryEducationFormStep(
                        state = state,
                        onIntent = onIntent,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                state.step == InquiryEducationStep.Success -> {
                    InquiryEducationSuccessStep(
                        state = state,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                state.step == InquiryEducationStep.Failure -> {
                    InquiryEducationFailureStep(
                        state = state,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

@Composable
private fun InquiryEducationHeader(
    headerBrush: Brush,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = CornerRadius.x3l, bottomEnd = CornerRadius.x3l))
            .background(headerBrush)
            .padding(bottom = Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TaminTopAppBar(
            title = stringResource(Res.string.inquiry_education_title),
            background = headerBrush,
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = null,
                    onClick = onBack,
                    bordered = true,
                )
            },
        )

        Spacer(modifier = Modifier.height(Spacing.smPlus))
        GlassIconTile(icon = vectorResource(Res.drawable.ic_request))
        Spacer(modifier = Modifier.height(Spacing.sm))
        Text(
            text = stringResource(Res.string.inquiry_education_subtitle),
            style = MaterialTheme.typography.labelLarge,
            color = colors.textHeaderSubtitle,
            modifier = Modifier.padding(horizontal = Spacing.lg),
        )
    }
}

@Composable
private fun InquiryEducationBottomBar(
    state: InquiryEducationUiState,
    onIntent: (InquiryEducationIntent) -> Unit,
) {
    TaminBottomBar(
        modifier = Modifier
            .navigationBarsPadding()
            .imePadding(),
    ) {
        when (state.step) {
            InquiryEducationStep.Form -> {
                LoadingButton(
                    text = stringResource(Res.string.inquiry_education_submit),
                    onClick = { onIntent(InquiryEducationIntent.Submit) },
                    isLoading = state.isSubmitting,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            InquiryEducationStep.Success -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.smd),
                ) {
                    TaminOutlinedButton(
                        text = stringResource(Res.string.inquiry_education_another),
                        onClick = { onIntent(InquiryEducationIntent.AnotherInquiry) },
                        modifier = Modifier.weight(1f),
                    )
                    TaminFilledButton(
                        text = stringResource(Res.string.inquiry_education_back_to_services),
                        onClick = { onIntent(InquiryEducationIntent.BackToServices) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            InquiryEducationStep.Failure -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.smd),
                ) {
                    TaminOutlinedButton(
                        text = stringResource(Res.string.inquiry_education_retry),
                        onClick = { onIntent(InquiryEducationIntent.Retry) },
                        modifier = Modifier.weight(1f),
                    )
                    TaminFilledButton(
                        text = stringResource(Res.string.inquiry_education_back_to_services),
                        onClick = { onIntent(InquiryEducationIntent.BackToServices) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}
