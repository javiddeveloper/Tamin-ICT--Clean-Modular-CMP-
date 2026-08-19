package com.tamin.taminhamrah.feature.deferredInstallment.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.deferredInstallment.ui.components.CalculatedAmountBox
import com.tamin.taminhamrah.feature.deferredInstallment.ui.components.DeferredInstallmentBankSheet
import com.tamin.taminhamrah.feature.deferredInstallment.ui.components.DeferredInstallmentOptionSheet
import com.tamin.taminhamrah.feature.deferredInstallment.ui.components.DeferredInstallmentTextField
import com.tamin.taminhamrah.feature.deferredInstallment.ui.components.PensionerNumberCard
import com.tamin.taminhamrah.feature.deferredInstallment.ui.contract.DeferredInstallmentEvent
import com.tamin.taminhamrah.feature.deferredInstallment.ui.contract.DeferredInstallmentFieldError
import com.tamin.taminhamrah.feature.deferredInstallment.ui.contract.DeferredInstallmentIntent
import com.tamin.taminhamrah.feature.deferredInstallment.ui.contract.DeferredInstallmentOptionUi
import com.tamin.taminhamrah.feature.deferredInstallment.ui.contract.DeferredInstallmentPicker
import com.tamin.taminhamrah.feature.deferredInstallment.ui.contract.DeferredInstallmentUiState
import com.tamin.taminhamrah.feature.deferredInstallment.ui.contract.GUARANTEE_FOR_OTHERS
import com.tamin.taminhamrah.feature.deferredInstallment.ui.contract.GUARANTEE_FOR_SELF
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.BackHandler
import com.tamin.taminhamrah.ui.components.BannerCard
import com.tamin.taminhamrah.ui.components.BannerType
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.LoadingButtonIconPosition
import com.tamin.taminhamrah.ui.components.PickerRow
import com.tamin.taminhamrah.ui.components.StepIndicator
import com.tamin.taminhamrah.ui.components.StepIndicatorModel
import com.tamin.taminhamrah.ui.components.StepState
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePickerBottomSheet
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.ButtonDimens
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_cancel
import taminx.core.core_ui.action_confirm
import taminx.core.core_ui.deferred_installment_amount_helper
import taminx.core.core_ui.deferred_installment_amount_label
import taminx.core.core_ui.deferred_installment_bank_placeholder
import taminx.core.core_ui.deferred_installment_bank_search
import taminx.core.core_ui.deferred_installment_bank_subtitle
import taminx.core.core_ui.deferred_installment_bank_title
import taminx.core.core_ui.deferred_installment_birth_date
import taminx.core.core_ui.deferred_installment_birth_date_placeholder
import taminx.core.core_ui.deferred_installment_borrower_others
import taminx.core.core_ui.deferred_installment_borrower_self
import taminx.core.core_ui.deferred_installment_branch_label
import taminx.core.core_ui.deferred_installment_branch_placeholder
import taminx.core.core_ui.deferred_installment_confirm_description
import taminx.core.core_ui.deferred_installment_confirm_title
import taminx.core.core_ui.deferred_installment_count_helper
import taminx.core.core_ui.deferred_installment_count_label
import taminx.core.core_ui.deferred_installment_error_amount
import taminx.core.core_ui.deferred_installment_error_min_amount
import taminx.core.core_ui.deferred_installment_error_bank
import taminx.core.core_ui.deferred_installment_error_birth_date
import taminx.core.core_ui.deferred_installment_error_branch
import taminx.core.core_ui.deferred_installment_error_count
import taminx.core.core_ui.deferred_installment_error_count_range
import taminx.core.core_ui.deferred_installment_error_name
import taminx.core.core_ui.deferred_installment_error_guarantee
import taminx.core.core_ui.deferred_installment_error_guarantee_amount
import taminx.core.core_ui.deferred_installment_error_last_name
import taminx.core.core_ui.deferred_installment_error_national_id
import taminx.core.core_ui.deferred_installment_error_pensioner
import taminx.core.core_ui.deferred_installment_first_name
import taminx.core.core_ui.deferred_installment_first_name_placeholder
import taminx.core.core_ui.deferred_installment_guarantee_amount_helper
import taminx.core.core_ui.deferred_installment_guarantee_amount_label
import taminx.core.core_ui.deferred_installment_guarantee_amount_placeholder
import taminx.core.core_ui.deferred_installment_guarantee_others
import taminx.core.core_ui.deferred_installment_guarantee_others_subtitle
import taminx.core.core_ui.deferred_installment_guarantee_self
import taminx.core.core_ui.deferred_installment_guarantee_self_subtitle
import taminx.core.core_ui.deferred_installment_guarantee_status
import taminx.core.core_ui.deferred_installment_info_banner
import taminx.core.core_ui.deferred_installment_last_name
import taminx.core.core_ui.deferred_installment_last_name_placeholder
import taminx.core.core_ui.deferred_installment_national_id
import taminx.core.core_ui.deferred_installment_national_id_placeholder
import taminx.core.core_ui.deferred_installment_next_step
import taminx.core.core_ui.deferred_installment_repayment_label
import taminx.core.core_ui.deferred_installment_step_loan
import taminx.core.core_ui.deferred_installment_step_request
import taminx.core.core_ui.deferred_installment_submit
import taminx.core.core_ui.deferred_installment_subtitle
import taminx.core.core_ui.deferred_installment_success_confirm
import taminx.core.core_ui.deferred_installment_success_description
import taminx.core.core_ui.deferred_installment_success_fallback
import taminx.core.core_ui.deferred_installment_success_title
import taminx.core.core_ui.deferred_installment_title
import taminx.core.core_ui.ic_close
import taminx.core.core_ui.ic_tamin_calendar
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_chevron_forward

