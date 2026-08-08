package com.tamin.taminhamrah.feature.myinbox.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.model.inbox.PersonalInboxItemPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.IconPosition
import com.tamin.taminhamrah.ui.components.IconTile
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_arrow_show_more
import taminx.core.core_ui.ic_check_label
import taminx.core.core_ui.ic_inbox
import taminx.core.core_ui.ic_setting
import taminx.core.core_ui.ic_tamin_copy
import taminx.core.core_ui.ic_tamin_cross
import taminx.core.core_ui.ic_tamin_track
import taminx.core.core_ui.ic_tamin_verified
import taminx.core.core_ui.action_hide_details
import taminx.core.core_ui.action_show_details
import taminx.core.core_ui.identity_field_email
import taminx.core.core_ui.identity_field_mobile
import taminx.core.core_ui.identity_field_national_code
import taminx.core.core_ui.inbox_action_operations
import taminx.core.core_ui.inbox_inquiry_password
import taminx.core.core_ui.inbox_item_header
import taminx.core.core_ui.inbox_status_delivered
import taminx.core.core_ui.inbox_status_rejected
import taminx.core.core_ui.inbox_tracking_code
import com.tamin.taminhamrah.ui.ActionMenuItem
import com.tamin.taminhamrah.ui.RecordActionMenu
import kotlinx.collections.immutable.ImmutableList

