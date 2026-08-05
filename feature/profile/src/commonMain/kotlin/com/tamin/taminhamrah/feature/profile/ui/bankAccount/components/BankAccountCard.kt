package com.tamin.taminhamrah.feature.profile.ui.bankAccount.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.bankAccount.BankAccountPR
import com.tamin.taminhamrah.model.bankAccount.cardGradient
import com.tamin.taminhamrah.model.bankAccount.cardGradientMidStop
import com.tamin.taminhamrah.model.bankAccount.ink
import com.tamin.taminhamrah.model.bankAccount.pillBackground
import com.tamin.taminhamrah.model.bankAccount.subInk
import com.tamin.taminhamrah.model.bankAccount.watermarkAlpha
import com.tamin.taminhamrah.model.bankAccount.watermarkGlow
import com.tamin.taminhamrah.ui.theme.TaminGreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.foundation.Image
import androidx.compose.ui.platform.LocalLayoutDirection
import taminx.core.core_ui.Res
import taminx.core.core_ui.bank_account_active
import taminx.core.core_ui.bank_account_end_date
import taminx.core.core_ui.bank_account_no_date
import taminx.core.core_ui.bank_account_start_date

private val CardHeight = 200.dp
private val CardCorner = 22.dp
private val BadgeSize = 40.dp
private val BadgeLogoSize = 29.dp
private val WatermarkSize = 158.dp
private val HaloSize = 210.dp

/**
 * One registered account.
 *
 * Every string arrives finished from the mapper, and the two gradients are remembered on the
 * palette rather than rebuilt per composition — a list of these scrolls, so nothing here may
 * allocate on the way past.
 */
@Composable
fun BankAccountCard(
    account: BankAccountPR,
    modifier: Modifier = Modifier,
) {
    val bank = account.bank
    val gradient = bank.cardGradient
    val midStop = bank.cardGradientMidStop
    val ink = bank.ink
    val subInk = bank.subInk

    val surface = remember(gradient, midStop) {
        Brush.linearGradient(
            0f to gradient.first(),
            midStop to gradient[1],
            1f to gradient.last(),
        )
    }
    // The design's directional light: a bright corner falling to a dark one.
    val sheen = remember {
        Brush.linearGradient(
            0f to Color.White.copy(alpha = 0.14f),
            0.38f to Color.Transparent,
            1f to Color.Black.copy(alpha = 0.10f),
        )
    }
    val halo = remember(bank) {
        Brush.radialGradient(listOf(bank.watermarkGlow, Color.Transparent))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(CardHeight)
            .clip(RoundedCornerShape(CardCorner))
            .drawBehind {
                drawRect(surface)
                drawRect(sheen)
            }
            .padding(horizontal = 20.dp, vertical = 18.dp),
    ) {
        if (bank != null) {
            BankWatermark(account = account, halo = halo)
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (bank != null) {
                        Box(
                            modifier = Modifier
                                .size(BadgeSize)
                                .clip(RoundedCornerShape(11.dp))
                                .background(Color.White.copy(alpha = 0.94f)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                painter = painterResource(bank.logo),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(BadgeLogoSize),
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                    }
                    Text(
                        text = bank?.label?.let { stringResource(it) }
                            ?: account.bankNameFallback.orEmpty(),
                        style = MaterialTheme.typography.titleMedium,
                        color = ink,
                    )
                }

                if (account.isActive) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(TaminGreen),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = stringResource(Res.string.bank_account_active),
                            style = MaterialTheme.typography.labelSmall,
                            color = subInk,
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            val typeLabel = account.accountType?.label?.let { stringResource(it) }
                ?: account.accountTypeNameFallback
            if (typeLabel != null) {
                Text(
                    text = typeLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = ink,
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .background(bank.pillBackground)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                )
            }

            // The number sits in the card's center, reading left-to-right like the printed one.
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Text(
                        text = account.accountNumber,
                        style = MaterialTheme.typography.headlineSmall,
                        color = ink,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                CardDate(
                    label = stringResource(Res.string.bank_account_start_date),
                    value = account.startDate,
                    ink = ink,
                    subInk = subInk,
                )
                CardDate(
                    label = stringResource(Res.string.bank_account_end_date),
                    value = account.endDate,
                    ink = ink,
                    subInk = subInk,
                )
            }
        }
    }
}

@Composable
private fun BoxScope.BankWatermark(account: BankAccountPR, halo: Brush) {
    val bank = account.bank ?: return
    Box(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .offset(x = (-46).dp, y = 52.dp)
            .size(HaloSize)
            .clip(CircleShape)
            .drawBehind { drawCircle(halo) },
    )
    Image(
        painter = painterResource(bank.logo),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        alpha = bank.watermarkAlpha,
        modifier = Modifier
            .align(Alignment.BottomStart)
            .offset(x = (-18).dp, y = 24.dp)
            .size(WatermarkSize),
    )
}

@Composable
private fun CardDate(label: String, value: String?, ink: Color, subInk: Color) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = subInk)
        Spacer(Modifier.height(3.dp))
        Text(
            text = value ?: stringResource(Res.string.bank_account_no_date),
            style = MaterialTheme.typography.bodyMedium,
            color = ink,
        )
    }
}