@Composable
fun DeferredInstallmentScreen(
    onBackClicked: () -> Unit,
    viewModel: DeferredInstallmentViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    HandleDeferredInstallmentEvents(
        events = viewModel.events,
        onBackClicked = onBackClicked,
    )

    val handleBack: () -> Unit = {
        if (uiState.currentStep == DeferredInstallmentStep.CertificateRequest) {
            onBackClicked()
        } else {
            viewModel.sendIntent(DeferredInstallmentIntent.BackToPreviousStep)
        }
    }

    BackHandler(onBack = handleBack)

    DeferredInstallmentContent(
        state = uiState,
        onIntent = viewModel::sendIntent,
        onBackClicked = handleBack,
        onCloseClicked = onBackClicked,
    )
}

@Composable
private fun HandleDeferredInstallmentEvents(
    events: Flow<DeferredInstallmentEvent>,
    onBackClicked: () -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            DeferredInstallmentEvent.NavigateBack -> onBackClicked()
        }
    }
}

@Composable
private fun DeferredInstallmentContent(
    state: DeferredInstallmentUiState,
    onIntent: (DeferredInstallmentIntent) -> Unit,
    onBackClicked: () -> Unit,
    onCloseClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val profileGradientBrush = remember(colors.profileGradientStops) {
        Brush.horizontalGradient(colors.profileGradientStops)
    }
    val guaranteeLabel = when (state.guaranteeType) {
        GUARANTEE_FOR_OTHERS -> stringResource(Res.string.deferred_installment_guarantee_others)
        GUARANTEE_FOR_SELF -> stringResource(Res.string.deferred_installment_guarantee_self)
        else -> stringResource(Res.string.deferred_installment_guarantee_status)
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bgPage),
        containerColor = colors.bgPage,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.deferred_installment_title),
                background = profileGradientBrush,
                bottomPadding = Spacing.xl,
                shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = null,
                        onClick = onBackClicked,
                        bordered = true,
                    )
                },
                action = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_close),
                        contentDescription = null,
                        onClick = onCloseClicked,
                        bordered = true,
                    )
                },
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    DecorativeBackgroundCircle(
                        size = 190.dp,
                        xOffset = 450.dp,
                        yOffset = (-150).dp,
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        AnimatedRingHeaderIcon(icon = Icons.Outlined.Description)
                        Spacer(modifier = Modifier.height(Spacing.md))
                        Text(
                            text = stringResource(Res.string.deferred_installment_subtitle),
                            style = MaterialTheme.typography.labelLarge,
                            color = colors.textHeaderSubtitle,
                        )
                    }
                }
            }
        },
        bottomBar = {
            DeferredInstallmentBottomBar(
                state = state,
                onIntent = onIntent,
                onBackClicked = onBackClicked,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding()),
        ) {
            DeferredInstallmentStepIndicator(
                currentStep = state.currentStep,
                modifier = Modifier.padding(horizontal = Spacing.page, vertical = Spacing.lg),
            )
            AnimatedContent(
                targetState = state.currentStep,
                transitionSpec = {
                    if (targetState.index > initialState.index) {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                            slideOutHorizontally { it } + fadeOut()
                    } else {
                        slideInHorizontally { it } + fadeIn() togetherWith
                            slideOutHorizontally { -it } + fadeOut()
                    }
                },
                label = "DeferredInstallmentStepTransition",
                modifier = Modifier.weight(1f),
            ) { step ->
                when (step) {
                    DeferredInstallmentStep.CertificateRequest -> CertificateRequestStep(
                        state = state,
                        guaranteeLabel = guaranteeLabel,
                        onIntent = onIntent,
                    )

                    DeferredInstallmentStep.LoanDetails -> LoanDetailsStep(
                        state = state,
                        onIntent = onIntent,
                    )
                }
            }
        }
    }

    when (state.picker) {
        DeferredInstallmentPicker.GUARANTEE -> DeferredInstallmentOptionSheet(
            title = stringResource(Res.string.deferred_installment_guarantee_status),
            options = persistentListOf(
                DeferredInstallmentOptionUi(
                    id = GUARANTEE_FOR_OTHERS,
                    label = stringResource(Res.string.deferred_installment_guarantee_others),
                    subtitle = stringResource(Res.string.deferred_installment_guarantee_others_subtitle),
                ),
                DeferredInstallmentOptionUi(
                    id = GUARANTEE_FOR_SELF,
                    label = stringResource(Res.string.deferred_installment_guarantee_self),
                    subtitle = stringResource(Res.string.deferred_installment_guarantee_self_subtitle),
                ),
            ),
            selectedId = state.guaranteeType,
            onSelect = { onIntent(DeferredInstallmentIntent.OnGuaranteePicked(it.id)) },
            onDismiss = { onIntent(DeferredInstallmentIntent.OnPickerDismissed) },
        )

        DeferredInstallmentPicker.BANK -> DeferredInstallmentBankSheet(
            title = stringResource(Res.string.deferred_installment_bank_title),
            subtitle = stringResource(Res.string.deferred_installment_bank_subtitle),
            searchPlaceholder = stringResource(Res.string.deferred_installment_bank_search),
            query = state.bankQuery,
            options = state.bankOptions,
            selectedId = state.bank?.id,
            isLoading = state.isLoadingBanks,
            onQueryChanged = { onIntent(DeferredInstallmentIntent.OnBankQueryChanged(it)) },
            onSelect = { onIntent(DeferredInstallmentIntent.OnBankPicked(it)) },
            onDismiss = { onIntent(DeferredInstallmentIntent.OnPickerDismissed) },
        )

        DeferredInstallmentPicker.BIRTH_DATE -> TaminJalaliDatePickerBottomSheet(
            title = stringResource(Res.string.deferred_installment_birth_date),
            onDismiss = { onIntent(DeferredInstallmentIntent.OnPickerDismissed) },
            onConfirm = { year, month, day ->
                onIntent(DeferredInstallmentIntent.OnBirthDatePicked(year, month, day))
            },
        )

        DeferredInstallmentPicker.NONE -> Unit
    }

    if (state.showConfirmDialog) {
        val borrower = if (state.isGuaranteeForOthers) {
            stringResource(
                Res.string.deferred_installment_borrower_others,
                state.firstName,
                state.lastName,
            )
        } else {
            stringResource(Res.string.deferred_installment_borrower_self)
        }
        TaminConfirmationDialog(
            title = stringResource(Res.string.deferred_installment_confirm_title),
            description = stringResource(
                Res.string.deferred_installment_confirm_description,
                state.pensionerId,
                state.bank?.label.orEmpty(),
                borrower,
            ),
            confirmButton = {
                TaminFilledButton(
                    text = stringResource(Res.string.action_confirm),
                    onClick = { onIntent(DeferredInstallmentIntent.OnConfirmSubmit) },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            dismissButton = {
                TaminOutlinedButton(
                    text = stringResource(Res.string.action_cancel),
                    onClick = { onIntent(DeferredInstallmentIntent.OnConfirmDismissed) },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            onDismissRequest = { onIntent(DeferredInstallmentIntent.OnConfirmDismissed) },
        )
    }

    if (state.hasSubmitted) {
        TaminConfirmationDialog(
            title = stringResource(Res.string.deferred_installment_success_title),
            description = state.submittedRefCode?.takeIf { it.isNotBlank() }?.let {
                stringResource(Res.string.deferred_installment_success_description, it)
            } ?: stringResource(Res.string.deferred_installment_success_fallback),
            confirmButton = {
                TaminFilledButton(
                    text = stringResource(Res.string.deferred_installment_success_confirm),
                    onClick = { onIntent(DeferredInstallmentIntent.OnSubmitSuccessAcknowledged) },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            dismissButton = {},
            onDismissRequest = { onIntent(DeferredInstallmentIntent.OnSubmitSuccessAcknowledged) },
            icon = Icons.Default.Check,
            iconTint = colors.greenText,
            iconBackground = colors.greenBg,
        )
    }

    ErrorStateView(
        message = state.error,
        onDismiss = onCloseClicked,
        onRetry = { onIntent(DeferredInstallmentIntent.LoadInitialData) },
    )
}

@Composable
private fun DeferredInstallmentStepIndicator(
    currentStep: DeferredInstallmentStep,
    modifier: Modifier = Modifier,
) {
    val currentIndex = currentStep.index
    StepIndicator(
        modifier = modifier,
        steps = persistentListOf(
            StepIndicatorModel(
                title = stringResource(Res.string.deferred_installment_step_request),
                stepNumber = "۱",
                state = when {
                    currentIndex == 0 -> StepState.Active
                    else -> StepState.Completed
                },
            ),
            StepIndicatorModel(
                title = stringResource(Res.string.deferred_installment_step_loan),
                stepNumber = "۲",
                state = if (currentIndex == 1) StepState.Active else StepState.Inactive,
            ),
        ),
    )
}

@Composable
private fun CertificateRequestStep(
    state: DeferredInstallmentUiState,
    guaranteeLabel: String,
    onIntent: (DeferredInstallmentIntent) -> Unit,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.page, vertical = Spacing.lg),
    ) {
        Text(
            text = stringResource(Res.string.deferred_installment_step_request),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight(700)),
            color = colors.textPrimary,
        )
        Spacer(Modifier.height(Spacing.lg))
        PensionerNumberCard(pensionerId = state.pensionerId)
        if (state.fieldError == DeferredInstallmentFieldError.PENSIONER) {
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = stringResource(Res.string.deferred_installment_error_pensioner),
                style = MaterialTheme.typography.labelSmall,
                color = colors.dangerText,
            )
        }
        Spacer(Modifier.height(Spacing.md))
        BannerCard(
            message = stringResource(Res.string.deferred_installment_info_banner),
            type = BannerType.Info,
        )
        Spacer(Modifier.height(Spacing.md))
        PickerRow(
            text = guaranteeLabel,
            isPlaceholder = state.guaranteeType == null,
            isError = state.fieldError == DeferredInstallmentFieldError.GUARANTEE,
            showChevron = true,
            onClick = {
                onIntent(
                    DeferredInstallmentIntent.OnPickerRequested(
                        DeferredInstallmentPicker.GUARANTEE
                    )
                )
            },
        )
        if (state.fieldError == DeferredInstallmentFieldError.GUARANTEE) {
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = stringResource(Res.string.deferred_installment_error_guarantee),
                style = MaterialTheme.typography.labelSmall,
                color = colors.dangerText,
            )
        }
        AnimatedVisibility(visible = state.isGuaranteeForOthers) {
            Column {
                Spacer(Modifier.height(Spacing.md))
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    DeferredInstallmentTextField(
                        modifier = Modifier.weight(1f),
                        value = state.firstName,
                        onValueChange = { onIntent(DeferredInstallmentIntent.OnFirstNameChanged(it)) },
                        label = stringResource(Res.string.deferred_installment_first_name),
                        placeholder = stringResource(Res.string.deferred_installment_first_name_placeholder),
                        isError = state.fieldError == DeferredInstallmentFieldError.FIRST_NAME,
                        errorMessage = stringResource(Res.string.deferred_installment_error_name),
                    )
                    DeferredInstallmentTextField(
                        modifier = Modifier.weight(1f),
                        value = state.lastName,
                        onValueChange = { onIntent(DeferredInstallmentIntent.OnLastNameChanged(it)) },
                        label = stringResource(Res.string.deferred_installment_last_name),
                        placeholder = stringResource(Res.string.deferred_installment_last_name_placeholder),
                        isError = state.fieldError == DeferredInstallmentFieldError.LAST_NAME,
                        errorMessage = stringResource(Res.string.deferred_installment_error_last_name),
                    )
                }
                Spacer(Modifier.height(Spacing.sm))
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    DeferredInstallmentTextField(
                        modifier = Modifier.weight(1f),
                        value = state.nationalId,
                        onValueChange = { onIntent(DeferredInstallmentIntent.OnNationalIdChanged(it)) },
                        label = stringResource(Res.string.deferred_installment_national_id),
                        placeholder = stringResource(Res.string.deferred_installment_national_id_placeholder),
                        keyboardType = KeyboardType.Number,
                        isError = state.fieldError == DeferredInstallmentFieldError.NATIONAL_ID,
                        errorMessage = stringResource(Res.string.deferred_installment_error_national_id),
                    )
                    DeferredInstallmentTextField(
                        modifier = Modifier.weight(1f),
                        value = state.birthDateLabel.orEmpty(),
                        onValueChange = {},
                        label = stringResource(Res.string.deferred_installment_birth_date),
                        placeholder = stringResource(Res.string.deferred_installment_birth_date_placeholder),
                        readOnly = true,
                        trailingIcon = vectorResource(Res.drawable.ic_tamin_calendar),
                        onClick = {
                            onIntent(
                                DeferredInstallmentIntent.OnPickerRequested(
                                    DeferredInstallmentPicker.BIRTH_DATE
                                )
                            )
                        },
                        isError = state.fieldError == DeferredInstallmentFieldError.BIRTH_DATE,
                        errorMessage = stringResource(Res.string.deferred_installment_error_birth_date),
                    )
                }
            }
        }
    }
}

