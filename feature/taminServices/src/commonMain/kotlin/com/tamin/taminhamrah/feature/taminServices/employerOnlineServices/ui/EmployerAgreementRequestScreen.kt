package com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.AgreementRequestStep
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.AgreementRequestUiState
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.EmployerOnlineServicesErrorSource
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.EmployerOnlineServicesIntent
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.EmployerOnlineServicesUiState
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.BackHandler
import com.tamin.taminhamrah.ui.components.InputRestriction
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.StepIndicator
import com.tamin.taminhamrah.ui.components.StepIndicatorModel
import com.tamin.taminhamrah.ui.components.StepState
import com.tamin.taminhamrah.ui.components.TaminBottomActionBar
import com.tamin.taminhamrah.ui.components.TaminStyledTextField
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.components.toast.AppToastHost
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.employer_online_services_request_code_card_title
import taminx.core.core_ui.employer_online_services_request_code_placeholder
import taminx.core.core_ui.employer_online_services_request_code_sent_to
import taminx.core.core_ui.employer_online_services_request_code_ttl_caption
import taminx.core.core_ui.employer_online_services_request_contact_hint
import taminx.core.core_ui.employer_online_services_request_edit_contact
import taminx.core.core_ui.employer_online_services_request_edit_hint
import taminx.core.core_ui.employer_online_services_request_email_error
import taminx.core.core_ui.employer_online_services_request_email_label
import taminx.core.core_ui.employer_online_services_request_field_placeholder
import taminx.core.core_ui.employer_online_services_request_mobile_error
import taminx.core.core_ui.employer_online_services_request_mobile_label
import taminx.core.core_ui.employer_online_services_request_resend_code
import taminx.core.core_ui.employer_online_services_request_section_title
import taminx.core.core_ui.employer_online_services_request_send_code
import taminx.core.core_ui.employer_online_services_request_step_accept
import taminx.core.core_ui.employer_online_services_request_step_validation
import taminx.core.core_ui.employer_online_services_request_title
import taminx.core.core_ui.employer_online_services_request_verify
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_edit

/** How long the security ticket is valid — drives the code screen's countdown. */
private const val TICKET_TTL_SECONDS = 300

/**
 * "درخواست استفاده از خدمات غیرحضوری" — the OTP-gated agreement-request wizard, opened from the
 * landing screen's "ثبت درخواست تعهدنامه". Driven entirely by the shared
 * [EmployerOnlineServicesUiState]/[EmployerOnlineServicesIntent] (like the inspection wizard), so it
 * and the landing list never disagree on state.
 *
 * Step 1 (اعتبارسنجی) has two sub-states switched by [AgreementRequestUiState.ticketRequested]:
 * confirm the contact details → send the code, then enter the code → verify. Step 2
 * (پذیرش تعهدنامه) is [AcceptAgreementStep]; a successful submit shows [EmployerAgreementSuccessDialog].
 */
