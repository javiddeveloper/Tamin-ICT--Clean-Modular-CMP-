package com.tamin.taminhamrah.feature.workshops.ui.assignerContracts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFormFooter
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopRecordCard
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopScreenShell
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.model.workshop.AssignerContractPR
import com.tamin.taminhamrah.model.workshop.AssignerPartyPR
import com.tamin.taminhamrah.model.workshop.ContractRowPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.assigner_contract_detail_title
import taminx.core.core_ui.assigner_empty_no_search_body
import taminx.core.core_ui.assigner_empty_no_search_title
import taminx.core.core_ui.assigner_field_address
import taminx.core.core_ui.assigner_field_branch
import taminx.core.core_ui.assigner_field_contract_number
import taminx.core.core_ui.assigner_field_contract_row
import taminx.core.core_ui.assigner_field_contract_subject
import taminx.core.core_ui.assigner_field_national_id
import taminx.core.core_ui.assigner_field_workshop_name
import taminx.core.core_ui.assigner_group_assigner
import taminx.core.core_ui.assigner_group_contract
import taminx.core.core_ui.assigner_group_contractor
import taminx.core.core_ui.assigner_contract_date
import taminx.core.core_ui.ic_tamin_assigner_contracts
import taminx.core.core_ui.settlement_title
import taminx.core.core_ui.workshop_code

/**
 * جزئیات پیمان — three grouped blocks of label/value cells.
 *
 * No request of its own: everything here arrived with the list row, which is why tapping through
 * is instant and why the screen has no loading state. The پیمان is found in the list by the ردیف
 * and sequence its route carries, so this screen can never be composed against a stale selection;
 * it is null only after process death, which the screen says rather than drawing a page of dashes.
 *
 * The middle group is **your own** کارگاه: the response carries both sides of the پیمان, so the
 * design's hardcoded `agMyWs()` block is real data here, not a profile lookup.
 */
@Composable
fun AssignerContractDetailScreen(
    viewModel: AssignerContractsViewModel,
    contractRow: String,
    contractSequence: String,
    onBack: () -> Unit,
    onRequestSettlement: (AssignerContractPR) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Found in the list this screen was opened from, by the two keys its route carries — the same
    // shape مبانی محاسباتی and جزئیات مبنا use. Null only after process death, when that list was
    // never fetched in this process.
    val contracts = state.list.items
    val contract = remember(contracts, contractRow, contractSequence) {
        contracts.findContract(contractRow, contractSequence)
    }

    AssignerContractDetailContent(
        contract = contract,
        onBack = onBack,
        onRequestSettlement = onRequestSettlement,
        modifier = modifier,
    )
}

/**
 * The پیمان a drill-down's route names, among the rows the list already holds. Shared by every
 * destination addressed by ردیف and sequence, so they cannot disagree about which پیمان that is.
 */
internal fun List<AssignerContractPR>.findContract(
    contractRow: String,
    contractSequence: String,
): AssignerContractPR? =
    firstOrNull { it.contractRow == contractRow && it.contractSequence == contractSequence }

@Composable
fun AssignerContractDetailContent(
    contract: AssignerContractPR?,
    onBack: () -> Unit,
    onRequestSettlement: (AssignerContractPR) -> Unit,
    modifier: Modifier = Modifier,
) {
    WorkshopScreenShell(
        title = stringResource(Res.string.assigner_contract_detail_title),
        onBack = onBack,
        modifier = modifier,
    ) {
        if (contract == null) {
            EmptyStateMessage(
                icon = vectorResource(Res.drawable.ic_tamin_assigner_contracts),
                title = stringResource(Res.string.assigner_empty_no_search_title),
                subtitle = stringResource(Res.string.assigner_empty_no_search_body),
                showIconTile = true,
            )
            return@WorkshopScreenShell
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                // A fixed three cards, not a data list — the scroll is for a short screen, not for
                // an unbounded number of rows.
                .verticalScroll(rememberScrollState())
                .padding(WorkshopDimens.listContentPadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            ContractInfoCard(contract = contract)
            PartyCard(
                title = stringResource(Res.string.assigner_group_assigner),
                party = contract.assigner,
            )
            PartyCard(
                title = stringResource(Res.string.assigner_group_contractor),
                party = contract.employer,
            )
        }

        // درخواست مفاصاحساب starts from the پیمان it is for — the old app's third action on the same
        // row. Pinned here rather than as a third card button, which does not fit three labels at
        // this width.
        WorkshopFormFooter(
            nextLabel = stringResource(Res.string.settlement_title),
            onNext = { onRequestSettlement(contract) },
        )
    }
}

/** «اطلاعات پیمان» — the four cells that describe the agreement itself. */
@Composable
private fun ContractInfoCard(
    contract: AssignerContractPR,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    WorkshopRecordCard(modifier = modifier) {
        GroupTitle(stringResource(Res.string.assigner_group_contract))
        DetailRow(
            label = stringResource(Res.string.assigner_field_contract_row),
            value = contract.card.rowLabel,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.assigner_field_contract_number),
            value = contract.contractNumber,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.assigner_contract_date),
            value = contract.contractDate,
            valueColor = colors.blueText,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.assigner_field_contract_subject),
            value = contract.contractSubject,
            // Prose, so it stays in the page's own direction rather than being forced LTR.
            numeric = false,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
    }
}