@Composable
private fun LoanDetailsStep(
    state: DeferredInstallmentUiState,
    onIntent: (DeferredInstallmentIntent) -> Unit,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.page, vertical = Spacing.lg),
    ) {
        Text(
            text = stringResource(Res.string.deferred_installment_step_loan),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight(700)),
            color = colors.textPrimary,
        )
        Spacer(Modifier.height(Spacing.lg))
        PickerRow(
            text = state.bank?.label
                ?: stringResource(Res.string.deferred_installment_bank_placeholder),
            isPlaceholder = state.bank == null,
            isError = state.fieldError == DeferredInstallmentFieldError.BANK,
            showChevron = true,
            onClick = {
                onIntent(
                    DeferredInstallmentIntent.OnPickerRequested(
                        DeferredInstallmentPicker.BANK
                    )
                )
            },
        )
        if (state.fieldError == DeferredInstallmentFieldError.BANK) {
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = stringResource(Res.string.deferred_installment_error_bank),
                style = MaterialTheme.typography.labelSmall,
                color = colors.dangerText,
            )
        }
        Spacer(Modifier.height(Spacing.sm))
        DeferredInstallmentTextField(
            value = state.branchName,
            onValueChange = { onIntent(DeferredInstallmentIntent.OnBranchChanged(it)) },
            label = stringResource(Res.string.deferred_installment_branch_label),
            placeholder = stringResource(Res.string.deferred_installment_branch_placeholder),
            isError = state.fieldError == DeferredInstallmentFieldError.BRANCH,
            errorMessage = stringResource(Res.string.deferred_installment_error_branch),
        )
        Spacer(Modifier.height(Spacing.sm))
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            DeferredInstallmentTextField(
                modifier = Modifier.weight(1f),
                value = state.installmentAmountDigits,
                onValueChange = { onIntent(DeferredInstallmentIntent.OnInstallmentAmountChanged(it)) },
                label = stringResource(Res.string.deferred_installment_amount_label),
                placeholder = "۵۰۰,۰۰۰",
                keyboardType = KeyboardType.Number,
                formatAsAmount = true,
                helperText = stringResource(Res.string.deferred_installment_amount_helper),
                isError = state.fieldError == DeferredInstallmentFieldError.AMOUNT ||
                    state.fieldError == DeferredInstallmentFieldError.AMOUNT_MIN,
                errorMessage = stringResource(
                    if (state.fieldError == DeferredInstallmentFieldError.AMOUNT_MIN) {
                        Res.string.deferred_installment_error_min_amount
                    } else {
                        Res.string.deferred_installment_error_amount
                    },
                ),
            )
            DeferredInstallmentTextField(
                modifier = Modifier.weight(1f),
                value = state.installmentCountDigits,
                onValueChange = { onIntent(DeferredInstallmentIntent.OnInstallmentCountChanged(it)) },
                label = stringResource(Res.string.deferred_installment_count_label),
                placeholder = "۱۲",
                keyboardType = KeyboardType.Number,
                helperText = stringResource(Res.string.deferred_installment_count_helper),
                isError = state.fieldError == DeferredInstallmentFieldError.COUNT ||
                    state.fieldError == DeferredInstallmentFieldError.COUNT_RANGE,
                errorMessage = stringResource(
                    if (state.fieldError == DeferredInstallmentFieldError.COUNT_RANGE) {
                        Res.string.deferred_installment_error_count_range
                    } else {
                        Res.string.deferred_installment_error_count
                    },
                ),
            )
        }
        Spacer(Modifier.height(Spacing.md))
        CalculatedAmountBox(
            label = stringResource(Res.string.deferred_installment_repayment_label),
            amount = state.repaymentAmount,
        )
        Spacer(Modifier.height(Spacing.sm))
        DeferredInstallmentTextField(
            value = state.guaranteeAmountDigits,
            onValueChange = { onIntent(DeferredInstallmentIntent.OnGuaranteeAmountChanged(it)) },
            label = stringResource(Res.string.deferred_installment_guarantee_amount_label),
            placeholder = stringResource(Res.string.deferred_installment_guarantee_amount_placeholder),
            keyboardType = KeyboardType.Number,
            formatAsAmount = true,
            helperText = stringResource(Res.string.deferred_installment_guarantee_amount_helper),
            isError = state.fieldError == DeferredInstallmentFieldError.GUARANTEE_AMOUNT,
            errorMessage = stringResource(Res.string.deferred_installment_error_guarantee_amount),
        )
        state.submitError?.let { message ->
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = colors.dangerText,
            )
        }
    }
}

