package com.tamin.taminhamrah.feature.workshops.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_workshop
import taminx.core.core_ui.workshop_code

/**
 * The frame every screen the workshop list launches into shares: the gradient bar, its back
 * control, and the workshop it is about named underneath.
 *
 * The name and code are what the old app pushed through toolbar bundle keys from screen to
 * screen. Here they are plain parameters, so a destination cannot be opened without them.
 */
@Composable
fun WorkshopScreenShell(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    workshopName: String? = null,
    workshopCode: String? = null,
    action: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = LocalTaminColors.current
    // The same navy the list header wears, so a workshop service does not change color when it
    // is opened from one.
    val headerGradient = remember(colors.profileGradientStops) {
        Brush.horizontalGradient(colors.profileGradientStops)
    }
    Column(modifier = modifier.fillMaxSize()) {
        TaminTopAppBar(
            title = title,
            background = headerGradient,
            navigationIcon = {
                // Both chevrons are autoMirrored, so under RTL the one named "back" is the ">"
                // the design draws.
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = null,
                    onClick = onBack,
                )
            },
            action = action,
        ) {
            if (workshopName != null) {
                WorkshopIdentityCard(name = workshopName, code = workshopCode)
            }
        }
        content()
    }
}

/**
 * Which workshop the screen underneath is about, on the translucent panel the design floats
 * inside the gradient bar.
 */
@Composable
private fun WorkshopIdentityCard(
    name: String,
    code: String?,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val shape = remember { RoundedCornerShape(CornerRadius.xl) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = Spacing.md)
            .clip(shape)
            .background(colors.glassIconTileBg)
            .border(Thickness.border, colors.glassIconTileBorder, shape)
            .padding(horizontal = Spacing.md, vertical = Spacing.smPlus),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.smPlus),
    ) {
        Box(
            modifier = Modifier
                .size(IconTileSize)
                .clip(RoundedCornerShape(CornerRadius.lg))
                .background(colors.glassIconTileBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_workshop),
                contentDescription = null,
                tint = colors.glassIconTileIconTint,
                modifier = Modifier.size(IconSize.banner),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = colors.txtNameProfile,
            )
            if (code != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    Text(
                        text = stringResource(Res.string.workshop_code),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textHeaderSubtitle,
                    )
                    NumericText(
                        text = code,
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.txtNameProfile,
                    )
                }
            }
        }
    }
}

/** `width:36px; height:36px` on the design's identity panel. */
private val IconTileSize = 36.dp
