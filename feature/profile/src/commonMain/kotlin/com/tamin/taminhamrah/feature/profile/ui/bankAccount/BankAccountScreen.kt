package com.tamin.taminhamrah.feature.profile.ui.bankAccount

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.profile.ui.bankAccount.components.AccountTypePickerSheet
import com.tamin.taminhamrah.feature.profile.ui.bankAccount.components.BankAccountCard
import com.tamin.taminhamrah.feature.profile.ui.bankAccount.components.BankAccountForm
import com.tamin.taminhamrah.feature.profile.ui.bankAccount.components.BankAccountListSkeleton
import com.tamin.taminhamrah.feature.profile.ui.bankAccount.components.BankPickerSheet
import com.tamin.taminhamrah.feature.profile.ui.bankAccount.contract.BankAccountEvent
import com.tamin.taminhamrah.feature.profile.ui.bankAccount.contract.BankAccountIntent
import com.tamin.taminhamrah.feature.profile.ui.bankAccount.contract.BankAccountMode
import com.tamin.taminhamrah.feature.profile.ui.bankAccount.contract.BankAccountPicker
import com.tamin.taminhamrah.feature.profile.ui.bankAccount.contract.BankAccountUiState
import com.tamin.taminhamrah.feature.profile.ui.bankAccount.model.BankAccountDraftPR
import com.tamin.taminhamrah.model.bankAccount.BankAccountPR
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePicker
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.components.rememberStaggeredEntranceState
import com.tamin.taminhamrah.ui.components.staggeredItemEntrance
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import kotlinx.coroutines.flow.Flow
import com.tamin.taminhamrah.ui.theme.TaminNavy300
import com.tamin.taminhamrah.ui.theme.TaminNavy900
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_back
import taminx.core.core_ui.bank_account_add
import taminx.core.core_ui.bank_account_empty_description
import taminx.core.core_ui.bank_account_empty_title
import taminx.core.core_ui.bank_account_error_title
import taminx.core.core_ui.bank_account_field_start_date
import taminx.core.core_ui.bank_account_iban_understood
import taminx.core.core_ui.bank_account_picker_bank
import taminx.core.core_ui.bank_account_picker_type
import taminx.core.core_ui.bank_account_registered
import taminx.core.core_ui.bank_account_registered_description
import taminx.core.core_ui.bank_account_tracking_code
import taminx.core.core_ui.bank_account_subtitle
import taminx.core.core_ui.bank_account_title
import taminx.core.core_ui.ic_number
import taminx.core.core_ui.ic_tamin_chevron_back

private const val PARAGRAPH_BREAK = "\n\n"

private const val ADD_BUTTON_KEY = "add"
private const val EMPTY_STATE_KEY = "empty"

private val CardSpacing = 14.dp
private val HeaderCorner = 40.dp
private val DecorCircleSize = 190.dp
private val DecorCircleX = 450.dp
private val DecorCircleY = (-150).dp

@Composable
fun BankAccountRoute(
    viewModel: BankAccountViewModel,
    onBackClicked: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.sendIntent(BankAccountIntent.LoadAccounts) }

    HandleBankAccountEvents(
        events = viewModel.events,
        onBackClicked = onBackClicked,
    )

    BankAccountScreen(
        state = uiState,
        onIntent = viewModel::sendIntent,
    )
}

@Composable
fun HandleBankAccountEvents(
    events: Flow<BankAccountEvent>,
    onBackClicked: () -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            is BankAccountEvent.NavigateBack -> onBackClicked()
        }
    }
}

@Composable
fun BankAccountScreen(
    state: BankAccountUiState,
    onIntent: (BankAccountIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val profileGradientBrush = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }

    // Same header as the change-mobile subpage, so the profile subpages read as one family.
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.bank_account_title),
                background = profileGradientBrush,
                bottomPadding = Spacing.xl,
                shape = RoundedCornerShape(bottomStart = HeaderCorner, bottomEnd = HeaderCorner),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = stringResource(Res.string.action_back),
                        onClick = { onIntent(BankAccountIntent.OnBackClicked) },
                        bordered = true,
                    )
                },
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    DecorativeBackgroundCircle(
                        size = DecorCircleSize,
                        xOffset = DecorCircleX,
                        yOffset = DecorCircleY,
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        AnimatedRingHeaderIcon(icon = vectorResource(Res.drawable.ic_number))
                        Spacer(Modifier.height(Spacing.md))
                        Text(
                            text = stringResource(Res.string.bank_account_subtitle),
                            style = MaterialTheme.typography.labelLarge,
                            color = taminColors.textHeaderSubtitle,
                        )
                    }
                }
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
                .navigationBarsPadding(),
        ) {
            when {
                state.isLoading && state.accounts.isEmpty() -> BankAccountListSkeleton()

                state.mode == BankAccountMode.ADD -> AddView(
                    draft = state.draft,
                    showValidation = state.showValidation,
                    isSubmitting = state.isSubmitting,
                    onIntent = onIntent,
                )

                else -> ListView(
                    accounts = state.accounts,
                    canShowEmptyState = state.hasLoadedOnce,
                    onIntent = onIntent,
                )
            }
        }
    }

    Overlays(
        picker = state.picker,
        error = state.error,
        hasSubmitted = state.hasSubmitted,
        referenceCode = state.submittedReferenceCode,
        onIntent = onIntent,
    )
}

