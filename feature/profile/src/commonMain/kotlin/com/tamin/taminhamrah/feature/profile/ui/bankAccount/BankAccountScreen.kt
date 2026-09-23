package com.tamin.taminhamrah.feature.profile.ui.bankAccount

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
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
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.pushBack
import com.tamin.taminhamrah.ui.pushForward
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.CopyIconButton
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.StaggeredEntranceState
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePicker
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.rememberCopyAction
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.components.rememberStaggeredEntranceState
import com.tamin.taminhamrah.ui.components.staggeredItemEntrance
import com.tamin.taminhamrah.ui.theme.Duration
import com.tamin.taminhamrah.ui.theme.Easing
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminNavy300
import com.tamin.taminhamrah.ui.theme.TaminNavy900
import com.tamin.taminhamrah.ui.toparea.TopAreaState
import com.tamin.taminhamrah.ui.toparea.driveTopArea
import com.tamin.taminhamrah.ui.toparea.rememberMeasuredTopAreaState
import com.tamin.taminhamrah.ui.toparea.topAreaHide
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow
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
import taminx.core.core_ui.bank_account_submit
import taminx.core.core_ui.bank_account_subtitle
import taminx.core.core_ui.bank_account_title
import taminx.core.core_ui.bank_account_tracking_code
import taminx.core.core_ui.ic_number
import taminx.core.core_ui.ic_tamin_chevron_back

private const val ADD_BUTTON_KEY = "add"
private const val EMPTY_STATE_KEY = "empty"

private val CardSpacing = 14.dp
private val TrackingCodeCorner = 12.dp
private val HeaderCorner = 40.dp
private val DecorCircleSize = 190.dp
private val DecorCircleX = 450.dp
private val DecorCircleY = (-150).dp

/**
 * The design's own gradient for the add button: diagonal, not the horizontal bar gradient.
 *
 * Built once at class-init rather than per composition — the stops are fixed brand colors, so
 * there is nothing for a `remember` to key on.
 */
private val AddButtonBrush = Brush.linearGradient(listOf(TaminNavy300, TaminNavy900))

/**
 * Which of the three bodies the page is showing.
 *
 * Declared shallowest-first on purpose: the order *is* the depth, and it is the whole rule that
 * tells a swap whether it is a step in (push) or a step back out (pop).
 */
private enum class BankAccountPane { LOADING, LIST, ADD }

private val BankAccountUiState.pane: BankAccountPane
    get() = when {
        isLoading && accounts.isEmpty() -> BankAccountPane.LOADING
        mode == BankAccountMode.ADD -> BankAccountPane.ADD
        else -> BankAccountPane.LIST
    }

/**
 * The skeleton turning into the list is one page finishing its load, not a step into another, so
 * it crosses over instead of sliding. Built once at class-init: the spec has nothing to key on.
 */
