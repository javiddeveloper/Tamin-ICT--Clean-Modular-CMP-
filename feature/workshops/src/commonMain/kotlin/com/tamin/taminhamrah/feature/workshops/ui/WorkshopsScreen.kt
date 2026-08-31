package com.tamin.taminhamrah.feature.workshops.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsIntent
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsUiState
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.common.ProvincePR
import com.tamin.taminhamrah.model.contracts.BranchPR
import com.tamin.taminhamrah.model.contractFlow.BranchSelectionFormPR
import com.tamin.taminhamrah.model.contractFlow.SelectBranchStepContent
import com.tamin.taminhamrah.model.contractFlow.SelectableField
import com.tamin.taminhamrah.model.workshop.EmployerAgreementPR
import com.tamin.taminhamrah.model.workshop.EmployerWorkshopPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.TaminTextField
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheetType
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_search
import taminx.core.core_ui.workshops_action_article16
import taminx.core.core_ui.workshops_action_debit_list
import taminx.core.core_ui.workshops_action_debt_inquiry
import taminx.core.core_ui.workshops_action_insured_registration
import taminx.core.core_ui.workshops_action_members
import taminx.core.core_ui.workshops_action_objection
import taminx.core.core_ui.workshops_action_payment_sheets
import taminx.core.core_ui.workshops_action_stakeholders
import taminx.core.core_ui.workshops_actions_title
import taminx.core.core_ui.workshops_card_branch
import taminx.core.core_ui.workshops_card_employer
import taminx.core.core_ui.workshops_empty_subtitle
import taminx.core.core_ui.workshops_empty_title
import taminx.core.core_ui.workshops_filter_status
import taminx.core.core_ui.workshops_status_active
import taminx.core.core_ui.workshops_status_any
import taminx.core.core_ui.workshops_status_inactive
import taminx.core.core_ui.workshops_status_semi_active
import taminx.core.core_ui.workshops_filter_workshop_id
import taminx.core.core_ui.workshops_search_action
import taminx.core.core_ui.workshops_title
import taminx.core.core_ui.workshops_value_unknown

@Composable
fun WorkshopsScreen(
    viewModel: WorkshopsViewModel = koinViewModel(),
    navigateToPaymentSheets: (String, String) -> Unit = { _, _ -> },
    navigateToWorkshopDebit: (String, String) -> Unit = { _, _ -> },
    navigateToWorkshopDebtInquiry: (String, String) -> Unit = { _, _ -> },
    navigateToManagementDebit: (String, String) -> Unit = { _, _ -> },
    navigateToWorkshopMembers: (String, String) -> Unit = { _, _ -> },
    navigateToWorkshopStackholders: (String, String) -> Unit = { _, _ -> },
    navigateToWorkshopRecentlyAddedMembers: (String, String) -> Unit = { _, _ -> },
    navigateToObjectionableDebit: (String, String) -> Unit = { _, _ -> }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var workshopId by remember { mutableStateOf("") }
    var workshopStatus by remember { mutableStateOf("") }
    var selectedWorkshop by remember { mutableStateOf<Pair<String, String>?>(null) }

    // Read once here so the lambda below captures a String rather than the whole UiState.
    val selectedBranchCode = uiState.branchSelection.branchCode

    WorkshopsContent(
        uiState = uiState,
        workshopId = workshopId,
        onWorkshopIdChange = { workshopId = it },
        workshopStatus = workshopStatus,
        onWorkshopStatusChange = { workshopStatus = it },
        onProvinceSelected = { viewModel.sendIntent(WorkshopsIntent.SelectProvince(it)) },
        onCitySelected = { viewModel.sendIntent(WorkshopsIntent.SelectCity(it)) },
        onBranchSelected = { viewModel.sendIntent(WorkshopsIntent.SelectBranch(it)) },
        onRetryProvinces = { viewModel.sendIntent(WorkshopsIntent.LoadProvinces) },
        onRetryCities = { viewModel.sendIntent(WorkshopsIntent.RetryCities) },
        onRetryBranches = { viewModel.sendIntent(WorkshopsIntent.RetryBranches) },
        onLoadClick = {
            viewModel.sendIntent(
                WorkshopsIntent.LoadWorkshops(
                    workshopId = workshopId.takeIf { it.isNotBlank() },
                    branchCode = selectedBranchCode.takeIf { it.isNotBlank() },
                    workshopStatus = workshopStatus.takeIf { it.isNotBlank() }
                )
            )
        },
        onWorkshopClick = { wId, bCode -> selectedWorkshop = wId to bCode }
    )

    selectedWorkshop?.let { (workshopIdentifier, branchCode) ->
        WorkshopActionsSheet(
            onDismiss = { selectedWorkshop = null },
            onAction = { navigate ->
                selectedWorkshop = null
                navigate(workshopIdentifier, branchCode)
            },
            navigateToPaymentSheets = navigateToPaymentSheets,
            navigateToWorkshopDebit = navigateToWorkshopDebit,
            navigateToWorkshopDebtInquiry = navigateToWorkshopDebtInquiry,
            navigateToObjectionableDebit = navigateToObjectionableDebit,
            navigateToWorkshopRecentlyAddedMembers = navigateToWorkshopRecentlyAddedMembers,
            navigateToManagementDebit = navigateToManagementDebit,
            navigateToWorkshopMembers = navigateToWorkshopMembers,
            navigateToWorkshopStackholders = navigateToWorkshopStackholders,
        )
    }
}

