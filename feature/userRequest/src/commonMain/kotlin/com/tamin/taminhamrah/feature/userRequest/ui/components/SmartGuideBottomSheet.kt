package com.tamin.taminhamrah.feature.userRequest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.userRequest.SmartGuidePR
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.feature.userrequest.generated.resources.Res as UserRequestRes
import taminx.feature.userrequest.generated.resources.user_request_action_close
import taminx.feature.userrequest.generated.resources.user_request_smart_guide_title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartGuideBottomSheet(
    title: String,
    items: List<SmartGuidePR>,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier,
        containerColor = LocalTaminColors.current.bgSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.page)
                .padding(bottom = Spacing.page),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // Header Row with Info Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    TaminText(
                        text = stringResource(UserRequestRes.string.user_request_smart_guide_title),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = LocalTaminColors.current.textPrimary
                    )
                    TaminText(
                        text = title,
                        style = MaterialTheme.typography.labelSmall,
                        color = LocalTaminColors.current.textTertiary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEFF6FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = Color(0xFF1F4FA3),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Questions List
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                items(items) { item ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, LocalTaminColors.current.divider, RoundedCornerShape(16.dp))
                            .padding(Spacing.md)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                            TaminText(
                                text = "• ${item.question}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = LocalTaminColors.current.textPrimary
                            )
                            TaminText(
                                text = item.reply,
                                style = MaterialTheme.typography.bodySmall,
                                color = LocalTaminColors.current.textSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xs))

            TaminOutlinedButton(
                text = stringResource(UserRequestRes.string.user_request_action_close),
                onClick = onDismissRequest,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun SmartGuideBottomSheetPreview() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        SmartGuideBottomSheet(
            title = "گواهی کسر اقساط معوق • تایید نهایی",
            items = listOf(
                SmartGuidePR(
                    id = 1L,
                    question = "درخواست تایید شد، پرداخت چه زمانی انجام می‌شود؟",
                    reply = "مبلغ تاییدشده بر اساس نوبت پرداخت سازمان به حساب بانکی اعلام‌شده شما واریز می‌شود.",
                    requestCode = "0018",
                    requestDesc = "",
                    isPublic = true,
                    title = "راهنمای هوشمند",
                    description = ""
                )
            ),
            onDismissRequest = {}
        )
    }
}

