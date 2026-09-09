package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import taminx.core.core_ui.ic_error
import taminx.core.core_ui.ic_tamin_check_circle
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

    val (badgeBg, badgeTextColor, iconBgBrush, iconRes) = when (workflowStatus) {
        UserRequestWorkflowStatus.PROCESSING_COMPLETE,
        UserRequestWorkflowStatus.FINAL_APPROVED,
        UserRequestWorkflowStatus.ARTICLE_SIXTEEN_APPROVED -> Quadruple(
            taminColors.greenBg,
            taminColors.greenText,
            Brush.verticalGradient(listOf(Color(0xFF27AE60), Color(0xFF1E824C))),
            Res.drawable.ic_tamin_check_circle,
        )

        UserRequestWorkflowStatus.SHOW_ERRORS,
        UserRequestWorkflowStatus.DISAPPROVED -> Quadruple(
            taminColors.dangerBg,
            taminColors.dangerText,
            Brush.verticalGradient(listOf(Color(0xFFEB5757), Color(0xFFC0392B))),
            Res.drawable.ic_error,
        )

        UserRequestWorkflowStatus.DOCUMENT_DEFECT,
        UserRequestWorkflowStatus.AWAITING_COMPLETION -> Quadruple(
            taminColors.orangeBg,
            taminColors.orangeText,
            Brush.verticalGradient(listOf(Color(0xFFF2994A), Color(0xFFD35400))),
            Res.drawable.ic_tamin_search,
        )

        UserRequestWorkflowStatus.PRE_PROCESSING,
        UserRequestWorkflowStatus.BRANCH_DELIVERED -> Quadruple(
            taminColors.blueBg,
            taminColors.blueText,
            Brush.verticalGradient(listOf(Color(0xFF2F80ED), Color(0xFF1B4F72))),
            Res.drawable.ic_tamin_search,
        )

        null -> when (request.statusTone) {
            UserRequestStatusTone.APPROVED -> Quadruple(
                taminColors.greenBg,
                taminColors.greenText,
                Brush.verticalGradient(listOf(Color(0xFF27AE60), Color(0xFF1E824C))),
                Res.drawable.ic_tamin_check_circle,
            )
            UserRequestStatusTone.ERROR -> Quadruple(
                taminColors.dangerBg,
                taminColors.dangerText,
                Brush.verticalGradient(listOf(Color(0xFFEB5757), Color(0xFFC0392B))),
                Res.drawable.ic_error,
            )
            UserRequestStatusTone.NEUTRAL -> Quadruple(
                taminColors.blueBg,
                taminColors.blueText,
                Brush.verticalGradient(listOf(Color(0xFF2F80ED), Color(0xFF1B4F72))),
                Res.drawable.ic_tamin_search,
            )
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
            )
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Icon container with gradient
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(CornerRadius.xl))
                    .background(iconBgBrush),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = vectorResource(iconRes),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp),
                )
            }

            Spacer(modifier = Modifier.width(Spacing.md))

            // Title & Creation Date
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
                    text = request.creationTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = taminColors.textMuted,
                )
            }

            Spacer(modifier = Modifier.width(Spacing.sm))

            // Status Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(badgeBg)
                    .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
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

private val samplePreviewRequests = listOf(
    UserRequestPR(
        id = 1L,
        refCode = "1048384001",
        title = "تأییدیه پزشکی",
        comment = "",
        creationTime = "۱۴۰۴/۰۳/۲۸",
        createByName = "",
        statusDesc = "تأیید شد",
        statusCode = "18",
        requestTypeId = 1L,
        requestTypeTitle = "تأییدیه پزشکی",
    ),
    UserRequestPR(
        id = 2L,
        refCode = "1048384002",
        title = "انعقاد قرارداد بیمه اختیاری",
        comment = "",
        creationTime = "۱۴۰۵/۰۴/۰۲",
        createByName = "",
        statusDesc = "ویرایش قرارداد",
        statusCode = "21",
        requestTypeId = 2L,
        requestTypeTitle = "انعقاد قرارداد بیمه اختیاری",
    ),
    UserRequestPR(
        id = 3L,
        refCode = "1048384456",
        title = "درخواست بیمه کارگران ساختمانی",
        comment = "",
        creationTime = "۱۴۰۳/۱۲/۱۸",
        createByName = "",
        statusDesc = "عدم تایید-فاقد شرایط",
        statusCode = "19",
        requestTypeId = 3L,
        requestTypeTitle = "درخواست بیمه کارگران ساختمانی",
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

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