@Composable
internal fun EmployerAgreementRequestScreen(
    uiState: EmployerOnlineServicesUiState,
    onIntent: (EmployerOnlineServicesIntent) -> Unit,
    onClose: () -> Unit,
) {
    val taminColors = LocalTaminColors.current
    val request = uiState.agreementRequest

    // Header chevron and system back: step back one sub-state, or leave the wizard from the first.
    val onBack: () -> Unit = {
        if (request.step == AgreementRequestStep.VALIDATION && !request.ticketRequested) {
            onClose()
        } else {
            onIntent(EmployerOnlineServicesIntent.EditAgreementContact)
        }
    }
    BackHandler(onBack = onBack)

    val step1Label = stringResource(Res.string.employer_online_services_request_step_validation)
    val step2Label = stringResource(Res.string.employer_online_services_request_step_accept)
    val steps = remember(request.step, step1Label, step2Label) {
        persistentListOf(
            StepIndicatorModel(
                title = step1Label,
                stepNumber = "1".toPersianDigits(),
                state = request.step.stepStateFor(AgreementRequestStep.VALIDATION),
            ),
            StepIndicatorModel(
                title = step2Label,
                stepNumber = "2".toPersianDigits(),
                state = request.step.stepStateFor(AgreementRequestStep.ACCEPT_AGREEMENT),
            ),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(taminColors.bgPage),
    ) {
        RequestWizardHeader(onBackClicked = onBack)

        StepIndicator(
            steps = steps,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        )

        AnimatedContent(
            targetState = request.step,
            transitionSpec = {
                val direction = if (targetState.ordinal > initialState.ordinal) {
                    AnimatedContentTransitionScope.SlideDirection.Start
                } else {
                    AnimatedContentTransitionScope.SlideDirection.End
                }
                slideIntoContainer(direction, animationSpec = tween(300)) togetherWith
                    slideOutOfContainer(direction, animationSpec = tween(300))
            },
            label = "AgreementRequestStepTransition",
            modifier = Modifier.weight(1f),
        ) { step ->
            when (step) {
                AgreementRequestStep.VALIDATION -> ValidationStep(
                    request = request,
                    ticketError = uiState.errors[EmployerOnlineServicesErrorSource.REQUEST_TICKET],
                    verifyError = uiState.errors[EmployerOnlineServicesErrorSource.VERIFY_CODE],
                    onIntent = onIntent,
                )

                AgreementRequestStep.ACCEPT_AGREEMENT -> AcceptAgreementStep(
                    request = request,
                    error = uiState.errors[EmployerOnlineServicesErrorSource.STEP2_CONTENT],
                    submitError = uiState.errors[EmployerOnlineServicesErrorSource.SUBMIT],
                    onIntent = onIntent,
                    onBack = onBack,
                )
            }
        }
    }

    if (request.isSubmitted) {
        EmployerAgreementSuccessDialog(
            onDismiss = { onIntent(EmployerOnlineServicesIntent.DismissAgreementSuccess) },
        )
    }
}

private fun AgreementRequestStep.stepStateFor(target: AgreementRequestStep): StepState = when {
    ordinal > target.ordinal -> StepState.Completed
    ordinal == target.ordinal -> StepState.Active
    else -> StepState.Inactive
}

// --------------------------------------------------------------------------- header

@Composable
private fun RequestWizardHeader(onBackClicked: () -> Unit) {
    val taminColors = LocalTaminColors.current
    val gradient = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = CornerRadius.x3l, bottomEnd = CornerRadius.x3l))
            .background(gradient),
    ) {
        TaminTopAppBar(
            title = stringResource(Res.string.employer_online_services_request_title),
            background = gradient,
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = null,
                    onClick = onBackClicked,
                )
            },
        )
    }
}

// ----------------------------------------------------------------- step 1: اعتبارسنجی

@Composable
private fun ValidationStep(
    request: AgreementRequestUiState,
    ticketError: String?,
    verifyError: String?,
    onIntent: (EmployerOnlineServicesIntent) -> Unit,
) {
    if (request.ticketRequested) {
        CodeEntryStep(request = request, error = verifyError, onIntent = onIntent)
    } else {
        ContactFormStep(request = request, error = ticketError, onIntent = onIntent)
    }
}