@Composable
private fun ListView(
    accounts: ImmutableList<BankAccountPR>,
    canShowEmptyState: Boolean,
    onIntent: (BankAccountIntent) -> Unit,
) {
    // Held across scrolls so a card that has already arrived does not fade in again.
    val staggerState = rememberStaggeredEntranceState(accounts.size)
    // The design's own gradient for this button: diagonal, not the horizontal bar gradient.
    val addButtonBrush = remember { Brush.linearGradient(listOf(TaminNavy300, TaminNavy900)) }

    // The add button is the first row of the list and scrolls with it, as in the design -- not a
    // floating bar, which would sit on top of the last card.
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Spacing.page),
        verticalArrangement = Arrangement.spacedBy(CardSpacing),
        overscrollEffect = rememberJellyOverscroll(),
    ) {
        item(key = ADD_BUTTON_KEY) {
            TaminPrimaryButton(
                text = stringResource(Res.string.bank_account_add),
                icon = Icons.Default.Add,
                onClick = { onIntent(BankAccountIntent.OnAddClicked) },
                background = addButtonBrush,
                iconAtStart = true,
            )
        }

        if (accounts.isEmpty()) {
            // Only once a load has actually returned. An empty list after a failure means "not
            // known", and dismissing the error must not silently turn that into "you have none".
            if (canShowEmptyState) {
                item(key = EMPTY_STATE_KEY) {
                    EmptyStateMessage(
                        icon = vectorResource(Res.drawable.ic_number),
                        title = stringResource(Res.string.bank_account_empty_title),
                        subtitle = stringResource(Res.string.bank_account_empty_description),
                        showIconTile = true,
                        modifier = Modifier.fillMaxWidth().padding(top = Spacing.xl),
                    )
                }
            }
        } else {
            itemsIndexed(accounts, key = { _, account -> account.id }) { index, account ->
                BankAccountCard(
                    account = account,
                    // Alpha and translation are driven through graphicsLayer, so the entrance
                    // costs no recomposition per frame.
                    modifier = Modifier.staggeredItemEntrance(
                        index = index,
                        key = account.id,
                        state = staggerState,
                    ),
                )
            }
        }
    }
}

@Composable
private fun AddView(
    draft: BankAccountDraftPR,
    showValidation: Boolean,
    isSubmitting: Boolean,
    onIntent: (BankAccountIntent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState(), overscrollEffect = rememberJellyOverscroll())
            .padding(Spacing.page),
    ) {
        BankAccountForm(
            draft = draft,
            showValidation = showValidation,
            isSubmitting = isSubmitting,
            onPickerRequested = { onIntent(BankAccountIntent.OnPickerRequested(it)) },
            onAccountNumberChanged = { onIntent(BankAccountIntent.OnAccountNumberChanged(it)) },
            onSubmit = { onIntent(BankAccountIntent.OnSubmitClicked) },
        )
    }
}

/** The pickers and the two dialogs, kept out of the page so the page reads as a layout. */
@Composable
private fun Overlays(
    picker: BankAccountPicker,
    error: String?,
    hasSubmitted: Boolean,
    referenceCode: String?,
    onIntent: (BankAccountIntent) -> Unit,
) {
    when (picker) {
        BankAccountPicker.DATE -> TaminJalaliDatePicker(
            title = stringResource(Res.string.bank_account_field_start_date),
            onDismiss = { onIntent(BankAccountIntent.OnPickerDismissed) },
            onConfirm = { year, month, day ->
                onIntent(
                    BankAccountIntent.OnStartDatePicked(
                        // UTC, because the service records a calendar day; see toEpochMillisUtc.
                        millis = PersianDateFormatter.toEpochMillisUtc(year, month, day),
                        label = PersianDateFormatter.format(year, month, day),
                    )
                )
            },
        )

        BankAccountPicker.BANK -> BankPickerSheet(
            title = stringResource(Res.string.bank_account_picker_bank),
            onSelect = { onIntent(BankAccountIntent.OnBankPicked(it)) },
            onDismiss = { onIntent(BankAccountIntent.OnPickerDismissed) },
        )

        BankAccountPicker.TYPE -> AccountTypePickerSheet(
            title = stringResource(Res.string.bank_account_picker_type),
            onSelect = { onIntent(BankAccountIntent.OnAccountTypePicked(it)) },
            onDismiss = { onIntent(BankAccountIntent.OnPickerDismissed) },
        )

        BankAccountPicker.NONE -> Unit
    }

    if (error != null && !hasSubmitted) {
        TaminConfirmationDialog(
            title = stringResource(Res.string.bank_account_error_title),
            description = error,
            onDismissRequest = { onIntent(BankAccountIntent.OnErrorDismissed) },
            confirmButton = {
                TaminFilledButton(
                    text = stringResource(Res.string.bank_account_iban_understood),
                    onClick = { onIntent(BankAccountIntent.OnErrorDismissed) },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            dismissButton = {},
        )
    }

    if (hasSubmitted) {
        // Registering files a request rather than inserting an account, so the message says what
        // happens next. The code is the only handle the person has on it until it is approved, but
        // it is optional -- its absence must not swallow the confirmation.
        val registeredDescription = stringResource(Res.string.bank_account_registered_description)
        val trackingCode = referenceCode?.let {
            stringResource(Res.string.bank_account_tracking_code, it.toPersianDigits())
        }
        val successMessage = remember(registeredDescription, trackingCode) {
            listOfNotNull(registeredDescription, trackingCode).joinToString(PARAGRAPH_BREAK)
        }
        TaminConfirmationDialog(
            title = stringResource(Res.string.bank_account_registered),
            description = successMessage,
            icon = Icons.Default.Check,
            onDismissRequest = { onIntent(BankAccountIntent.OnSuccessDismissed) },
            confirmButton = {
                TaminFilledButton(
                    text = stringResource(Res.string.bank_account_iban_understood),
                    onClick = { onIntent(BankAccountIntent.OnSuccessDismissed) },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            dismissButton = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewBankAccountScreen() {
    PreviewRtlThemeContent {
        BankAccountScreen(
            state = BankAccountUiState(),
            onIntent = {},
        )
    }
}



