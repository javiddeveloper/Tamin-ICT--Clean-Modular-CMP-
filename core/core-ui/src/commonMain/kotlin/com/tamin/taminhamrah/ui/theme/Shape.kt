package com.tamin.taminhamrah.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val TaminHamrahShapes = Shapes(
    small = RoundedCornerShape(CornerRadius.sm),
    medium = RoundedCornerShape(CornerRadius.md),
    large = RoundedCornerShape(CornerRadius.lg),
)

object ListShapes {
    val cornerSize = CornerRadius.md
    val top = RoundedCornerShape(topStart = cornerSize, topEnd = cornerSize)
    val middle = RoundedCornerShape(0.dp)
    val bottom = RoundedCornerShape(bottomStart = cornerSize, bottomEnd = cornerSize)
    val single = RoundedCornerShape(cornerSize)
}

object Spacing {
    val xxs = 2.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 24.dp
    val xxl = 32.dp

    // Gaps taken from the Tamin Man App design that fall between the steps above.
    // Named for what they space so call sites stay free of magic numbers.
    val page = 18.dp
    val cardGap = 11.dp
}

object CornerRadius {
    val xxs = 2.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 24.dp
    val xxl = 32.dp

    // Radii taken from the Tamin Man App design that fall between the steps above.
    // Named for the element they belong to rather than an abstract size step.
    val avatarTile = 10.dp
    val listRow = 13.dp
    val chip = 14.dp
    val iconTile = 18.dp
    val cardCompact = 20.dp
    val card = 22.dp
    val sheet = 28.dp
}

object Elevation {
    val xs = 1.dp
    val sm = 2.dp
    val md = 4.dp
    val lg = 8.dp
}

object IconSize {
    val statIcon = 10.dp
    val small = 16.dp
    val medium = 24.dp
    val errorPlaceholder = 36.dp
    val large = 48.dp
    val xlarge = 56.dp
    val navBar = 24.dp
}
