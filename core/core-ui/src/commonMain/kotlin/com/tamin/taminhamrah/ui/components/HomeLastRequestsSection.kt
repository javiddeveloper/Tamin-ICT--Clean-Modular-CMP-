package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.userRequest.UserRequestPR
import com.tamin.taminhamrah.model.userRequest.UserRequestStatusTone
import com.tamin.taminhamrah.model.userRequest.UserRequestWorkflowStatus
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.home_see_all
import taminx.core.core_ui.home_section_last_requests
import taminx.core.core_ui.ic_tamin_alert_circle
import taminx.core.core_ui.ic_tamin_check_circle
import taminx.core.core_ui.ic_tamin_cross
import taminx.core.core_ui.ic_tamin_edit
import taminx.core.core_ui.ic_tamin_search

@Composable
fun HomeLastRequestsSection(
    requests: List<UserRequestPR>,
    onSeeAllClick: () -> Unit,
    onRequestClick: (UserRequestPR) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (requests.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(
            title = stringResource(Res.string.home_section_last_requests),
            trailing = stringResource(Res.string.home_see_all),
            onTrailingClick = onSeeAllClick,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.xs),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            requests.take(3).forEach { request ->
                LastRequestItemCard(
                    request = request,
                    onClick = { onRequestClick(request) },
                )
            }
        }
    }
}

@Composable
private fun LastRequestItemCard(
    request: UserRequestPR,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val colorScheme = MaterialTheme.colorScheme

    val workflowStatus = UserRequestWorkflowStatus.fromCode(request.statusCode)

    val (badgeBg, badgeTextColor, accentBarColor, iconBg, iconTint, iconRes) = when (workflowStatus) {
        UserRequestWorkflowStatus.PROCESSING_COMPLETE,
        UserRequestWorkflowStatus.FINAL_APPROVED,
        UserRequestWorkflowStatus.ARTICLE_SIXTEEN_APPROVED -> Sextuple(
            taminColors.greenBg,
            taminColors.greenText,
            taminColors.greenText,
            taminColors.greenBg,
            taminColors.greenText,
            Res.drawable.ic_tamin_check_circle,
        )

        UserRequestWorkflowStatus.SHOW_ERRORS,
        UserRequestWorkflowStatus.DISAPPROVED -> Sextuple(
            taminColors.dangerBg,
            taminColors.dangerText,
            taminColors.dangerText,
            taminColors.dangerBg,
            taminColors.dangerText,
            Res.drawable.ic_tamin_cross,
        )

        UserRequestWorkflowStatus.DOCUMENT_DEFECT -> Sextuple(
            taminColors.dangerBg,
            taminColors.dangerText,
            taminColors.dangerText,
            taminColors.dangerBg,
            taminColors.dangerText,
            Res.drawable.ic_tamin_alert_circle,
        )

        UserRequestWorkflowStatus.AWAITING_COMPLETION -> Sextuple(
            taminColors.blueBg,
            taminColors.blueText,
            taminColors.blueText,
            taminColors.blueBg,
            taminColors.blueText,
            Res.drawable.ic_tamin_edit,
        )

        UserRequestWorkflowStatus.PRE_PROCESSING,
        UserRequestWorkflowStatus.BRANCH_DELIVERED -> Sextuple(
            taminColors.blueBg,
            taminColors.blueText,
            taminColors.blueText,
            taminColors.blueBg,
            taminColors.blueText,
            Res.drawable.ic_tamin_search,
        )

        null -> when (request.statusTone) {
            UserRequestStatusTone.APPROVED -> Sextuple(
                taminColors.greenBg,
                taminColors.greenText,
                taminColors.greenText,
                taminColors.greenBg,
                taminColors.greenText,
                Res.drawable.ic_tamin_check_circle,
            )
            UserRequestStatusTone.ERROR -> Sextuple(
                taminColors.dangerBg,
                taminColors.dangerText,
                taminColors.dangerText,
                taminColors.dangerBg,
                taminColors.dangerText,
                Res.drawable.ic_tamin_cross,
            )
            UserRequestStatusTone.NEUTRAL -> Sextuple(
                taminColors.blueBg,
                taminColors.blueText,
                taminColors.blueText,
                taminColors.blueBg,
                taminColors.blueText,
                Res.drawable.ic_tamin_search,
            )
        }
    }

    val subtitleText = remember(request.refCode, request.creationTime) {
        if (request.refCode.isNotBlank()) {
            "${request.refCode} • ${request.creationTime}"
        } else {
            request.creationTime
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(taminColors.bgSurface)
            .border(
                width = 1.dp,
                color = taminColors.border,
                shape = RoundedCornerShape(CornerRadius.lg),
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        ) {
            // Right vertical accent strip
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(accentBarColor)
            )

            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Spacing.md, vertical = Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Icon container with soft background
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(CornerRadius.lg))
                        .background(iconBg),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = vectorResource(iconRes),
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp),
                    )
                }

                Spacer(modifier = Modifier.width(Spacing.md))

                // Title & Subtitle (RefCode • Creation Date)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = request.title.ifBlank { request.requestTypeTitle },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(Spacing.xxs))
                    Text(
                        text = subtitleText,
                        style = MaterialTheme.typography.bodySmall,
                        color = taminColors.textMuted,
                    )
                }

                Spacer(modifier = Modifier.width(Spacing.sm))

                // Status Badge
                if (request.statusDesc.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(badgeBg)
                            .padding(horizontal = Spacing.smPlus, vertical = Spacing.xs),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = request.statusDesc,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = badgeTextColor,
                        )
                    }
                }
            }
        }
    }
}

private val samplePreviewRequests = listOf(
    UserRequestPR(
        id = 1L,
        refCode = "۱۰۴۸۴۰۱۸۴۹",
        title = "غرامت دستمزد ایام بیماری",
        comment = "",
        creationTime = "۱۴۰۵/۰۳/۱۱",
        createByName = "",
        statusDesc = "نقص مدارک ارسالی",
        statusCode = "21",
        requestTypeId = 1L,
        requestTypeTitle = "غرامت دستمزد ایام بیماری",
    ),
    UserRequestPR(
        id = 2L,
        refCode = "۱۰۴۸۳۹۷۲۱۵",
        title = "درخواست بررسی مدارک ارسالی",
        comment = "",
        creationTime = "۱۴۰۵/۰۲/۲۸",
        createByName = "",
        statusDesc = "عدم تأیید",
        statusCode = "19",
        requestTypeId = 2L,
        requestTypeTitle = "درخواست بررسی مدارک ارسالی",
    ),
    UserRequestPR(
        id = 3L,
        refCode = "۱۰۴۸۳۸۴۰۰۲",
        title = "غرامت دستمزد ایام بارداری",
        comment = "",
        creationTime = "۱۴۰۵/۰۲/۰۵",
        createByName = "",
        statusDesc = "در انتظار تکمیل اطلاعات",
        statusCode = "14",
        requestTypeId = 3L,
        requestTypeTitle = "غرامت دستمزد ایام بارداری",
    ),
)

@PreviewRtlTheme
@Composable
private fun HomeLastRequestsSectionPreview() {
    PreviewRtlThemeContent {
        HomeLastRequestsSection(
            requests = samplePreviewRequests,
            onSeeAllClick = {},
            onRequestClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun HomeLastRequestsSectionPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        HomeLastRequestsSection(
            requests = samplePreviewRequests,
            onSeeAllClick = {},
            onRequestClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

private data class Sextuple<A, B, C, D, E, F>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E,
    val sixth: F,
)
