package com.tamin.taminhamrah.feature.workshops.ui.assignerContracts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.components.AssignerStatusPill
import com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.contract.AssignerContractsIntent
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopCardButton
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopCardButtonTone
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopFormBanner
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopReviewGroup
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopReviewRow
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopScreenShell
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.model.workshop.AssignerContractPR
import com.tamin.taminhamrah.model.workshop.AssignerPartyPR
import com.tamin.taminhamrah.model.workshop.ContractRowPR
import com.tamin.taminhamrah.model.workshop.SettlementCertificatePR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.assigner_bases_title
import taminx.core.core_ui.assigner_certificate_issued
import taminx.core.core_ui.assigner_contract_date
import taminx.core.core_ui.assigner_contract_detail_title
import taminx.core.core_ui.assigner_contract_subtitle
import taminx.core.core_ui.assigner_empty_no_search_body
import taminx.core.core_ui.assigner_empty_no_search_title
import taminx.core.core_ui.assigner_field_address
import taminx.core.core_ui.assigner_field_branch
import taminx.core.core_ui.assigner_field_contract_number
import taminx.core.core_ui.assigner_field_contract_row
import taminx.core.core_ui.assigner_field_contract_sequence
import taminx.core.core_ui.assigner_field_contract_subject
import taminx.core.core_ui.assigner_field_national_id
import taminx.core.core_ui.assigner_field_workshop_name
import taminx.core.core_ui.assigner_group_assigner
import taminx.core.core_ui.assigner_group_contract
import taminx.core.core_ui.assigner_group_contract_preview
import taminx.core.core_ui.assigner_group_contractor
import taminx.core.core_ui.assigner_status_active_note
import taminx.core.core_ui.assigner_status_finished_note
import taminx.core.core_ui.ic_tamin_assigner_contracts
import taminx.core.core_ui.ic_tamin_computational_base
import taminx.core.core_ui.ic_tamin_document_lines
import taminx.core.core_ui.ic_tamin_user
import taminx.core.core_ui.ic_tamin_workshop
import taminx.core.core_ui.settlement_title
import taminx.core.core_ui.workshop_code

/**
 * جزئیات پیمان — where the پیمان stands, then three collapsible groups: the پیمان, your own کارگاه,
 * and the پیمانکار.
 *
 * No request for the groups: everything arrived with the list row, which is why tapping through is
 * instant. The پیمان is found in the list by the ردیف and sequence its route carries, so this screen
 * can never be composed against a stale selection; it is null only after process death, which the
 * screen says rather than drawing a page of dashes. A خاتمه‌یافته پیمان also asks for the certificate
 * it was settled under, and prints it in the status line.
 */
