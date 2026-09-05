package com.tamin.taminhamrah.feature.pregnancyPay.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.pregnancy_pay_subtitle
import taminx.core.core_ui.pregnancy_pay_title

@Composable
internal fun PregnancyPayHeader(
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val gradient = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }

    TaminTopAppBar(
        title = stringResource(Res.string.pregnancy_pay_title),
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
    ) {
        DecorativeBackgroundCircle(
            size = 190.dp,
            xOffset = 450.dp,
            yOffset = (-150).dp
        )
        AnimatedRingHeaderIcon(
            icon = Icons.Filled.Favorite,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Text(
            text = stringResource(Res.string.pregnancy_pay_subtitle),
            style = MaterialTheme.typography.labelLarge,
            color = taminColors.textHeaderSubtitle,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}
