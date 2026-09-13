package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.ColorFilter
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
import taminx.core.core_ui.ic_info
import taminx.core.core_ui.ic_tamin_alert_circle
import taminx.core.core_ui.ic_tamin_check_circle
import taminx.core.core_ui.ic_tamin_check_label
import taminx.core.core_ui.ic_tamin_cross
import taminx.core.core_ui.ic_tamin_edit

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
            requests.forEach { request ->
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
            taminColors.dangerBorder,
            taminColors.dangerText,
            taminColors.dangerText,
            taminColors.dangerBorder,
            taminColors.dangerText,
            Res.drawable.ic_error,
        )

        UserRequestWorkflowStatus.DOCUMENT_DEFECT -> Sextuple(
            taminColors.dangerBorder,
            taminColors.dangerText,
            taminColors.dangerText,
            taminColors.dangerBorder,
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
            Res.drawable.ic_info,
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
                taminColors.dangerBorder,
                taminColors.dangerText,
                taminColors.dangerText,
                taminColors.dangerBorder,
                taminColors.dangerText,
                Res.drawable.ic_tamin_cross,
            )
            UserRequestStatusTone.NEUTRAL -> Sextuple(
                taminColors.blueBg,
                taminColors.blueText,
                taminColors.blueText,
                taminColors.blueBg,
                taminColors.blueText,
                Res.drawable.ic_tamin_check_label,
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
                    Image(
                        imageVector = vectorResource(iconRes),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(color = iconTint),
                        modifier = Modifier.size(22.dp),
                    )
                }

                Spacer(modifier = Modifier.width(Spacing.md))

                // Title & Subtitle (RefCode • Creation Date)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                ) {
                    AutoResizeText(
                        maxLines = 1,
                        text = request.title.ifBlank { request.requestTypeTitle },
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(Spacing.xxs))
                    AutoResizeText(
                        maxLines = 1,
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
    // 2: PRE_PROCESSING
    UserRequestPR(
        id = 1L,
        refCode = "۱۰۴۸۴۰۱۸۴۱",
        title = "کمک هزینه ازدواج",
        comment = "",
        creationTime = "۱۴۰۵/۰۳/۱۲",
        createByName = "",
        statusDesc = "پیش‌پردازش",
        statusCode = "2",
        requestTypeId = 4L,
        requestTypeTitle = "کمک هزینه ازدواج",
    ),
    // 6: SHOW_ERRORS
    UserRequestPR(
        id = 2L,
        refCode = "۱۰۴۸۴۰۱۸۴۲",
        title = "غرامت دستمزد ایام بیماری",
        comment = "",
        creationTime = "۱۴۰۵/۰۳/۱۱",
        createByName = "",
        statusDesc = "نمایش خطا",
        statusCode = "6",
        requestTypeId = 1L,
        requestTypeTitle = "غرامت دستمزد ایام بیماری",
    ),
    // 9: BRANCH_DELIVERED
    UserRequestPR(
        id = 3L,
        refCode = "۱۰۴۸۴۰۱۸۴۳",
        title = "درخواست بازرسی",
        comment = "",
        creationTime = "۱۴۰۵/۰۳/۱۰",
        createByName = "",
        statusDesc = "تحویل به شعبه",
        statusCode = "9",
        requestTypeId = 5L,
        requestTypeTitle = "درخواست بازرسی",
    ),
    // 14: AWAITING_COMPLETION
    UserRequestPR(
        id = 4L,
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
    // 16: PROCESSING_COMPLETE
    UserRequestPR(
        id = 5L,
        refCode = "۱۰۴۸۴۰۱۸۴۵",
        title = "صدور دفترچه",
        comment = "",
        creationTime = "۱۴۰۵/۰۳/۰۹",
        createByName = "",
        statusDesc = "پردازش تکمیل شد",
        statusCode = "16",
        requestTypeId = 6L,
        requestTypeTitle = "صدور دفترچه",
    ),
    // 18: FINAL_APPROVED
    UserRequestPR(
        id = 6L,
        refCode = "۱۰۴۸۴۰۱۸۴۶",
        title = "تعهدات کوتاه مدت",
        comment = "",
        creationTime = "۱۴۰۵/۰۳/۰۸",
        createByName = "",
        statusDesc = "تایید نهایی",
        statusCode = "18",
        requestTypeId = 7L,
        requestTypeTitle = "تعهدات کوتاه مدت",
    ),
    // 19: DISAPPROVED
    UserRequestPR(
        id = 7L,
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
    // 21: DOCUMENT_DEFECT
    UserRequestPR(
        id = 8L,
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
    // 2602: ARTICLE_SIXTEEN_APPROVED
    UserRequestPR(
        id = 9L,
        refCode = "۱۰۴۸۴۰۱۸۵۰",
        title = "کمک هزینه مراسم ترحیم",
        comment = "",
        creationTime = "۱۴۰۵/۰۳/۰۷",
        createByName = "",
        statusDesc = "تایید ماده ۱۶",
        statusCode = "2602",
        requestTypeId = 8L,
        requestTypeTitle = "کمک هزینه مراسم ترحیم",
    ),
    // Unknown - NEUTRAL tone
    UserRequestPR(
        id = 10L,
        refCode = "۱۰۴۸۴۰۱۸۵۱",
        title = "وضعیت نامشخص - خنثی",
        comment = "",
        creationTime = "۱۴۰۵/۰۳/۰۶",
        createByName = "",
        statusDesc = "در حال بررسی اولیه",
        statusCode = "9999",
        requestTypeId = 9L,
        requestTypeTitle = "سایر درخواست‌ها",
        statusTone = UserRequestStatusTone.NEUTRAL,
    ),
    // Unknown - APPROVED tone
    UserRequestPR(
        id = 11L,
        refCode = "۱۰۴۸۴۰۱۸۵۲",
        title = "وضعیت نامشخص - تایید",
        comment = "",
        creationTime = "۱۴۰۵/۰۳/۰۵",
        createByName = "",
        statusDesc = "تایید استثنا",
        statusCode = "9998",
        requestTypeId = 9L,
        requestTypeTitle = "سایر درخواست‌ها",
        statusTone = UserRequestStatusTone.APPROVED,
    ),
    // Unknown - ERROR tone
    UserRequestPR(
        id = 12L,
        refCode = "۱۰۴۸۴۰۱۸۵۳",
        title = "وضعیت نامشخص - خطا",
        comment = "",
        creationTime = "۱۴۰۵/۰۳/۰۴",
        createByName = "",
        statusDesc = "رد استثنا",
        statusCode = "9997",
        requestTypeId = 9L,
        requestTypeTitle = "سایر درخواست‌ها",
        statusTone = UserRequestStatusTone.ERROR,
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
