package com.tamin.taminhamrah.feature.orotezprotez.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import com.tamin.taminhamrah.ui.components.GlassIconTile
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_help
import taminx.core.core_ui.ic_info
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.orotez_protez_subtitle
import taminx.core.core_ui.orotez_protez_title

/**
 * The gradient hero bar: back on the right, a decorative help affordance on the left (no target
 * screen exists for it yet, so it is a no-op like the equivalent button in
 * [com.tamin.taminhamrah.feature.profile.ui.contactUs.components.ContactUsHeader]), and the
 * feature's own icon and subtitle underneath — the same icon already assigned to this service in
 * the main menu (see `ServiceCard.getIconForName("crutch")`).
 */
@Composable
internal fun OrotezProtezHeader(
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val gradient = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = CornerRadius.x3l, bottomEnd = CornerRadius.x3l))
            .background(gradient)
            .padding(bottom = Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TaminTopAppBar(
            title = stringResource(Res.string.orotez_protez_title),
            background = gradient,
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = null,
                    onClick = onBackClicked,
                    bordered = true,
                )
            },
            action = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_help),
                    contentDescription = null,
                    onClick = {},
                    bordered = true,
                )
            },
        )

        Spacer(Modifier.height(Spacing.smPlus))
        GlassIconTile(icon = Icons.Filled.Accessibility)
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = stringResource(Res.string.orotez_protez_subtitle),
            style = MaterialTheme.typography.labelLarge,
            color = taminColors.textHeaderSubtitle,
        )
    }
}
