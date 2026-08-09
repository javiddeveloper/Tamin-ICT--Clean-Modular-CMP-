package com.tamin.taminhamrah.feature.profile.ui.contactUs.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contact_us_subtitle
import taminx.core.core_ui.contact_us_title
import taminx.core.core_ui.ic_support
import taminx.core.core_ui.ic_tamin_chevron_back

@Composable
fun ContactUsHeader(
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    val profileGradientBrush = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }

    TaminTopAppBar(
        modifier = modifier,
        title = stringResource(Res.string.contact_us_title),
        background = profileGradientBrush,
        bottomPadding = Spacing.xl,
        shape = RoundedCornerShape(
            bottomStart = 40.dp,
            bottomEnd = 40.dp
        ),
        navigationIcon = {
            TaminTopAppBarButton(
                icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                contentDescription = null,
                onClick = onBackClicked,
                bordered = true
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            DecorativeBackgroundCircle(
                size = 190.dp,
                xOffset = 450.dp,
                yOffset = (-150).dp
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedRingHeaderIcon(icon = vectorResource(Res.drawable.ic_support))
                Spacer(modifier = Modifier.height(Spacing.md))
                Text(
                    text = stringResource(Res.string.contact_us_subtitle),
                    style = MaterialTheme.typography.labelLarge,
                    color = taminColors.textHeaderSubtitle
                )
            }
        }
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun PreviewContactUsHeaderLight() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        ContactUsHeader(onBackClicked = {})
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun PreviewContactUsHeaderDark() {
    com.tamin.taminhamrah.ui.theme.TaminHamrahTheme(darkTheme = true) {
        ContactUsHeader(onBackClicked = {})
    }
}
