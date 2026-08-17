package com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.components.ReviewStep
import com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.components.SelectTypeStep
import com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.components.SuccessModal
import com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.contract.SendHistoryStep
import com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.contract.SendHistoryToInstitutionsEvent
import com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.contract.SendHistoryToInstitutionsIntent
import com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.contract.SendHistoryToInstitutionsUiState
import com.tamin.taminhamrah.model.history.UserInfoPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.StepIndicator
import com.tamin.taminhamrah.ui.components.StepIndicatorModel
import com.tamin.taminhamrah.ui.components.StepState
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_request
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.send_history_subtitle
import taminx.core.core_ui.send_history_title
import taminx.core.core_ui.send_history_step_type
import taminx.core.core_ui.send_history_step_confirm
import taminx.core.core_ui.send_history_type_all
import taminx.core.core_ui.send_history_type_combined
import taminx.core.core_ui.send_history_type_wages

@Composable
fun SendHistoryToInstitutionsScreen(
    viewModel: SendHistoryToInstitutionsViewModel = koinViewModel(),
    onBack: () -> Unit,
    onDone: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current
    var showSuccessModal by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.sendIntent(SendHistoryToInstitutionsIntent.LoadUserInfo)
    }

    HandleSendHistoryEvents(
        events = viewModel.events,
        toaster = toaster,
        onShowSuccessModal = { showSuccessModal = true }
    )

    SendHistoryContent(
        uiState = uiState,
        onBack = {
            if (uiState.currentStep == SendHistoryStep.SelectType) onBack()
            else viewModel.sendIntent(SendHistoryToInstitutionsIntent.GoToPreviousStep)
        },
        onIntent = viewModel::sendIntent
    )

    if (showSuccessModal) {
        SuccessModal(onDismiss = {
            showSuccessModal = false
            onDone()
        })
    }
}

@Composable
private fun HandleSendHistoryEvents(
    events: kotlinx.coroutines.flow.Flow<SendHistoryToInstitutionsEvent>,
    toaster: com.tamin.taminhamrah.ui.components.toast.ToasterState,
    onShowSuccessModal: () -> Unit
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            is SendHistoryToInstitutionsEvent.ShowToast -> toaster.error(event.message)
            is SendHistoryToInstitutionsEvent.DisplaySuccessModal -> onShowSuccessModal()
        }
    }
}

@Composable
private fun SendHistoryContent(
    uiState: SendHistoryToInstitutionsUiState,
    onBack: () -> Unit,
    onIntent: (SendHistoryToInstitutionsIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    val headerBrush = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }

    val typeLabels = listOf(
        stringResource(Res.string.send_history_type_all),
        stringResource(Res.string.send_history_type_wages),
        stringResource(Res.string.send_history_type_combined)
    )
    val typeColors = listOf(
        taminColors.greenBg,
        taminColors.blueBg,
        taminColors.fuchsiaBlue.copy(alpha = 0.3f)
    )
    val typeTextColors = listOf(taminColors.greenText, taminColors.blueText, taminColors.fuchsiaBlue)

    val step1Title = stringResource(Res.string.send_history_step_type)
    val step2Title = stringResource(Res.string.send_history_step_confirm)
    val steps = remember(uiState.currentStep, step1Title, step2Title) {
        persistentListOf(
            StepIndicatorModel(
                title = step1Title,
                stepNumber = "۱",
                state = when (uiState.currentStep) {
                    SendHistoryStep.SelectType -> StepState.Active
                    SendHistoryStep.Review -> StepState.Completed
                }
            ),
            StepIndicatorModel(
                title = step2Title,
                stepNumber = "۲",
                state = when (uiState.currentStep) {
                    SendHistoryStep.SelectType -> StepState.Inactive
                    SendHistoryStep.Review -> StepState.Active
                }
            )
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.send_history_title),
                background = headerBrush,
                bottomPadding = Spacing.xl,
                shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = null,
                        onClick = onBack,
                        bordered = true
                    )
                }
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    DecorativeBackgroundCircle(
                        size = 190.dp,
                        xOffset = 450.dp,
                        yOffset = (-150).dp
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AnimatedRingHeaderIcon(icon = vectorResource(Res.drawable.ic_request))
                        Spacer(modifier = Modifier.height(Spacing.md))
                        Text(
                            text = stringResource(Res.string.send_history_subtitle),
                            style = MaterialTheme.typography.labelLarge,
                            color = taminColors.textHeaderSubtitle
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
                .navigationBarsPadding()
        ) {
            StepIndicator(
                steps = steps,
                modifier = Modifier.padding(
                    start = Spacing.lg,
                    end = Spacing.lg,
                    top = Spacing.md,
                    bottom = Spacing.md
                )
            )

            AnimatedContent(
                targetState = uiState.currentStep,
                transitionSpec = {
                    if (targetState == SendHistoryStep.Review) {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                            slideOutHorizontally { it } + fadeOut()
                    } else {
                        slideInHorizontally { it } + fadeIn() togetherWith
                            slideOutHorizontally { -it } + fadeOut()
                    }
                },
                label = "SendHistoryStepTransition",
                modifier = Modifier.weight(1f)
            ) { step ->
                when (step) {
                    SendHistoryStep.SelectType -> SelectTypeStep(
                        uiState = uiState,
                        typeLabels = typeLabels,
                        typeColors = typeColors,
                        typeTextColors = typeTextColors,
                        onIntent = onIntent
                    )
                    SendHistoryStep.Review -> ReviewStep(
                        uiState = uiState,
                        typeLabels = typeLabels,
                        typeColors = typeColors,
                        typeTextColors = typeTextColors,
                        onIntent = onIntent
                    )
                }
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun SendHistorySelectTypePreview() {
    PreviewRtlThemeContent {
        SendHistoryContent(
            uiState = SendHistoryToInstitutionsUiState(currentStep = SendHistoryStep.SelectType),
            onBack = {},
            onIntent = {}
        )
    }
}

@PreviewRtlTheme
@Composable
private fun SendHistoryReviewPreview() {
    PreviewRtlThemeContent {
        SendHistoryContent(
            uiState = SendHistoryToInstitutionsUiState(
                currentStep = SendHistoryStep.Review,
                isType1Selected = true,
                isType2Selected = true,
                isType3Selected = true,
                userInfo = UserInfoPR(
                    firstName = "سنا",
                    lastName = "حقیقی",
                    insuranceNumber = "۰۰۱۶۳۱۸۹۴۱",
                    serial1 = "", militaryServiceCode = "", fatherName = "",
                    serial2 = "", creationTime = 0, lastModificationTime = 0,
                    cityCode = "", socialSecurityNumber = "", lastModifiedBy = "",
                    issueplaceName = "", birthDate = "", genderCode = "",
                    nationalID = "", marriageCode = "", createdBy = "",
                    identityNumber = "", countryCode = "", id = "",
                    birthDateTimestamp = 0, issueplace = "", nationCode = ""
                )
            ),
            onBack = {},
            onIntent = {}
        )
    }
}
