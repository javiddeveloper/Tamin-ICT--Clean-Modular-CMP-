package com.tamin.taminhamrah.feature.pensionSurvivor.ui.survivorInfo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.SurvivorContactDraft
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.survivorInfo.contract.SurvivorInfoEvent
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.survivorInfo.contract.SurvivorInfoIntent
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.survivorInfo.contract.SurvivorInfoUiState
import com.tamin.taminhamrah.model.personal.survivorDependent.SurvivorDependentPR
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.LoadingButtonIconPosition
import com.tamin.taminhamrah.ui.components.TaminBottomBar
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminTextField
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.components.toast.success
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.HeaderDecoration
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.PersianDateFormatter
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.amount_unknown
import taminx.core.core_ui.girl_survivor_address_label
import taminx.core.core_ui.girl_survivor_label_birth_date
import taminx.core.core_ui.girl_survivor_label_father_name
import taminx.core.core_ui.girl_survivor_label_full_name
import taminx.core.core_ui.girl_survivor_label_insurance_id
import taminx.core.core_ui.girl_survivor_phone_label
import taminx.core.core_ui.ic_request
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ic_tamin_cross
import taminx.core.core_ui.identity_field_mobile
import taminx.core.core_ui.pension_survivor_deceased_documents_title
import taminx.core.core_ui.pension_survivor_soon
import taminx.core.core_ui.pension_survivor_step_survivors
import taminx.core.core_ui.pension_survivor_title
import taminx.core.core_ui.upload_submit_final
import taminx.core.core_ui.verify_label_national_id

@Composable
fun SurvivorInfoScreen(
    survivor: SurvivorDependentPR,
    deceasedNationalId: String,
    address: String = "",
    phoneNumber: String = "",
    mobileNumber: String = "",
    onSaved: (String, SurvivorContactDraft) -> Unit = { _, _ -> },
    onBack: () -> Unit,
    viewModel: SurvivorInfoViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current

    LaunchedEffect(survivor.nationalId, deceasedNationalId, address, phoneNumber, mobileNumber) {
        viewModel.sendIntent(
            SurvivorInfoIntent.Init(
                survivor = survivor,
                deceasedNationalId = deceasedNationalId,
                address = address,
                phoneNumber = phoneNumber,
                mobileNumber = mobileNumber,
            ),
        )
    }

    HandleSurvivorInfoEvents(
        events = viewModel.events,
        onNavigateBack = onBack,
        onShowError = { toaster.error(it) },
        onShowSuccess = { toaster.success(it) },
        onSaved = onSaved,
    )

    SurvivorInfoContent(
        state = state,
        onBack = { viewModel.sendIntent(SurvivorInfoIntent.OnBack) },
        onIntent = viewModel::sendIntent,
    )
}

@Composable
private fun HandleSurvivorInfoEvents(
    events: Flow<SurvivorInfoEvent>,
    onNavigateBack: () -> Unit,
    onShowError: (String) -> Unit,
    onShowSuccess: (String) -> Unit,
    onSaved: (String, SurvivorContactDraft) -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            SurvivorInfoEvent.NavigateBack -> onNavigateBack()
            is SurvivorInfoEvent.ShowError -> onShowError(event.message)
            is SurvivorInfoEvent.Saved -> onSaved(event.nationalId, event.draft)
            is SurvivorInfoEvent.ShowSuccess -> onShowSuccess(event.message)
        }
    }
}

