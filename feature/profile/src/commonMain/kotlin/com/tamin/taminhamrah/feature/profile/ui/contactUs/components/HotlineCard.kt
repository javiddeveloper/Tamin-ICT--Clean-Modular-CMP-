package com.tamin.taminhamrah.feature.profile.ui.contactUs.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.model.contactUs.HotlinePR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.CustomChip
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.DarkTaminColors
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contact_us_btn_call
import taminx.core.core_ui.ic_mobile

@Composable
fun HotlineCard(
    hotline: HotlinePR,
    onCallClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current

    val gradient = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(gradient)
            .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(20.dp))
            .padding(horizontal = Spacing.lg, vertical = Spacing.md)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(CornerRadius.lg))
                        .background(Color.White.copy(alpha = 0.15f))
                        .border(
                            1.dp,
                            Color.White.copy(alpha = 0.25f),
                            RoundedCornerShape(CornerRadius.lg)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_mobile),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(Spacing.sm))
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = hotline.title,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = hotline.number,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            textAlign = TextAlign.End
                        )
                    )
                }
            }
            CustomChip(
                text = stringResource(Res.string.contact_us_btn_call),
                modifier = Modifier.height(40.dp).clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) { onCallClicked() },
                containerColor = Color.White,
                textColor = taminColors.blueText,
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewHotlineCardLight() {
    PreviewRtlThemeContent {
        HotlineCard(
            hotline = HotlinePR(
                title = "مرکز تماس شبانه‌روزی",
                number = "۱۴۲۰",
                dialNumber = "1420"
            ),
            onCallClicked = {}
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewHotlineCardDark() {
    TaminHamrahTheme(darkTheme = true) {
        HotlineCard(
            hotline = HotlinePR(
                title = "مرکز تماس شبانه‌روزی",
                number = "۱۴۲۰",
                dialNumber = "1420"
            ),
            onCallClicked = {}
        )
    }
}
