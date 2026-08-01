package com.tamin.taminhamrah.feature.treatment.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.treatment.ui.TreatmentDimens
import com.tamin.taminhamrah.ui.components.AutoResizeText
import com.tamin.taminhamrah.ui.components.IconTile
import com.tamin.taminhamrah.ui.components.StatTile
import com.tamin.taminhamrah.ui.components.startToEndGradient
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminCardAmberEnd
import com.tamin.taminhamrah.ui.theme.TaminCardAmberMid
import com.tamin.taminhamrah.ui.theme.TaminCardAmberStart
import com.tamin.taminhamrah.ui.theme.TaminCardBlueEnd
import com.tamin.taminhamrah.ui.theme.TaminCardBlueMid
import com.tamin.taminhamrah.ui.theme.TaminCardBlueStart
import com.tamin.taminhamrah.ui.theme.TaminCardPurpleEnd
import com.tamin.taminhamrah.ui.theme.TaminCardPurpleMid
import com.tamin.taminhamrah.ui.theme.TaminCardPurpleStart
import com.tamin.taminhamrah.ui.theme.TaminCardTealEnd
import com.tamin.taminhamrah.ui.theme.TaminCardTealMid
import com.tamin.taminhamrah.ui.theme.TaminCardTealStart
import com.tamin.taminhamrah.ui.theme.TaminTeal500
import com.tamin.taminhamrah.ui.theme.TaminTeal900
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_check
import taminx.core.core_ui.ic_tamin_ejtemaei_logo

/**
 * Components for the treatment hub landing screen: the quick-access and navigation cards, the
 * service category grid and the yearly cost summary. The insured-person card, its morph and its
 * carousel live in InsuranceCardComponents.kt.
 *
 * None of these depend on a domain model — they take display strings and lambdas, so
 * they stay decoupled from whatever shape the treatment entities settle on.
 */

/** The main insured person's card: teal fading into brand blue. */
private val MainInsuredCardStops = listOf(TaminCardTealStart, TaminCardTealMid, TaminCardTealEnd)

/**
 * Dependants cycle through these, so a person with more of them than the design has colors still
 * gets a card: the palette repeats every three, which is enough to keep neighboring cards in the
 * carousel apart. Ordered as the design lists them: spouse, then children.
 */
private val DependantCardStops = listOf(
    listOf(TaminCardPurpleStart, TaminCardPurpleMid, TaminCardPurpleEnd),
    listOf(TaminCardBlueStart, TaminCardBlueMid, TaminCardBlueEnd),
    listOf(TaminCardAmberStart, TaminCardAmberMid, TaminCardAmberEnd),
)


/** The prominent teal gradient behind the "سوابق درمانی من" quick-access card. */
@Composable
fun quickAccessGradient(): Brush = startToEndGradient(listOf(TaminTeal900, TaminTeal500))

/** Soft translucent swooshes that stop the gradient card reading as a flat rectangle. */
private fun Modifier.cardDecoration(): Modifier = drawBehind {
    val decor = Color.White.copy(alpha = TreatmentDimens.cardDecorAlpha)
    drawOval(
        color = decor,
        topLeft = Offset(-size.width * 0.15f, size.height * 0.55f),
        size = Size(size.width * 0.7f, size.height * 0.8f),
    )
    drawOval(
        color = decor,
        topLeft = Offset(size.width * 0.6f, -size.height * 0.5f),
        size = Size(size.width * 0.6f, size.height * 0.7f),
    )
}

@Composable
private fun InsuranceCardBrandRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Box(
            modifier = Modifier
                .size(TreatmentDimens.brandTileSize)
                .background(
                    Color.White.copy(alpha = 0.13f),
                    RoundedCornerShape(CornerRadius.avatarTile),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_ejtemaei_logo),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(TreatmentDimens.brandTileIconSize),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "سازمان تامین اجتماعی",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
            )
            Text(
                text = "کارت الکترونیک بیمهٔ درمان",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.75f),
            )
        }
        Box(
            modifier = Modifier
                .size(TreatmentDimens.brandTickSize)
                .background(Color.White.copy(alpha = 0.13f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_check),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(TreatmentDimens.brandTickIconSize),
            )
        }
    }
}


@Composable
private fun InsuranceCardFooter(
    coverageLabel: String,
    badge: @Composable (() -> Unit)?,
    action: @Composable (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.08f))
            .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        badge?.invoke()
        Text(
            text = coverageLabel,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            modifier = Modifier.weight(1f),
        )
        action?.invoke()
    }
}

/**
 * Takes the [pagerState] rather than the current page so that swiping recomposes the dots only —
 * reading `currentPage` in the carousel above would recompose the pager and every card with it.
 *
 * Scrolls rather than wraps or clips: a person can have more dependants than a row of dots fits,
 * and the strip keeps the active one in view instead of running off the edge. It centres itself
 * while the dots do fit, so the common two- or three-card case looks exactly as before.
 */
@Composable
private fun PageIndicator(
    pageCount: Int,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val selectedPage = pagerState.currentPage
    val listState = rememberLazyListState()

    LaunchedEffect(selectedPage) {
        listState.animateScrollToItem(selectedPage)
    }

    LazyRow(
        state = listState,
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        // The pager owns the gesture; these are a read-out, not a second way to page.
        userScrollEnabled = false,
    ) {
        items(pageCount) { page ->
            val isSelected = page == selectedPage
            Box(
                modifier = Modifier
                    .size(
                        width = if (isSelected) SelectedDotWidth else DotSize,
                        height = DotSize,
                    )
                    .background(
                        color = if (isSelected) colors.teal else colors.chevron,
                        shape = CircleShape,
                    ),
            )
        }
    }
}

private val DotSize = 6.dp
private val SelectedDotWidth = 16.dp

/**
 * One square tile in the hub's three-up service grid — electronic prescriptions,
 * medical confirmations, miscellaneous claims.
 */
@Composable
fun CategoryTile(
    label: String,
    icon: ImageVector,
    iconTint: Color,
    iconBackground: Brush,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .taminSurface(CornerRadius.cardCompact)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.sm, vertical = Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        IconTile(
            icon = icon,
            tint = iconTint,
            background = iconBackground,
            size = TreatmentDimens.categoryTileIconSize,
            cornerRadius = CornerRadius.lg,
        )
        AutoResizeText(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
            ),
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xxs),
        )
    }
}

/**
 * Yearly spend summary: the insured person's share beside the organization's share.
 */
@Composable
fun CostSummaryCard(
    title: String,
    insuredShareLabel: String,
    /** `null` while the year's totals are still loading; the tiles shimmer until they land. */
    insuredShareAmount: String?,
    organizationShareLabel: String,
    organizationShareAmount: String?,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(CornerRadius.card)
            .padding(Spacing.page),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = colors.textPrimary,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            StatTile(
                label = insuredShareLabel,
                amount = insuredShareAmount,
                containerColor = colors.greenBg,
                contentColor = colors.greenText,
                modifier = Modifier.weight(1f),
            )
            StatTile(
                label = organizationShareLabel,
                amount = organizationShareAmount,
                containerColor = colors.blueBg,
                contentColor = colors.blueText,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
