package com.tamin.taminhamrah.feature.userRequest.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.userRequest.UserRequestPR
import com.tamin.taminhamrah.model.userRequest.UserRequestStatusTone
import com.tamin.taminhamrah.model.userRequest.UserRequestTypeIds
import com.tamin.taminhamrah.model.userRequest.UserRequestViewCapability
import com.tamin.taminhamrah.ui.components.IconPosition
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius as TaminCornerRadius
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import taminx.feature.userrequest.generated.resources.Res as UserRequestRes
import taminx.feature.userrequest.generated.resources.user_request_errors_btn
import taminx.feature.userrequest.generated.resources.user_request_follow_up_objection
import taminx.feature.userrequest.generated.resources.user_request_guide_btn
import taminx.feature.userrequest.generated.resources.user_request_tracking_code_prefix
import taminx.feature.userrequest.generated.resources.user_request_view_request

@Composable
fun UserRequestCard(
    request: UserRequestPR,
    onViewDetails: (UserRequestPR) -> Unit,
    onOpenGuide: (UserRequestPR) -> Unit,
    onOpenErrors: (UserRequestPR) -> Unit,
    onCopyTrackingCode: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val clipboardManager = LocalClipboardManager.current
    val taminColors = LocalTaminColors.current
    val colorScheme = MaterialTheme.colorScheme

    val statusBg = when (request.statusTone) {
        UserRequestStatusTone.ERROR -> taminColors.dangerBorder
        UserRequestStatusTone.APPROVED -> taminColors.greenBg
        UserRequestStatusTone.NEUTRAL -> taminColors.divider
    }
    val statusTextColor = when (request.statusTone) {
        UserRequestStatusTone.ERROR -> colorScheme.error
        UserRequestStatusTone.APPROVED -> taminColors.greenText
        UserRequestStatusTone.NEUTRAL -> taminColors.textSecondary
    }
    val accentBarColor = when (request.statusTone) {
        UserRequestStatusTone.ERROR -> colorScheme.error
        UserRequestStatusTone.APPROVED -> taminColors.greenText
        UserRequestStatusTone.NEUTRAL -> colorScheme.primary
    }

    val showViewButton = request.viewCapability != UserRequestViewCapability.NONE
    val viewButtonLabel = if (request.viewCapability == UserRequestViewCapability.FOLLOW_UP_OBJECTION) {
        stringResource(UserRequestRes.string.user_request_follow_up_objection)
    } else {
        stringResource(UserRequestRes.string.user_request_view_request)
    }
    val showErrorButton = request.showErrorsAction

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs),
        shape = RoundedCornerShape(TaminCornerRadius.cardCompact),
        colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.xs)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {


            // Right accent strip
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(accentBarColor)
            )
            // Main content column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(Spacing.page),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                // Header Row: Category Tag (Top Right) & Date (Top Left)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    if (request.statusDesc.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(statusBg)
                                .padding(horizontal = Spacing.smPlus, vertical = Spacing.badgeVertical)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(statusTextColor)
                                )
                                TaminText(
                                    text = request.statusDesc,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = statusTextColor
                                )
                            }
                        }
                    }

                    TaminText(
                        text = request.creationTime,
                        style = MaterialTheme.typography.labelMedium,
                        color = taminColors.textMuted
                    )
                }

                // Title & Status Badge Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TaminText(
                        text = request.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = taminColors.textPrimary
                    )

                }

                // Tracking Code Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            clipboardManager.setText(AnnotatedString(request.refCode))
                            onCopyTrackingCode(request.refCode)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Description,
                            contentDescription = null,
                            tint = taminColors.textMuted,
                            modifier = Modifier.size(IconSize.small)
                        )
                        TaminText(
                            text = stringResource(UserRequestRes.string.user_request_tracking_code_prefix).removeSuffix(":").trim(),
                            style = MaterialTheme.typography.labelMedium,
                            color = taminColors.textSecondary
                        )
                    }

                    Canvas(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = Spacing.sm)
                            .height(Thickness.border)
                    ) {
                        drawLine(
                            color = taminColors.outerBorder,
                            start = Offset(0f, size.height / 2),
                            end = Offset(size.width, size.height / 2),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                        )
                    }

                    val codeBlue = colorScheme.primary
                    Box(
                        modifier = Modifier
                            .drawWithContent {
                                drawContent()
                                val stroke = Stroke(
                                    width = Thickness.border.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                                )
                                drawRoundRect(
                                    color = codeBlue,
                                    cornerRadius = CornerRadius(TaminCornerRadius.avatarTile.toPx(), TaminCornerRadius.avatarTile.toPx()),
                                    style = stroke
                                )
                            }
                            .padding(horizontal = Spacing.smPlus, vertical = Spacing.badgeVertical)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.tabSelector)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = null,
                                tint = codeBlue,
                                modifier = Modifier.size(Spacing.smd)
                            )
                            TaminText(
                                text = request.refCode,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = codeBlue
                            )
                        }
                    }
                }

                // 4-Step Progress Bar
                UserRequestStepProgress(phase = request.progressPhase)

                // Buttons Row
                if (showErrorButton) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TaminOutlinedButton(
                            text = stringResource(UserRequestRes.string.user_request_guide_btn),
                            onClick = { onOpenGuide(request) },
                            icon = Icons.Outlined.StarBorder,
                            shape = RoundedCornerShape(TaminCornerRadius.xl),
                            height = IconSize.largePlus,
                            borderColor = taminColors.blueBorder,
                            contentColor = colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )

                        Box(
                            modifier = Modifier
                                .height(IconSize.largePlus)
                                .weight(1.2f)
                                .clip(RoundedCornerShape(TaminCornerRadius.xl))
                                .background(taminColors.dangerBorder)
                                .border(Thickness.border, colorScheme.error.copy(alpha = 0.4f), RoundedCornerShape(TaminCornerRadius.xl))
                                .clickable { onOpenErrors(request) },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = Spacing.sm),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ErrorOutline,
                                    contentDescription = null,
                                    tint = colorScheme.error,
                                    modifier = Modifier.size(IconSize.small)
                                )
                                TaminText(
                                    text = stringResource(UserRequestRes.string.user_request_errors_btn),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = colorScheme.error
                                )

                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowLeft,
                                    contentDescription = null,
                                    tint = colorScheme.error,
                                    modifier = Modifier.size(IconSize.small)
                                )

                            }
                        }

                        if (showViewButton) {
                            TaminFilledButton(
                                text = viewButtonLabel,
                                onClick = { onViewDetails(request) },
                                icon = Icons.Outlined.Visibility,
                                iconPosition = IconPosition.End,
                                shape = RoundedCornerShape(TaminCornerRadius.xl),
                                height = IconSize.largePlus,
                                background = taminColors.buttonGradient,
                                modifier = Modifier.weight(1.3f)
                            )
                        }
                    }
                } else if (showViewButton) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TaminOutlinedButton(
                            text = stringResource(UserRequestRes.string.user_request_guide_btn),
                            onClick = { onOpenGuide(request) },
                            icon = Icons.Outlined.StarBorder,
                            shape = RoundedCornerShape(TaminCornerRadius.xl),
                            height = IconSize.largePlus,
                            borderColor = taminColors.blueBorder,
                            contentColor = colorScheme.primary,
                            modifier = Modifier.weight(0.8f)
                        )

                        TaminFilledButton(
                            text = viewButtonLabel,
                            onClick = { onViewDetails(request) },
                            icon = Icons.Outlined.Visibility,
                            iconPosition = IconPosition.End,
                            shape = RoundedCornerShape(TaminCornerRadius.xl),
                            height = IconSize.largePlus,
                            background = taminColors.buttonGradient,
                            modifier = Modifier.weight(1.2f)
                        )
                    }
                } else {
                    TaminOutlinedButton(
                        text = stringResource(UserRequestRes.string.user_request_guide_btn),
                        onClick = { onOpenGuide(request) },
                        icon = Icons.Outlined.StarBorder,
                        shape = RoundedCornerShape(TaminCornerRadius.xl),
                        height = IconSize.largePlus,
                        borderColor = taminColors.blueBorder,
                        contentColor = colorScheme.primary
                    )
                }
            }

        }
    }
}