@Composable
private fun ContactFormStep(
    request: AgreementRequestUiState,
    error: String?,
    onIntent: (EmployerOnlineServicesIntent) -> Unit,
) {
    val colors = LocalTaminColors.current
    val placeholder = stringResource(Res.string.employer_online_services_request_field_placeholder)

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        containerColor = colors.bgPage,
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            TaminBottomActionBar(
                primaryText = stringResource(Res.string.employer_online_services_request_send_code),
                onPrimaryClick = { onIntent(EmployerOnlineServicesIntent.RequestAgreementTicket) },
                primaryEnabled = request.isMobileValid && request.isEmailValid && !request.isSubmitting,
                isPrimaryLoading = request.isSubmitting,
                showChevron = false,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg),
        ) {
            Spacer(Modifier.height(Spacing.md))

            Text(
                text = stringResource(Res.string.employer_online_services_request_section_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
            )

            Spacer(Modifier.height(Spacing.lg))

            Column(
                modifier = Modifier.fillMaxWidth().taminSurface().padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.lg),
            ) {
                TaminStyledTextField(
                    value = request.mobile,
                    onValueChange = { onIntent(EmployerOnlineServicesIntent.UpdateRequestMobile(it)) },
                    label = stringResource(Res.string.employer_online_services_request_mobile_label),
                    placeholder = placeholder,
                    leadingIcon = Icons.Outlined.Phone,
                    isRequired = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    inputRestriction = InputRestriction.DigitsOnly,
                    maxLength = 11,
                    isValid = request.mobile.takeIf { it.isNotBlank() }?.let { request.isMobileValid },
                    errorText = stringResource(Res.string.employer_online_services_request_mobile_error),
                )
                TaminStyledTextField(
                    value = request.email,
                    onValueChange = { onIntent(EmployerOnlineServicesIntent.UpdateRequestEmail(it)) },
                    label = stringResource(Res.string.employer_online_services_request_email_label),
                    placeholder = placeholder,
                    isRequired = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isValid = request.email.takeIf { it.isNotBlank() }
                        ?.let { request.isEmailValid },
                    errorText = stringResource(Res.string.employer_online_services_request_email_error),
                )
            }

            Spacer(Modifier.height(Spacing.md))

            Text(
                text = stringResource(Res.string.employer_online_services_request_contact_hint),
                style = MaterialTheme.typography.labelMedium,
                color = colors.textMuted,
            )

            RequestError(message = error)
            Spacer(Modifier.height(Spacing.lg))
        }
    }
}

@Composable
private fun CodeEntryStep(
    request: AgreementRequestUiState,
    error: String?,
    onIntent: (EmployerOnlineServicesIntent) -> Unit,
) {
    val colors = LocalTaminColors.current

    var secondsLeft by remember(request.ticketNonce) { mutableIntStateOf(TICKET_TTL_SECONDS) }
    LaunchedEffect(request.ticketNonce) {
        secondsLeft = TICKET_TTL_SECONDS
        while (secondsLeft > 0) {
            delay(1_000)
            secondsLeft -= 1
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        containerColor = colors.bgPage,
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            TaminBottomActionBar(
                primaryText = stringResource(Res.string.employer_online_services_request_verify),
                onPrimaryClick = { onIntent(EmployerOnlineServicesIntent.VerifyAgreementCode) },
                primaryEnabled = request.isCodeComplete && !request.isSubmitting,
                isPrimaryLoading = request.isSubmitting,
                showChevron = false,
                secondaryText = stringResource(Res.string.employer_online_services_request_edit_contact),
                onSecondaryClick = { onIntent(EmployerOnlineServicesIntent.EditAgreementContact) },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg),
        ) {
            Spacer(Modifier.height(Spacing.md))

            Column(
                modifier = Modifier.fillMaxWidth().taminSurface().padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        Text(
                            text = stringResource(Res.string.employer_online_services_request_code_card_title),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = colors.textPrimary,
                        )
                        Text(
                            text = stringResource(
                                Res.string.employer_online_services_request_code_sent_to,
                                request.mobile.toPersianDigits(),
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.textMuted,
                        )
                    }

                    // While the ticket is live, the countdown; once it expires, it is swapped for the
                    // "ارسال مجدد کد" button, which on tap issues a fresh ticket and the countdown returns.
                    if (secondsLeft > 0) {
                        CountdownPill(secondsLeft = secondsLeft)
                    } else {
                        ResendCodeButton(
                            enabled = !request.isSubmitting,
                            onClick = { onIntent(EmployerOnlineServicesIntent.RequestAgreementTicket) },
                        )
                    }
                }

                Text(
                    text = stringResource(Res.string.employer_online_services_request_code_ttl_caption),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .border(1.5.dp, colors.border, RoundedCornerShape(15.dp))
                            .clip(RoundedCornerShape(15.dp))
                            .clickable(enabled = !request.isSubmitting) {
                                onIntent(EmployerOnlineServicesIntent.EditAgreementContact)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.ic_tamin_edit),
                            contentDescription = stringResource(
                                Res.string.employer_online_services_request_edit_contact,
                            ),
                            colorFilter = ColorFilter.tint(color = colors.textMuted),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(Spacing.sm))
                    TaminStyledTextField(
                        modifier = Modifier.weight(1f),
                        value = request.code,
                        onValueChange = { onIntent(EmployerOnlineServicesIntent.UpdateRequestCode(it)) },
                        label = "",
                        placeholder = stringResource(Res.string.employer_online_services_request_code_placeholder),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        inputRestriction = InputRestriction.DigitsOnly,
                        maxLength = AgreementRequestUiState.CODE_LENGTH,
                    )
                }
            }

            Spacer(Modifier.height(Spacing.md))

            Text(
                text = stringResource(Res.string.employer_online_services_request_edit_hint),
                style = MaterialTheme.typography.labelMedium,
                color = colors.textMuted,
            )

            RequestError(message = error)
            Spacer(Modifier.height(Spacing.lg))
        }
    }
}