private val LoadCrossfade =
    fadeIn(tween(Duration.normal, easing = Easing.standard)) togetherWith
        fadeOut(tween(Duration.normal, easing = Easing.standard))

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
    // Above the swap on purpose: the state is what remembers a card has already arrived, so
    // leaving the list for the form and coming back does not replay every entrance underneath the
    // slide. Keyed as before, so a reload that changes the list still animates it in.
    val staggerState = rememberStaggeredEntranceState(state.accounts.size)

    // Folds from the add-form's own drag; the list and loading panes never drive it, so it always
    // reads as expanded there. Measured against the real header so the drag budget can't drift out
    // of sync with a copy or font change.
    val topArea = rememberMeasuredTopAreaState { probeState ->
        BankAccountHeader(onBack = {}, topAreaState = probeState)
    }
    LaunchedEffect(state.pane) {
        if (state.pane != BankAccountPane.ADD) topArea.expandFully()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            BankAccountHeader(
                onBack = { onIntent(BankAccountIntent.OnBackClicked) },
                topAreaState = topArea,
            )
        },
        bottomBar = {
            if (state.pane == BankAccountPane.ADD) {
                BankAccountBottomBar(
                    isSubmitting = state.isSubmitting,
                    onSubmit = { onIntent(BankAccountIntent.OnSubmitClicked) },
                )
            }
        },
    ) { padding ->
        // The header stays put and only the body travels, which is what makes the form read as a
        // second view of this page rather than a page of its own.
        //
        // The transition runs entirely in the graphics layer, so a frame of it costs no
        // recomposition; the body inside is only recomposed by its own state changing.
        AnimatedContent(
            targetState = state.pane,
            transitionSpec = {
                when {
                    initialState == BankAccountPane.LOADING ||
                        targetState == BankAccountPane.LOADING -> LoadCrossfade

                    targetState > initialState -> pushForward()
                    else -> pushBack()
                }
            },
            label = "bank-account-pane",
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding(),
                ),
        ) { pane ->
            when (pane) {
                BankAccountPane.LOADING -> BankAccountListSkeleton(
                    modifier = Modifier.navigationBarsPadding(),
                )

                BankAccountPane.ADD -> AddView(
                    draft = state.draft,
                    showValidation = state.showValidation,
                    topArea = topArea,
                    onIntent = onIntent,
                )

                BankAccountPane.LIST -> ListView(
                    accounts = state.accounts,
                    canShowEmptyState = state.hasLoadedOnce,
                    staggerState = staggerState,
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

/**
 * The page header — same one as the change-mobile subpage, so the profile subpages read as one
 * family.
 *
 * Its own composable rather than a lambda inside `Scaffold`: nothing here depends on the state, but
 * a `topBar` lambda is re-executed every time the page recomposes, which on this screen is every
 * keystroke in the account-number field. As a function with one stable parameter it skips instead,
 * so typing no longer re-runs the gradient bar, the decorative circle and the ring icon.
 */
@Composable
private fun BankAccountHeader(onBack: () -> Unit, topAreaState: TopAreaState) {
    val taminColors = LocalTaminColors.current
    val profileGradientBrush = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }

    TaminTopAppBar(
        title = stringResource(Res.string.bank_account_title),
        background = profileGradientBrush,
        bottomPadding = Spacing.xl,
        shape = RoundedCornerShape(bottomStart = HeaderCorner, bottomEnd = HeaderCorner),
        navigationIcon = {
            TaminTopAppBarButton(
                icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                contentDescription = stringResource(Res.string.action_back),
                onClick = onBack,
                bordered = true,
            )
        },
    ) {
        // Only this furniture folds away as the add-form scrolls; the title row above stays put
        // so the bar reads the same as the rest of the app once collapsed.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .topAreaHide(topAreaState),
        ) {
            DecorativeBackgroundCircle(
                size = DecorCircleSize,
                xOffset = DecorCircleX,
                yOffset = DecorCircleY,
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AnimatedRingHeaderIcon(
                    icon = vectorResource(Res.drawable.ic_number),
                    animated = !topAreaState.isMeasureProbe,
                )
                Spacer(Modifier.height(Spacing.md))
                Text(
                    text = stringResource(Res.string.bank_account_subtitle),
                    style = MaterialTheme.typography.labelLarge,
                    color = taminColors.textHeaderSubtitle,
                )
            }
        }
    }
}

@Composable
private fun ListView(
    accounts: ImmutableList<BankAccountPR>,
    canShowEmptyState: Boolean,
    /** Held by the page, not by this list: it has to outlive the swap to the form and back. */
    staggerState: StaggeredEntranceState,
    onIntent: (BankAccountIntent) -> Unit,
) {
    // The add button is the first row of the list and scrolls with it, as in the design -- not a
    // floating bar, which would sit on top of the last card.
    LazyColumn(
        modifier = Modifier.fillMaxSize().navigationBarsPadding(),
        contentPadding = PaddingValues(Spacing.page),
        verticalArrangement = Arrangement.spacedBy(CardSpacing),
        overscrollEffect = rememberJellyOverscroll(),
    ) {
        item(key = ADD_BUTTON_KEY) {
            TaminPrimaryButton(
                text = stringResource(Res.string.bank_account_add),
                icon = Icons.Default.Add,
                onClick = { onIntent(BankAccountIntent.OnAddClicked) },
                background = AddButtonBrush,
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
    topArea: TopAreaState,
    onIntent: (BankAccountIntent) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
            // Folds the shared header from this form's own drag; the list and loading panes never
            // attach this, so the header only ever collapses while the form is on screen.
            .driveTopArea(topArea, scrollState)
            .verticalScroll(scrollState, overscrollEffect = rememberJellyOverscroll())
            .padding(Spacing.page),
    ) {
        BankAccountForm(
            draft = draft,
            showValidation = showValidation,
            onPickerRequested = {
                focusManager.clearFocus()
                onIntent(BankAccountIntent.OnPickerRequested(it))
            },
            onAccountNumberChanged = { onIntent(BankAccountIntent.OnAccountNumberChanged(it)) },
        )
    }
}

/** The form's submit action, pinned to the bottom of the scaffold instead of scrolling with it. */
@Composable
private fun BankAccountBottomBar(
    isSubmitting: Boolean,
    onSubmit: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val focusManager = LocalFocusManager.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.bgPage)
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = Spacing.page, vertical = Spacing.md),
    ) {
        // Carries its own spinner and disabled tone, so the screen needs no overlay while the
        // request is in flight.
        LoadingButton(
            text = stringResource(Res.string.bank_account_submit),
            onClick = {
                focusManager.clearFocus()
                onSubmit()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSubmitting,
            isLoading = isSubmitting,
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
        TaminConfirmationDialog(
            title = stringResource(Res.string.bank_account_registered),
            description = stringResource(Res.string.bank_account_registered_description),
            icon = Icons.Default.Check,
            content = referenceCode?.let { code -> { TrackingCodeRow(code = code) } },
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

/**
 * The tracking code, copyable.
 *
 * The whole row is the tap target, not just the glyph — the icon is the affordance, but a 24dp
 * icon is a poor thing to aim at. What lands on the clipboard is the raw code, never the
 * Persian-digit rendering: those digits are for reading, and pasting them anywhere that expects a
 * code would produce something the service cannot match.
 */
@Composable
private fun TrackingCodeRow(code: String) {
    val colors = LocalTaminColors.current
    val copyDescription = stringResource(Res.string.bank_account_tracking_code)
    // The clipboard write and its confirmation come from core-ui, so this row and every copy
    // glyph elsewhere behave the same way rather than each growing its own version.
    val copy = rememberCopyAction(code)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(TrackingCodeCorner))
            .background(colors.bgPage)
            .clickable(onClick = copy)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = copyDescription,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            // A code is read as printed, so it stays left-to-right on this right-to-left page.
            NumericText(
                text = code.toPersianDigits(),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary,
            )
            // The row already copies, so the glyph is affordance only — no second tap target and
            // no second announcement for a screen reader.
            CopyIconButton(
                value = code,
                tint = colors.blueText,
                interactive = false,
            )
        }
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



