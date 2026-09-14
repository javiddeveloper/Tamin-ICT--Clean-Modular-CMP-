package com.tamin.taminhamrah.feature.retirementPension.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.feature.retirementPension.ui.RetirementStage
import com.tamin.taminhamrah.model.pension.retirement.RetirementHistoryPR
import com.tamin.taminhamrah.model.pension.retirement.RetirementInsuredPR
import com.tamin.taminhamrah.ui.components.CopyIconButton
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminStageTimeline
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.retirement_pension_stage_authentication
import taminx.core.core_ui.retirement_pension_stage_branch_history_review
import taminx.core.core_ui.retirement_pension_stage_branch_quit_letter_review
import taminx.core.core_ui.retirement_pension_stage_branch_review_needed
import taminx.core.core_ui.retirement_pension_stage_current
import taminx.core.core_ui.retirement_pension_stage_issue_edict
import taminx.core.core_ui.retirement_pension_stage_submit_info
import taminx.core.core_ui.retirement_pension_stage_upload_identity_documents
import taminx.core.core_ui.retirement_pension_stage_upload_quit_letter
import taminx.core.core_ui.retirement_pension_track_code_label
import taminx.core.core_ui.retirement_pension_track_stages_title
import taminx.core.core_ui.retirement_pension_track_status_branch_review
import taminx.core.core_ui.retirement_pension_track_summary_title
import taminx.core.core_ui.retirement_pension_value_placeholder

/**
 * The tracking screen: the request's code, where the branch has it, and the recap of what was sent.
 *
 * The current stage comes from the status code the service reports — never a fixed index.
 */
@Composable
internal fun RetirementTrackContent(
    trackingCode: String?,
    statusCode: String?,
    insured: RetirementInsuredPR?,
    phoneNumber: String,
    address: String,
    workshopName: String,
    workshopCode: String,
    branchName: String,
    history: RetirementHistoryPR?,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val stages = retirementStageLabels()
    val currentIndex = remember(statusCode) { RetirementStage.indexOf(statusCode) }

    RetirementStepColumn(modifier = modifier) {
        RetirementCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    TaminText(
                        text = stringResource(Res.string.retirement_pension_track_code_label),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.textMuted,
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // The code is copied in ASCII: pasting Persian digits matches nothing.
                        if (!trackingCode.isNullOrBlank()) {
                            CopyIconButton(
                                value = trackingCode,
                                label = stringResource(Res.string.retirement_pension_track_code_label),
                            )
                        }
                        NumericText(
                            text = trackingCode?.toPersianDigits()
                                ?: stringResource(Res.string.retirement_pension_value_placeholder),
                            style = MaterialTheme.typography.titleMedium
                                .copy(fontWeight = FontWeight.Bold),
                            color = colors.textPrimary,
                        )
                    }
                }
                StatusPill(
                    text = stringResource(Res.string.retirement_pension_track_status_branch_review),
                    containerColor = colors.orangeBg,
                    contentColor = colors.orangeText,
                )
            }
        }

        RetirementCard {
            TaminText(
                text = stringResource(Res.string.retirement_pension_track_stages_title),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
                modifier = Modifier.padding(bottom = Spacing.smPlus),
            )
            TaminStageTimeline(
                stages = stages,
                currentIndex = currentIndex,
                currentLabel = stringResource(Res.string.retirement_pension_stage_current),
            )
        }

        RetirementSummaryCard(
            title = stringResource(Res.string.retirement_pension_track_summary_title),
            insured = insured,
            phoneNumber = phoneNumber,
            address = address,
            workshopName = workshopName,
            workshopCode = workshopCode,
            branchName = branchName,
            history = history,
        )
    }
}

/**
 * The stage names, in [RetirementStage] order.
 *
 * Built once per composition into an [ImmutableList] so the timeline's parameter is both a stable
 * type and the same instance between recompositions.
 */
@Composable
private fun retirementStageLabels(): ImmutableList<String> {
    val authentication = stringResource(Res.string.retirement_pension_stage_authentication)
    val submitInfo = stringResource(Res.string.retirement_pension_stage_submit_info)
    val identityDocuments =
        stringResource(Res.string.retirement_pension_stage_upload_identity_documents)
    val branchReview = stringResource(Res.string.retirement_pension_stage_branch_review_needed)
    val historyReview = stringResource(Res.string.retirement_pension_stage_branch_history_review)
    val quitLetter = stringResource(Res.string.retirement_pension_stage_upload_quit_letter)
    val quitLetterReview =
        stringResource(Res.string.retirement_pension_stage_branch_quit_letter_review)
    val edict = stringResource(Res.string.retirement_pension_stage_issue_edict)

    return remember(
        authentication, submitInfo, identityDocuments, branchReview,
        historyReview, quitLetter, quitLetterReview, edict,
    ) {
        listOf(
            authentication, submitInfo, identityDocuments, branchReview,
            historyReview, quitLetter, quitLetterReview, edict,
        ).toImmutableList()
    }
}