@Composable
private fun SurvivorInfoContent(
    state: SurvivorInfoUiState,
    onBack: () -> Unit,
    onIntent: (SurvivorInfoIntent) -> Unit,
) {
    val colors = LocalTaminColors.current
    val survivor = state.survivor

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.pension_survivor_title),
                bottomPadding = Spacing.xl,
                shape = RoundedCornerShape(
                    bottomStart = CornerRadius.x3l,
                    bottomEnd = CornerRadius.x3l,
                ),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = null,
                        onClick = onBack,
                        bordered = true,
                    )
                },
                action = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_cross),
                        contentDescription = null,
                        onClick = onBack,
                        bordered = true,
                    )
                },
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    DecorativeBackgroundCircle(
                        size = HeaderDecoration.circleSize,
                        xOffset = HeaderDecoration.circleXOffset,
                        yOffset = HeaderDecoration.circleYOffset,
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        AnimatedRingHeaderIcon(icon = vectorResource(Res.drawable.ic_request))
                        Spacer(modifier = Modifier.height(Spacing.md))
                        Text(
                            text = stringResource(Res.string.pension_survivor_step_survivors),
                            style = MaterialTheme.typography.labelLarge,
                            color = colors.textHeaderSubtitle,
                        )
                    }
                }
            }
        },
        bottomBar = {
            TaminBottomBar(
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding(),
            ) {
                LoadingButton(
                    text = stringResource(Res.string.upload_submit_final),
                    onClick = { onIntent(SurvivorInfoIntent.Save) },
                    isLoading = state.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                    iconPosition = LoadingButtonIconPosition.TRAILING,
                )
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            survivor?.let {
                SurvivorIdentityCard(survivor = it)
            }

            TaminTextField(
                value = state.address,
                onValueChange = { onIntent(SurvivorInfoIntent.AddressChanged(it)) },
                label = stringResource(Res.string.girl_survivor_address_label),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.smd),
            ) {
                TaminTextField(
                    value = state.phoneNumber,
                    onValueChange = { onIntent(SurvivorInfoIntent.PhoneNumberChanged(it)) },
                    label = stringResource(Res.string.girl_survivor_phone_label),
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f),
                )
                TaminTextField(
                    value = state.mobileNumber,
                    onValueChange = { onIntent(SurvivorInfoIntent.MobileNumberChanged(it)) },
                    label = stringResource(Res.string.identity_field_mobile),
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(1f),
                )
            }

            Text(
                text = stringResource(Res.string.pension_survivor_deceased_documents_title),
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
            )

            // TODO(upload-component): wire shared image upload component from other branch (death cert / ID pages).
            PlaceholderUploadCard(
                title = stringResource(Res.string.pension_survivor_deceased_documents_title),
                caption = stringResource(Res.string.pension_survivor_soon),
                icon = vectorResource(Res.drawable.ic_request),
            )
        }
    }
}

@Composable
private fun SurvivorIdentityCard(
    survivor: SurvivorDependentPR,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val rows = buildIdentityRows(survivor)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = colors.bgSurface,
                shape = RoundedCornerShape(CornerRadius.card),
            )
            .border(
                width = Thickness.border,
                color = colors.border,
                shape = RoundedCornerShape(CornerRadius.card),
            )
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        rows.forEachIndexed { index, row ->
            DetailRow(
                label = row.label,
                value = row.value,
                numeric = row.numeric,
            )
            if (index < rows.lastIndex) {
                TaminDivider()
            }
        }
    }
}

@Composable
private fun buildIdentityRows(survivor: SurvivorDependentPR): List<IdentityRow> {
    val birthDate = survivor.dateOfBirth
        .toLongOrNull()
        ?.let(PersianDateFormatter::formatTimestamp)
        .orEmpty()

    return listOf(
        IdentityRow(
            label = stringResource(Res.string.girl_survivor_label_full_name),
            value = listOf(survivor.firstName, survivor.lastName)
                .filter(String::isNotBlank)
                .joinToString(" ")
                .ifBlank { stringResource(Res.string.amount_unknown) },
            numeric = false,
        ),
        IdentityRow(
            label = stringResource(Res.string.verify_label_national_id),
            value = survivor.nationalId.orUnknown(),
        ),
        IdentityRow(
            label = stringResource(Res.string.girl_survivor_label_father_name),
            value = survivor.fatherName.orUnknown(),
            numeric = false,
        ),
        IdentityRow(
            label = stringResource(Res.string.girl_survivor_label_insurance_id),
            value = survivor.insuranceId.orUnknown(),
        ),
        IdentityRow(
            label = stringResource(Res.string.girl_survivor_label_birth_date),
            value = birthDate.ifBlank { stringResource(Res.string.amount_unknown) },
            numeric = false,
        ),
    )
}

@Composable
private fun PlaceholderUploadCard(
    title: String,
    caption: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = colors.bgSurface,
                shape = RoundedCornerShape(CornerRadius.lg),
            )
            .border(
                width = Thickness.border,
                color = colors.border,
                shape = RoundedCornerShape(CornerRadius.lg),
            )
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textPrimary,
        )
        Text(
            text = caption,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
        )
        LoadingButton(
            text = caption,
            onClick = {},
            enabled = false,
            icon = icon,
            iconPosition = LoadingButtonIconPosition.TRAILING,
        )
    }
}

@Composable
private fun String.orUnknown(): String {
    return if (isBlank()) stringResource(Res.string.amount_unknown) else this
}

private data class IdentityRow(
    val label: String,
    val value: String,
    val numeric: Boolean = true,
)
