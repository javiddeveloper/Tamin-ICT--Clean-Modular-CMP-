package com.tamin.taminhamrah.feature.profile.ui.versionHistory.components

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.taminTopAppBarGradient
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.DarkTaminColors
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_history
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.profile_version_history
import taminx.core.core_ui.profile_version_history_last_updated

@Composable
internal fun VersionHistoryHeader(
    lastUpdatedDate: String,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    val isDark = taminColors == DarkTaminColors

    val topBarGradient = remember(isDark) { Brush.horizontalGradient(taminColors.profileGradientStops) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraLarge)
            .background(taminTopAppBarGradient(taminColors.profileGradientStops))
            .padding(bottom = Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TaminTopAppBar(
            title = stringResource(Res.string.profile_version_history),
            centerTitle = true,
            background = topBarGradient,
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = null,
                    onClick = onBackClicked,
                    modifier = Modifier
                )
            },
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        // Hero Icon with concentric rings
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(vertical = Spacing.xs)
        ) {
            // Outer ring
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
                    .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
            )
            // Middle ring
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(Color.White.copy(alpha = 0.12f), CircleShape)
                    .border(1.dp, Color.White.copy(alpha = 0.22f), CircleShape)
            )
            // Inner icon container
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    .border(1.dp, Color.White.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_history),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.sm))

        // Subtitle
        Text(
            text = stringResource(Res.string.profile_version_history_last_updated, lastUpdatedDate),
            style = MaterialTheme.typography.bodySmall,
            color = taminColors.txtNatProfile
        )
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun PreviewVersionHistoryHeader() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        VersionHistoryHeader(
            lastUpdatedDate = "۳۰ فروردین ۱۴۰۵",
            onBackClicked = {}
        )
    }
}

