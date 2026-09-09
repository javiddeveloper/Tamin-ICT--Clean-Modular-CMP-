package com.tamin.taminhamrah.feature.stories.ui.viewer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.stories.model.StoryChannel
import com.tamin.taminhamrah.feature.stories.model.StoryItem
import com.tamin.taminhamrah.feature.stories.model.StoryMedia
import com.tamin.taminhamrah.feature.stories.ui.theme.StoryAvatarDot
import com.tamin.taminhamrah.feature.stories.ui.theme.StoryAvatarRing
import com.tamin.taminhamrah.feature.stories.ui.theme.StoryBodyInk
import com.tamin.taminhamrah.feature.stories.ui.theme.StoryCloseBorder
import com.tamin.taminhamrah.feature.stories.ui.theme.StoryCloseFill
import com.tamin.taminhamrah.feature.stories.ui.theme.StoryCtaShadow
import com.tamin.taminhamrah.feature.stories.ui.theme.StoryDimens
import com.tamin.taminhamrah.feature.stories.ui.theme.StoryGlassBorder
import com.tamin.taminhamrah.feature.stories.ui.theme.StoryGlassFill
import com.tamin.taminhamrah.feature.stories.ui.theme.StoryGlowCore
import com.tamin.taminhamrah.feature.stories.ui.theme.StoryHintInk
import com.tamin.taminhamrah.feature.stories.ui.theme.StoryKickerBorder
import com.tamin.taminhamrah.feature.stories.ui.theme.StoryKickerFill
import com.tamin.taminhamrah.feature.stories.ui.theme.StoryLikeActive
import com.tamin.taminhamrah.feature.stories.ui.theme.StoryOnBackdrop
import com.tamin.taminhamrah.feature.stories.ui.theme.StoryScrimInk
import com.tamin.taminhamrah.feature.stories.ui.theme.StoryTextStyles
import com.tamin.taminhamrah.feature.stories.ui.theme.StoryTimeInk
import com.tamin.taminhamrah.feature.stories.ui.theme.storyTextStyles
import com.tamin.taminhamrah.feature.stories.ui.viewer.contract.StoryViewerEvent
import com.tamin.taminhamrah.feature.stories.ui.viewer.contract.StoryViewerIntent
import com.tamin.taminhamrah.feature.stories.ui.viewer.contract.StoryViewerUiState
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.BackHandler
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.LoadAsyncImage
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.angledLinearGradient
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.coroutines.withTimeoutOrNull
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.feature.stories.generated.resources.Res
import taminx.feature.stories.generated.resources.ic_story_bookmark
import taminx.feature.stories.generated.resources.ic_story_bookmark_filled
import taminx.feature.stories.generated.resources.ic_story_comment
import taminx.feature.stories.generated.resources.ic_story_heart
import taminx.feature.stories.generated.resources.ic_story_heart_filled
import taminx.feature.stories.generated.resources.stories_close
import taminx.feature.stories.generated.resources.stories_comment_hint
import taminx.feature.stories.generated.resources.stories_comment_send
import taminx.feature.stories.generated.resources.stories_like
import taminx.feature.stories.generated.resources.stories_media_failed
import taminx.feature.stories.generated.resources.stories_save
import taminx.core.core_ui.Res as CoreRes
import taminx.core.core_ui.ic_close
import taminx.core.core_ui.ic_send
import taminx.core.core_ui.ic_tamin_chevron_forward

private val PillShape = RoundedCornerShape(CornerRadius.max)
private val CtaShape = RoundedCornerShape(CornerRadius.xl)

/**
 * The full-screen story viewer.
 *
 * Everything is stacked over the channel's own gradient, which is the slide's background, its
 * media's loading placeholder, and its fallback when the media cannot be shown — the same surface
 * doing all three, so a slide never flashes a different color on its way to any of them.
 *
 * The reader drives it with two invisible columns over the whole picture: the wide one moves the
 * story on, the narrow one steps back, and holding a finger anywhere on either pauses. They start
 * below the header so the close button stays reachable.
 */
