package com.tamin.taminhamrah.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val TaminHamrahShapes = Shapes(
    small = RoundedCornerShape(CornerRadius.sm),
    medium = RoundedCornerShape(CornerRadius.md),
    large = RoundedCornerShape(CornerRadius.lg),
    extraLarge = RoundedCornerShape(CornerRadius.chip),
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
    val smPlus = 10.dp
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

    val smd = 14.dp
    val page = 18.dp
    val cardGap = 11.dp
    val tabSelector = 6.dp
    val badgeVertical = 5.dp
    val highlightHeight = 36.5.dp
}

object CornerRadius {
    val none = 0.dp
    val xs = 2.dp
    val sm = 4.dp
    val md = 8.dp
    val lg = 12.dp
    val xl = 16.dp
    val xlg = 17.dp
    val x2l = 24.dp
    val x3l = 40.dp
    val full = 9999.dp

    val avatarTile = 10.dp
    val listRow = 13.dp
    val chip = 14.dp
    val iconTile = 18.dp
    val cardCompact = 20.dp
    val card = 22.dp
    val sheet = 28.dp
    val max = 100.dp
    val textFieldIcon = 11.dp
}

object Elevation {
    val none = 0.dp
    val xxs = 1.dp
    val xs = 2.dp
    val sm = 4.dp
    val smPlus = 5.dp
    val md = 6.dp
    val lg = 12.dp
    val xl = 24.dp
    val xxl = 30.dp

    val button = 8.dp
}

object ButtonDimens {
    val height = 56.dp
    val loadingIndicatorSize = 18.dp
    val loadingIndicatorStroke = 2.dp
}

object TextDimens {
    val phoneLetterSpacing = 1.5.sp
    val stepperTitle = 10.5.sp
    val stepperNumber = 13.sp
}

object IconSize {
    val statIcon = 10.dp
    val small = 16.dp
    val badgeInner = 17.dp
    val banner = 20.dp
    val medium = 24.dp
    val badge = 32.dp
    val large = 38.dp
    val largePlus = 42.dp
    val xlarge = 48.dp
    val xxlarge = 56.dp
    val xxxlarge = 80.dp
    val tile = 58.dp
    val tileInner = 28.dp
    val textFieldIconContainer = 34.dp
    val headerIconOuter = 104.dp
    val headerIconInner = 78.dp
    val navBar = 24.dp
    val stepperCircle = 30.dp
    val stepperConnectorHeight = 2.dp
    val stepperConnectorWidth = 44.dp
}

object Thickness {
    val border = 1.dp
    val medium = 2.dp
}

/** Decorative wash behind [com.tamin.taminhamrah.ui.components.TaminTopAppBar] hero content. */
object HeaderDecoration {
    val circleSize = 190.dp
    val circleXOffset = 450.dp
    val circleYOffset = (-150).dp
}

/**
 * Placeholder sizes for a value that has not arrived, so a shimmering figure occupies roughly what
 * the real one will and nothing resizes when it lands.
 */
object ShimmerSize {
    val valueWidth = 56.dp
    val valueHeight = 14.dp
    val labelWidth = 72.dp
    val sectionLabelWidth = 48.dp
    val hintWidth = 120.dp
    val titleWidth = 160.dp
    val chipWidth = 64.dp
    val infoBodyHeight = 80.dp
    val copyRowHeight = 36.dp
    val sonCardHeight = 72.dp
    val fieldHeight = ButtonDimens.height
    val helperLineWidth = 200.dp
    val titleHeight = 14.dp
    val subtitleWidth = 180.dp
    val subtitleHeight = 12.dp
    val badgeWidth = 56.dp
    val badgeHeight = 24.dp
}
