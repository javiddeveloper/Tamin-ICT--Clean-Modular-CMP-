package com.tamin.taminhamrah.feature.userRequest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.userRequest.UserRequestTypePR
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_check
import taminx.feature.userrequest.generated.resources.Res as UserRequestRes
import taminx.feature.userrequest.generated.resources.user_request_all_types
import taminx.feature.userrequest.generated.resources.user_request_type_placeholder

private val OptionRowMinHeight = 56.dp
private val OptionsListMaxHeight = 360.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserRequestTypeBottomSheet(
    selectedTypeId: String?,
    requestTypes: List<UserRequestTypePR>,
    onTypeSelected: (String?, String?) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val taminColors = LocalTaminColors.current

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
            TaminText(
                text = stringResource(UserRequestRes.string.user_request_type_placeholder),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = taminColors.textPrimary
            )

            Spacer(modifier = Modifier.height(Spacing.xs))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = OptionsListMaxHeight),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                item {
                    UserRequestTypeRow(
                        label = stringResource(UserRequestRes.string.user_request_all_types),
                        isSelected = selectedTypeId == null,
                        onClick = {
                            onTypeSelected(null, null)
                            onDismissRequest()
                        }
                    )
                }
                items(requestTypes) { type ->
                    val typeId = type.id.toString()
                    UserRequestTypeRow(
                        label = type.title,
                        isSelected = selectedTypeId == typeId,
                        onClick = {
                            onTypeSelected(typeId, type.title)
                            onDismissRequest()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun UserRequestTypeRow(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val taminColors = LocalTaminColors.current
    val backgroundColor = if (isSelected) taminColors.blueBg else taminColors.bgPage
    val borderColor = if (isSelected) taminColors.blueBorder else taminColors.border

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = OptionRowMinHeight)
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(backgroundColor)
            .border(Thickness.border, borderColor, RoundedCornerShape(CornerRadius.lg))
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        TaminText(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = taminColors.textPrimary,
            modifier = Modifier.weight(1f)
        )
        if (isSelected) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_check),
                contentDescription = null,
                tint = taminColors.blueText,
                modifier = Modifier.size(IconSize.small)
            )
        } else {
            Spacer(modifier = Modifier.size(IconSize.small))
        }
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun UserRequestTypeBottomSheetPreview() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        UserRequestTypeBottomSheet(
            selectedTypeId = "11",
            requestTypes = listOf(
                UserRequestTypePR(id = 10L, title = "غرامت دستمزد ایام بیماری", description = ""),
                UserRequestTypePR(id = 11L, title = "غرامت دستمزد ایام بارداری", description = ""),
                UserRequestTypePR(id = 22L, title = "گواهی کسر اقساط معوق", description = "")
            ),
            onTypeSelected = { _, _ -> },
            onDismissRequest = {}
        )
    }
}
