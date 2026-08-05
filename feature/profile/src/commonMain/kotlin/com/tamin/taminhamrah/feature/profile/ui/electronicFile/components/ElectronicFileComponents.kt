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
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.tamin.taminhamrah.model.erecords.ElectronicFilePR
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.LoadAsyncImage
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.shimmer
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.electronic_file_category
import taminx.core.core_ui.electronic_file_empty
import taminx.core.core_ui.electronic_file_registered_subtitle
import taminx.core.core_ui.ic_tamin_medical_records

private const val COLUMNS = 2
private const val THUMB_ASPECT = 1.25f
private const val SKELETON_CARDS = 6

/**
 * What sits under the title in the header: the document mark, ringed, over the gradient.
 *
 * Laid out exactly as the change-mobile header is — same ringed tile, same spacing, same subtitle
 * treatment — so the two screens read as one family rather than two takes on the same idea.
 */
@Composable
fun ElectronicFileHeader(
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Box(modifier = modifier.fillMaxWidth()) {
        DecorativeBackgroundCircle(
            size = 190.dp,
            xOffset = 450.dp,
            yOffset = (-150).dp,
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AnimatedRingHeaderIcon(icon = vectorResource(Res.drawable.ic_tamin_medical_records))
            Spacer(modifier = Modifier.height(Spacing.md))
            Text(
                text = stringResource(Res.string.electronic_file_registered_subtitle),
                style = MaterialTheme.typography.labelLarge,
                color = colors.textHeaderSubtitle,
            )
        }
    }
}

/**
 * The document grid.
 *
 * Lazy in both senses: only visible cards compose, and only visible thumbnails are fetched and
 * decoded — which is where the cost of this screen actually is.
 */
@Composable
fun DocumentGrid(
    documents: LazyPagingItems<ElectronicFilePR>,
    onOpen: (ElectronicFilePR) -> Unit,
    modifier: Modifier = Modifier,
) {
    val refresh = documents.loadState.refresh

    ErrorStateView(
        message = (refresh as? LoadState.Error)?.error?.message,
        onRetry = documents::retry,
    )

    when {
        refresh is LoadState.Loading && documents.itemCount == 0 ->
            DocumentGridSkeleton(modifier = modifier)

        // Guarded on error: a refresh that failed has no idea whether the person has documents.
        refresh !is LoadState.Error && documents.itemCount == 0 ->
            TaminEmptyState(
                message = stringResource(Res.string.electronic_file_empty),
                modifier = modifier,
            )

        else -> LazyVerticalGrid(
            columns = GridCells.Fixed(COLUMNS),
            modifier = modifier,
            contentPadding = PaddingValues(Spacing.page),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            items(
                count = documents.itemCount,
                key = { index -> documents[index]?.id ?: index },
            ) { index ->
                val document = documents[index]
                if (document != null) {
                    DocumentCard(
                        name = document.name,
                        category = document.categoryName,
                        thumb = document.thumb,
                        onClick = { onOpen(document) },
                    )
                }
            }

            if (documents.loadState.append is LoadState.Loading) {
                item(span = { GridItemSpan(COLUMNS) }) { AppendSpinner() }
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
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(CornerRadius.card))
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
    }
}

@Composable
private fun DocumentGridSkeleton(modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(COLUMNS),
        modifier = modifier,
        contentPadding = PaddingValues(Spacing.page),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
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

@Composable
private fun AppendSpinner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(Spacing.md)
            .clip(RoundedCornerShape(CornerRadius.card))
            .shimmer(),
    )
}

