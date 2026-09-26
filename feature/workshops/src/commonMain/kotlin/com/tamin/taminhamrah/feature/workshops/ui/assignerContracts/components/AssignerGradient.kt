package com.tamin.taminhamrah.feature.workshops.ui.assignerContracts.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import com.tamin.taminhamrah.ui.components.startToEndGradient
import com.tamin.taminhamrah.ui.theme.IconGradientBlueEnd
import com.tamin.taminhamrah.ui.theme.IconGradientBlueStart

/**
 * واگذارندگان's primary fill: the app's blue, pale at the start of the reading direction and deep at
 * its end — under RTL, light on the right running to dark on the left.
 *
 * The theme's `buttonGradient` is a plain horizontal gradient, fixed left-to-right whatever the
 * layout direction, so on this RTL page it read from dark to light. It stays as it is for the rest
 * of the app; this feature's selected tab and primary buttons use this one instead.
 */
@Composable
internal fun assignerPrimaryGradient(): Brush =
    startToEndGradient(listOf(IconGradientBlueStart, IconGradientBlueEnd))
