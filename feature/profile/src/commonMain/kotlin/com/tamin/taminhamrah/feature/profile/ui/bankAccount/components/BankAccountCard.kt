package com.tamin.taminhamrah.feature.profile.ui.bankAccount.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import com.tamin.taminhamrah.model.bankAccount.BankAccountPR
import com.tamin.taminhamrah.model.bankAccount.cardGradient
import com.tamin.taminhamrah.model.bankAccount.cardInk
import com.tamin.taminhamrah.model.bankAccount.cardNumberInk
import com.tamin.taminhamrah.model.bankAccount.pillBackground
import com.tamin.taminhamrah.model.bankAccount.subInk
import com.tamin.taminhamrah.model.bankAccount.watermarkAlpha
import com.tamin.taminhamrah.ui.components.CustomChip
import com.tamin.taminhamrah.ui.theme.TaminGreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.bank_account_active
import taminx.core.core_ui.bank_account_end_date
import taminx.core.core_ui.bank_account_no_date
import taminx.core.core_ui.bank_account_start_date

// sheen(): radial highlight at 86%/6% fading by 44%, then a banded 112deg linear.
private const val SPOT_X = 0.86f
private const val SPOT_Y = 0.06f
private const val SPOT_RADIUS = 0.44f
private const val BAND_ANGLE_DEGREES = 112f
private const val HALF_TURN_DEGREES = 180f

private val SheenSpotColors = listOf(Color.White.copy(alpha = 0.55f), Color.Transparent)

/** Hard-edged bands: each pair repeats a stop so the color steps rather than blends. */
private val SheenBandStops = arrayOf(
    0f to Color.White.copy(alpha = 0.30f),
    0.18f to Color.White.copy(alpha = 0.30f),
    0.185f to Color.Transparent,
    0.38f to Color.Transparent,
    0.385f to Color.White.copy(alpha = 0.16f),
    0.48f to Color.White.copy(alpha = 0.16f),
    0.485f to Color.Transparent,
    1f to Color.Transparent,
)

private val CardHeight = 200.dp
private val CardCorner = 22.dp
private val CardPaddingX = 20.dp
private val CardPaddingY = 18.dp
private val BadgeSize = 40.dp
private val BadgeCorner = 11.dp
private val BadgeLogoSize = 29.dp
private val WatermarkSize = 158.dp

// The design positions these from the card's own edges, so the offsets undo the card padding.
private val PillTop = 52.dp - CardPaddingY
private val DatesBottom = 16.dp - CardPaddingY

private val WatermarkX = (-18).dp - CardPaddingX
private val WatermarkY = 24.dp + CardPaddingY

/**
 * One registered account.
 *
 * Laid out the way the design does — absolutely, not as a column — because three of the four
 * elements are pinned to the card's own edges rather than stacked.
 *
 * Sides are deliberate. The bank name and the status row follow the reading direction, so they
 * flip with the locale; the logo watermark and the account-type pill are anchored to the *physical*
 * left through [AbsoluteAlignment], which is where the design puts them regardless of direction,
 * and the account number reads left-to-right like the number printed on the card.
 *
 * Every string arrives finished from the mapper and both gradients are remembered on the palette,
 * so a list of these allocates nothing as it scrolls.
 */
@Composable
fun BankAccountCard(
    account: BankAccountPR,
    modifier: Modifier = Modifier,
) {
    val bank = account.bank
    val gradient = bank.cardGradient
    val ink = bank.cardInk
    val subInk = bank.subInk

    // Two stops on the 135deg diagonal, exactly as the design writes it.
    val surface = remember(gradient) { Brush.linearGradient(gradient) }

    // The soft directional wash that sits over the sheen; direction-independent, so it is built
    // once rather than per draw.
    val sheen = remember {
        Brush.linearGradient(
            0f to Color.White.copy(alpha = 0.14f),
            0.38f to Color.Transparent,
            1f to Color.Black.copy(alpha = 0.10f),
        )
    }


    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(CardHeight)
            .clip(RoundedCornerShape(CardCorner))
            .drawBehind {
                drawRect(surface)
                // The design's "sheen": a highlight near the top-trailing corner, then two hard
                // diagonal bands. Built here rather than as remembered brushes because every stop
                // is a fraction of the card's measured size.
                drawRect(
                    Brush.radialGradient(
                        colors = SheenSpotColors,
                        center = Offset(size.width * SPOT_X, size.height * SPOT_Y),
                        radius = size.maxDimension * SPOT_RADIUS,
                    )
                )
                val angle = BAND_ANGLE_DEGREES * PI.toFloat() / HALF_TURN_DEGREES
                val direction = Offset(sin(angle), -cos(angle))
                drawRect(
                    Brush.linearGradient(
                        colorStops = SheenBandStops,
                        start = Offset.Zero,
                        end = Offset(direction.x * size.width, direction.y * size.height),
                    )
                )
                drawRect(sheen)
            }
            .padding(horizontal = CardPaddingX, vertical = CardPaddingY),
    ) {
        if (bank != null) {
            Watermark(logoAlpha = bank.watermarkAlpha, account = account)
        }

        // Top row: name follows the reading direction, status sits opposite it.
        Row(
            modifier = Modifier.fillMaxWidth().align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (bank != null) {
                    Box(
                        modifier = Modifier
                            .size(BadgeSize)
                            .clip(RoundedCornerShape(BadgeCorner))
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
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
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

        val typeLabel = account.accountType?.label?.let { stringResource(it) }
            ?: account.accountTypeNameFallback
        if (typeLabel != null) {
            CustomChip(
                text = typeLabel,
                containerColor = bank.pillBackground,
                textColor = ink,
                modifier = Modifier.align(AbsoluteAlignment.TopLeft).offset(y = PillTop),
            )
        }

        // Centred on the whole card, not on the space left over by the surrounding rows.
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Text(
                text = account.accountNumber,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                color = bank.cardNumberInk,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxSize().wrapContentHeight(Alignment.CenterVertically),
            )
        }

        Row(
            modifier = Modifier.align(Alignment.BottomStart).offset(y = -DatesBottom),
            horizontalArrangement = Arrangement.spacedBy(22.dp),
        ) {
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

/**
 * The ghost logo behind the card, pinned to the physical bottom-left.
 *
 * [AbsoluteAlignment] rather than [Alignment.BottomStart]: in a right-to-left layout `Start` is the
 * right edge, which would throw the watermark across the card and put it under the account number.
 */
@Composable
private fun BoxScope.Watermark(logoAlpha: Float, account: BankAccountPR) {
    val bank = account.bank ?: return
    Image(
        painter = painterResource(bank.logo),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        alpha = logoAlpha,
        modifier = Modifier
            .align(AbsoluteAlignment.BottomLeft)
            .offset(x = WatermarkX, y = WatermarkY)
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


