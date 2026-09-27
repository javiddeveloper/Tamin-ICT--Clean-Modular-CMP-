package com.tamin.taminhamrah.feature.myinbox.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity
import com.tamin.taminhamrah.ui.theme.SheetDimens
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.inbox.PermitDurationPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.ListGroupView
import com.tamin.taminhamrah.ui.components.ListItemData
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.warning
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHamrahShapes
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.label_dear_user
import taminx.core.core_ui.label_inbox_inquiry_desc
import taminx.core.core_ui.label_inquiry_permit_validity
import taminx.core.core_ui.label_issue_inquiry_permit
import taminx.core.core_ui.label_issue_permit_duration

@Composable
fun InquiryPermitBottomSheet(
    durations: ImmutableList<PermitDurationPR>,
    onDismissRequest: () -> Unit,
    onConfirm: (duration: PermitDurationPR) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDurationPicker by remember { mutableStateOf(false) }
    var selectedDuration by remember { mutableStateOf<PermitDurationPR?>(null) }
    val toaster = LocalToaster.current
    // Leftover fling at the text's scroll bounds must not start a sheet dismiss / bounce.
    val consumeOverscroll = remember {
        object : NestedScrollConnection {
            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity =
                available
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier,
        containerColor = LocalTaminColors.current.bgPage,
        shape = RoundedCornerShape(topStart = CornerRadius.sheet, topEnd = CornerRadius.sheet),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = Spacing.md)
                    .size(width = 32.dp, height = 4.dp)
                    .background(
                        color = LocalTaminColors.current.border,
                        shape = RoundedCornerShape(CornerRadius.full)
                    )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.page)
                .padding(bottom = Spacing.xl, top = Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            // Title
            TaminText(
                text = stringResource(Res.string.label_dear_user),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = LocalTaminColors.current.textPrimary
                ),
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )

            // Content Box — capped and scrollable: at full screen height the sheet's status-bar
            // padding changes with its offset, which re-anchors it mid-drag and makes it jump.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .heightIn(max = SheetDimens.contentMaxHeight)
                    .nestedScroll(consumeOverscroll)
                    .clip(TaminHamrahShapes.large)
                    .background(LocalTaminColors.current.bgSurface)
                    .border(
                        width = 1.dp,
                        color = LocalTaminColors.current.border,
                        shape = TaminHamrahShapes.large
                    )
                    .padding(Spacing.md)
            ) {
                TaminText(
                    text = stringResource(Res.string.label_inbox_inquiry_desc),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = LocalTaminColors.current.textSecondary,
                        lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.4
                    ),
                    textAlign = TextAlign.Right,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                )
            }

            // Duration Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(LocalTaminColors.current.bgSurface)
                    .border(
                        width = 1.dp,
                        color = LocalTaminColors.current.divider,
                        shape = TaminHamrahShapes.large
                    )
                    .clip(TaminHamrahShapes.large)
                    .clickable { showDurationPicker = true }
                    .padding(horizontal = Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TaminText(
                    text = selectedDuration?.let {
                        stringResource(Res.string.label_issue_permit_duration, it.label)
                    } ?: stringResource(Res.string.label_issue_inquiry_permit),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (selectedDuration != null) LocalTaminColors.current.textPrimary else LocalTaminColors.current.textTertiary
                    ),
                    textAlign = TextAlign.Right
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = LocalTaminColors.current.textTertiary
                )
            }

            // Action Button
            TaminFilledButton(
                text = stringResource(Res.string.label_issue_inquiry_permit),
                onClick = {
                    selectedDuration?.let {
                        onConfirm(it)
                    } ?: run {
                        toaster.warning("لطفاً یک مورد را انتخاب کنید")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (showDurationPicker) {
        PermitDurationBottomSheet(
            selectedDuration = selectedDuration,
            durations = durations,
            onDurationSelected = {
                selectedDuration = it
                showDurationPicker = false
            },
            onDismissRequest = { showDurationPicker = false }
        )
    }
}

@Composable
fun PermitDurationBottomSheet(
    selectedDuration: PermitDurationPR?,
    durations: ImmutableList<PermitDurationPR>,
    onDurationSelected: (PermitDurationPR) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier,
        containerColor = LocalTaminColors.current.bgPage,
        shape = RoundedCornerShape(topStart = CornerRadius.sheet, topEnd = CornerRadius.sheet),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = Spacing.md)
                    .size(width = 32.dp, height = 4.dp)
                    .background(
                        color = LocalTaminColors.current.border,
                        shape = RoundedCornerShape(CornerRadius.full)
                    )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.page)
                .padding(bottom = Spacing.xl, top = Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            TaminText(
                text = stringResource(Res.string.label_inquiry_permit_validity),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = LocalTaminColors.current.textPrimary
                ),
                textAlign = TextAlign.Center
            )

            ListGroupView(
                items = persistentListOf(
                    *durations.map { duration ->
                        ListItemData(
                            title = duration.label,
                            showArrow = false,
                            onClick = { onDurationSelected(duration) },
                            customTrailingContent = {
                                RadioButton(
                                    selected = selectedDuration == duration,
                                    onClick = null,
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = LocalTaminColors.current.blueText,
                                        unselectedColor = LocalTaminColors.current.divider
                                    )
                                )
                            }
                        )
                    }.toTypedArray()
                ),
                containerShape = TaminHamrahShapes.large,
                containerBorder = BorderStroke(1.dp, LocalTaminColors.current.divider),
                itemContentPadding = PaddingValues(horizontal = Spacing.lg, vertical = Spacing.sm)
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun InquiryPermitBottomSheetPreview() {
    PreviewRtlThemeContent {
        InquiryPermitBottomSheet(
            durations = persistentListOf(
                PermitDurationPR(label = "یک روز", valueInDays = 1),
                PermitDurationPR(label = "یک هفته", valueInDays = 7)
            ),
            onDismissRequest = {},
            onConfirm = {}
        )
    }
}
