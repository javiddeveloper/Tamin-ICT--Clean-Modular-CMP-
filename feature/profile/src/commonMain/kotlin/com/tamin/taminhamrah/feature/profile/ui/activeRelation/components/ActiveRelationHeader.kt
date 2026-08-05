package com.tamin.taminhamrah.feature.profile.ui.activeRelation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.taminTopAppBarGradient
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.DarkTaminColors
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_communication
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.profile_active_relation

@Composable
internal fun ActiveRelationHeader(
    activeCount: Int,
    inactiveCount: Int,
    lastCheckTime: String,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    val isDark = taminColors == DarkTaminColors

    val topBarGradient =
        remember(isDark) { Brush.horizontalGradient(taminColors.profileGradientStops) }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    bottomEnd = CornerRadius.chip,
                    bottomStart = CornerRadius.chip
                )
            )
            .background(taminTopAppBarGradient(taminColors.profileGradientStops))
            .padding(bottom = Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TaminTopAppBar(
            title = stringResource(Res.string.profile_active_relation),
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

        AnimatedRingHeaderIcon(
            icon = vectorResource(Res.drawable.ic_communication)
        )

        Spacer(modifier = Modifier.height(Spacing.sm))

        val statusText = if (activeCount > 0) "ارتباط شما برقرار است" else "ارتباط شما برقرار نیست"
        val statusColor =
            if (activeCount > 0) taminColors.springGreenText else taminColors.dangerText

        Row(
            modifier = Modifier.padding(bottom = Spacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        if (activeCount > 0) taminColors.springGreenText else taminColors.textMuted,
                        RoundedCornerShape(50)
                    )
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = statusText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = statusColor
            )
        }

        val activeText = "${activeCount.toString().toPersianDigits()} ارتباط فعال"
        val inactiveText = "${inactiveCount.toString().toPersianDigits()} ارتباط غیرفعال"
        val checkTimeText = "بررسی: امروز $lastCheckTime"

        Text(
            text = "$activeText · $inactiveText · $checkTimeText",
            style = MaterialTheme.typography.bodySmall,
            color = taminColors.txtNatProfile
        )
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun PreviewActiveRelationHeader() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        ActiveRelationHeader(
            activeCount = 1,
            inactiveCount = 0,
            lastCheckTime = "۱۰:۲۴",
            onBackClicked = {}
        )
    }
}
