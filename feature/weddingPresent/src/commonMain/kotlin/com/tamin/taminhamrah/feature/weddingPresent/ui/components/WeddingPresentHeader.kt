package com.tamin.taminhamrah.feature.weddingPresent.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.HeaderDecoration
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_calculator
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.wedding_present_calculate_cd
import taminx.core.core_ui.wedding_present_subtitle
import taminx.core.core_ui.wedding_present_title

@Composable
internal fun WeddingPresentHeader(
    onBackClicked: () -> Unit,
    onCalculateClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val gradient = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }

    TaminTopAppBar(
        title = stringResource(Res.string.wedding_present_title),
        modifier = modifier,
        background = gradient,
        shape = RoundedCornerShape(bottomStart = CornerRadius.x3l, bottomEnd = CornerRadius.x3l),
        bottomPadding = Spacing.smPlus,
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
                icon = vectorResource(Res.drawable.ic_calculator),
                contentDescription = stringResource(Res.string.wedding_present_calculate_cd),
                onClick = onCalculateClicked,
                bordered = true,
            )
        },
    ) {
        DecorativeBackgroundCircle(
            size = HeaderDecoration.circleSize,
            xOffset = HeaderDecoration.circleXOffset,
            yOffset = HeaderDecoration.circleYOffset,
        )
        AnimatedRingHeaderIcon(
            icon = Icons.Filled.CardGiftcard,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        Text(
            text = stringResource(Res.string.wedding_present_subtitle),
            style = MaterialTheme.typography.labelLarge,
            color = taminColors.textHeaderSubtitle,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}
