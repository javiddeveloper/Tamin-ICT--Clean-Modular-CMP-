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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.userRequest.UserRequestTypePR
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.feature.userrequest.generated.resources.Res as UserRequestRes
import taminx.feature.userrequest.generated.resources.user_request_all_types
import taminx.feature.userrequest.generated.resources.user_request_filter_close_desc
import taminx.feature.userrequest.generated.resources.user_request_search_button
import taminx.feature.userrequest.generated.resources.user_request_search_ref_code_placeholder
import taminx.feature.userrequest.generated.resources.user_request_search_title
import taminx.feature.userrequest.generated.resources.user_request_type_placeholder

@Composable
fun UserRequestFilterPanel(
    refCode: String,
    selectedTypeName: String?,
    requestTypes: List<UserRequestTypePR>,
    onRefCodeChanged: (String) -> Unit,
    onTypeSelected: (String?, String?) -> Unit,
    onSearch: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isDropdownOpen by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.sm),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LocalTaminColors.current.bgSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
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
                    color = LocalTaminColors.current.textPrimary
                )

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(LocalTaminColors.current.bgPage)
                        .clickable { onClose() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(UserRequestRes.string.user_request_filter_close_desc),
                        tint = LocalTaminColors.current.textSecondary,
                        modifier = Modifier.size(16.dp)
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
                        color = LocalTaminColors.current.textTertiary
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = LocalTaminColors.current.divider,
                    focusedBorderColor = LocalTaminColors.current.blueText
                )
            )

            // Request Type Selector Dropdown Box
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, LocalTaminColors.current.divider, RoundedCornerShape(14.dp))
                        .clickable { isDropdownOpen = true }
                        .padding(horizontal = Spacing.md, vertical = Spacing.md),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TaminText(
                        text = selectedTypeName ?: stringResource(UserRequestRes.string.user_request_type_placeholder),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (selectedTypeName != null) LocalTaminColors.current.textPrimary else LocalTaminColors.current.textTertiary
                    )

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = LocalTaminColors.current.textSecondary
                    )
                }

                DropdownMenu(
                    expanded = isDropdownOpen,
                    onDismissRequest = { isDropdownOpen = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    DropdownMenuItem(
                        text = { TaminText(stringResource(UserRequestRes.string.user_request_all_types)) },
                        onClick = {
                            onTypeSelected(null, null)
                            isDropdownOpen = false
                        }
                    )
                    requestTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { TaminText(type.title) },
                            onClick = {
                                onTypeSelected(type.id.toString(), type.title)
                                isDropdownOpen = false
                            }
                        )
                    }
                }
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
            selectedTypeName = "غرامت دستمزد ایام بیماری",
            requestTypes = emptyList(),
            onRefCodeChanged = {},
            onTypeSelected = { _, _ -> },
            onSearch = {},
            onClose = {}
        )
    }
}

