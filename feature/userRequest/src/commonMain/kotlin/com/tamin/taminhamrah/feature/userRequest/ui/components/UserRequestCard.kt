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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.userRequest.ui.contract.UserRequestKeywords
import com.tamin.taminhamrah.model.userRequest.UserRequestPR
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.feature.userrequest.generated.resources.Res as UserRequestRes
import taminx.feature.userrequest.generated.resources.user_request_errors_btn
import taminx.feature.userrequest.generated.resources.user_request_follow_up_objection
import taminx.feature.userrequest.generated.resources.user_request_guide_btn
import taminx.feature.userrequest.generated.resources.user_request_other_requests
import taminx.feature.userrequest.generated.resources.user_request_tracking_code_prefix
import taminx.feature.userrequest.generated.resources.user_request_view_request

// Request type IDs — mirror of RequestTypeEnumClass in legacy my-tamin-droid
private const val REQUEST_TYPE_ILL_DAY = 10L
private const val REQUEST_TYPE_PREGNANCY = 11L
private const val REQUEST_TYPE_ORTHOTICS_PROSTHESIS = 12L
private const val REQUEST_TYPE_FOLLOW_UP_OBJECTION = 8L
private const val REQUEST_TYPE_ARTICLE16 = 26L
private const val REQUEST_TYPE_DEFERRED_INSTALLMENT_CERTIFICATE = 22L
private const val REQUEST_TYPE_MEDICAL_COMMISSION = 27L

/**
 * Mirrors the exact button-visibility logic from MyRequestAdapter.bindItem() in my-tamin-droid.
 *
 * @return Pair(showViewButton, viewButtonLabel) — null showViewButton means hidden.
 */
private fun resolveViewButtonVisibility(
    requestTypeId: Long,
    statusCode: String,
    viewLabel: String,
    followUpLabel: String,
): Pair<Boolean, String> {
    return when (requestTypeId) {
        REQUEST_TYPE_ILL_DAY ->
            Pair(statusCode == "0021", viewLabel)

        REQUEST_TYPE_PREGNANCY ->
            Pair(statusCode == "0014", viewLabel)

        REQUEST_TYPE_ORTHOTICS_PROSTHESIS ->
            Pair(statusCode == "0021" || statusCode == "0019", viewLabel)

        REQUEST_TYPE_ARTICLE16 ->
            Pair(statusCode == "2602", viewLabel)

        REQUEST_TYPE_DEFERRED_INSTALLMENT_CERTIFICATE ->
            Pair(statusCode == "0018", viewLabel)

        REQUEST_TYPE_FOLLOW_UP_OBJECTION ->
            // Always visible but with a different label
            Pair(true, followUpLabel)

        REQUEST_TYPE_MEDICAL_COMMISSION ->
            // Never visible
            Pair(false, "")

        else ->
            // All other request types: button is hidden
            Pair(false, "")
    }
}

/**
 * Returns whether the error (خطاها) button should be visible for this request.
 * Mirrors MyRequestAdapter logic for requestType ids 18, 9, 19.
 */
private fun resolveErrorButtonVisibility(
    requestTypeId: Long,
    statusCode: String,
): Boolean {
    return when (requestTypeId) {
        18L -> statusCode == "0006"
        9L, 19L -> statusCode == "0019"
        REQUEST_TYPE_MEDICAL_COMMISSION -> false
        REQUEST_TYPE_FOLLOW_UP_OBJECTION -> false
        else -> false
    }
}

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

    val isStatusError = request.statusDesc.contains(UserRequestKeywords.DISAPPROVAL) ||
            request.statusDesc.contains(UserRequestKeywords.DEFECT) ||
            request.statusDesc.contains(UserRequestKeywords.ERROR)
    val isStatusApproved = request.statusDesc.contains(UserRequestKeywords.APPROVED) ||
            request.statusDesc.contains(UserRequestKeywords.CLOSED) ||
            request.statusDesc.contains(UserRequestKeywords.COMPLETED)

    val statusBg = when {
        isStatusError -> Color(0xFFFEE2E2)
        isStatusApproved -> Color(0xFFDCFCE7)
        else -> Color(0xFFF1F5F9)
    }
    val statusTextColor = when {
        isStatusError -> Color(0xFFDC2626)
        isStatusApproved -> Color(0xFF16A34A)
        else -> Color(0xFF64748B)
    }

    val viewLabel = stringResource(UserRequestRes.string.user_request_view_request)
    val followUpLabel = stringResource(UserRequestRes.string.user_request_follow_up_objection)

    val (showViewButton, viewButtonLabel) = remember(request.requestTypeId, request.statusCode, viewLabel, followUpLabel) {
        resolveViewButtonVisibility(request.requestTypeId, request.statusCode, viewLabel, followUpLabel)
    }
    val showErrorButton = remember(request.requestTypeId, request.statusCode) {
        resolveErrorButtonVisibility(request.requestTypeId, request.statusCode)
    }

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
                        text = request.requestTypeTitle.ifEmpty { stringResource(UserRequestRes.string.user_request_other_requests) },
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
                            color = statusTextColor
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
                        text = stringResource(UserRequestRes.string.user_request_tracking_code_prefix),
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

            // Buttons Row — only rendered when at least one button is visible
            if (showViewButton || showErrorButton) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (showViewButton) {
                        TaminFilledButton(
                            text = viewButtonLabel,
                            onClick = { onViewDetails(request) },
                            modifier = Modifier.weight(1.2f)
                        )
                    }

                    if (showErrorButton) {
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
                                    text = stringResource(UserRequestRes.string.user_request_errors_btn),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFFDC2626)
                                )
                            }
                        }
                    }

                    TaminOutlinedButton(
                        text = stringResource(UserRequestRes.string.user_request_guide_btn),
                        onClick = { onOpenGuide(request) },
                        modifier = Modifier.weight(0.8f)
                    )
                }
            } else {
                // راهنما always shown even when no view button
                TaminOutlinedButton(
                    text = stringResource(UserRequestRes.string.user_request_guide_btn),
                    onClick = { onOpenGuide(request) },
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
                statusCode = "0021",
                requestTypeId = REQUEST_TYPE_ILL_DAY,
                requestTypeTitle = "غرامت دستمزد ایام بیماری"
            ),
            onViewDetails = {},
            onOpenGuide = {},
            onOpenErrors = {},
            onCopyTrackingCode = {}
        )
    }
}



