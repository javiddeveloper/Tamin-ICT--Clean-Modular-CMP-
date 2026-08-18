package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.ui.graphics.Shape
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent

private val HEADER_BUTTON_SIZE = 36.dp

// The design draws a 19px glyph in a 38px button; keeping that half-of-the-button ratio
// stops the icon reading as a faint speck inside the translucent chip.
private val HEADER_BUTTON_ICON_SIZE = 18.dp

/**
 * The app's gradient hero bar: one wash running from behind the status bar down to a
 * rounded bottom edge, carrying a title, an optional back affordance and trailing action,
 * and a [content] slot for anything under the title (a filter row, a search field).
 *
 * It draws behind the status bar, so the host must not consume the top window inset —
 * otherwise the bar is pushed down and the system strip is left showing the page color.
 *
 * Colors default to the theme's top-app-bar stops, which already differ between light
 * and dark, so screens normally pass none of them.
 */
@Composable
fun TaminTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    action: @Composable (() -> Unit)? = null,
    centerTitle: Boolean = true,
    background: Brush = taminTopAppBarGradient(),
    shape: RoundedCornerShape = RoundedCornerShape(
        bottomStart = CornerRadius.sheet,
        bottomEnd = CornerRadius.sheet,
    ),
    cornerRadius: Dp = CornerRadius.sheet,
    bottomPadding: Dp = Spacing.page,
    // Overrides the plain [title] text with anything the caller needs there instead — a
    // crossfade between two strings, an icon, and so on. Null keeps the default Text.
    titleContent: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = background,
                shape = shape,
            )
            // One painted area covering the status bar and the bar below it, so the two
            // are the same color by construction rather than by keeping a separate fill
            // in step. Applied after the background so the gradient fills the system
            // strip, and before the content padding so the title clears the icons.
            .windowInsetsPadding(WindowInsets.statusBars)
            // Also inside the background: callers wanting a deeper bar for content to
            // overlap into need the gradient to grow with it. Passing that depth via
            // `modifier` would inset the gradient instead.
            .padding(
                start = Spacing.page,
                end = Spacing.page,
                top = Spacing.lg,
                bottom = bottomPadding,
            ),
    ) {
        /*
         * The title is centred against the bar, not against the space left over between the two
         * end caps. Those caps are only equal in width while each holds one button -- give one
         * side a second action and a title laid out between them slides off center.
         *
         * So the caps are pinned to the two edges and the title is centred over the whole width
         * underneath them. It is drawn first, which keeps the buttons on top and hittable.
         *
         * The clearance is the wider of the two caps as actually measured, not one button's worth
         * assumed: a screen with a share *and* a download in one cap would otherwise run a long
         * title underneath them. Both caps report their width, and the title keeps the larger on
         * both sides so it stays centred on the bar rather than on the gap.
         */
        var navCapWidth by remember { mutableIntStateOf(0) }
        var actionCapWidth by remember { mutableIntStateOf(0) }
        val capClearance = with(LocalDensity.current) {
            maxOf(navCapWidth, actionCapWidth).toDp()
        }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = capClearance + Spacing.sm),
            ) {
                if (titleContent != null) {
                    titleContent()
                } else {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        textAlign = if (centerTitle) TextAlign.Center else TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                HeaderSlot(
                    modifier = Modifier.onSizeChanged { navCapWidth = it.width },
                ) { navigationIcon?.invoke() }
                HeaderSlot(
                    modifier = Modifier.onSizeChanged { actionCapWidth = it.width },
                ) { action?.invoke() }
            }
        }
        content()
    }
}

/**
 * The bar's wash, sweeping left to right. Stops come from the theme, so it follows light
 * and dark without the caller choosing colors.
 *
 * Deliberately *not* direction-aware: the bar runs the opposite way to the cards beneath
 * it, which do follow the reading direction. Routing this through `startToEndGradient`
 * would mirror it under RTL and make the two sweep the same way.
 */
@Composable
fun taminTopAppBarGradient(
    stops: List<Color> = LocalTaminColors.current.topAppBarStops,
): Brush = Brush.horizontalGradient(stops)

/**
 * Translucent chip holding a single bar icon — a back chevron, a search or share action.
 * The design gives every one of these the same container, so the bar owns it rather than
 * leaving each caller to rebuild it.
 *
 * The colors default to the treatment header's white-on-teal. A caller placing one of these on a
 * plain surface — a sheet's close button, say — overrides them rather than hand-rolling a second
 * kind of icon button, so the size, shape and touch target stay the app's single answer.
 */
@Composable
fun TaminTopAppBarButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    bordered: Boolean = false,
    shape: Shape = RoundedCornerShape(CornerRadius.chip),
    containerColor: Color = Color.White.copy(alpha = 0.125f),
    contentColor: Color = Color.White,
    borderColor: Color = Color.White.copy(alpha = 0.2f),
) {

    Box(
        modifier = modifier
            .size(HEADER_BUTTON_SIZE)
            .clip(shape)
            .background(containerColor)
            .then(
                if (bordered) {
                    Modifier.border(1.dp, borderColor, shape)
                } else {
                    Modifier
                },
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = contentColor,
            modifier = Modifier.size(HEADER_BUTTON_ICON_SIZE),
        )
    }
}

/**
 * End cap holding the navigation icon or the actions.
 *
 * One button wide *at minimum*, not exactly — that is what keeps the title optically centred when
 * a bar has an icon on one side only. It has to grow past that when a caller supplies more than one
 * action, though: pinning it to [HEADER_BUTTON_SIZE] silently clipped everything after the first
 * button, so a bar with a download and a share showed only the download.
 */
@Composable
private fun HeaderSlot(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .defaultMinSize(minWidth = HEADER_BUTTON_SIZE, minHeight = HEADER_BUTTON_SIZE),
        contentAlignment = Alignment.Center,
        content = { content() },
    )
}

@PreviewRtlTheme
@Composable
private fun TaminTopAppBarTitleOnlyPreview() {
    PreviewRtlThemeContent {
        TaminTopAppBar(
            title = "عنوان صفحه",
            centerTitle = true
        )
    }
}

@PreviewRtlTheme
@Composable
private fun TaminTopAppBarFullPreview() {
    PreviewRtlThemeContent {
        TaminTopAppBar(
            title = "عنوان صفحه",
            centerTitle = true,
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "بازگشت",
                    onClick = {}
                )
            },
            action = {
                TaminTopAppBarButton(
                    icon = Icons.Default.MoreVert,
                    contentDescription = "بیشتر",
                    onClick = {}
                )
            }
        )
    }
}

@PreviewRtlTheme
@Composable
private fun TaminTopAppBarTitleOnlyPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        TaminTopAppBar(
            title = "عنوان صفحه",
            centerTitle = true
        )
    }
}

@PreviewRtlTheme
@Composable
private fun TaminTopAppBarFullPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        TaminTopAppBar(
            title = "عنوان صفحه",
            centerTitle = true,
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "بازگشت",
                    onClick = {}
                )
            },
            action = {
                TaminTopAppBarButton(
                    icon = Icons.Default.MoreVert,
                    contentDescription = "بیشتر",
                    onClick = {}
                )
            }
        )
    }
}
