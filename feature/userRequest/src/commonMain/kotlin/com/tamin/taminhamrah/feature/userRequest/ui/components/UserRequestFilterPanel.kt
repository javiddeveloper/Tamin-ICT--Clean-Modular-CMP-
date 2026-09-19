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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.model.userRequest.UserRequestTypePR
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import taminx.feature.userrequest.generated.resources.Res as UserRequestRes
import taminx.feature.userrequest.generated.resources.user_request_filter_close_desc
import taminx.feature.userrequest.generated.resources.user_request_search_button
import taminx.feature.userrequest.generated.resources.user_request_search_ref_code_placeholder
import taminx.feature.userrequest.generated.resources.user_request_search_title
import taminx.feature.userrequest.generated.resources.user_request_type_placeholder

@Composable
fun UserRequestFilterPanel(
    refCode: String,
    selectedTypeId: String?,
    selectedTypeName: String?,
    requestTypes: List<UserRequestTypePR>,
    onRefCodeChanged: (String) -> Unit,
    onTypeSelected: (String?, String?) -> Unit,
    onSearch: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isTypeSheetOpen by remember { mutableStateOf(false) }
    val taminColors = LocalTaminColors.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.sm),
        shape = RoundedCornerShape(CornerRadius.cardCompact),
        colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.xs)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.page),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TaminText(
                    text = stringResource(UserRequestRes.string.user_request_search_title),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = taminColors.textPrimary
                )

                Box(
                    modifier = Modifier
                        .size(IconSize.badge)
                        .clip(RoundedCornerShape(CornerRadius.avatarTile))
                        .background(taminColors.bgPage)
                        .clickable { onClose() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(UserRequestRes.string.user_request_filter_close_desc),
                        tint = taminColors.textSecondary,
                        modifier = Modifier.size(IconSize.small)
                    )
                }
            }

            // Tracking Code Input
            OutlinedTextField(
                value = refCode,
                onValueChange = onRefCodeChanged,
                placeholder = {
                    TaminText(
                        text = stringResource(UserRequestRes.string.user_request_search_ref_code_placeholder),
                        style = MaterialTheme.typography.bodySmall,
                        color = taminColors.textTertiary
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(CornerRadius.chip),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = taminColors.divider,
                    focusedBorderColor = taminColors.blueText
                )
            )

            // Request Type Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(CornerRadius.chip))
                    .border(Thickness.border, taminColors.divider, RoundedCornerShape(CornerRadius.chip))
                    .clickable { isTypeSheetOpen = true }
                    .padding(horizontal = Spacing.md, vertical = Spacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TaminText(
                    text = selectedTypeName ?: stringResource(UserRequestRes.string.user_request_type_placeholder),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (selectedTypeName != null) taminColors.textPrimary else taminColors.textTertiary
                )

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = taminColors.textSecondary
                )
            }

            if (isTypeSheetOpen) {
                UserRequestTypeBottomSheet(
                    selectedTypeId = selectedTypeId,
                    requestTypes = requestTypes,
                    onTypeSelected = onTypeSelected,
                    onDismissRequest = { isTypeSheetOpen = false }
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xs))

            TaminFilledButton(
                text = stringResource(UserRequestRes.string.user_request_search_button),
                onClick = onSearch,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun UserRequestFilterPanelPreview() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        UserRequestFilterPanel(
            refCode = "۱۰۴۸۴۰۱۸۴۹",
            selectedTypeId = "10",
            selectedTypeName = "غرامت دستمزد ایام بیماری",
            requestTypes = emptyList(),
            onRefCodeChanged = {},
            onTypeSelected = { _, _ -> },
            onSearch = {},
            onClose = {}
        )
    }
}