/** The eight services a picked workshop opens onto. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkshopActionsSheet(
    onDismiss: () -> Unit,
    onAction: (navigate: (String, String) -> Unit) -> Unit,
    navigateToPaymentSheets: (String, String) -> Unit,
    navigateToWorkshopDebit: (String, String) -> Unit,
    navigateToWorkshopDebtInquiry: (String, String) -> Unit,
    navigateToObjectionableDebit: (String, String) -> Unit,
    navigateToWorkshopRecentlyAddedMembers: (String, String) -> Unit,
    navigateToManagementDebit: (String, String) -> Unit,
    navigateToWorkshopMembers: (String, String) -> Unit,
    navigateToWorkshopStackholders: (String, String) -> Unit,
) {
    val colors = LocalTaminColors.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.bgPage,
        shape = RoundedCornerShape(topStart = CornerRadius.sheet, topEnd = CornerRadius.sheet),
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            TaminText(
                text = stringResource(Res.string.workshops_actions_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                modifier = Modifier.padding(bottom = Spacing.sm),
            )

            WorkshopActionRow(Res.string.workshops_action_payment_sheets) {
                onAction(navigateToPaymentSheets)
            }
            WorkshopActionRow(Res.string.workshops_action_debit_list) {
                onAction(navigateToWorkshopDebit)
            }
            WorkshopActionRow(Res.string.workshops_action_debt_inquiry) {
                onAction(navigateToWorkshopDebtInquiry)
            }
            WorkshopActionRow(Res.string.workshops_action_objection) {
                onAction(navigateToObjectionableDebit)
            }
            // Was a dead end — it dismissed the sheet without navigating, while the
            // `navigateToWorkshopRecentlyAddedMembers` callback it belongs to went unused.
            WorkshopActionRow(Res.string.workshops_action_insured_registration) {
                onAction(navigateToWorkshopRecentlyAddedMembers)
            }
            WorkshopActionRow(Res.string.workshops_action_article16) {
                onAction(navigateToManagementDebit)
            }
            WorkshopActionRow(Res.string.workshops_action_members) {
                onAction(navigateToWorkshopMembers)
            }
            WorkshopActionRow(Res.string.workshops_action_stakeholders) {
                onAction(navigateToWorkshopStackholders)
            }
        }
    }
}

@Composable
private fun WorkshopActionRow(
    label: org.jetbrains.compose.resources.StringResource,
    onClick: () -> Unit,
) {
    val colors = LocalTaminColors.current
    TaminText(
        text = stringResource(label),
        style = MaterialTheme.typography.bodyMedium,
        color = colors.textPrimary,
        modifier = Modifier
            .fillMaxWidth()
            .taminSurface()
            .clickable(onClick = onClick)
            .padding(Spacing.lg),
    )
}

@Composable
fun WorkshopsContent(
    uiState: WorkshopsUiState,
    workshopId: String,
    onWorkshopIdChange: (String) -> Unit,
    workshopStatus: String,
    onWorkshopStatusChange: (String) -> Unit,
    onProvinceSelected: (ProvincePR) -> Unit,
    onCitySelected: (CityPR) -> Unit,
    onBranchSelected: (BranchPR) -> Unit,
    onRetryProvinces: () -> Unit,
    onRetryCities: () -> Unit,
    onRetryBranches: () -> Unit,
    onLoadClick: () -> Unit,
    onWorkshopClick: (String, String) -> Unit
) {
    val colors = LocalTaminColors.current
    Column(modifier = Modifier.fillMaxSize()) {
        TaminTopAppBar(title = stringResource(Res.string.workshops_title))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            TaminTextField(
                value = workshopId,
                onValueChange = onWorkshopIdChange,
                label = stringResource(Res.string.workshops_filter_workshop_id),
                keyboardType = KeyboardType.Number,
            )

            // The branch is chosen through استان → شهر → شعبه rather than typed: only its
            // resolved branchCode is a filter the workshops endpoint understands.
            SelectBranchStepContent(
                branchSelection = uiState.branchSelection,
                provinces = uiState.provinces,
                cities = uiState.cities,
                branches = uiState.branches,
                isProvincesLoading = uiState.isProvincesLoading,
                isCitiesLoading = uiState.isCitiesLoading,
                isBranchesLoading = uiState.isBranchesLoading,
                onProvinceSelected = onProvinceSelected,
                onCitySelected = onCitySelected,
                onBranchSelected = onBranchSelected,
                notices = persistentListOf(),
                provincesError = uiState.provincesError,
                citiesError = uiState.citiesError,
                branchesError = uiState.branchesError,
                onRetryProvinces = onRetryProvinces,
                onRetryCities = onRetryCities,
                onRetryBranches = onRetryBranches,
            )

            // وضعیت کارگاه is a code on the wire ("01" فعال, "02" نیمه فعال, "03" غیرفعال), not
            // free text — typing it was an invitation to send something the endpoint ignores.
            val statusLabel = stringResource(Res.string.workshops_filter_status)
            val statusOptions = workshopStatusOptions()
            SelectableField(
                label = statusLabel,
                options = statusOptions,
                selectedCode = workshopStatus,
                selectedName = statusOptions.firstOrNull { it.code == workshopStatus }?.label
                    .orEmpty(),
                optionCode = { it.code },
                optionName = { it.label },
                isLoading = false,
                onSelected = { onWorkshopStatusChange(it.code) },
                sheetType = TaminBottomSheetType.WORKSHOP_STATUS,
            )

            TaminPrimaryButton(
                text = stringResource(Res.string.workshops_search_action),
                onClick = onLoadClick,
                modifier = Modifier.fillMaxWidth(),
            )

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> CircularProgressIndicator(
                        color = colors.blueText,
                        modifier = Modifier.align(Alignment.Center),
                    )

                    uiState.error != null -> ErrorStateView(
                        message = uiState.error,
                        onDismiss = {},
                        onRetry = onLoadClick,
                        modifier = Modifier.align(Alignment.Center),
                    )

                    uiState.agreements.isEmpty() -> EmptyStateMessage(
                        icon = vectorResource(Res.drawable.ic_tamin_search),
                        title = stringResource(Res.string.workshops_empty_title),
                        subtitle = stringResource(Res.string.workshops_empty_subtitle),
                        modifier = Modifier.align(Alignment.Center),
                    )

                    else -> LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            bottom = Spacing.lg,
                        ),
                    ) {
                        items(uiState.agreements) { agreement ->
                            WorkshopItem(agreement = agreement, onClick = onWorkshopClick)
                        }
                    }
                }
            }
        }
    }
}

/** One row of the وضعیت کارگاه picker: the code the endpoint filters on, and its label. */
private data class WorkshopStatusOption(val code: String, val label: String)