@Composable
fun StoryViewerScreen(
    channelIndex: Int,
    onClose: () -> Unit,
    onOpenFeature: (FeatureFlag) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StoryViewerViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(channelIndex) {
        viewModel.sendIntent(StoryViewerIntent.Open(channelIndex))
    }

    viewModel.events.collectWithLifecycleAware { event ->
        when (event) {
            StoryViewerEvent.Close -> onClose()
            is StoryViewerEvent.OpenFeature -> onOpenFeature(event.flag)
        }
    }

    StoryViewerBody(
        state = state,
        onIntent = viewModel::sendIntent,
        onDismissError = onClose,
        modifier = modifier,
    )
}

/** The viewer without its ViewModel, so any slide it can show is one call away in a preview. */
@Composable
internal fun StoryViewerBody(
    state: StoryViewerUiState,
    onIntent: (StoryViewerIntent) -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val type = storyTextStyles()
    val channel = state.channel
    val item = state.item

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    // Dropping focus is the whole of it: the field reports that back, and the story resumes from
    // there rather than from two places having to agree.
    val dismissComment: () -> Unit = {
        keyboardController?.hide()
        focusManager.clearFocus()
    }

    // While the keyboard is up, back closes it instead of the viewer.
    BackHandler(enabled = state.isComposingComment, onBack = dismissComment)

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(if (channel != null) Modifier.storyBackdrop(channel) else Modifier),
    ) {
        if (channel != null && item != null) {
            StoryMediaLayer(
                item = item,
                isPaused = state.isPaused,
                mediaFailed = state.mediaFailed,
                onIntent = onIntent,
            )

            // Keeps the copy legible over whatever the media turned out to be.
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(StoryDimens.viewerScrimHeight)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                StoryScrimInk.copy(alpha = StoryDimens.viewerScrimAlpha),
                            ),
                        ),
                    ),
            )

            StoryTapZones(
                isComposingComment = state.isComposingComment,
                onIntent = onIntent,
                onDismissComment = dismissComment,
            )

            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars),
            ) {
                StoryProgressBar(
                    segmentCount = state.itemCount,
                    currentIndex = state.itemIndex,
                    segmentToken = state.segmentToken,
                    durationMs = state.segmentDurationMs,
                    elapsedMs = state.segmentElapsedMs,
                    isPlaying = state.isPlaying,
                    modifier = Modifier.padding(
                        start = StoryDimens.progressPaddingHorizontal,
                        end = StoryDimens.progressPaddingHorizontal,
                        top = StoryDimens.progressPaddingTop,
                    ),
                )
                StoryHeader(
                    channel = channel,
                    type = type,
                    onClose = { onIntent(StoryViewerIntent.Close) },
                    modifier = Modifier.padding(
                        start = StoryDimens.headerPaddingHorizontal,
                        end = StoryDimens.headerPaddingHorizontal,
                        top = StoryDimens.headerPaddingTop,
                    ),
                )
            }

            StoryContent(
                state = state,
                channel = channel,
                item = item,
                type = type,
                onIntent = onIntent,
                onSubmitComment = {
                    onIntent(StoryViewerIntent.CommentSubmitted)
                    dismissComment()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    // The union rather than either alone: the copy sits above the navigation bar
                    // normally, and above the keyboard while the comment field has it open.
                    .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
                    // padding: 0 20px 30px — no top inset, so the copy hangs off the bottom of
                    // the screen the way the design has it rather than floating 30 above it.
                    .padding(
                        start = StoryDimens.contentPaddingHorizontal,
                        end = StoryDimens.contentPaddingHorizontal,
                        bottom = StoryDimens.contentPaddingBottom,
                    ),
            )
        }

        // A catalogue that would not load leaves nothing to show, so acknowledging the failure
        // leaves the viewer rather than stranding the reader on an empty screen.
        ErrorStateView(
            message = state.error,
            onDismiss = onDismissError,
        )
    }
}