@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun UserRequestCardErrorStatePreview() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        UserRequestCard(
            request = UserRequestPR(
                id = 101L,
                refCode = "۱۰۴۸۴۰۱۸۴۹",
                title = "غرامت دستمزد ایام بیماری",
                comment = "",
                creationTime = "۱۴۰۵/۰۳/۱۱",
                createByName = "سیدرحمت اله میرفضلی",
                statusDesc = "نقص مدارک ارسالی",
                statusCode = "0021",
                requestTypeId = UserRequestTypeIds.ILL_DAY,
                requestTypeTitle = "غرامت دستمزد ایام بیماری"
            ),
            onViewDetails = {},
            onOpenGuide = {},
            onOpenErrors = {},
            onCopyTrackingCode = {}
        )
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun UserRequestCardApprovedStatePreview() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        UserRequestCard(
            request = UserRequestPR(
                id = 102L,
                refCode = "۱۰۴۸۴۰۱۸۵۰",
                title = "کمک هزینه بارداری",
                comment = "",
                creationTime = "۱۴۰۵/۰۲/۱۵",
                createByName = "سیدرحمت اله میرفضلی",
                statusDesc = "تایید شده",
                statusCode = "0014",
                requestTypeId = UserRequestTypeIds.PREGNANCY,
                requestTypeTitle = "کمک هزینه بارداری"
            ),
            onViewDetails = {},
            onOpenGuide = {},
            onOpenErrors = {},
            onCopyTrackingCode = {}
        )
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun UserRequestCardInProgressStatePreview() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        UserRequestCard(
            request = UserRequestPR(
                id = 103L,
                refCode = "۱۰۴۸۴۰۱۸۵۱",
                title = "اروتز و پروتز",
                comment = "",
                creationTime = "۱۴۰۵/۰۱/۲۰",
                createByName = "سیدرحمت اله میرفضلی",
                statusDesc = "در حال بررسی شعبه",
                statusCode = "0010",
                requestTypeId = UserRequestTypeIds.ORTHOTICS_PROSTHESIS,
                requestTypeTitle = "اروتز و پروتز"
            ),
            onViewDetails = {},
            onOpenGuide = {},
            onOpenErrors = {},
            onCopyTrackingCode = {}
        )
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun UserRequestCardWithErrorsButtonPreview() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        UserRequestCard(
            request = UserRequestPR(
                id = 104L,
                refCode = "۱۰۴۸۳۹۷۲۱۵",
                title = "درخواست بررسی مدارک ارسالی",
                comment = "",
                creationTime = "۱۴۰۵/۰۲/۲۸",
                createByName = "سیدرحمت اله میرفضلی",
                statusDesc = "عدم تایید",
                statusCode = "0006",
                requestTypeId = UserRequestTypeIds.OTHER_REQUEST,
                requestTypeTitle = "سایر درخواست‌ها"
            ),
            onViewDetails = {},
            onOpenGuide = {},
            onOpenErrors = {},
            onCopyTrackingCode = {}
        )
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun UserRequestCardFollowUpObjectionPreview() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        UserRequestCard(
            request = UserRequestPR(
                id = 105L,
                refCode = "۱۰۴۸۴۰۱۸۵۳",
                title = "اعتراض به سوابق بیمه",
                comment = "",
                creationTime = "۱۴۰۴/۱۱/۱۰",
                createByName = "سیدرحمت اله میرفضلی",
                statusDesc = "در حال بررسی",
                statusCode = "0001",
                requestTypeId = UserRequestTypeIds.FOLLOW_UP_OBJECTION,
                requestTypeTitle = "اعتراض به سوابق بیمه"
            ),
            onViewDetails = {},
            onOpenGuide = {},
            onOpenErrors = {},
            onCopyTrackingCode = {}
        )
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun UserRequestCardGuideOnlyPreview() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        UserRequestCard(
            request = UserRequestPR(
                id = 106L,
                refCode = "۱۰۴۸۴۰۱۸۵۴",
                title = "کمیسیون پزشکی",
                comment = "",
                creationTime = "۱۴۰۴/۱۰/۰۱",
                createByName = "سیدرحمت اله میرفضلی",
                statusDesc = "ارسال به کمیسیون",
                statusCode = "0005",
                requestTypeId = UserRequestTypeIds.MEDICAL_COMMISSION,
                requestTypeTitle = "کمیسیون پزشکی"
            ),
            onViewDetails = {},
            onOpenGuide = {},
            onOpenErrors = {},
            onCopyTrackingCode = {}
        )
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun UserRequestCardAllSituationsPreview() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        Column(
            modifier = Modifier.padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            UserRequestCard(
                request = UserRequestPR(
                    id = 101L,
                    refCode = "۱۰۴۸۴۰۱۸۴۹",
                    title = "غرامت دستمزد ایام بیماری",
                    comment = "",
                    creationTime = "۱۴۰۵/۰۳/۱۱",
                    createByName = "سیدرحمت اله میرفضلی",
                    statusDesc = "نقص مدارک ارسالی",
                    statusCode = "0021",
                    requestTypeId = UserRequestTypeIds.ILL_DAY,
                    requestTypeTitle = "غرامت دستمزد ایام بیماری"
                ),
                onViewDetails = {},
                onOpenGuide = {},
                onOpenErrors = {},
                onCopyTrackingCode = {}
            )
            UserRequestCard(
                request = UserRequestPR(
                    id = 104L,
                    refCode = "۱۰۴۸۳۹۷۲۱۵",
                    title = "درخواست بررسی مدارک ارسالی",
                    comment = "",
                    creationTime = "۱۴۰۵/۰۲/۲۸",
                    createByName = "سیدرحمت اله میرفضلی",
                    statusDesc = "عدم تایید",
                    statusCode = "0006",
                    requestTypeId = UserRequestTypeIds.OTHER_REQUEST,
                    requestTypeTitle = "سایر درخواست‌ها"
                ),
                onViewDetails = {},
                onOpenGuide = {},
                onOpenErrors = {},
                onCopyTrackingCode = {}
            )
            UserRequestCard(
                request = UserRequestPR(
                    id = 102L,
                    refCode = "۱۰۴۸۴۰۱۸۵۰",
                    title = "کمک هزینه بارداری",
                    comment = "",
                    creationTime = "۱۴۰۵/۰۲/۱۵",
                    createByName = "سیدرحمت اله میرفضلی",
                    statusDesc = "تایید شده",
                    statusCode = "0014",
                    requestTypeId = UserRequestTypeIds.PREGNANCY,
                    requestTypeTitle = "کمک هزینه بارداری"
                ),
                onViewDetails = {},
                onOpenGuide = {},
                onOpenErrors = {},
                onCopyTrackingCode = {}
            )
            UserRequestCard(
                request = UserRequestPR(
                    id = 103L,
                    refCode = "۱۰۴۸۴۰۱۸۵۱",
                    title = "اروتز و پروتز",
                    comment = "",
                    creationTime = "۱۴۰۵/۰۱/۲۰",
                    createByName = "سیدرحمت اله میرفضلی",
                    statusDesc = "در حال بررسی شعبه",
                    statusCode = "0010",
                    requestTypeId = UserRequestTypeIds.ORTHOTICS_PROSTHESIS,
                    requestTypeTitle = "اروتز و پروتز"
                ),
                onViewDetails = {},
                onOpenGuide = {},
                onOpenErrors = {},
                onCopyTrackingCode = {}
            )
            UserRequestCard(
                request = UserRequestPR(
                    id = 105L,
                    refCode = "۱۰۴۸۴۰۱۸۵۳",
                    title = "اعتراض به سوابق بیمه",
                    comment = "",
                    creationTime = "۱۴۰۴/۱۱/۱۰",
                    createByName = "سیدرحمت اله میرفضلی",
                    statusDesc = "در حال بررسی",
                    statusCode = "0001",
                    requestTypeId = UserRequestTypeIds.FOLLOW_UP_OBJECTION,
                    requestTypeTitle = "اعتراض به سوابق بیمه"
                ),
                onViewDetails = {},
                onOpenGuide = {},
                onOpenErrors = {},
                onCopyTrackingCode = {}
            )
            UserRequestCard(
                request = UserRequestPR(
                    id = 106L,
                    refCode = "۱۰۴۸۴۰۱۸۵۴",
                    title = "کمیسیون پزشکی",
                    comment = "",
                    creationTime = "۱۴۰۴/۱۰/۰۱",
                    createByName = "سیدرحمت اله میرفضلی",
                    statusDesc = "ارسال به کمیسیون",
                    statusCode = "0005",
                    requestTypeId = UserRequestTypeIds.MEDICAL_COMMISSION,
                    requestTypeTitle = "کمیسیون پزشکی"
                ),
                onViewDetails = {},
                onOpenGuide = {},
                onOpenErrors = {},
                onCopyTrackingCode = {}
            )
        }
    }
}
