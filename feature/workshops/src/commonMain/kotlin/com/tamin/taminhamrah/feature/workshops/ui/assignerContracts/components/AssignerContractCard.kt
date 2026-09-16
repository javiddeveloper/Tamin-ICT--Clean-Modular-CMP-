package com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopCardButton
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopCardButtonTone
import com.tamin.taminhamrah.feature.workshops.ui.components.dashedTopRule
import com.tamin.taminhamrah.feature.workshops.ui.contractRows.components.ContractRowTile
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.model.workshop.AssignerContractPR
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.assigner_action_bases
import taminx.core.core_ui.assigner_action_certificate
import taminx.core.core_ui.assigner_action_detail
import taminx.core.core_ui.assigner_action_settlement
import taminx.core.core_ui.assigner_card_subtitle
import taminx.core.core_ui.assigner_contract_date
import taminx.core.core_ui.assigner_field_branch
import taminx.core.core_ui.assigner_field_contract_number
import taminx.core.core_ui.assigner_field_contract_row
import taminx.core.core_ui.assigner_tab_active
import taminx.core.core_ui.assigner_tab_finished
import taminx.core.core_ui.ic_tamin_check
import taminx.core.core_ui.ic_tamin_computational_base
import taminx.core.core_ui.ic_tamin_document_lines
import taminx.core.core_ui.ic_tamin_objection_document
import taminx.core.core_ui.workshop_code

/**
 * One پیمان on واگذارندگان, as the design draws it: the پیمانکار and the work, whether the پیمان is
 * still running, five tiles, and the three actions under a dashed rule.
 *
 * Its own card rather than ردیف‌های پیمان's: that one is a workshop and its commitment date, this is
 * a پیمان with a status and three destinations. The tiles are that card's own, so the two still read
 * alike.
 *
 * The third action follows the status: a running پیمان takes a درخواست مفاصاحساب, a finished one
 * answers with the certificate it was settled under.
 */
@Composable
fun AssignerContractCard(
    contract: AssignerContractPR,
    onOpenDetail: () -> Unit,
    onOpenBases: () -> Unit,
    onRequestSettlement: () -> Unit,
    onShowCertificate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val card = contract.card
    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(WorkshopDimens.cardCorner)
            .padding(
                horizontal = WorkshopDimens.contractRowCardHorizontalPadding,
                vertical = WorkshopDimens.contractRowCardVerticalPadding,
            ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
            ) {
                Text(
                    text = card.name,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                )
                Text(
                    text = stringResource(Res.string.assigner_card_subtitle, contract.contractSubject),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted,
                )
            }
            AssignerStatusPill(isFinished = contract.isFinished)
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(WorkshopDimens.contractRowTileGap),
        ) {
            ContractRowTile(
                label = stringResource(Res.string.assigner_field_contract_row),
                value = card.rowLabel,
                modifier = Modifier.weight(1f),
            )
            ContractRowTile(
                label = stringResource(Res.string.assigner_field_contract_number),
                value = contract.contractNumber,
                modifier = Modifier.weight(1f),
            )
            ContractRowTile(
                label = stringResource(Res.string.assigner_contract_date),
                value = contract.contractDate,
                valueColor = colors.blueText,
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = WorkshopDimens.contractRowTileGap),
            horizontalArrangement = Arrangement.spacedBy(WorkshopDimens.contractRowTileGap),
        ) {
            ContractRowTile(
                label = stringResource(Res.string.workshop_code),
                value = card.workshopCodeLabel,
                modifier = Modifier.weight(WORKSHOP_TILE_WEIGHT),
            )
            ContractRowTile(
                label = stringResource(Res.string.assigner_field_branch),
                value = contract.employer.branchName,
                numeric = false,
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.sm)
                .dashedTopRule(colors.divider)
                .padding(top = Spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(WorkshopDimens.cardButtonGap),
        ) {
            WorkshopCardButton(
                text = stringResource(Res.string.assigner_action_detail),
                tone = WorkshopCardButtonTone.NEUTRAL,
                onClick = onOpenDetail,
                icon = vectorResource(Res.drawable.ic_tamin_document_lines),
            )
            WorkshopCardButton(
                text = stringResource(Res.string.assigner_action_bases),
                // A پیمان missing any of the four keys cannot address its own bases, so the button is
                // plainly unavailable rather than opening another contract's records.
                tone = if (contract.canOpenBases) WorkshopCardButtonTone.NEUTRAL else WorkshopCardButtonTone.DISABLED,
                onClick = onOpenBases,
                icon = vectorResource(Res.drawable.ic_tamin_computational_base),
            )
            if (contract.isFinished) {
                WorkshopCardButton(
                    text = stringResource(Res.string.assigner_action_certificate),
                    tone = WorkshopCardButtonTone.SUCCESS_SOFT,
                    onClick = onShowCertificate,
                    icon = vectorResource(Res.drawable.ic_tamin_check),
                )
            } else {
                WorkshopCardButton(
                    text = stringResource(Res.string.assigner_action_settlement),
                    // Filed under an id built from the same four keys, so a row missing one offers it
                    // disabled rather than filing under the wrong id.
                    tone = if (contract.canRequestSettlement) WorkshopCardButtonTone.INFO else WorkshopCardButtonTone.DISABLED,
                    onClick = onRequestSettlement,
                    icon = vectorResource(Res.drawable.ic_tamin_objection_document),
                )
            }
        }
    }
}

/** جاری in green, خاتمه‌یافته in grey — the pill the card and جزئیات پیمان both wear. */
@Composable
internal fun AssignerStatusPill(isFinished: Boolean, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    StatusPill(
        text = stringResource(if (isFinished) Res.string.assigner_tab_finished else Res.string.assigner_tab_active),
        containerColor = if (isFinished) colors.bgPage else colors.greenBg,
        contentColor = if (isFinished) colors.textSecondary else colors.greenText,
        borderColor = if (isFinished) colors.border else colors.greenBorder,
        fontWeight = FontWeight.SemiBold,
        verticalPadding = WorkshopDimens.contractRowBadgeVerticalPadding,
        modifier = modifier,
    )
}

/** `grid-template-columns:1.25fr 1fr` — the workshop code is the wider tile of the second row. */
private const val WORKSHOP_TILE_WEIGHT = 1.25f
