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
import androidx.compose.material.icons.outlined.Warning
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
import com.tamin.taminhamrah.model.userRequest.RequestErrorPR
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestErrorsBottomSheet(
    title: String,
    items: List<RequestErrorPR>,
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
            // Header Row with Red Error Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    TaminText(
                        text = "خطاهای ثبت‌شده",
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
                        .background(Color(0xFFFEE2E2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Warning,
                        contentDescription = null,
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Error Items List
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
                                text = "• ${item.errorMessage}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFDC2626)
                            )
                            if (item.creationTime.isNotBlank()) {
                                TaminText(
                                    text = "ثبت‌شده در ${item.creationTime}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = LocalTaminColors.current.textTertiary
                                )
                            }
                        }

                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xs))

            TaminOutlinedButton(
                text = "بستن",
                onClick = onDismissRequest,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun RequestErrorsBottomSheetPreview() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        RequestErrorsBottomSheet(
            title = "درخواست بررسی مدارک ارسالی • عدم تایید",
            items = listOf(
                RequestErrorPR(
                    id = 1L,
                    errorMessage = "مدارک آپلود شده خوانا نمی‌باشد",
                    errorType = "",
                    errorStatus = "",
                    creationTime = "۱۴۰۵/۰۲/۲۹"
                ),
                RequestErrorPR(
                    id = 2L,
                    errorMessage = "تصویر گواهی استراحت پزشکی ناقص است",
                    errorType = "",
                    errorStatus = "",
                    creationTime = "۱۴۰۵/۰۳/۰۲"
                )
            ),
            onDismissRequest = {}
        )
    }
}

