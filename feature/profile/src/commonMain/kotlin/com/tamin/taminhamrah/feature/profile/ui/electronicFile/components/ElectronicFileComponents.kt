package com.tamin.taminhamrah.feature.profile.ui.electronicFile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.tamin.taminhamrah.model.erecords.ElectronicFilePR
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.LoadAsyncImage
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.taminTopAppBarGradient
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.HeaderDecoration
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.ui.theme.shimmer
import com.tamin.taminhamrah.ui.toparea.TopAreaState
import com.tamin.taminhamrah.ui.toparea.topAreaHide
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_back
import taminx.core.core_ui.electronic_file_category
import taminx.core.core_ui.electronic_file_empty
import taminx.core.core_ui.electronic_file_registered_subtitle
import taminx.core.core_ui.electronic_file_section
import taminx.core.core_ui.electronic_file_title
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_medical_records
import kotlin.math.roundToInt

private const val COLUMNS = 2
private const val THUMB_ASPECT = 1.25f
private const val SKELETON_CARDS = 6
private val CollapsedBottomSpace = Spacing.lg

/**
 * Gradient hero for the electronic-file screen: title row stays put while the ring icon and
 * subtitle fold away under the grid's own drag (see [TopAreaState]).
 */
@Composable
fun ElectronicFileHeader(
    topAreaState: TopAreaState,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val gradient = remember(colors.profileGradientStops) {
        Brush.horizontalGradient(colors.profileGradientStops)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = CornerRadius.x3l, bottomEnd = CornerRadius.x3l))
            .background(taminTopAppBarGradient(colors.profileGradientStops)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TaminTopAppBar(
            title = stringResource(Res.string.electronic_file_title),
            centerTitle = true,
            background = gradient,
            bottomPadding = Spacing.none,
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = stringResource(Res.string.action_back),
                    onClick = onBackClicked,
                    bordered = true,
                )
            },
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .topAreaHide(topAreaState)
                .padding(bottom = Spacing.xl),
        ) {
            DecorativeBackgroundCircle(
                size = HeaderDecoration.circleSize,
                xOffset = HeaderDecoration.circleXOffset,
                yOffset = HeaderDecoration.circleYOffset,
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AnimatedRingHeaderIcon(
                    icon = vectorResource(Res.drawable.ic_tamin_medical_records),
                    animated = !topAreaState.isMeasureProbe,
                )
                Spacer(modifier = Modifier.height(Spacing.md))
                Text(
                    text = stringResource(Res.string.electronic_file_registered_subtitle),
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.textHeaderSubtitle,
                )
            }
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .topAreaReveal(topAreaState, CollapsedBottomSpace),
        )
    }
}

/** Grows from zero up to [height] as [state] folds — inverse of [topAreaHide]. */
private fun Modifier.topAreaReveal(state: TopAreaState, height: Dp): Modifier =
    layout { measurable, constraints ->
        val targetPx = height.roundToPx()
        val revealedPx = (targetPx * state.progress).roundToInt()
        val placeable = measurable.measure(
            constraints.copy(minHeight = 0, maxHeight = revealedPx.coerceAtLeast(0)),
        )
        layout(placeable.width, revealedPx) { placeable.place(0, 0) }
    }

/**
 * The document grid.
 *
 * Lazy in both senses: only visible cards compose, and only visible thumbnails are fetched and
 * decoded — which is where the cost of this screen actually is.
 */
@Composable
fun DocumentGrid(
    documents: ImmutableList<ElectronicFilePR>,
    isLoading: Boolean,
    hasError: Boolean,
    onOpen: (ElectronicFilePR) -> Unit,
    modifier: Modifier = Modifier,
    gridState: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(Spacing.page),
) {
    when {
        isLoading && documents.isEmpty() -> DocumentGridSkeleton(
            modifier = modifier,
            gridState = gridState,
            contentPadding = contentPadding,
        )

        // Guarded on error: a request that failed has no idea whether the person has documents.
        !hasError && documents.isEmpty() ->
            TaminEmptyState(
                message = stringResource(Res.string.electronic_file_empty),
                modifier = modifier.padding(contentPadding),
            )

        else -> LazyVerticalGrid(
            columns = GridCells.Fixed(COLUMNS),
            state = gridState,
            modifier = modifier,
            contentPadding = contentPadding,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            item(span = { GridItemSpan(COLUMNS) }) {
                Text(
                    text = stringResource(Res.string.electronic_file_section),
                    style = MaterialTheme.typography.titleSmall,
                    color = LocalTaminColors.current.textTertiary,
                    modifier = Modifier.padding(top = Spacing.md, bottom = Spacing.xs),
                )
            }
            items(items = documents, key = { it.id }) { document ->
                DocumentCard(
                    name = document.name,
                    category = document.categoryName,
                    thumb = document.thumb,
                    onClick = { onOpen(document) },
                )
            }
        }
    }
}

/**
 * One document card.
 *
 * Takes the strings it draws rather than the record, so a page loading elsewhere in the grid
 * cannot invalidate a card whose own text is unchanged.
 */
@Composable
fun DocumentCard(
    name: String,
    category: String,
    thumb: String,
    onClick: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.card)
    Column(
        modifier = Modifier
            .clip(shape)
            .background(colors.bgSurface)
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(THUMB_ASPECT)
                .background(colors.bgPage),
            contentAlignment = Alignment.Center,
        ) {
            LoadAsyncImage(
                model = thumb,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = stringResource(Res.string.electronic_file_category, category),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textTertiary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        // Same bottom accent as dependents list cards — clipped by the card's rounded shape.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(Thickness.accent)
                .background(colors.buttonGradient),
        )
    }
}

@Composable
private fun DocumentGridSkeleton(
    modifier: Modifier = Modifier,
    gridState: LazyGridState = rememberLazyGridState(),
    contentPadding: PaddingValues = PaddingValues(Spacing.page),
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(COLUMNS),
        state = gridState,
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        item(span = { GridItemSpan(COLUMNS) }) {
            Text(
                text = stringResource(Res.string.electronic_file_section),
                style = MaterialTheme.typography.titleSmall,
                color = LocalTaminColors.current.textTertiary,
                modifier = Modifier.padding(top = Spacing.md, bottom = Spacing.xs),
            )
        }
        items(count = SKELETON_CARDS) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(THUMB_ASPECT)
                    .clip(RoundedCornerShape(CornerRadius.card))
                    .shimmer(),
            )
        }
    }
}