/**
 * The statuses the workshops endpoint understands, taken from `old_android`'s
 * `FilterWorkshopEnumClass`. The blank code is "no filter" — the old client spelled it `"00"`, but
 * that is a sentinel it never sends, so omitting the filter says the same thing more honestly.
 */
@Composable
private fun workshopStatusOptions(): List<WorkshopStatusOption> {
    val any = stringResource(Res.string.workshops_status_any)
    val active = stringResource(Res.string.workshops_status_active)
    val semiActive = stringResource(Res.string.workshops_status_semi_active)
    val inactive = stringResource(Res.string.workshops_status_inactive)
    return remember(any, active, semiActive, inactive) {
        listOf(
            WorkshopStatusOption(code = "", label = any),
            WorkshopStatusOption(code = "01", label = active),
            WorkshopStatusOption(code = "02", label = semiActive),
            WorkshopStatusOption(code = "03", label = inactive),
        )
    }
}

@Composable
fun WorkshopItem(
    agreement: EmployerAgreementPR,
    onClick: (String, String) -> Unit
) {
    val colors = LocalTaminColors.current
    val workshop = agreement.workshop
    val unknown = stringResource(Res.string.workshops_value_unknown)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .taminSurface()
            .clickable {
                val wId = workshop?.workshopId.orEmpty()
                val bCode = workshop?.branchCode.orEmpty()
                if (wId.isNotBlank() && bCode.isNotBlank()) onClick(wId, bCode)
            }
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        TaminText(
            text = workshop?.workshopName?.takeIf { it.isNotBlank() } ?: unknown,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
        )
        WorkshopItemRow(
            label = stringResource(Res.string.workshops_filter_workshop_id),
            value = workshop?.workshopId?.takeIf { it.isNotBlank() } ?: unknown,
        )
        WorkshopItemRow(
            label = stringResource(Res.string.workshops_card_employer),
            value = workshop?.employerName?.takeIf { it.isNotBlank() } ?: unknown,
        )
        WorkshopItemRow(
            label = stringResource(Res.string.workshops_card_branch),
            value = workshop?.branchTitle?.takeIf { it.isNotBlank() } ?: unknown,
        )
    }
}