@Composable
private fun DeferredInstallmentBottomBar(
    state: DeferredInstallmentUiState,
    onIntent: (DeferredInstallmentIntent) -> Unit,
    onBackClicked: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.page, vertical = Spacing.md).navigationBarsPadding()
            .imePadding(),
    ) {
        when (state.currentStep) {
            DeferredInstallmentStep.CertificateRequest -> LoadingButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.deferred_installment_next_step),
                onClick = { onIntent(DeferredInstallmentIntent.OnNextStepClicked) },
                enabled = state.canGoNext,
                isLoading = state.isLoading,
                icon = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                iconPosition = LoadingButtonIconPosition.TRAILING,
            )

            DeferredInstallmentStep.LoanDetails -> {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    StepBackButton(onClick = onBackClicked)
                    LoadingButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(Res.string.deferred_installment_submit),
                        onClick = { onIntent(DeferredInstallmentIntent.OnSubmitClicked) },
                        enabled = state.canSubmit && !state.isSubmitting,
                        isLoading = state.isSubmitting,
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                        iconPosition = LoadingButtonIconPosition.TRAILING,
                    )
                }
            }
        }
    }
}

@Composable
private fun StepBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.xl)
    Box(
        modifier = modifier
            .size(ButtonDimens.height)
            .clip(shape)
            .background(colors.bgSurface)
            .border(Thickness.border, colors.border, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_chevron_back),
            contentDescription = null,
            tint = colors.textPrimary,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewDeferredInstallmentStepOne() {
    PreviewRtlThemeContent {
        DeferredInstallmentContent(
            state = DeferredInstallmentUiState(
                pensionerId = "1003406938",
                guaranteeType = GUARANTEE_FOR_SELF,
            ),
            onIntent = {},
            onBackClicked = {},
            onCloseClicked = {},
        )
    }
}
