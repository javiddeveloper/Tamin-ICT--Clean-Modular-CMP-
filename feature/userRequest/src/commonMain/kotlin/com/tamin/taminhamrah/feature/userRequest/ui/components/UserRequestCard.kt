package com.tamin.taminhamrah.feature.userRequest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.userRequest.UserRequestPR
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing

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

    val isError = request.statusDesc.contains("عدم") || request.statusDesc.contains("نقص") || request.statusDesc.contains("خطا")
    val isApproved = request.statusDesc.contains("تایید") || request.statusDesc.contains("مختومه") || request.statusDesc.contains("تکمیل")

    val statusBg = if (isError) Color(0xFFFEE2E2) else if (isApproved) Color(0xFFDCFCE7) else Color(0xFFF1F5F9)
    val statusText = if (isError) Color(0xFFDC2626) else if (isApproved) Color(0xFF16A34A) else Color(0xFF64748B)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LocalTaminColors.current.bgSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.page),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // Header Row: Date & Category Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TaminText(
                    text = request.creationTime,
                    style = MaterialTheme.typography.labelMedium,
                    color = LocalTaminColors.current.textTertiary
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(LocalTaminColors.current.bgPage)
                        .padding(horizontal = Spacing.md, vertical = Spacing.xs)
                ) {
                    TaminText(
                        text = request.requestTypeTitle.ifEmpty { "سایر درخواست‌ها" },
                        style = MaterialTheme.typography.labelSmall,
                        color = LocalTaminColors.current.textSecondary
                    )
                }
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
                    color = LocalTaminColors.current.textPrimary
                )

                if (request.statusDesc.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(statusBg)
                            .padding(horizontal = Spacing.sm, vertical = Spacing.xs)
                    ) {
                        TaminText(
                            text = request.statusDesc,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = statusText
                        )
                    }
                }
            }

            // Tracking Code Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, LocalTaminColors.current.divider, RoundedCornerShape(12.dp))
                    .clickable {
                        clipboardManager.setText(AnnotatedString(request.refCode))
                        onCopyTrackingCode(request.refCode)
                    }
                    .padding(horizontal = Spacing.md, vertical = Spacing.xs),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TaminText(
                        text = "کد پیگیری: ",
                        style = MaterialTheme.typography.labelSmall,
                        color = LocalTaminColors.current.textTertiary
                    )
                    TaminText(
                        text = request.refCode,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = LocalTaminColors.current.blueText
                    )
                }
            }

            // 4-Step Progress Bar
            UserRequestStepProgress(statusDesc = request.statusDesc)

            // Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TaminFilledButton(
                    text = "مشاهده درخواست",
                    onClick = { onViewDetails(request) },
                    modifier = Modifier.weight(1.2f)
                )

                if (isError) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFEE2E2))
                            .clickable { onOpenErrors(request) }
                            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            TaminText(
                                text = "خطاها ۲",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFDC2626)
                            )
                        }
                    }
                }

                TaminOutlinedButton(
                    text = "راهنما",
                    onClick = { onOpenGuide(request) },
                    modifier = Modifier.weight(0.8f)
                )
            }
        }
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun UserRequestCardPreview() {
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
                requestTypeTitle = "غرامت دستمزد ایام بیماری"
            ),
            onViewDetails = {},
            onOpenGuide = {},
            onOpenErrors = {},
            onCopyTrackingCode = {}
        )
    }
}