/** The channel gradient, plus the soft light the design puts in its upper corner. */
private fun Modifier.storyBackdrop(channel: StoryChannel): Modifier = this.drawWithCache {
    val palette = channel.palette
    val gradient = angledLinearGradient(
        angleDeg = StoryDimens.VIEWER_GRADIENT_ANGLE_DEG,
        stops = listOf(
            0f to palette.backdropStart,
            palette.backdropMidStop to palette.backdropMid,
            1f to palette.backdropEnd,
        ),
        width = size.width,
        height = size.height,
    )
    // Pinned to the physical top-left, as the design has it: DrawScope coordinates never mirror,
    // which is exactly what this needs on a right-to-left page.
    val glowCenter = Offset(
        x = -StoryDimens.viewerGlowCenterX.toPx(),
        y = -StoryDimens.viewerGlowCenterY.toPx(),
    )
    val glowRadius = StoryDimens.viewerGlowRadius.toPx()
    val glow = Brush.radialGradient(
        0f to StoryGlowCore,
        StoryDimens.VIEWER_GLOW_FADE_STOP to Color.Transparent,
        center = glowCenter,
        radius = glowRadius,
    )
    onDrawBehind {
        drawRect(brush = gradient)
        drawCircle(brush = glow, radius = glowRadius, center = glowCenter)
    }
}

/**
 * The slide's picture or clip, if it has one.
 *
 * Only the current slide is composed, so exactly one image request and at most one player exist at
 * any moment — no story ahead is fetched and nothing behind is left running.
 */
