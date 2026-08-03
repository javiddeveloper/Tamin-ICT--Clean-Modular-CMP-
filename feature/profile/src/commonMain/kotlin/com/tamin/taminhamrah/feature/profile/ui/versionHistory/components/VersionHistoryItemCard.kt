package com.tamin.taminhamrah.feature.profile.ui.versionHistory.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.versionHistory.VersionHistoryPR
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits

@Composable
internal fun VersionHistoryItemCard(
    item: VersionHistoryPR,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    val rotationState by animateFloatAsState(targetValue = if (item.isExpanded) 180f else 0f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onToggleExpand() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = taminColors.bgSurface
        ),
        border = BorderStroke(1.dp, taminColors.border)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        Text(
                            text = "نسخه ${item.versionName.toPersianDigits()}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = taminColors.blueText
                        )

                        if (item.isLatest) {
                            LatestBadge()
                        }
                    }

                    // Expand/Collapse Chevron Button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(taminColors.bgPage, RoundedCornerShape(10.dp))
                            .border(1.dp, taminColors.border, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = taminColors.textSecondary,
                            modifier = Modifier
                                .size(24.dp)
                                .rotate(rotationState)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.xs))

                Text(
                    text = "تاریخ انتشار: ${item.releaseDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = taminColors.textMuted
                )
            }

            // Expanded Details Section
            AnimatedVisibility(
                visible = item.isExpanded && (item.newFeatures.isNotEmpty() || item.debug.isNotEmpty()),
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.lg)
                        .padding(bottom = Spacing.lg)
                ) {
                    TaminDivider(modifier = Modifier.padding(bottom = Spacing.md))

                    var itemCounter = 1

                    // New Features Section
                    if (item.newFeatures.isNotEmpty()) {
                        Text(
                            text = "ویژگی‌های جدید",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = taminColors.teal
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))

                        item.newFeatures.forEach { featureText ->
                            ChangesRow(
                                number = itemCounter++,
                                text = featureText
                            )
                        }
                    }

                    if (item.newFeatures.isNotEmpty() && item.debug.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(Spacing.md))
                    }

                    // Bug Fixes & Improvements Section
                    if (item.debug.isNotEmpty()) {
                        Text(
                            text = "رفع اشکال و بهینه‌سازی",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = taminColors.orangeText
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))

                        item.debug.forEach { debugText ->
                            ChangesRow(
                                number = itemCounter++,
                                text = debugText
                            )
                        }
                    }
                }
            }

            // Bottom Accent Indicator Line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(taminColors.blueText)
            )
        }
    }
}

@Composable
private fun ChangesRow(
    number: Int,
    text: String,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        // Number Badge
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(taminColors.blueBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            NumericText(
                text = number.toString().toPersianDigits(),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = taminColors.blueText
            )
        }

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = taminColors.textPrimary
        )
    }
}

@Composable
private fun LatestBadge(modifier: Modifier = Modifier) {
    val taminColors = LocalTaminColors.current
    Box(
        modifier = modifier
            .background(taminColors.greenBg, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "جدیدترین",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = taminColors.greenText
        )
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun PreviewVersionHistoryItemCardExpanded() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        VersionHistoryItemCard(
            item = VersionHistoryPR(
                versionName = "1.12.3",
                versionCode = 53,
                releaseDate = "یکشنبه - 30 فروردین 1405",
                isLatest = true,
                newFeatures = listOf("افزودن بخش جدید"),
                debug = listOf(
                    "بهبود فرایند ورود به اپلیکیشن",
                    "بهبود رابط کاربری",
                    "رفع برخی مشکلات گزارش شده"
                ),
                isExpanded = true
            ),
            onToggleExpand = {}
        )
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun PreviewVersionHistoryItemCardCollapsed() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        VersionHistoryItemCard(
            item = VersionHistoryPR(
                versionName = "1.12.2",
                versionCode = 52,
                releaseDate = "شنبه - 04 بهمن 1404",
                isLatest = false,
                newFeatures = emptyList(),
                debug = emptyList(),
                isExpanded = false
            ),
            onToggleExpand = {}
        )
    }
}
