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


@Composable
fun KhadamatFeaturedCarousel(
    featuredServices: List<MainServiceDN>,
    onServiceClick: (MainServiceDN) -> Unit,
    modifier: Modifier = Modifier
) {
    if (featuredServices.isEmpty()) return

    val cardColors = listOf(
        Color(0xFF1D4ED8),
        Color(0xFF0D9488),
        Color(0xFF7C3AED),
        Color(0xFFEA580C),
        Color(0xFFDB2777)
    )

    val fallbackSubtitles = mapOf(
        7 to "مشاهده ریز سوابق بیمه‌ای",
        10 to "ثبت اعتراض به سوابق جامانده",
        34 to "ثبت‌نام بیمه دانشجویان",
        35 to "امور قراردادها و پرداخت حق بیمه",
        36 to "ثبت‌نام بیمه زنان خانه‌دار"
    )

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "دسترسی سریع",
            color = Color(0xFF64748B),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 12.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
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
            .clip(RoundedCornerShape(22.dp))
            .background(backgroundColor)
            .clickable(
                enabled = true,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(16.dp)
            .alpha(cardAlpha),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                .padding(8.dp),
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

            Spacer(modifier = Modifier.height(4.dp))

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
