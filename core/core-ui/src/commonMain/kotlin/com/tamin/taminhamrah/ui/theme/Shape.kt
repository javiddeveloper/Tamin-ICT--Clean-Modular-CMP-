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
    val none = 0.dp
    val xxs = 2.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xlg = 20.dp
    val xl = 24.dp
    val xxl = 32.dp
    val xxxl = 40.dp
    val xxxxl = 48.dp
    val xxxxxl = 64.dp
    val xxxxxxl = 80.dp
    val xxxxxxxl = 96.dp
}

object CornerRadius {
    val none = 0.dp
    val xs = 2.dp
    val sm = 4.dp
    val md = 8.dp
    val lg = 12.dp
    val xl = 16.dp
    val x2l = 24.dp
    val full = 9999.dp
}

object Elevation {
    val none = 0.dp
    val xxs = 1.dp
    val xs = 2.dp
    val sm = 4.dp
    val md = 6.dp
    val lg = 12.dp
    val xl = 24.dp
}

object IconSize {
    val statIcon = 10.dp
    val small = 16.dp
    val medium = 24.dp
    val large = 38.dp
    val xlarge = 48.dp
    val xxlarge = 56.dp
    val navBar = 24.dp
}
