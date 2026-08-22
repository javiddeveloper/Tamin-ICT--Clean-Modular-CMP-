package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.LocalTaminColors

/**
 * The glass-morphic bottom action bar used by multi-step forms: a primary button
 * (optionally switching to a spinner via [isPrimaryLoading]) and an optional secondary
 * square icon button. Shared by occurrence reporting and health self-declaration (see
 * `.claude/rules/architecture.md`).
 */
@Composable
fun TaminBottomActionBar(
    primaryText: String,
    onPrimaryClick: () -> Unit,
    primaryEnabled: Boolean = true,
    /** Non-null switches the primary action to [LoadingButton], spinning while true. */
    isPrimaryLoading: Boolean? = null,
    showChevron: Boolean = true,
    secondaryText: String? = null,
    onSecondaryClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val navBarBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val imeBottom = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
    val bottomInset = maxOf(navBarBottom, imeBottom)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(taminColors.glassSolid)
            .padding(
                start = 12.dp,
                top = 14.dp,
                end = 12.dp,
                bottom = 14.dp + bottomInset
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (secondaryText != null && onSecondaryClick != null) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .border(1.5.dp, taminColors.border, RoundedCornerShape(15.dp))
                        .clip(RoundedCornerShape(15.dp))
                        .clickable { onSecondaryClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (isPrimaryLoading != null) {
                LoadingButton(
                    text = primaryText,
                    onClick = onPrimaryClick,
                    modifier = Modifier.weight(1f),
                    enabled = primaryEnabled,
                    isLoading = isPrimaryLoading,
                    iconPosition = LoadingButtonIconPosition.TRAILING,
                    icon = if (showChevron) Icons.AutoMirrored.Filled.KeyboardArrowRight else null,
                )
            } else {
                TaminFilledButton(
                    text = primaryText,
                    onClick = onPrimaryClick,
                    enabled = primaryEnabled,
                    modifier = Modifier.weight(1f),
                    icon = if (showChevron) Icons.AutoMirrored.Filled.KeyboardArrowRight else null
                )
            }
        }
    }
}
