package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminStyledTextField
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme
import com.tamin.taminhamrah.ui.theme.TaminNavy300
import com.tamin.taminhamrah.ui.theme.TaminNavy900
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.all_items
import taminx.core.core_ui.branch_code
import taminx.core.core_ui.btn_search
import taminx.core.core_ui.contract_affairs_search_type_optional
import taminx.core.core_ui.file_number
import taminx.core.core_ui.file_unit
import taminx.core.core_ui.ic_tamin_search
import taminx.core.core_ui.label_request_number
import taminx.core.core_ui.label_workshop_search
import taminx.core.core_ui.workshop_number

@Composable
fun ConstructionSearchCard(
    itemCount: Int,
    isExpanded: Boolean,
    fileNoQuery: String,
    reqNoQuery: String,
    workshopIdQuery: String,
    branchCodeQuery: String,
    onToggleExpanded: (Boolean) -> Unit,
    onFileNoChanged: (String) -> Unit,
    onReqNoChanged: (String) -> Unit,
    onWorkshopIdChanged: (String) -> Unit,
    onBranchCodeChanged: (String) -> Unit,
    onExecuteSearch: () -> Unit,
    onResetSearch: () -> Unit,
    modifier: Modifier = Modifier,
    isNoticeVisible: Boolean = false,
    onInfoIconClicked: () -> Unit
) {

    val taminColors = LocalTaminColors.current

    Column(modifier = modifier.fillMaxWidth()) {
        // Top Search Bar Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search Bar Card
            TaminStyledTextField(
                modifier = Modifier.weight(1f).border(
                    width = 1.dp,
                    shape = RoundedCornerShape(13.dp),
                    color = taminColors.border
                ),
                value = "",
                onValueChange = {},
                label = "",
                placeholder = stringResource(Res.string.label_workshop_search),
                leadingIconPainter = painterResource(Res.drawable.ic_tamin_search),
                trailingIcon = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                readOnly = true,
                onClick = { onToggleExpanded(!isExpanded) }
            )

            // Info Icon Button
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(CornerRadius.xl))
                    .background(taminColors.orangeBg)
                    .border(
                        width = 1.dp,
                        color = taminColors.orangeText,
                        shape = RoundedCornerShape(
                            CornerRadius.xl
                        )
                    )
                    .clickable { onInfoIconClicked() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = taminColors.orangeText
                )
            }
            // Item Count Badge

            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        color = taminColors.bgSurface,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = taminColors.border,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 12.dp)
            ) {
                TaminText(
                    text = "$itemCount",
                    color = taminColors.textPrimary
                )
                TaminText(
                    text = stringResource(Res.string.file_unit),
                    color = taminColors.textMuted,
                    fontSize = 12.sp
                )
            }
        }

        // Expanded Filter Form
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .coloredShadow(
                        color = taminColors.shadowSubtle,
                        borderRadius = CornerRadius.lg,
                        blurRadius = Elevation.lg,
                        offsetY = Spacing.xs
                    ),
                shape = RoundedCornerShape(CornerRadius.lg),
                color = taminColors.bgSurface,
                tonalElevation = Elevation.none
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TaminStyledTextField(
                            value = reqNoQuery,
                            onValueChange = onReqNoChanged,
                            label = stringResource(Res.string.label_request_number),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            placeholder = stringResource(Res.string.contract_affairs_search_type_optional),
                        )
                        TaminStyledTextField(
                            value = fileNoQuery,
                            onValueChange = onFileNoChanged,
                            label = stringResource(Res.string.file_number),
                            modifier = Modifier.weight(1f),
                            placeholder = stringResource(Res.string.contract_affairs_search_type_optional)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TaminStyledTextField(
                            value = branchCodeQuery,
                            onValueChange = onBranchCodeChanged,
                            label = stringResource(Res.string.branch_code),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            placeholder = stringResource(Res.string.contract_affairs_search_type_optional)
                        )
                        TaminStyledTextField(
                            value = workshopIdQuery,
                            onValueChange = onWorkshopIdChanged,
                            label = stringResource(Res.string.workshop_number),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            placeholder = stringResource(Res.string.contract_affairs_search_type_optional)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TaminOutlinedButton(
                            text = stringResource(Res.string.all_items),
                            onClick = onResetSearch,
                            borderColor = taminColors.border,
                            containerColor = taminColors.chipBg,
                            contentColor = taminColors.textSecondary,
                            textStyle = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.weight(0.5f),
                        )
                        TaminFilledButton(
                            background = Brush.linearGradient(
                                colors = listOf(
                                    TaminNavy300,
                                    TaminNavy900
                                )
                            ),
                            modifier = Modifier.weight(1f),
                            text = stringResource(Res.string.btn_search),
                            onClick = onExecuteSearch,
                            painter = painterResource(Res.drawable.ic_tamin_search),
                        )

                    }
                }
            }
        }

        // Animated Info / Notice Card
        AnimatedVisibility(
            visible = isNoticeVisible,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            NoticeCard(
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ConstructionSearchCardPreview() {
    PreviewRtlThemeContent {
        ConstructionSearchCard(
            itemCount = 24,
            isExpanded = true,
            fileNoQuery = "۱۲۳۴۵۶",
            reqNoQuery = "۹۸۷۶۵۴",
            workshopIdQuery = "۱۰۲۳۴",
            branchCodeQuery = "۱۲۰۱",
            onToggleExpanded = {},
            onFileNoChanged = {},
            onReqNoChanged = {},
            onWorkshopIdChanged = {},
            onBranchCodeChanged = {},
            onExecuteSearch = {},
            onResetSearch = {},
            modifier = Modifier.padding(16.dp),
            onInfoIconClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ConstructionSearchCardPreviewDark() {
    TaminHamrahTheme(darkTheme = true) {
        ConstructionSearchCard(
            itemCount = 24,
            isExpanded = true,
            fileNoQuery = "۱۲۳۴۵۶",
            reqNoQuery = "۹۸۷۶۵۴",
            workshopIdQuery = "۱۰۲۳۴",
            branchCodeQuery = "۱۲۰۱",
            onToggleExpanded = {},
            onFileNoChanged = {},
            onReqNoChanged = {},
            onWorkshopIdChanged = {},
            onBranchCodeChanged = {},
            onExecuteSearch = {},
            onResetSearch = {},
            modifier = Modifier.padding(16.dp),
            onInfoIconClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ConstructionSearchCardCollapsedPreview() {
    PreviewRtlThemeContent {
        ConstructionSearchCard(
            itemCount = 24,
            isExpanded = false,
            fileNoQuery = "",
            reqNoQuery = "",
            workshopIdQuery = "",
            branchCodeQuery = "",
            onToggleExpanded = {},
            onFileNoChanged = {},
            onReqNoChanged = {},
            onWorkshopIdChanged = {},
            onBranchCodeChanged = {},
            onExecuteSearch = {},
            onResetSearch = {},
            modifier = Modifier.padding(16.dp),
            onInfoIconClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ConstructionSearchCardCollapsedPreviewDark() {
    TaminHamrahTheme(darkTheme = true) {
        ConstructionSearchCard(
            itemCount = 24,
            isExpanded = false,
            fileNoQuery = "",
            reqNoQuery = "",
            workshopIdQuery = "",
            branchCodeQuery = "",
            onToggleExpanded = {},
            onFileNoChanged = {},
            onReqNoChanged = {},
            onWorkshopIdChanged = {},
            onBranchCodeChanged = {},
            onExecuteSearch = {},
            onResetSearch = {},
            modifier = Modifier.padding(16.dp),
            onInfoIconClicked = {},
        )
    }
}
