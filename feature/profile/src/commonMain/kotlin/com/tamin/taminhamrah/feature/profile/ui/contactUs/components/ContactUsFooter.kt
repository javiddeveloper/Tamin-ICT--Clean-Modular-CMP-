package com.tamin.taminhamrah.feature.profile.ui.contactUs.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contact_us_footer_subtitle
import taminx.core.core_ui.contact_us_footer_title
import taminx.core.core_ui.ic_support

@Composable
fun ContactUsFooter(
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(taminColors.chipBg),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_support),
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                colorFilter = ColorFilter.tint(color = taminColors.blueText)
            )
        }

        Spacer(modifier = Modifier.height(Spacing.xs))

        Text(
            text = stringResource(Res.string.contact_us_footer_title),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = taminColors.textPrimary
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = stringResource(Res.string.contact_us_footer_subtitle),
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                color = taminColors.textSecondary
            ),
            textAlign = TextAlign.Center
        )
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun PreviewContactUsFooterLight() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        ContactUsFooter()
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun PreviewContactUsFooterDark() {
    com.tamin.taminhamrah.ui.theme.TaminHamrahTheme(darkTheme = true) {
        ContactUsFooter()
    }
}
