package com.tamin.taminhamrah.feature.treatment.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.Dp
import com.tamin.taminhamrah.feature.treatment.ui.TreatmentDimens
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius

/**
 * Lifts a card off the page, without touching what it is filled with.
 *
 * For the cards that paint their own background — the hub's gradient rows — where [raisedCard]
 * would overwrite it.
 *
 * The ambient and spot colors are left at the framework's default on purpose. They look like the
 * obvious place for `shadowSubtle`, but that token is already `Gray900` at 10% alpha, and the
 * platform multiplies its own opacity ramp by whatever it is given — passing it produced a shadow
 * roughly ten times fainter than stock, which is to say invisible. The tokens are for shadows drawn
 * by hand, where the caller supplies the final alpha.
 */
@Composable
internal fun Modifier.raisedShadow(cornerRadius: Dp = CornerRadius.card): Modifier =
    shadow(
        elevation = TreatmentDimens.cardElevation,
        shape = RoundedCornerShape(cornerRadius),
    )

/**
 * The card surface every treatment screen uses: [taminSurface], lifted off the page.
 *
 * `taminSurface` on its own is flat — fill, hairline border, rounded corner — which is right for
 * the rest of the app but leaves a scrolling list of treatment records reading as one continuous
 * sheet. The shadow is cast before the fill is clipped on, so it falls outside the card rather than
 * darkening its edge.
 *
 * Deliberately not folded into `taminSurface` itself — that modifier is shared with every other
 * feature, and raising their cards is not this change's business.
 */
@Composable
internal fun Modifier.raisedCard(cornerRadius: Dp = CornerRadius.card): Modifier =
    raisedShadow(cornerRadius).taminSurface(cornerRadius)
