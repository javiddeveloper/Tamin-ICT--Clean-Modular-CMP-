package com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.sms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.workshops.ui.components.DashedEmptyStateCard
import com.tamin.taminhamrah.feature.workshops.ui.components.WorkshopListScaffold
import com.tamin.taminhamrah.feature.workshops.ui.components.colors
import com.tamin.taminhamrah.feature.workshops.ui.components.tint
import com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.components.ObjectionSummaryHeader
import com.tamin.taminhamrah.feature.workshops.ui.objectionStatus.components.labelRes
import com.tamin.taminhamrah.model.workshop.SmsMessagePR
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionStatus
import com.tamin.taminhamrah.model.workshop.WorkShopObjectionType
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.objection_sms_empty
import taminx.core.core_ui.objection_sms_order_oldest_first
import taminx.core.core_ui.objection_sms_section_title
import taminx.core.core_ui.objection_sms_title
import taminx.core.core_ui.objection_status_action_document

@Composable
fun ObjectionSmsScreen(
    seqNo: Long,
    debitNumber: String,
    objectionType: WorkShopObjectionType,
    objectionStatus: WorkShopObjectionStatus,
    onBack: () -> Unit,
    onOpenDocument: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ObjectionSmsViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(seqNo) {
        viewModel.sendIntent(
            ObjectionSmsIntent.Open(seqNo, debitNumber, objectionType, objectionStatus)
        )
    }

    ObjectionSmsContent(
        state = state,
        onBack = onBack,
        onOpenDocument = onOpenDocument,
        onLoadMore = { viewModel.sendIntent(ObjectionSmsIntent.LoadMore) },
        modifier = modifier,
    )
}

@Composable
fun ObjectionSmsContent(
    state: ObjectionSmsUiState,
    onBack: () -> Unit,
    onOpenDocument: () -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Column(modifier = modifier.fillMaxWidth().background(colors.bgPage)) {
        TaminTopAppBar(
            title = stringResource(Res.string.objection_sms_title),
            background = Brush.horizontalGradient(colors.profileGradientStops),
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = null,
                    onClick = onBack,
                    bordered = true,
                )
            },
        ) {
            ObjectionSummaryHeader(
                status = state.objectionStatus,
                objectionType = state.objectionType,
                objectionNumber = state.seqNo.toString().toPersianDigits(),
                onNavigateToSibling = onOpenDocument,
                siblingIcon = Icons.Default.Description,
                siblingContentDescription = stringResource(Res.string.objection_status_action_document),
            )
        }

        WorkshopListScaffold(
            state = state.list,
            onLoadMore = onLoadMore,
            emptyContent = { ObjectionSmsEmptyState() },
            key = { it.id ?: it.hashCode() },
            header = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(Res.string.objection_sms_section_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                    )
                    Text(
                        text = stringResource(Res.string.objection_sms_order_oldest_first),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted,
                    )
                }
            },
            row = { sms, itemModifier ->
                SmsTimelineItem(
                    index = state.list.items.indexOf(sms) + 1,
                    sms = sms,
                    modifier = itemModifier,
                )
            },
        )
    }
}

/** «پیامکی برای این اعتراض ارسال نشده است» — no per-message identity needed, just the one line. */
@Composable
private fun ObjectionSmsEmptyState(modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    DashedEmptyStateCard(modifier = modifier) {
        Text(
            text = stringResource(Res.string.objection_sms_empty),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
        )
    }
}

/** One پیامک: a numbered badge and a connecting line, next to its status pill and body text. */
@Composable
private fun SmsTimelineItem(index: Int, sms: SmsMessagePR, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    val (pillBackground, pillForeground) = sms.status.tint.colors()
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(TimelineBadgeSize)
                    .background(pillBackground, CircleShape)
                    .border(1.dp, pillForeground.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = index.toString().toPersianDigits(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = pillForeground,
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .taminSurface(CornerRadius.lg)
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Row(
                modifier = Modifier
                    .background(pillBackground, RoundedCornerShape(CornerRadius.chip))
                    .padding(horizontal = Spacing.sm, vertical = Spacing.xxs),
            ) {
                Text(
                    text = stringResource(sms.status.labelRes),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = pillForeground,
                )
            }
            Text(
                text = sms.description,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textPrimary,
            )
        }
    }
}

private val TimelineBadgeSize = 26.dp

@PreviewRtlTheme
@Composable
private fun ObjectionSmsScreenPreview() {
    PreviewRtlThemeContent {
        ObjectionSmsContent(
            state = ObjectionSmsUiState(
                seqNo = 1403008720,
                debitNumber = "۱۴۰۲/۴۴۱۹۰",
                objectionType = WorkShopObjectionType.ESTIMATE,
                objectionStatus = WorkShopObjectionStatus.BOARD_REVIEW,
                list = com.tamin.taminhamrah.feature.workshops.ui.model.PagedListState(
                    items = persistentListOf(
                        SmsMessagePR(
                            id = 1,
                            description = "اعتراض شما به شمارهٔ ۱۴۰۳۰۰۸۷۲ در شعبهٔ ۷ تهران ثبت شد.",
                            status = WorkShopObjectionStatus.SUBMITTED,
                        ),
                        SmsMessagePR(
                            id = 2,
                            description = "پروندهٔ بدهی جهت بازنگری محاسبات به واحد درآمد ارجاع شد.",
                            status = WorkShopObjectionStatus.CALCULATION_REVIEW,
                        ),
                    ),
                ),
            ),
            onBack = {},
            onOpenDocument = {},
            onLoadMore = {},
        )
    }
}