@Composable
fun InboxItemCard(
    item: PersonalInboxItemPR,
    actions: ImmutableList<ActionMenuItem<String>>,
    onActionSelect: (String) -> Unit,
    onCopyClick: () -> Unit,
    modifier: Modifier = Modifier,
    initialExpanded: Boolean = false
) {
    val colors = LocalTaminColors.current
    var isExpanded by remember { mutableStateOf(initialExpanded) }
    var isActionsMenuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .animateContentSize()
            .taminSurface()
    ) {
        Icon(
            imageVector = vectorResource(if (item.seen) Res.drawable.ic_check_label else Res.drawable.ic_check_label),
            contentDescription = null,
            tint = colors.textPrimary.copy(alpha = 0.50f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(IconSize.xxxlarge)
                .graphicsLayer {
                    translationX = Spacing.smPlus.toPx()
                    translationY = Spacing.badgeVertical.toPx()
                }
        )

        // Vertical Gradient Indicator (Right edge in RTL)
        Box(
            modifier = Modifier
                .width(Spacing.xs)
                .fillMaxHeight()
                .align(Alignment.CenterStart)
                .background(colors.iconGradientPrimary)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // Top Row: Category and Counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(Res.string.inbox_item_header),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.none)
                ) {
                    Icon(
                        imageVector = vectorResource(if (item.seen) Res.drawable.ic_tamin_verified else Res.drawable.ic_tamin_cross),
                        contentDescription = null,
                        tint = if (item.seen) colors.greenText else colors.dangerText.copy(alpha = 0.80f),
                        modifier = Modifier.size(IconSize.medium)
                    )
                    Text(
                        text = if (item.seen) stringResource(Res.string.inbox_status_delivered) else stringResource(Res.string.inbox_status_rejected),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (item.seen) colors.greenText else colors.dangerText.copy(alpha = 0.80f)
                    )
                }
            }
            // Main Content Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                IconTile(
                    modifier = Modifier.border(
                        Thickness.border,
                        colors.border,
                        RoundedCornerShape(CornerRadius.lg)
                    ),
                    icon = vectorResource(Res.drawable.ic_inbox),
                    tint = colors.blueText,
                    background = Brush.verticalGradient(
                        listOf(colors.blueBg, colors.blueBg)
                    ),
                    cornerRadius = CornerRadius.lg,
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    Text(
                        text = item.subject,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 15.sp),
                        color = colors.textPrimary,
                        maxLines = 2
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusPill(
                            text = "سابقه",
                            containerColor = colors.blueBg,
                            contentColor = colors.blueText,
                            fontWeight = FontWeight.Bold
                        )
                        NumericText(
                            text = item.requestDate,
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textMuted
                        )
                    }
                }
            }

            // Tracking Code Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_track),
                    contentDescription = null,
                    tint = colors.textMuted,
                    modifier = Modifier.size(IconSize.small)
                )
                Text(
                    text = stringResource(Res.string.inbox_tracking_code),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(Thickness.border)
                        .drawBehind {
                            drawLine(
                                color = colors.border,
                                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
                            )
                        }
                )

                NumericText(
                    text = item.id.toString().toPersianDigits(),
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textPrimary
                )
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_copy),
                    contentDescription = null,
                    tint = colors.textMuted,
                    modifier = Modifier
                        .size(IconSize.small)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onCopyClick() }
                )
            }

            if (isExpanded) {
                Column {
                    TaminDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                    DetailRow(label = stringResource(Res.string.identity_field_national_code), value =  item.natCode.toPersianDigits())
                    TaminDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                    DetailRow(label = stringResource(Res.string.identity_field_email), value = item.email, numeric = false)
                    TaminDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                    DetailRow(label = stringResource(Res.string.identity_field_mobile), value = item.mobile.toPersianDigits())
                    TaminDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                    DetailRow(label = stringResource(Res.string.inbox_inquiry_password), value = item.permissionPassword.toPersianDigits())
                }
            }

            TaminDivider()

            // Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                TaminOutlinedButton(
                    text = if (isExpanded) stringResource(Res.string.action_hide_details) else stringResource(Res.string.action_show_details),
                    onClick = { isExpanded = !isExpanded },
                    icon = vectorResource(Res.drawable.ic_arrow_show_more),
                    iconModifier = Modifier.graphicsLayer {
                        rotationZ = if (isExpanded) 90f else 270f
                    },
                    containerColor = colors.bgPage,
                    contentColor = colors.blueText,
                    borderColor = Color.Transparent,
                    textStyle = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.weight(1.5f),

                    )
                Box(modifier = Modifier.weight(1f)) {
                    TaminFilledButton(
                        text = stringResource(Res.string.inbox_action_operations),
                        onClick = { isActionsMenuExpanded = true },
                        icon = vectorResource(Res.drawable.ic_setting),
                        background = colors.iconGradientSuccess,
                        iconPosition = IconPosition.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                    RecordActionMenu(
                        expanded = isActionsMenuExpanded,
                        items = actions,
                        onDismiss = { isActionsMenuExpanded = false },
                        onSelect = { action ->
                            isActionsMenuExpanded = false
                            onActionSelect(action)
                        }
                    )
                }
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun InboxItemCardExpandedPreview() {
    PreviewRtlThemeContent {
        Box(modifier = Modifier.fillMaxWidth().padding(Spacing.page)) {
            InboxItemCard(
                item = PersonalInboxItemPR(
                    id = 1,
                    refCode = "۳۱۸۶۲۲۶۲۱",
                    requestDate = "۱۴۰۴/۱۲/۱۹",
                    subject = "اعلام سابقه به مؤسسات",
                    seen = true,
                    system = "سازمان تأمین اجتماعی",
                    passwordCode = "۱۲۳۴۵۶",
                    natCode = "222222",
                    email = "",
                    mobile = "",
                    permissionPassword = "",
                ),
                actions = kotlinx.collections.immutable.persistentListOf(),
                onActionSelect = {},
                initialExpanded = true,
                onCopyClick = {}
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun InboxItemCardExpandedPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        Box(modifier = Modifier.fillMaxWidth().padding(Spacing.page)) {
            InboxItemCard(
                item = PersonalInboxItemPR(
                    id = 1,
                    refCode = "۳۱۸۶۲۲۶۲۱",
                    requestDate = "۱۴۰۴/۱۲/۱۹",
                    subject = "اعلام سابقه به مؤسسات",
                    seen = true,
                    system = "سازمان تأمین اجتماعی",
                    passwordCode = "۱۲۳۴۵۶",
                    natCode = "2222222",
                    email = "",
                    mobile = "",
                    permissionPassword = ""
                ),
                actions = kotlinx.collections.immutable.persistentListOf(),
                onActionSelect = {},
                initialExpanded = true,
                onCopyClick = {}
            )
        }
    }
}
