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
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.model.userRequest.SmartGuidePR
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
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
    val taminColors = LocalTaminColors.current
    val colorScheme = MaterialTheme.colorScheme

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier,
        containerColor = taminColors.bgSurface,
        shape = RoundedCornerShape(topStart = CornerRadius.x2l, topEnd = CornerRadius.x2l)
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
                        color = taminColors.textPrimary
                    )
                    TaminText(
                        text = title,
                        style = MaterialTheme.typography.labelSmall,
                        color = taminColors.textTertiary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(IconSize.large)
                        .clip(RoundedCornerShape(CornerRadius.lg))
                        .background(taminColors.chipBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = colorScheme.primary,
                        modifier = Modifier.size(IconSize.banner)
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
                            .clip(RoundedCornerShape(CornerRadius.xl))
                            .border(Thickness.border, taminColors.divider, RoundedCornerShape(CornerRadius.xl))
                            .padding(Spacing.md)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                            TaminText(
                                text = "• ${item.question}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = taminColors.textPrimary
                            )
                            TaminText(
                                text = item.reply,
                                style = MaterialTheme.typography.bodySmall,
                                color = taminColors.textSecondary
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

