package com.tamin.taminhamrah.feature.profile.ui.saveEvents

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.profile.model.SavedEventPR
import com.tamin.taminhamrah.feature.profile.ui.saveEvents.contract.SaveEventsEvent
import com.tamin.taminhamrah.feature.profile.ui.saveEvents.contract.SaveEventsIntent
import com.tamin.taminhamrah.feature.profile.ui.saveEvents.contract.SaveEventsUiState
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.EmptyStateMessage
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.components.taminTopAppBarGradient
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.DarkTaminColors
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_back

@Composable
fun SaveEventsRoute(
    viewModel: SaveEventsViewModel,
    onBackClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    viewModel.events.collectWithLifecycleAware { event ->
        when (event) {
            is SaveEventsEvent.NavigateBack -> onBackClicked()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.sendIntent(SaveEventsIntent.LoadData)
    }

    SaveEventsScreen(
        state = uiState,
        onIntent = viewModel::sendIntent
    )
}

@Composable
internal fun SaveEventsScreen(
    state: SaveEventsUiState,
    onIntent: (SaveEventsIntent) -> Unit
) {
    val taminColors = LocalTaminColors.current
    val listState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(taminColors.bgPage)
    ) {
        SaveEventsHeader(
            onBackClicked = { onIntent(SaveEventsIntent.NavigateBack) }
        )

        if (!state.isLoading && state.events.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                EmptyStateMessage(
                    icon = Icons.Filled.Bookmark,
                    title = "هنوز رویدادی ذخیره نشده است",
                    subtitle = "در استوری‌های صفحه اصلی روی نشان ذخیره بزنید تا رویداد اینجا نگه داشته شود.",
                    showIconTile = true
                )
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(
                    start = Spacing.page,
                    end = Spacing.page,
                    bottom = Spacing.xxl,
                    top = Spacing.lg
                ),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                items(state.events) { event ->
                    SaveEventCard(
                        event = event,
                        onClick = { /* handle */ },
                        onToggleSave = { onIntent(SaveEventsIntent.ToggleSave(event.id)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SaveEventsHeader(
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    val isDark = taminColors == DarkTaminColors

    val topBarGradient =
        remember(isDark) { Brush.horizontalGradient(taminColors.profileGradientStops) }

    val title = "ذخیره رویدادها"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    bottomEnd = 40.dp,
                    bottomStart = 40.dp
                )
            )
            .background(taminTopAppBarGradient(taminColors.profileGradientStops)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TaminTopAppBar(
            title = title,
            centerTitle = true,
            background = topBarGradient,
            bottomPadding = Spacing.none,

            navigationIcon = {
                TaminTopAppBarButton(
                    bordered = true,
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = "بازگشت",
                    onClick = onBackClicked,
                )
            }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.xs),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedRingHeaderIcon(
                icon = Icons.Filled.Bookmark,
                animated = true
            )
        }

    }
}

@Composable
private fun SaveEventCard(
    event: SavedEventPR,
    onClick: () -> Unit,
    onToggleSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface()
            .clickable(onClick = onClick)
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            Text(
                text = "${event.category} • ${event.time}",
                style = MaterialTheme.typography.bodySmall,
                color = taminColors.textMuted
            )
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = taminColors.textPrimary
            )
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium,
                color = taminColors.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(Spacing.md))

        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(RoundedCornerShape(CornerRadius.iconTile))
                .background(taminColors.blueBg)
                .border(Thickness.border, taminColors.blueBorder, RoundedCornerShape(CornerRadius.iconTile))
                .clickable(onClick = onToggleSave),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Bookmark,
                contentDescription = null,
                tint = taminColors.blueText,
                modifier = Modifier.size(IconSize.banner)
            )
        }
    }
}