/**
 * "ارسال مجدد کد" — takes the countdown's place once it expires. Tapping it issues a fresh ticket,
 * which bumps the nonce and brings the countdown back (see [CodeEntryStep]).
 */
@Composable
private fun ResendCodeButton(enabled: Boolean, onClick: () -> Unit) {
    val colors = LocalTaminColors.current
    val contentColor = if (enabled) colors.blueText else colors.textMuted

    Row(
        modifier = Modifier
            .clip(CircleShape)
            .border(1.dp, colors.border, CircleShape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Icon(
            imageVector = Icons.Outlined.Refresh,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = stringResource(Res.string.employer_online_services_request_resend_code),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = contentColor,
        )
    }
}

/** The live "mm:ss" pill — only rendered while the ticket has time left; see [CodeEntryStep]. */
@Composable
private fun CountdownPill(secondsLeft: Int) {
    val colors = LocalTaminColors.current
    val mm = (secondsLeft / 60).toString().padStart(2, '0')
    val ss = (secondsLeft % 60).toString().padStart(2, '0')

    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(colors.chipBg)
            .padding(horizontal = Spacing.smPlus, vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        NumericText(
            text = "$mm:$ss".toPersianDigits(),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.blueText,
        )
        Icon(
            imageVector = Icons.Outlined.Schedule,
            contentDescription = null,
            tint = colors.blueText,
            modifier = Modifier.size(14.dp),
        )
    }
}

/** Inline, retry-in-place error line under a wizard card (the action is retried by pressing the CTA again). */
@Composable
private fun RequestError(message: String?) {
    if (message.isNullOrBlank()) return
    Spacer(Modifier.height(Spacing.sm))
    Text(
        text = message,
        style = MaterialTheme.typography.labelMedium,
        color = LocalTaminColors.current.dangerText,
    )
}

// --------------------------------------------------------------------------- previews

private val PreviewContactState = EmployerOnlineServicesUiState(
    agreementRequest = AgreementRequestUiState(
        mobile = "09153214478",
        email = "h.tavakoli@gmail.com",
    ),
)

private val PreviewCodeState = EmployerOnlineServicesUiState(
    agreementRequest = AgreementRequestUiState(
        mobile = "09153214478",
        email = "h.tavakoli@gmail.com",
        ticketRequested = true,
        ticketNonce = 1,
        code = "۱۲۳",
    ),
)

@PreviewRtlTheme
@Composable
private fun EmployerAgreementRequestContactPreviewLight() {
    PreviewRtlThemeContent {
        AppToastHost {
            EmployerAgreementRequestScreen(
                uiState = PreviewContactState,
                onIntent = {},
                onClose = {})
        }
    }
}

@PreviewRtlTheme
@Composable
private fun EmployerAgreementRequestContactPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        AppToastHost {
            EmployerAgreementRequestScreen(
                uiState = PreviewContactState,
                onIntent = {},
                onClose = {})
        }
    }
}

@PreviewRtlTheme
@Composable
private fun EmployerAgreementRequestCodePreviewLight() {
    PreviewRtlThemeContent {
        AppToastHost {
            EmployerAgreementRequestScreen(uiState = PreviewCodeState, onIntent = {}, onClose = {})
        }
    }
}

@PreviewRtlTheme
@Composable
private fun EmployerAgreementRequestCodePreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        AppToastHost {
            EmployerAgreementRequestScreen(uiState = PreviewCodeState, onIntent = {}, onClose = {})
        }
    }
}