@Composable
fun AssignerContractDetailScreen(
    viewModel: AssignerContractsViewModel,
    contractRow: String,
    contractSequence: String,
    onBack: () -> Unit,
    onOpenBases: (AssignerContractPR) -> Unit,
    onRequestSettlement: (AssignerContractPR) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Found in the list this screen was opened from, by the two keys its route carries — the same
    // shape مبانی محاسباتی and جزئیات مبنا use.
    val contracts = state.list.items
    val contract = remember(contracts, contractRow, contractSequence) {
        contracts.findContract(contractRow, contractSequence)
    }
    LaunchedEffect(contract) {
        if (contract?.isFinished == true) {
            viewModel.sendIntent(AssignerContractsIntent.CertificateRequested(contract, announce = false))
        }
    }
    val lookup = state.certificate
    val certificate = remember(lookup, contract) { lookup?.takeIf { it.contract == contract }?.certificate }

    AssignerContractDetailContent(
        contract = contract,
        certificate = certificate,
        onBack = onBack,
        onOpenBases = onOpenBases,
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
    certificate: SettlementCertificatePR?,
    onBack: () -> Unit,
    onOpenBases: (AssignerContractPR) -> Unit,
    onRequestSettlement: (AssignerContractPR) -> Unit,
    modifier: Modifier = Modifier,
) {
    val subtitle = if (contract != null) {
        stringResource(Res.string.assigner_contract_subtitle, contract.card.name, contract.card.rowLabel)
    } else {
        null
    }
    WorkshopScreenShell(
        title = stringResource(Res.string.assigner_contract_detail_title),
        onBack = onBack,
        subtitle = subtitle,
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

        val colors = LocalTaminColors.current
        // The first group opens on arrival, as the design has it; the other two wait for a tap.
        var isContractOpen by rememberSaveable { mutableStateOf(true) }
        var isAssignerOpen by rememberSaveable { mutableStateOf(false) }
        var isContractorOpen by rememberSaveable { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                // A fixed handful of groups, not a data list — the scroll is for a short screen.
                .verticalScroll(rememberScrollState())
                .padding(WorkshopDimens.listContentPadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            WorkshopFormBanner(
                text = when {
                    !contract.isFinished -> stringResource(Res.string.assigner_status_active_note)
                    certificate != null -> stringResource(
                        Res.string.assigner_certificate_issued,
                        certificate.number,
                        certificate.date,
                    )

                    else -> stringResource(Res.string.assigner_status_finished_note)
                },
                leading = { AssignerStatusPill(isFinished = contract.isFinished) },
            )
            WorkshopReviewGroup(
                title = stringResource(Res.string.assigner_group_contract),
                rows = rememberContractRows(contract),
                isOpen = isContractOpen,
                onToggle = { isContractOpen = !isContractOpen },
                icon = vectorResource(Res.drawable.ic_tamin_document_lines),
                iconTint = colors.blueText,
                iconBackground = colors.blueBg,
                preview = stringResource(
                    Res.string.assigner_group_contract_preview,
                    contract.contractNumber,
                    contract.contractDate,
                ),
                showCount = false,
            )
            WorkshopReviewGroup(
                title = stringResource(Res.string.assigner_group_assigner),
                rows = rememberPartyRows(contract.assigner),
                isOpen = isAssignerOpen,
                onToggle = { isAssignerOpen = !isAssignerOpen },
                icon = vectorResource(Res.drawable.ic_tamin_workshop),
                iconTint = colors.tealText,
                iconBackground = colors.tealBg,
                preview = contract.assigner.workshopName,
                showCount = false,
            )
            WorkshopReviewGroup(
                title = stringResource(Res.string.assigner_group_contractor),
                rows = rememberPartyRows(contract.employer),
                isOpen = isContractorOpen,
                onToggle = { isContractorOpen = !isContractorOpen },
                icon = vectorResource(Res.drawable.ic_tamin_user),
                iconTint = colors.orangeText,
                iconBackground = colors.orangeBg,
                preview = contract.employer.workshopName,
                showCount = false,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(WorkshopDimens.cardButtonGap),
            ) {
                WorkshopCardButton(
                    text = stringResource(Res.string.assigner_bases_title),
                    tone = if (contract.canOpenBases) {
                        WorkshopCardButtonTone.TEAL
                    } else {
                        WorkshopCardButtonTone.DISABLED
                    },
                    onClick = { onOpenBases(contract) },
                    icon = vectorResource(Res.drawable.ic_tamin_computational_base),
                )
                // A خاتمه‌یافته پیمان takes no new request, so it is not offered one; its certificate
                // is in the status line above.
                if (!contract.isFinished) {
                    WorkshopCardButton(
                        text = stringResource(Res.string.settlement_title),
                        // Disabled only for a پیمان missing a key of the request id.
                        tone = if (contract.canRequestSettlement) {
                            WorkshopCardButtonTone.PRIMARY
                        } else {
                            WorkshopCardButtonTone.DISABLED
                        },
                        onClick = { onRequestSettlement(contract) },
                    )
                }
            }
        }
    }
}

/** «اطلاعات پیمان» — the five cells that describe the agreement itself. */
@Composable
private fun rememberContractRows(contract: AssignerContractPR): ImmutableList<WorkshopReviewRow> {
    val row = stringResource(Res.string.assigner_field_contract_row)
    val sequence = stringResource(Res.string.assigner_field_contract_sequence)
    val number = stringResource(Res.string.assigner_field_contract_number)
    val date = stringResource(Res.string.assigner_contract_date)
    val subject = stringResource(Res.string.assigner_field_contract_subject)
    return remember(contract, row, sequence, number, date, subject) {
        persistentListOf(
            WorkshopReviewRow(row, contract.card.rowLabel),
            WorkshopReviewRow(sequence, contract.sequenceLabel),
            WorkshopReviewRow(number, contract.contractNumber),
            WorkshopReviewRow(date, contract.contractDate),
            // Prose, so it stays in the page's own direction rather than being forced LTR.
            WorkshopReviewRow(subject, contract.contractSubject, isNumeric = false),
        )
    }
}

/** One side of the پیمان — واگذارنده or پیمانکار, the same five cells either way. */
@Composable
private fun rememberPartyRows(party: AssignerPartyPR): ImmutableList<WorkshopReviewRow> {
    val name = stringResource(Res.string.assigner_field_workshop_name)
    val code = stringResource(Res.string.workshop_code)
    val nationalId = stringResource(Res.string.assigner_field_national_id)
    val branch = stringResource(Res.string.assigner_field_branch)
    val address = stringResource(Res.string.assigner_field_address)
    return remember(party, name, code, nationalId, branch, address) {
        persistentListOf(
            WorkshopReviewRow(name, party.workshopName, isNumeric = false),
            WorkshopReviewRow(code, party.workshopCode),
            WorkshopReviewRow(nationalId, party.nationalId),
            WorkshopReviewRow(branch, party.branchName, isNumeric = false),
            WorkshopReviewRow(address, party.address, isNumeric = false),
        )
    }
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
    branchCode = "0210",
    contractNumber = "۴۴۱۲۲",
    sequenceLabel = "۱",
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
private fun AssignerContractDetailActivePreview() = PreviewRtlThemeContent {
    AssignerContractDetailContent(
        contract = PreviewContract,
        certificate = null,
        onBack = {},
        onOpenBases = {},
        onRequestSettlement = {},
    )
}

/** خاتمه‌یافته, with the certificate it was settled under in the status line. */
@PreviewRtlTheme
@Composable
private fun AssignerContractDetailCertificatePreview() = PreviewRtlThemeContent {
    AssignerContractDetailContent(
        contract = PreviewContract.copy(isFinished = true),
        certificate = SettlementCertificatePR(number = "۳۸-۷۷۱۲۴۰۵", date = "۱۴۰۲/۱۱/۰۳"),
        onBack = {},
        onOpenBases = {},
        onRequestSettlement = {},
    )
}

/** After process death, with nothing selected. */
@PreviewRtlTheme
@Composable
private fun AssignerContractDetailEmptyPreview() = PreviewRtlThemeContent {
    AssignerContractDetailContent(
        contract = null,
        certificate = null,
        onBack = {},
        onOpenBases = {},
        onRequestSettlement = {},
    )
}
