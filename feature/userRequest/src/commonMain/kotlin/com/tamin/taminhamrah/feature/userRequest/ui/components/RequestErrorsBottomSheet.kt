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
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.model.userRequest.RequestErrorPR
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
import taminx.feature.userrequest.generated.resources.user_request_error_registered_at
import taminx.feature.userrequest.generated.resources.user_request_errors_sheet_title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestErrorsBottomSheet(
    title: String,
    items: List<RequestErrorPR>,
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
            // Header Row with Red Error Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    TaminText(
                        text = stringResource(UserRequestRes.string.user_request_errors_sheet_title),
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
                        .background(taminColors.dangerBorder),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Warning,
                        contentDescription = null,
                        tint = colorScheme.error,
                        modifier = Modifier.size(IconSize.banner)
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
                            .clip(RoundedCornerShape(CornerRadius.xl))
                            .border(Thickness.border, taminColors.divider, RoundedCornerShape(CornerRadius.xl))
                            .padding(Spacing.md)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                            TaminText(
                                text = "• ${item.errorMessage}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = colorScheme.error
                            )
                            if (item.creationTimeJalali.isNotBlank()) {
                                TaminText(
                                    text = stringResource(UserRequestRes.string.user_request_error_registered_at, item.creationTimeJalali),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = taminColors.textTertiary
                                )
                            }
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
                    creationTimeJalali = "۱۴۰۵/۰۲/۲۹"
                ),
                RequestErrorPR(
                    id = 2L,
                    errorMessage = "تصویر گواهی استراحت پزشکی ناقص است",
                    errorType = "",
                    errorStatus = "",
                    creationTimeJalali = "۱۴۰۵/۰۳/۰۲"
                )
            ),
            onDismissRequest = {}
        )
    }
}

