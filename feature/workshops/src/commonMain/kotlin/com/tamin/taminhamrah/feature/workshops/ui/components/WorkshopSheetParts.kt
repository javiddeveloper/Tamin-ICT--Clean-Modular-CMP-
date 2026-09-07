package com.tamin.taminhamrah.feature.workshops.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing

/**
 * The parts every کارگاه bottom sheet in this design opens with.
 *
 * Every sheet here switches `ModalBottomSheet`'s own `dragHandle` off and draws the design's
 * grabber instead, then a centred title and an optional line under it. Three sheets drew that same
 * opening; this is it declared once.
 */

/** The design's own grabber — `width:44px; height:4px; border-radius:100px`. */
@Composable
fun WorkshopSheetGrabber(modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Box(
        modifier = modifier
            .size(
                width = WorkshopDimens.contractRowGrabberWidth,
                height = WorkshopDimens.contractRowGrabberHeight,
            )
            .background(colors.border, RoundedCornerShape(CornerRadius.full)),
    )
}

/**
 * A sheet's body: page insets, the navigation-bar inset, the grabber, a title, and its content.
 *
 * The column scrolls because a sheet that does not can push its own primary button past the bottom
 * edge on a short screen — a bug this design already shipped once. Every sheet here holds a bounded
 * number of rows, so a plain scroll is right; a lazy list inside a sheet that already scrolls would
 * nest two scrollers.
 */
@Composable
fun WorkshopSheetBody(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.page)
            .padding(top = Spacing.smd, bottom = Spacing.page)
            .padding(WindowInsets.navigationBars.asPaddingValues()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        WorkshopSheetGrabber(modifier = Modifier.padding(bottom = Spacing.smd))
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = Spacing.xs),
            )
        }
        content()
    }
}