@Composable
private fun WorkshopItemRow(label: String, value: String) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        TaminText(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
        )
        TaminText(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textPrimary,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun WorkshopsContentPreview() {
    PreviewRtlThemeContent {
        WorkshopsContent(
            uiState = WorkshopsUiState(
                agreements = listOf(sampleAgreement),
                branchSelection = BranchSelectionFormPR(
                    provinceCode = "07",
                    provinceName = "تهران",
                    cityCode = "0701",
                    cityName = "تهران",
                    branchCode = "123",
                    branchName = "شعبه ۱ تهران",
                ),
                provinces = listOf(ProvincePR("07", "تهران")),
                cities = listOf(CityPR("0701", "تهران", "07")),
                branches = listOf(BranchPR("123", "شعبه ۱ تهران")),
            ),
            workshopId = "9900020917749",
            onWorkshopIdChange = {},
            workshopStatus = "",
            onWorkshopStatusChange = {},
            onProvinceSelected = {},
            onCitySelected = {},
            onBranchSelected = {},
            onRetryProvinces = {},
            onRetryCities = {},
            onRetryBranches = {},
            onLoadClick = {},
            onWorkshopClick = { _, _ -> }
        )
    }
}

@PreviewRtlTheme
@Composable
private fun WorkshopsEmptyPreview() {
    PreviewRtlThemeContent {
        WorkshopsContent(
            uiState = WorkshopsUiState(agreements = emptyList()),
            workshopId = "",
            onWorkshopIdChange = {},
            workshopStatus = "",
            onWorkshopStatusChange = {},
            onProvinceSelected = {},
            onCitySelected = {},
            onBranchSelected = {},
            onRetryProvinces = {},
            onRetryCities = {},
            onRetryBranches = {},
            onLoadClick = {},
            onWorkshopClick = { _, _ -> }
        )
    }
}

private val sampleAgreement = EmployerAgreementPR(
    pymseq = null,
    regno = null,
    firstname = null,
    emailaddr = null,
    nationalno = null,
    mobileno = null,
    startdate = null,
    mastcusttype = null,
    createdt = null,
    masttyp = null,
    logicalDeleted = null,
    regemailseq = null,
    lastname = null,
    special = null,
    risuid = null,
    nationalcode = null,
    enddate = null,
    letDate = null,
    regdate = null,
    roletype = null,
    dname = null,
    letNo = null,
    createuid = null,
    workshop = EmployerWorkshopPR(
        sswn = null,
        branchTitle = "شعبه نمونه",
        workshopApproveDate = null,
        inclusionDate = null,
        brhCode = null,
        activityName = null,
        workshopRegisterDate = null,
        branchCode = "123",
        workshopName = "کارگاه کامپیوتر توکلی",
        employerName = "علی توکلی",
        actitvityCode = null,
        userId = null,
        workshopId = "9900020917749",
        workshopUnemployedStat = null
    )
)