@Composable
private fun BoxScope.StoryMediaLayer(
    item: StoryItem,
    isPaused: Boolean,
    mediaFailed: Boolean,
    onIntent: (StoryViewerIntent) -> Unit,
) {
    if (mediaFailed) return

    when (val media = item.media) {
        StoryMedia.None -> Unit

        is StoryMedia.Image -> LoadAsyncImage(
            model = media.url,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            // The gradient underneath is the fallback, so nothing is drawn here — the slot exists
            // to tell the ViewModel, which is what turns the failure into a message and keeps the
            // story moving.
            errorContent = {
                LaunchedEffect(media.url) { onIntent(StoryViewerIntent.MediaFailed) }
            },
        )

        is StoryMedia.Video -> StoryVideoPlayer(
            url = media.url,
            isPaused = isPaused,
            onReady = { durationMs -> onIntent(StoryViewerIntent.MediaReady(durationMs)) },
            onEnded = { onIntent(StoryViewerIntent.MediaEnded) },
            onFailed = { onIntent(StoryViewerIntent.MediaFailed) },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

/**
 * The two invisible columns the reader drives the story with.
 *
 * Start and End rather than left and right: under this app's right-to-left layout that puts the
 * wide "next" column on the left and the narrow "back" column on the right, which is what the
 * design and the requirement both ask for — and it stays correct rather than inverted if a
 * left-to-right layout is ever added.
 */
@Composable
private fun BoxScope.StoryTapZones(
    isComposingComment: Boolean,
    onIntent: (StoryViewerIntent) -> Unit,
    onDismissComment: () -> Unit,
) {
    StoryTapZone(
        widthFraction = StoryDimens.VIEWER_PREVIOUS_ZONE_FRACTION,
        alignment = Alignment.TopStart,
        isComposingComment = isComposingComment,
        onTap = { onIntent(StoryViewerIntent.Previous) },
        onDismissComment = onDismissComment,
        onIntent = onIntent,
    )
    StoryTapZone(
        widthFraction = 1f - StoryDimens.VIEWER_PREVIOUS_ZONE_FRACTION,
        alignment = Alignment.TopEnd,
        isComposingComment = isComposingComment,
        onTap = { onIntent(StoryViewerIntent.Next) },
        onDismissComment = onDismissComment,
        onIntent = onIntent,
    )
}

/**
 * One tap column.
 *
 * ### Tap and hold are told apart by a threshold
 *
 * A finger that lifts before the platform's long-press timeout is a tap and moves the story. A
 * finger that outlives it is a hold: it pauses, and it must never move the story when it lifts.
 * `onLongPress` is supplied for exactly that reason — with it set, `detectTapGestures` stops
 * reporting a long hold as a tap, which is what used to make a hold jump to the next slide.
 *
 * The two clocks below are deliberately the same number, so a gesture cannot be counted as both.
 *
 * ### `pointerInput(Unit)`, and why it matters here
 *
 * The callbacks are read through [rememberUpdatedState] so this key can be `Unit`. Keying on the
 * lambdas instead — which are new instances on every recomposition — tore the gesture down
 * mid-press: pausing recomposes the viewer, which restarted `pointerInput`, which cancelled
 * `tryAwaitRelease()` before it could resume. The story then stayed paused for good.
 */
@Composable
private fun BoxScope.StoryTapZone(
    widthFraction: Float,
    alignment: Alignment,
    isComposingComment: Boolean,
    onTap: () -> Unit,
    onDismissComment: () -> Unit,
    onIntent: (StoryViewerIntent) -> Unit,
) {
    val currentOnTap by rememberUpdatedState(onTap)
    val currentOnDismissComment by rememberUpdatedState(onDismissComment)
    val currentOnIntent by rememberUpdatedState(onIntent)
    val currentIsComposing by rememberUpdatedState(isComposingComment)

    Box(
        modifier = Modifier
            .align(alignment)
            .fillMaxWidth(widthFraction)
            .fillMaxHeight()
            .padding(top = StoryDimens.viewerTapZoneTop)
            .pointerInput(Unit) {
                val holdThresholdMs = viewConfiguration.longPressTimeoutMillis
                detectTapGestures(
                    onPress = {
                        // While the keyboard is up the story is already held by it, and the only
                        // thing a touch out here means is "put that away".
                        if (!currentIsComposing) {
                            // Null means the finger was still down when the threshold passed —
                            // a hold. Anything shorter is a tap and is left to onTap below, so an
                            // ordinary tap never flickers the progress bar.
                            val heldPastThreshold =
                                withTimeoutOrNull(holdThresholdMs) { tryAwaitRelease() } == null
                            if (heldPastThreshold) {
                                currentOnIntent(StoryViewerIntent.Pause)
                                tryAwaitRelease()
                                currentOnIntent(StoryViewerIntent.Resume)
                            }
                        }
                    },
                    // Empty, and load-bearing: its presence is what keeps a hold from also being
                    // reported as a tap. The pause itself is handled above, where the release can
                    // be awaited.
                    onLongPress = { },
                    onTap = {
                        if (currentIsComposing) currentOnDismissComment() else currentOnTap()
                    },
                )
            },
    )
}

@Composable
private fun StoryHeader(
    channel: StoryChannel,
    type: StoryTextStyles,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(StoryDimens.headerGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(StoryDimens.headerAvatarSize)
                .border(StoryDimens.headerAvatarRingWidth, StoryAvatarRing, CircleShape)
                .clip(CircleShape)
                .drawWithCache {
                    val brush = angledLinearGradient(
                        angleDeg = StoryDimens.RING_GRADIENT_ANGLE_DEG,
                        stops = listOf(
                            0f to channel.palette.avatarStart,
                            1f to channel.palette.avatarEnd,
                        ),
                        width = size.width,
                        height = size.height,
                    )
                    onDrawBehind { drawCircle(brush) }
                },
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(StoryDimens.headerAvatarDotSize)
                    .clip(CircleShape)
                    .background(StoryAvatarDot),
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            TaminText(
                text = channel.name,
                style = type.channelName,
                color = StoryOnBackdrop,
                maxLines = 1,
            )
            TaminText(
                text = channel.time,
                modifier = Modifier.padding(top = StoryDimens.headerTimeTopGap),
                style = type.channelTime,
                color = StoryTimeInk,
                maxLines = 1,
            )
        }

        Box(
            modifier = Modifier
                .size(StoryDimens.closeButtonSize)
                .clip(CircleShape)
                .background(StoryCloseFill)
                .border(Thickness.border, StoryCloseBorder, CircleShape)
                .clickable(onClick = onClose),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = vectorResource(CoreRes.drawable.ic_close),
                contentDescription = stringResource(Res.string.stories_close),
                tint = StoryOnBackdrop,
                modifier = Modifier.size(StoryDimens.closeIconSize),
            )
        }
    }
}

@Composable
private fun StoryContent(
    state: StoryViewerUiState,
    channel: StoryChannel,
    item: StoryItem,
    type: StoryTextStyles,
    onIntent: (StoryViewerIntent) -> Unit,
    onSubmitComment: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        TaminText(
            text = channel.name,
            modifier = Modifier
                .clip(PillShape)
                .background(StoryKickerFill)
                .border(Thickness.border, StoryKickerBorder, PillShape)
                .padding(
                    horizontal = StoryDimens.kickerPaddingHorizontal,
                    vertical = StoryDimens.kickerPaddingVertical,
                ),
            style = type.kicker,
            color = StoryOnBackdrop,
        )

        TaminText(
            text = item.title,
            modifier = Modifier.padding(top = StoryDimens.titleTopGap),
            style = type.slideTitle,
            color = StoryOnBackdrop,
        )

        TaminText(
            text = item.body,
            modifier = Modifier.padding(top = StoryDimens.bodyTopGap),
            style = type.slideBody,
            color = StoryBodyInk,
        )

        if (state.mediaFailed) {
            TaminText(
                text = stringResource(Res.string.stories_media_failed),
                modifier = Modifier.padding(top = StoryDimens.bodyTopGap),
                style = type.actionHint,
                color = StoryHintInk,
            )
        }

        item.cta?.let { cta ->
            Row(
                modifier = Modifier
                    .padding(top = StoryDimens.ctaTopGap)
                    .fillMaxWidth()
                    .height(StoryDimens.ctaHeight)
                    .coloredShadow(
                        color = StoryCtaShadow,
                        borderRadius = CornerRadius.xl,
                        blurRadius = StoryDimens.ctaShadowBlur,
                        offsetY = StoryDimens.ctaShadowOffsetY,
                    )
                    .clip(CtaShape)
                    .background(StoryOnBackdrop)
                    .clickable { onIntent(StoryViewerIntent.CtaClicked) },
                horizontalArrangement = Arrangement.spacedBy(
                    space = StoryDimens.ctaGap,
                    alignment = Alignment.CenterHorizontally,
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TaminText(
                    text = cta.label,
                    style = type.ctaLabel,
                    color = channel.palette.ctaTone,
                )
                // Auto-mirrored, so it points the way the story opens: left on this page.
                Icon(
                    imageVector = vectorResource(CoreRes.drawable.ic_tamin_chevron_forward),
                    contentDescription = null,
                    tint = channel.palette.ctaTone,
                    modifier = Modifier.size(StoryDimens.ctaChevronSize),
                )
            }
        }

        StoryActions(
            state = state,
            type = type,
            onIntent = onIntent,
            onSubmitComment = onSubmitComment,
            modifier = Modifier.padding(top = StoryDimens.actionsTopGap),
        )
    }
}

/**
 * The bar of glass pills under the copy: a comment field, a like count and a bookmark.
 *
 * The field is a real one — the keyboard opens, the story holds while it is up, and sending
 * clears it. Nothing is sent anywhere: there is no comments service yet, and how the field feels
 * to type into is what this stage is for.
 *
 * While the keyboard is up the field takes the whole bar. The chips would be squeezed to nothing
 * beside it, and neither is a sensible target with a thumb on the keyboard anyway.
 */
@Composable
private fun StoryActions(
    state: StoryViewerUiState,
    type: StoryTextStyles,
    onIntent: (StoryViewerIntent) -> Unit,
    onSubmitComment: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(StoryDimens.actionsGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StoryCommentField(
            draft = state.commentDraft,
            type = type,
            onDraftChange = { onIntent(StoryViewerIntent.CommentChanged(it)) },
            onFocusChanged = { onIntent(StoryViewerIntent.CommentFocusChanged(it)) },
            onSubmit = onSubmitComment,
            modifier = Modifier.weight(1f),
        )

        if (state.isComposingComment) return@Row

        Row(
            modifier = Modifier
                .height(StoryDimens.actionHeight)
                .glassPill()
                .clickable { onIntent(StoryViewerIntent.ToggleLike) }
                .padding(horizontal = StoryDimens.actionPaddingHorizontal),
            horizontalArrangement = Arrangement.spacedBy(StoryDimens.actionInnerGap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = vectorResource(
                    if (state.isLiked) Res.drawable.ic_story_heart_filled else Res.drawable.ic_story_heart,
                ),
                contentDescription = stringResource(Res.string.stories_like),
                tint = if (state.isLiked) StoryLikeActive else StoryOnBackdrop,
                modifier = Modifier.size(StoryDimens.actionIconSize),
            )
            NumericText(
                text = state.likeCount.toString().toPersianDigits(),
                style = type.likeCount,
                color = StoryOnBackdrop,
            )
        }

        Box(
            modifier = Modifier
                .size(StoryDimens.actionHeight)
                .glassPill()
                .clickable { onIntent(StoryViewerIntent.ToggleSave) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = vectorResource(
                    if (state.isSaved) Res.drawable.ic_story_bookmark_filled else Res.drawable.ic_story_bookmark,
                ),
                contentDescription = stringResource(Res.string.stories_save),
                tint = StoryOnBackdrop,
                modifier = Modifier.size(StoryDimens.actionIconSize),
            )
        }
    }
}

/**
 * The comment field: the design's glass pill, with a real text field inside it.
 *
 * Focus is the single signal. Gaining it opens the keyboard and holds the story; losing it —
 * whether by sending, by tapping the picture, or by the system back button — releases both. That
 * is why the caller never has to pair a "start" with a matching "stop".
 *
 * The send affordance appears only once there is something to send, and does nothing beyond
 * clearing the field: there is no comments service yet. `ImeAction.Send` on the keyboard does the
 * same thing, so either route works.
 */
@Composable
private fun StoryCommentField(
    draft: String,
    type: StoryTextStyles,
    onDraftChange: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    // The pill is wider than the field's own text, so the strip either side has to focus it too.
    // No indication: a ripple across a glass capsule reads as a smear, and the caret appearing is
    // the feedback that matters.
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .height(StoryDimens.actionHeight)
            .glassPill()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) { focusRequester.requestFocus() }
            .padding(horizontal = StoryDimens.commentPaddingHorizontal),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_story_comment),
            contentDescription = null,
            tint = StoryOnBackdrop,
            modifier = Modifier.size(StoryDimens.commentIconSize),
        )

        BasicTextField(
            value = draft,
            onValueChange = onDraftChange,
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester)
                .onFocusChanged { onFocusChanged(it.isFocused) },
            textStyle = type.actionHint.copy(color = StoryOnBackdrop),
            cursorBrush = SolidColor(StoryOnBackdrop),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { onSubmit() }),
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (draft.isEmpty()) {
                        TaminText(
                            text = stringResource(Res.string.stories_comment_hint),
                            style = type.actionHint,
                            color = StoryHintInk,
                            maxLines = 1,
                        )
                    }
                    innerTextField()
                }
            },
        )

        if (draft.isNotBlank()) {
            Box(
                modifier = Modifier
                    .size(StoryDimens.commentSendSize)
                    .clip(CircleShape)
                    .clickable(onClick = onSubmit),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = vectorResource(CoreRes.drawable.ic_send),
                    contentDescription = stringResource(Res.string.stories_comment_send),
                    tint = StoryOnBackdrop,
                    modifier = Modifier.size(StoryDimens.commentIconSize),
                )
            }
        }
    }
}

/** The frosted capsule the design gives every control in the action bar. */
private fun Modifier.glassPill(): Modifier = this
    .clip(PillShape)
    .background(StoryGlassFill)
    .border(Thickness.border, StoryGlassBorder, PillShape)
