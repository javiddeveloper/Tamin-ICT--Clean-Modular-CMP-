package com.tamin.taminhamrah.feature.workshops.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.Spacing

/**
 * Every measurement the کارگاه‌های کارفرما screens are drawn to, taken from the design.
 *
 * One table, because these numbers are shared: the same card carries a payment sheet, a debt and
 * a member, and the same panel searches all of them. Spread across a dozen files they drifted —
 * three files each declared their own `CardCorner`, and eight their own `CellVerticalPadding`.
 *
 * Anything the app-wide scale already names — [Spacing], `CornerRadius`, `IconSize` — is used from
 * there instead of being repeated here. What lives here is only what the design asks for and the
 * scale has no name for.
 */
object WorkshopDimens {

    // ---------------------------------------------------------------------- page
    /** `padding:14px 18px 0` — what the design insets every card list by. */
    val listContentPadding = PaddingValues(
        start = Spacing.page,
        end = Spacing.page,
        top = Spacing.smd,
        bottom = Spacing.page,
    )

    /** The gradient bar's own depth on the list screen, and how far the stats strip rides into it. */
    val headerBottomPadding = 64.dp
    val statsCardOverlap = 42.dp

    // --------------------------------------------------------------- stats strip
    /** `border-radius:18px; padding:13px 6px`. */
    val statsCardCorner = 18.dp
    val statsCardVerticalPadding = 13.dp
    val statsCardHorizontalPadding = 6.dp

    /** Placeholders standing in for a figure that has not been counted yet. */
    val statShimmerWidth = 28.dp
    val statShimmerHeight = 20.dp

    /** The count beside a section heading: `padding:2px 8px`. */
    val countBadgeVerticalPadding = 2.dp

    // --------------------------------------------------------------- record card
    /** `border-radius:18px; padding:13px 14px 12px`. */
    val cardCorner = 18.dp
    val cardHorizontalPadding = 14.dp
    val cardTopPadding = 13.dp
    val cardBottomPadding = 12.dp

    /** `margin-top:10px` above the first cell of a card that has a head. */
    val cardCellsTopMargin = 10.dp

    /** `padding:8px 0` per cell, i.e. 8 above and below the value. */
    val cellVerticalPadding = 8.dp

    // ------------------------------------------------------------- card actions
    /** `gap:8px; margin-top:11px`; each button `min-height:42px; min-width:96px`. */
    val cardButtonsTopMargin = 11.dp
    val cardButtonGap = 8.dp
    val cardButtonHeight = 42.dp
    val cardButtonMinWidth = 96.dp
    val cardButtonBorderWidth = 1.3.dp

    /** The glyph in a compact card button: the design's `width="13"` on واگذارندگان's three-up row. */
    val cardButtonCompactIcon = 13.dp

    /**
     * How strongly a tinted button's outline shows through.
     *
     * Tuned so the derived border lands on the design's own values — #D32F2F at this alpha over
     * the danger fill reads as #F3C9C4, which is what it draws.
     */
    const val cardButtonOutlineAlpha = 0.35f

    // ------------------------------------------------------------ expand toggle
    /** `margin-top:10px; border-top:1px dashed`, then a 14px chevron. */
    val toggleTopMargin = 10.dp

    /**
     * Above and below the «جزئیات بیشتر» label, inside its tap area.
     *
     * The design puts 9px above the label and leaves the card's 12px bottom padding below it, which
     * sets the label visibly high between the rule and the card's edge. The same 21px, split evenly,
     * centres it without changing the card's height — and the tap area now reaches the bottom.
     */
    val toggleVerticalPadding = 10.5.dp
    val toggleDashOn = 3.dp
    val toggleDashOff = 3.dp
    val toggleChevronSize = 14.dp

    /** `transform:rotate(180deg)` once the card is open. */
    const val toggleHalfTurn = 180f

    // --------------------------------------------------------------- status pill
    /**
     * How strongly a pill's outline shows through.
     *
     * The design outlines each pill in a paler shade of its own text color (#BFE6CF on green,
     * #F0DCA8 on orange, #F3C9C4 on red); deriving it reproduces those without three more palette
     * entries, and keeps working in dark theme where fixed pastels would not.
     */
    const val statusPillBorderAlpha = 0.20f