/**
 * One side of the پیمان — واگذارنده or پیمانکار, the same five cells either way.
 *
 * Takes the party and its heading rather than the whole contract, so a card redraws only when the
 * side it shows changes.
 */
@Composable
private fun PartyCard(
    title: String,
    party: AssignerPartyPR,
    modifier: Modifier = Modifier,
) {
    WorkshopRecordCard(modifier = modifier) {
        GroupTitle(title)
        DetailRow(
            label = stringResource(Res.string.assigner_field_workshop_name),
            value = party.workshopName,
            numeric = false,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.workshop_code),
            value = party.workshopCode,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.assigner_field_national_id),
            value = party.nationalId,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.assigner_field_branch),
            value = party.branchName,
            numeric = false,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.assigner_field_address),
            value = party.address,
            numeric = false,
            verticalPadding = WorkshopDimens.cellVerticalPadding,
        )
    }
}

/** The heading the design prints inside each card, above its first cell. */
@Composable
private fun GroupTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.Bold,
        color = LocalTaminColors.current.textPrimary,
        modifier = modifier.padding(bottom = Spacing.xxs),
    )
}

// ------------------------------------------------------------------------------- previews

private val PreviewContract = AssignerContractPR(
    card = ContractRowPR(
        workshopId = "9028212822",
        branchCode = "0210",
        name = "دبستان کارن ۲ مجتبی غلامیان",
        rowLabel = "۱",
        workshopCodeLabel = "۹۰۲۸۲۱۲۸۲۲",
        commitmentDate = "۱۴۰۱/۰۲/۱۰",
    ),
    contractRow = "1",
    contractSequence = "1",
    contractNumber = "۴۴۱۲۲",
    contractDate = "۱۴۰۱/۰۲/۱۰",
    contractSubject = "خدمات نظافت و پشتیبانی",
    assigner = AssignerPartyPR(
        workshopName = "آموزشگاه کامپیوتر توکلی",
        workshopCode = "۰۹۶۸۲۱۰۱۷۰",
        nationalId = "۴۲۳۱۰۹۸۸۷۶",
        branchName = "شعبهٔ ۱۰ تهران",
        address = "تهران، خیابان انقلاب، پلاک ۱۲",
    ),
    employer = AssignerPartyPR(
        workshopName = "دبستان کارن ۲ مجتبی غلامیان",
        workshopCode = "۹۰۲۸۲۱۲۸۲۲",
        nationalId = "۱۰۲۲۳۳۴۴۵۵",
        branchName = "شعبهٔ ۲ بجنورد",
        // The service sent no address; the mapper's dash is what reaches the cell.
        address = "—",
    ),
)

@PreviewRtlTheme
@Composable
private fun AssignerContractDetailPreview() = PreviewRtlThemeContent {
    AssignerContractDetailContent(contract = PreviewContract, onBack = {}, onRequestSettlement = {})
}

/** After process death, with nothing selected. */
@PreviewRtlTheme
@Composable
private fun AssignerContractDetailEmptyPreview() = PreviewRtlThemeContent {
    AssignerContractDetailContent(contract = null, onBack = {}, onRequestSettlement = {})
}
