package com.tamin.taminhamrah.feature.myinbox.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_arrow_show_more
import taminx.core.core_ui.ic_inbox
import taminx.core.core_ui.ic_setting
import taminx.core.core_ui.ic_tamin_copy
import taminx.core.core_ui.ic_tamin_track
import taminx.core.core_ui.ic_tamin_verified

@Composable
fun InboxItemCard(
    item: PersonalInboxItemPR,
    onActionsClick: () -> Unit,
    modifier: Modifier = Modifier,
    initialExpanded: Boolean = false
) {
    val colors = LocalTaminColors.current
    var isExpanded by remember { mutableStateOf(initialExpanded) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .animateContentSize()
            .taminSurface()
    ) {
        // Vertical Gradient Indicator (Right edge in RTL)
        Box(
            modifier = Modifier
                .width(4.dp)
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "سند رسمی • سازمان تأمین اجتماعی",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted
                )
                NumericText(
                    text = "۰۱ / ۰۱",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted
                )
            }

            // Main Content Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                IconTile(
                    modifier = Modifier.border(
                        1.dp,
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

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
                    modifier = Modifier.width(64.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .drawBehind {
                                drawCircle(
                                    brush = Brush.verticalGradient(
                                        listOf(colors.border, Color.Transparent)
                                    ),
                                    style = Stroke(
                                        width = 1.dp.toPx(),
                                        pathEffect = PathEffect.dashPathEffect(
                                            floatArrayOf(5f, 5f),
                                            0f
                                        )
                                    )
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.ic_tamin_verified),
                            contentDescription = null,
                            tint = colors.greenText,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = "تحویل شد",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.greenText
                    )
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
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "کد پیگیری",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
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
                    text = item.refCode,
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textPrimary
                )
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_copy),
                    contentDescription = null,
                    tint = colors.textMuted,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { /* TODO: Copy to clipboard */ }
                )
            }

            if (isExpanded) {
                Column {
                    TaminDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                    DetailRow(label = "کد ملی", value = "۵۵۸۹۷۴۳۴۵۱")
                    TaminDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                    DetailRow(label = "پست الکترونیک", value = "—", numeric = false)
                    TaminDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                    DetailRow(label = "شماره تلفن همراه", value = "۰۹۱۸۶۴۵۳۵۱۱")
                    TaminDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                    DetailRow(label = "کد رمز استعلام", value = item.passwordCode.ifEmpty { "—" })
                }
            }

            TaminDivider()

            // Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                TaminOutlinedButton(
                    text = if (isExpanded) "مخفی کردن جزئیات" else "نمایش جزئیات",
                    onClick = { isExpanded = !isExpanded },
                    icon = vectorResource(Res.drawable.ic_arrow_show_more),
                    iconModifier = Modifier.graphicsLayer {
                        rotationZ = if (isExpanded) 90f else 270f
                    },
                    containerColor = colors.bgPage,
                    contentColor = colors.blueText,
                    borderColor = Color.Transparent,
                    textStyle = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.weight(1.5f),

                    )
                TaminFilledButton(
                    text = "عملیات",
                    onClick = onActionsClick,
                    icon = vectorResource(Res.drawable.ic_setting),
                    background = colors.iconGradientSuccess,
                    iconPosition = IconPosition.End,
                    modifier = Modifier.weight(1f)
                )
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
                    passwordCode = "۱۲۳۴۵۶"
                ),
                onActionsClick = {},
                initialExpanded = true
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
                    passwordCode = "۱۲۳۴۵۶"
                ),
                onActionsClick = {},
                initialExpanded = true
            )
        }
    }
}
