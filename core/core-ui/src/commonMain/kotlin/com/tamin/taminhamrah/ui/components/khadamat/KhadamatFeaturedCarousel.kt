package com.tamin.taminhamrah.ui.components.khadamat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.common.MenuServiceStatusDN
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.*

@Composable
fun KhadamatFeaturedCarousel(
    featuredServices: List<MainServiceDN>,
    onServiceClick: (MainServiceDN) -> Unit,
    modifier: Modifier = Modifier
) {
    if (featuredServices.isEmpty()) return

    val cardColors = listOf(
        TaminNavy700,
        TaminTeal700,
        TaminPurple700,
        TaminOrange,
        TaminRed
    )

    val fallbackSubtitles = mapOf(
        // Insured tab
        7 to "مشاهده ریز سوابق بیمه‌ای",
        10 to "ثبت اعتراض به سوابق جامانده",
        34 to "ثبت‌نام بیمه دانشجویان",
        35 to "امور قراردادها و پرداخت حق بیمه",
        36 to "ثبت‌نام بیمه زنان خانه‌دار",
        // Pensioner tab
        105 to "فیش حقوقی ماهانه",
        106 to "مشاهده آخرین حکم مستمری",
        107 to "دریافت گواهی حقوق مستمری",
        108 to "صدور گواهی کسر اقساط",
        112 to "ثبت درخواست مستمری بازماندگان",
        // Employer tab
        1001 to "مشاهده لیست کارگاه‌های فعال",
        1002 to "مشاهده و پیگیری قراردادهای پیمان",
        1004 to "ثبت و ویرایش اطلاعات کارفرما",
        1006 to "مشاهده نتایج اعتراضات ثبت شده",
        1010 to "ثبت نام و پرداخت بیمه کارگران"
    )

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "دسترسی سریع",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(horizontal = Spacing.xlg)
                .padding(bottom = Spacing.md)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = Spacing.xlg),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            itemsIndexed(featuredServices) { index, service ->
                val cardColor = cardColors[index % cardColors.size]
                val subtitleText = service.subtitle ?: fallbackSubtitles[service.id] ?: "مشاهده جزئیات سرویس"

                FeaturedServiceCard(
                    service = service,
                    subtitle = subtitleText,
                    backgroundColor = cardColor,
                    onClick = { onServiceClick(service) }
                )
            }
        }
    }
}

@Composable
private fun FeaturedServiceCard(
    service: MainServiceDN,
    subtitle: String,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDisabled = service.status == MenuServiceStatusDN.DISABLED ||
            service.status == MenuServiceStatusDN.TEMPORARY_DISABLED ||
            service.status == MenuServiceStatusDN.COMPLETELY_DISABLED

    val cardAlpha = if (isDisabled) 0.5f else 1.0f

    Column(
        modifier = modifier
            .width(170.dp)
            .height(135.dp)
            .clip(RoundedCornerShape(CornerRadius.x2l))
            .background(backgroundColor)
            .clickable(
                enabled = true,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(Spacing.lg)
            .alpha(cardAlpha),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(IconSize.large)
                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(CornerRadius.lg))
                .padding(Spacing.sm),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getIconForName(service.icon),
                contentDescription = service.name,
                tint = Color.White,
                modifier = Modifier.fillMaxSize()
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = service.name ?: "",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(Spacing.xs))

            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun KhadamatFeaturedCarouselPreview() {
    PreviewRtlThemeContent {
        KhadamatFeaturedCarousel(
            featuredServices = listOf(
                MainServiceDN(id = 7, name = "کلیه سوابق", icon = "bill"),
                MainServiceDN(id = 10, name = "اعتراض به سوابق", icon = "protest"),
                MainServiceDN(id = 34, name = "بیمه دانشجویی", icon = "student")
            ),
            onServiceClick = {}
        )
    }
}
