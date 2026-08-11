package com.tamin.taminhamrah.feature.treatment.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.Dp
import com.tamin.taminhamrah.feature.treatment.ui.TreatmentDimens
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors

/**
 * The card surface every treatment screen uses: [taminSurface], lifted off the page.
 *
 * `taminSurface` on its own is flat — fill, hairline border, rounded corner — which is right for
 * the rest of the app but leaves a scrolling list of treatment records reading as one continuous
 * sheet. The shadow is cast before the fill is clipped on, so it falls outside the card rather than
 * darkening its edge.
 *
 * The color comes from [com.tamin.taminhamrah.ui.theme.TaminColors.shadowSubtle] rather than the
 * platform default black: on the dark theme a black shadow under a dark card is invisible, and that
 * token already carries the per-theme value.
 *
 * Deliberately not folded into `taminSurface` itself — that modifier is shared with every other
 * feature, and raising their cards is not this change's business.
 */
@Composable
internal fun Modifier.raisedCard(cornerRadius: Dp = CornerRadius.card): Modifier {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(cornerRadius)
    return shadow(
        elevation = TreatmentDimens.cardElevation,
        shape = shape,
        ambientColor = colors.shadowSubtle,
        spotColor = colors.shadowSubtle,
    ).taminSurface(cornerRadius)
}
