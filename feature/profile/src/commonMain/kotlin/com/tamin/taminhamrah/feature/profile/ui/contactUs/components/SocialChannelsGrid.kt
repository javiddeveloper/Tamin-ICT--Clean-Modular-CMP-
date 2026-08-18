package com.tamin.taminhamrah.feature.profile.ui.contactUs.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.model.contactUs.SocialChannelPR
import com.tamin.taminhamrah.model.contactUs.SocialChannelTypePR
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contact_us_social_section
import taminx.feature.profile.generated.resources.Res.drawable
import taminx.feature.profile.generated.resources.ic_aparat
import taminx.feature.profile.generated.resources.ic_bale
import taminx.feature.profile.generated.resources.ic_eitaa
import taminx.feature.profile.generated.resources.ic_email
import taminx.feature.profile.generated.resources.ic_faq
import taminx.feature.profile.generated.resources.ic_gap
import taminx.feature.profile.generated.resources.ic_igap
import taminx.feature.profile.generated.resources.ic_rubika
import taminx.feature.profile.generated.resources.ic_soroush
import taminx.feature.profile.generated.resources.ic_whatsapp

@Composable
fun SocialChannelsGrid(
    channels: ImmutableList<SocialChannelPR>,
    onChannelClick: (SocialChannelPR) -> Unit,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(Res.string.contact_us_social_section),
            style = MaterialTheme.typography.titleSmall.copy(
                fontSize = 13.sp,
                color = taminColors.textSecondary
            ),
            modifier = Modifier.padding(horizontal = Spacing.xs, vertical = Spacing.xs)
        )

        Spacer(modifier = Modifier.height(Spacing.xs))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = 5
        ) {
            channels.forEach { channel ->
                SocialChannelTile(
                    channel = channel,
                    onClick = { onChannelClick(channel) },
                    modifier = Modifier.width(64.dp)
                )
            }
        }
    }
}

@Composable
private fun SocialChannelTile(
    channel: SocialChannelPR,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(14.dp))
                .background(taminColors.bgSurface)
                .border(1.dp, taminColors.border, RoundedCornerShape(14.dp))
                .clickable(
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            SocialChannelIcon(type = channel.type)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = channel.title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                color = taminColors.textSecondary
            ),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SocialChannelIcon(
    type: SocialChannelTypePR,
    modifier: Modifier = Modifier
) {

    val iconVector = remember(type) { getSocialVector(type) }

    Image(
        painter = painterResource(iconVector),
        contentDescription = null,
        modifier = modifier.size(24.dp)
    )
}


private fun getSocialVector(type: SocialChannelTypePR): DrawableResource {

    when (type) {
        SocialChannelTypePR.EMAIL -> {
            return drawable.ic_email
        }

        SocialChannelTypePR.FAQ -> {
            return drawable.ic_faq
        }

        SocialChannelTypePR.WHATSAPP -> {
            return drawable.ic_whatsapp
        }

        SocialChannelTypePR.IGAP -> {
            return drawable.ic_igap
        }

        SocialChannelTypePR.BALE -> {
            return drawable.ic_bale
        }

        SocialChannelTypePR.APARAT -> {
            return drawable.ic_aparat
        }

        SocialChannelTypePR.GAP -> {
            return drawable.ic_gap
        }

        SocialChannelTypePR.SOROUSH -> {
            return drawable.ic_soroush
        }

        SocialChannelTypePR.RUBIKA -> {
            return drawable.ic_rubika
        }

        SocialChannelTypePR.EITAA -> {
            return drawable.ic_eitaa
        }
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun PreviewSocialChannelsGridLight() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        SocialChannelsGrid(
            channels = PreviewSampleSocialChannels,
            onChannelClick = {}
        )
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun PreviewSocialChannelsGridDark() {
    com.tamin.taminhamrah.ui.theme.TaminHamrahTheme(darkTheme = true) {
        SocialChannelsGrid(
            channels = PreviewSampleSocialChannels,
            onChannelClick = {}
        )
    }
}

private val PreviewSampleSocialChannels = kotlinx.collections.immutable.persistentListOf(
    SocialChannelPR("1", "ایمیل", SocialChannelTypePR.EMAIL, "mailto:info@tamin.ir"),
    SocialChannelPR("2", "سوالات متداول", SocialChannelTypePR.FAQ, "https://tamin.ir/faq"),
    SocialChannelPR("3", "واتساپ", SocialChannelTypePR.WHATSAPP, "https://wa.me/989000000000"),
    SocialChannelPR("4", "آی‌گپ", SocialChannelTypePR.IGAP, "https://igap.net/tamin"),
    SocialChannelPR("5", "بله", SocialChannelTypePR.BALE, "https://ble.ir/tamin"),
    SocialChannelPR("6", "آپارات", SocialChannelTypePR.APARAT, "https://aparat.com"),
    SocialChannelPR("7", "گپ", SocialChannelTypePR.GAP, "https://gap.im/tamin"),
    SocialChannelPR("8", "سروش", SocialChannelTypePR.SOROUSH, "https://splus.ir/tamin"),
    SocialChannelPR("9", "روبیکا", SocialChannelTypePR.RUBIKA, "https://rubika.ir/tamin"),
    SocialChannelPR("10", "ایتا", SocialChannelTypePR.EITAA, "https://eitaa.com/tamin")
)