    // --------------------------------------------------------------- search panel
    /** `border-radius:18px; padding:14px`, with `gap:10px` between grid cells. */
    val panelCorner = 18.dp
    val panelPadding = 14.dp
    val fieldGap = 10.dp
    val panelButtonsTopMargin = 12.dp

    /** A field: a 11px caption 5px above a `min-height:44px; padding:0 12px` box. */
    val fieldLabelGap = 5.dp
    val fieldHeight = 44.dp
    val fieldHorizontalPadding = 12.dp
    val fieldVerticalPadding = 11.dp
    val pickerChevronSize = 14.dp

    /** The panel's own two buttons: `min-height:46px; border-radius:14px; border:1.4px`. */
    val panelButtonHeight = 46.dp
    val panelButtonBorderWidth = 1.4.dp

    // --------------------------------------------------------------- filter chips
    /** `gap:7px`, each `padding:5px 10px` with a 12px cross. */
    val chipGap = 7.dp
    val chipHorizontalPadding = 10.dp
    val chipVerticalPadding = 5.dp
    val chipCrossSize = 12.dp

    // ------------------------------------------------------- عملیات این کارگاه
    /** One service row: `gap:9px` between cards, `padding:11px 13px` inside, 15px chevron. */
    val serviceRowGap = 9.dp
    val serviceRowHorizontalPadding = 13.dp
    val serviceRowVerticalPadding = 11.dp
    val serviceRowChevronSize = 15.dp

    // ------------------------------------------------------------- identity panel
    /** `width:36px; height:36px` on the tile inside the gradient bar. */
    val identityIconTile = 36.dp

    // ----------------------------------------------------------- ردیف‌های پیمان
    /*
     * The contract-row card is its own shape in the design — a grid of filled tiles rather than the
     * divided label/value stack every other کارگاه card uses — so its measurements sit apart from
     * the `card*` block above rather than being folded into it.
     */

    /** `padding:11px 13px` inside the card, over the shared 18px corner. */
    val contractRowCardHorizontalPadding = 13.dp
    val contractRowCardVerticalPadding = 11.dp

    /** `padding:3px 8px` on the «ردیف N» badge. */
    val contractRowBadgeVerticalPadding = 3.dp

    /** One tile: `gap:7px` between them, `padding:7px 9px` inside. */
    val contractRowTileGap = 7.dp
    val contractRowTileHorizontalPadding = 9.dp
    val contractRowTileVerticalPadding = 7.dp

    /** The tab strip: `padding:4px; gap:4px; border-radius:16px`, each tab `min-height:44px`. */
    val contractRowTabStripPadding = 4.dp
    val contractRowTabGap = 4.dp
    val contractRowTabHeight = 44.dp

    /**
     * «حذف» on the picker sheet — `flex:none; min-width:92px`.
     *
     * A fixed width, not a weight: `TaminOutlinedButton` applies `fillMaxWidth()` after the
     * caller's modifier, so left to size itself it swallows the row and squeezes the primary
     * button to nothing.
     */
    val contractRowResetButtonWidth = 92.dp

    /** The picker sheet's grabber — `width:44px; height:4px`. */
    val contractRowGrabberWidth = 44.dp
    val contractRowGrabberHeight = 4.dp

    /**
     * How wide کد کارگاه sits against کد شعبه — `flex:1.4` against `flex:1`.
     *
     * A ten-digit code beside a four-digit one, so the split is not even.
     */
    const val contractRowWorkshopFieldWeight = 1.4f
    const val contractRowBranchFieldWeight = 1f

    // ---------------------------------------------------------------- list states
    /** Card-shaped blocks standing in for rows that have not arrived, and the paging spinner. */
    val skeletonRowHeight = 132.dp
    val footerSpinnerSize = 28.dp

    // ------------------------------------------------ افزودن پرسنل جدید
    /** `min-height:48px; border-radius:15px` on the design's add button. */
    val addButtonHeight = 48.dp
    val addButtonCorner = 15.dp
}
