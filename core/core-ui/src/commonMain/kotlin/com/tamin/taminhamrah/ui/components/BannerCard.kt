package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.IconSize
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_error
import taminx.core.core_ui.ic_info
import taminx.core.core_ui.ic_success
import taminx.core.core_ui.ic_warning
import com.tamin.taminhamrah.ui.theme.TaminTealBg
import com.tamin.taminhamrah.ui.theme.TaminTeal900


enum class BannerType {
    Info,
    Warning,
    Error,
    Success,

    /** A fact about the data rather than a verdict on it — the calm teal note. */
    Tip,
}

@Composable
fun BannerCard(
    message: String,
    modifier: Modifier = Modifier,
    type: BannerType = BannerType.Info,
    icon: ImageVector? = null,
    showIcon: Boolean = true
) {
    val taminColors = LocalTaminColors.current
    val (bgColor, contentColor, defaultIcon) = when (type) {
        BannerType.Info -> Triple(taminColors.blueBg, taminColors.blueText, vectorResource(Res.drawable.ic_info))
        BannerType.Warning -> Triple(taminColors.orangeBg, taminColors.orangeText, vectorResource(Res.drawable.ic_warning))
        BannerType.Error -> Triple(taminColors.dangerBg, taminColors.dangerText, vectorResource(Res.drawable.ic_error))
        BannerType.Success -> Triple(taminColors.greenBg, taminColors.greenText, vectorResource(Res.drawable.ic_success))
        // Neither good news nor bad — a fact about the data, which the design paints teal.
        BannerType.Tip -> Triple(TaminTealBg, TaminTeal900, vectorResource(Res.drawable.ic_info))
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(CornerRadius.listRow))
            .border(1.dp, contentColor.copy(alpha = 0.1f), RoundedCornerShape(CornerRadius.listRow))
            .padding(horizontal = Spacing.smd, vertical = Spacing.md),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start
    ) {
        if (showIcon) {
            Icon(
                imageVector = icon ?: defaultIcon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier
                    .size(IconSize.banner)
                    .padding(top = Spacing.xxs)
            )
            Spacer(modifier = Modifier.width(Spacing.sm))
        }
        TaminText(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = contentColor,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview
@Composable
private fun BannerCardLightPreview() {
    TaminHamrahTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(LocalTaminColors.current.bgSurface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BannerCard(
                message = "این یک پیام اطلاع‌رسانی در حالت روشن است.",
                type = BannerType.Info
            )
            BannerCard(
                message = "هشدار: این یک پیام هشدار در حالت روشن است.",
                type = BannerType.Warning
            )
            BannerCard(
                message = "خطا: مشکلی در فرآیند به وجود آمده است.",
                type = BannerType.Error
            )
            BannerCard(
                message = "موفقیت: عملیات با موفقیت انجام شد.",
                type = BannerType.Success
            )
        }
    }
}

@Preview
@Composable
private fun BannerCardDarkPreview() {
    TaminHamrahTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(LocalTaminColors.current.bgSurface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BannerCard(
                message = "این یک پیام اطلاع‌رسانی در حالت تیره است.",
                type = BannerType.Info
            )
            BannerCard(
                message = "هشدار: این یک پیام هشدار در حالت تیره است.",
                type = BannerType.Warning
            )
            BannerCard(
                message = "خطا: مشکلی در فرآیند به وجود آمده است.",
                type = BannerType.Error
            )
            BannerCard(
                message = "موفقیت: عملیات با موفقیت انجام شد.",
                type = BannerType.Success
            )
        }
    }
}
