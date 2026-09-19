package com.tamin.taminhamrah.feature.agent.ui

import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextOverflow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import com.tamin.taminhamrah.ui.components.IconTile
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.taminTopAppBarGradient
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import taminx.core.core_ui.agent_screen_title
import taminx.core.core_ui.agent_processing
import taminx.core.core_ui.agent_processing_done
import taminx.core.core_ui.agent_step_done
import taminx.core.core_ui.agent_history
import taminx.core.core_ui.agent_new_chat
import taminx.core.core_ui.agent_input_placeholder
import taminx.core.core_ui.agent_send
import taminx.core.core_ui.agent_stop
import taminx.core.core_ui.agent_record_voice
import taminx.core.core_ui.agent_suggestions_label
import taminx.core.core_ui.agent_offline_banner
import taminx.core.core_ui.agent_checking_permission
import taminx.core.core_ui.agent_empty_subtitle
import taminx.core.core_ui.agent_suggestion_history
import taminx.core.core_ui.agent_suggestion_pension
import taminx.core.core_ui.agent_suggestion_prescription
import taminx.core.core_ui.agent_suggestion_early_retirement
import taminx.core.core_ui.agent_like
import taminx.core.core_ui.agent_dislike
import taminx.core.core_ui.agent_share
import taminx.core.core_ui.action_back
import taminx.core.core_ui.action_copy
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_history
import taminx.core.core_ui.ic_send
import taminx.core.core_ui.ic_tamin_copy
import taminx.core.core_ui.ic_share
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Mic
import androidx.compose.foundation.text.BasicTextField
import kotlinx.coroutines.delay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.util.toPersianDigits
import com.tamin.taminhamrah.deeplink.DeepLinkParser
import com.tamin.taminhamrah.deeplink.DeepLinkSource
import com.tamin.taminhamrah.deeplink.ParsedDeepLink
import com.tamin.taminhamrah.feature.agent.ui.markdown.MarkdownContent
import com.tamin.taminhamrah.feature.agent.markdown.MarkdownParser
import com.tamin.taminhamrah.ui.deeplink.LocalDeepLinkHandler
import com.tamin.taminhamrah.feature.agent.audio.rememberMicPermission
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.ui.bubble.ChartBubble
import com.tamin.taminhamrah.feature.agent.ui.bubble.ImageBubble
import com.tamin.taminhamrah.feature.agent.ui.bubble.PinnedVoicePlayer
import com.tamin.taminhamrah.feature.agent.ui.bubble.TableBubble
import com.tamin.taminhamrah.feature.agent.ui.bubble.RichTextBubble
import com.tamin.taminhamrah.feature.agent.ui.bubble.VideoBubble
import com.tamin.taminhamrah.feature.agent.ui.contract.AgentEvent
import com.tamin.taminhamrah.feature.agent.ui.contract.AgentIntent
import com.tamin.taminhamrah.feature.agent.ui.contract.AgentProcessingState
import com.tamin.taminhamrah.feature.agent.ui.contract.AgentUiState
import com.tamin.taminhamrah.feature.agent.ui.contract.ChatItem
import com.tamin.taminhamrah.feature.agent.ui.contract.ChatSender
import com.tamin.taminhamrah.ui.blur.AppBarScrim
import com.tamin.taminhamrah.ui.blur.safeHazeEffect
import com.tamin.taminhamrah.ui.blur.safeHazeSource
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.agent_not_allowed_default
import taminx.core.core_ui.agent_not_allowed_title
import kotlin.math.roundToInt

// ─── AgentScreen ──────────────────────────────────────────────────────────────

@Composable
fun AgentScreen(
    viewModel: AgentViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {},
    onShareText: (String) -> Unit = {}
) {
    val deepLinkHandler = LocalDeepLinkHandler.current
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var autoScrollActive by remember { mutableStateOf(true) }

    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            if (totalItems > 0 && lastVisible < totalItems - 1) {
                autoScrollActive = false
            } else if (!listState.canScrollForward) {
                autoScrollActive = true
            }
        }
    }

    LaunchedEffect(listState.canScrollForward) {
        if (!listState.canScrollForward && !listState.isScrollInProgress) {
            autoScrollActive = true
        }
    }

    // Only one scroll animation at a time: without this every request stacks another
    // coroutine, and overlapping animateScrollToItem calls stutter the list.
    var scrollJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
    val requestScrollToBottom: () -> Unit = {
        if (autoScrollActive && scrollJob?.isActive != true) {
            scrollJob = coroutineScope.launch {
                val lastIndex = (uiState.chatItems.size - 1).coerceAtLeast(0)
                listState.animateScrollToItem(lastIndex)
            }
        }
    }

    LaunchedEffect(uiState.chatItems.lastOrNull()?.id) {
        val lastItem = uiState.chatItems.lastOrNull()
        if (lastItem?.sender == ChatSender.User) {
            autoScrollActive = true
            requestScrollToBottom()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AgentEvent.ScrollToBottom -> requestScrollToBottom()
                is AgentEvent.ShowError -> { /* handled via bubble */ }
                is AgentEvent.NavigateToDeepLink ->
                    deepLinkHandler.open(event.destination.toAgentDeepLink(), DeepLinkSource.AGENT)
                is AgentEvent.NavigateToWebView -> { /* External navigation */ }
                is AgentEvent.ShareText -> onShareText(event.text)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.sendIntent(AgentIntent.CheckPermission)
    }

    AgentContent(
        uiState = uiState,
        listState = listState,
        onIntent = { viewModel.sendIntent(it) },
        onRequestScroll = requestScrollToBottom,
        onNavigateBack = onNavigateBack,
    )
}

/** A bare destination key becomes the `@key` form; a full link is passed through unchanged. */
internal fun String.toAgentDeepLink(): String =
    if (startsWith("@") || contains("://")) this else "@$this"

@Composable
private fun AgentContent(
    uiState: AgentUiState,
    listState: LazyListState,
    onIntent: (AgentIntent) -> Unit,
    onRequestScroll: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    when {
        // The header stays on every state, so the screen always has its title and a way back.
        uiState.isCheckingPermission || uiState.isNotAllowed -> Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LocalTaminColors.current.bgPage)
        ) {
            AgentTopBar(
                isGenerating = false,
                onIntent = onIntent,
                onNavigateBack = onNavigateBack,
                showSessionActions = false,
            )
            Box(modifier = Modifier.weight(1f)) {
                if (uiState.isCheckingPermission) {
                    PermissionCheckingIndicator()
                } else {
                    NotAllowedMessage(message = uiState.notAllowedMessage)
                }
            }
        }
        else -> ChatLayout(
            uiState = uiState,
            listState = listState,
            onIntent = onIntent,
            onRequestScroll = onRequestScroll,
            onNavigateBack = onNavigateBack,
        )
    }

    // Saved conversations. Rendered here rather than inside ChatLayout so it stays
    // reachable regardless of which state the screen is in.
    if (uiState.isHistoryVisible) {
        ChatHistorySheet(
            sessions = uiState.sessions,
            activeSessionId = uiState.activeSessionId,
            onDismiss = { onIntent(AgentIntent.CloseChatHistory) },
            onOpenSession = { onIntent(AgentIntent.LoadChatSession(it)) },
            onDeleteSession = { onIntent(AgentIntent.DeleteChatSession(it)) },
            onRenameSession = { id, title -> onIntent(AgentIntent.RenameChatSession(id, title)) },
            onStartNewChat = { onIntent(AgentIntent.StartNewSession) }
        )
    }
}

@Composable
private fun ChatLayout(
    uiState: AgentUiState,
    listState: LazyListState,
    onIntent: (AgentIntent) -> Unit,
    onRequestScroll: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val hazeState = remember { HazeState() }
    val density = LocalDensity.current
    // Bars overlay the content, so the chat list must reserve space for them via
    // contentPadding. We measure the real bar heights (they vary with system insets)
    // and feed them back as padding — giving the "content scrolls under the blur" look.
    var topBarHeightPx by remember { mutableStateOf(0) }
    var inputBarHeightPx by remember { mutableStateOf(0) }
    val topPad = with(density) { topBarHeightPx.toDp() }
    val bottomPad = with(density) { inputBarHeightPx.toDp() }
    val layoutScope = rememberCoroutineScope()
    // Jumping back to the message a pinned player belongs to.
    val scrollToIndex: (Int) -> Unit = { index ->
        layoutScope.launch { listState.animateScrollToItem(index) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalTaminColors.current.bgPage)
    ) {
        // ── Scrolling content: the haze source, sitting behind both bars ──
        if (uiState.chatItems.isEmpty() && !uiState.isGenerating) {
            EmptyState(
                onPromptClick = { onIntent(AgentIntent.SendTextPrompt(it)) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = topPad, bottom = bottomPad)
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .safeHazeSource(state = hazeState),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = topPad + 12.dp,
                    bottom = bottomPad + 12.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                // Top-to-bottom: no reverseLayout, newest items at bottom
            ) {
                itemsIndexed(uiState.chatItems, key = { _, it -> it.id }) { index, item ->
                    // SuggestedPrompts appear after their preceding text bubble finishes
                    // typing, so its reveal delay tracks that text's length. Only computed
                    // for that content type to avoid coupling neighbors' recomposition.
                    val typingDelay = if (item.content is ChatBubbleContent.SuggestedPrompts && item.isTypingAnimating) {
                        val prevLen = (uiState.chatItems.getOrNull(index - 1)?.content
                            as? ChatBubbleContent.Text)?.message?.length ?: 0
                        if (prevLen > 0) (prevLen * 15L) + 200L else 1500L
                    } else 1500L

                    val isVoicePlaying = uiState.playingVoiceId == item.id
                    ChatBubbleItem(
                        item = item,
                        typingDelay = typingDelay,
                        onIntent = onIntent,
                        onRequestScroll = onRequestScroll,
                        isVoicePlaying = isVoicePlaying,
                        voicePositionMs = if (isVoicePlaying) uiState.voicePlaybackPositionMs else 0
                    )
                }

                // Only the processing card stays inline with the conversation; the bare
                // 3-dot indicator is lifted out into a floating chip (see overlay below).
                if (uiState.isGenerating && uiState.processingState != null) {
                    item { TypingIndicatorBubble(processingState = uiState.processingState) }
                }
            }
        }

        // ── Pinned voice player: keeps a playing message reachable while scrolling ──
        uiState.playingVoiceId?.let { playingId ->
            PinnedVoicePlayer(
                isPlaying = uiState.isVoicePlaying,
                positionMs = uiState.voicePlaybackPositionMs,
                durationMs = uiState.voicePlaybackDurationMs,
                onTogglePlay = {
                    uiState.chatItems.firstOrNull { it.id == playingId }
                        ?.let { it.content as? ChatBubbleContent.Voice }
                        ?.let { onIntent(AgentIntent.ToggleVoicePlayback(playingId, it.source)) }
                },
                onStop = { onIntent(AgentIntent.StopVoicePlayback) },
                onClick = {
                    val index = uiState.chatItems.indexOfFirst { it.id == playingId }
                    if (index >= 0) scrollToIndex(index)
                },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = topPad + 8.dp, start = 16.dp, end = 16.dp)
            )
        }

        // ── Offline notice: the cached conversation stays readable, sending is off ──
        if (uiState.isOffline) {
            OfflineBanner(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = topPad + 8.dp, start = 16.dp, end = 16.dp)
            )
        }

        // ── Floating 3-dot indicator: separated from the list, pinned top-center ──
        AnimatedVisibility(
            visible = uiState.isGenerating && uiState.processingState == null,
            enter = fadeIn() + scaleIn(initialScale = 0.8f),
            exit = fadeOut() + scaleOut(targetScale = 0.8f),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = topPad + 12.dp)
        ) {
            FloatingTypingIndicator()
        }

        // ── Top toolbar: blurred, overlays the content, seen-through from the top ──
        AgentTopBar(
            isGenerating = uiState.isGenerating,
            onIntent = onIntent,
            onNavigateBack = onNavigateBack,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .onSizeChanged { topBarHeightPx = it.height }
        )

        // ── Bottom input: global scrim gradient behind, solid pill on top ──
        // The typing/processing indicator is rendered once inside the LazyColumn above.
        //
        // Inset handling: the activity is edge-to-edge, so the window is NOT resized for the
        // keyboard (adjustResize has no effect) and the bar must lift itself. The union of the
        // keyboard and navigation-bar insets is the larger of the two: above the keyboard while
        // it is open, above the navigation bar otherwise — never both added together.
        //
        // onSizeChanged sits before the padding so it reports the bar's *total* occupied
        // height (content + insets); the chat list reserves exactly that much space.
        val micPermission = rememberMicPermission()
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .onSizeChanged { inputBarHeightPx = it.height }
                .background(AppBarScrim.bottomGradient)
                .windowInsetsPadding(WindowInsets.ime.union(WindowInsets.navigationBars))
        ) {
            val barPadding = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 8.dp)
            when {
                uiState.voiceRecording != null -> VoiceRecorderBar(
                    state = uiState.voiceRecording,
                    onStop = { onIntent(AgentIntent.StopVoiceRecording) },
                    modifier = barPadding
                )
                uiState.voicePreview != null -> VoicePreviewBar(
                    state = uiState.voicePreview,
                    onDelete = { onIntent(AgentIntent.DeleteVoiceRecording) },
                    onTogglePlay = { onIntent(AgentIntent.TogglePreviewPlayback) },
                    onSeek = { onIntent(AgentIntent.SeekPreview(it)) },
                    onSend = { onIntent(AgentIntent.SendVoiceRecording) },
                    modifier = barPadding
                )
                else -> AgentInputBar(
                    isGenerating = uiState.isGenerating,
                    isEnabled = !uiState.isOffline,
                    isVoiceEnabled = uiState.canSendVoice,
                    onSend = { onIntent(AgentIntent.SendTextPrompt(it)) },
                    onCancel = { onIntent(AgentIntent.CancelGeneration) },
                    onStartVoice = {
                        if (micPermission.granted) {
                            onIntent(AgentIntent.StartVoiceRecording)
                        } else {
                            micPermission.request { granted ->
                                if (granted) onIntent(AgentIntent.StartVoiceRecording)
                            }
                        }
                    }
                )
            }
        }
    }
}

/**
 * The app's header: [TaminTopAppBar] on the brand gradient with its round-cornered bottom, the
 * back chevron in the start cap and the conversation actions in the end cap, as
 * [TaminTopAppBarButton]s. While a reply is generating the title carries a status line.
 */
@Composable
private fun AgentTopBar(
    isGenerating: Boolean,
    onIntent: (AgentIntent) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    showSessionActions: Boolean = true,
) {
    val taminColors = LocalTaminColors.current
    val title = stringResource(Res.string.agent_screen_title)
    TaminTopAppBar(
        title = title,
        modifier = modifier,
        navigationIcon = {
            TaminTopAppBarButton(
                icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                contentDescription = stringResource(Res.string.action_back),
                onClick = onNavigateBack,
            )
        },
        action = if (showSessionActions) {
            {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_history),
                        contentDescription = stringResource(Res.string.agent_history),
                        onClick = { onIntent(AgentIntent.OpenChatHistory) },
                    )
                    TaminTopAppBarButton(
                        icon = Icons.Rounded.Add,
                        contentDescription = stringResource(Res.string.agent_new_chat),
                        onClick = { onIntent(AgentIntent.StartNewSession) },
                    )
                }
            }
        } else {
            null
        },
        titleContent = {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = taminColors.onGradient,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                AnimatedVisibility(visible = isGenerating) {
                    Text(
                        text = stringResource(Res.string.agent_processing),
                        style = MaterialTheme.typography.labelSmall,
                        color = taminColors.textHeaderSubtitle,
                    )
                }
            }
        },
    )
}

// ─── Extension Card ───────────────────────────────────────────────────────────

@Composable
private fun ExtensionCard(
    state: AgentProcessingState,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    // Single shared shimmer clock for the whole card — the active step reads
    // this instead of each step spinning up its own infinite transition.
    val shimmer = rememberInfiniteTransition(label = "step_shimmer")
    val pulseAlpha by shimmer.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )

    Surface(
        modifier = modifier.animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
        border = null
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AnimatedVisibility(
                    visible = state.isCompleted,
                    enter = fadeIn(tween(500)),
                    exit = fadeOut(tween(500))
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = taminColors.greenText,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = stringResource(if (state.isCompleted) Res.string.agent_processing_done else Res.string.agent_processing),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = if (state.isCompleted) taminColors.greenText else taminColors.blueText,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }



            state.steps.forEachIndexed { index, step ->
                val visibleState = remember { MutableTransitionState(false).apply { targetState = true } }
                AnimatedVisibility(
                    visibleState = visibleState,
                    enter = fadeIn(tween(500)) + expandVertically(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val isDone = state.isCompleted || index < state.currentActiveIndex
                    val isActive = index == state.currentActiveIndex && !state.isCompleted
                    val showLine = !state.isCompleted && index < state.steps.lastIndex
                    ExtensionCardStep(
                        label = step,
                        isActive = isActive,
                        isDone = isDone,
                        showLine = showLine,
                        pulseAlpha = pulseAlpha
                    )
                }
            }
        }
    }
}

@Composable
private fun ExtensionCardStep(
    label: String,
    isActive: Boolean,
    isDone: Boolean,
    showLine: Boolean = false,
    pulseAlpha: Float = 1f
) {
    val taminColors = LocalTaminColors.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isActive && !isDone) Modifier.alpha(pulseAlpha) else Modifier)
    ) {
        // Icon column with connector line
        Box(modifier = Modifier.width(20.dp), contentAlignment = Alignment.Center) {
            if (showLine) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(24.dp)
                        .offset(y = 16.dp)
                        .background(taminColors.divider)
                )
            }
            androidx.compose.animation.AnimatedVisibility(
                visible = isDone,
                enter = fadeIn(tween(400)),
                exit = fadeOut(tween(400))
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(Res.string.agent_step_done),
                    tint = taminColors.greenText,
                    modifier = Modifier.size(14.dp)
                )
            }
            androidx.compose.animation.AnimatedVisibility(
                visible = isActive && !isDone,
                enter = fadeIn(tween(400)),
                exit = fadeOut(tween(400))
            ) {
                IosSpinner(
                    modifier = Modifier.size(14.dp),
                    color = taminColors.blueText
                )
            }
            androidx.compose.animation.AnimatedVisibility(
                visible = !isActive && !isDone,
                enter = fadeIn(tween(400)),
                exit = fadeOut(tween(400))
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(taminColors.chevron)
                )
            }
        }

        Spacer(Modifier.width(10.dp))

        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall.copy(
                color = when {
                    isDone   -> taminColors.textSecondary
                    isActive -> taminColors.textPrimary
                    else     -> taminColors.textMuted
                }
            )
        )

        if (isDone) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = null,
                tint = taminColors.greenText.copy(alpha = 0.7f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// ─── Spinner ──────────────────────────────────────────────────────────────────

@Composable
private fun IosSpinner(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    petalCount: Int = 8
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ios_spinner")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ios_spinner_angle"
    )

    androidx.compose.foundation.Canvas(modifier = modifier) {
        val radius = minOf(size.width, size.height) / 2f
        val innerRadius = radius * 0.45f
        val outerRadius = radius * 0.9f
        val petalWidth = radius * 0.35f
        val currentTick = ((angle / 360f) * petalCount).toInt() % petalCount

        for (i in 0 until petalCount) {
            val petalAngle = (i * 360f / petalCount) - 90f
            val diff = (currentTick - i + petalCount) % petalCount
            val a = 1f - (diff.toFloat() / petalCount) * 0.8f
            val rad = petalAngle * (kotlin.math.PI / 180f).toFloat()
            val start = androidx.compose.ui.geometry.Offset(
                x = center.x + innerRadius * kotlin.math.cos(rad),
                y = center.y + innerRadius * kotlin.math.sin(rad)
            )
            val end = androidx.compose.ui.geometry.Offset(
                x = center.x + outerRadius * kotlin.math.cos(rad),
                y = center.y + outerRadius * kotlin.math.sin(rad)
            )
            drawLine(
                color = color.copy(alpha = a),
                start = start,
                end = end,
                strokeWidth = petalWidth,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        }
    }
}

// ─── Typing Indicator ─────────────────────────────────────────────────────────

@Composable
private fun TypingIndicatorBubble(processingState: AgentProcessingState?) {
    val currentLayoutDirection = LocalLayoutDirection.current
    val taminColors = LocalTaminColors.current
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides currentLayoutDirection) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                ) {
                    if (processingState != null) {
                        ExtensionCard(state = processingState, modifier = Modifier.fillMaxWidth())
                    } else {
                        TypingDotsIndicator()
                    }
                }
            }
        }
    }
}

/**
 * Follow-up suggestion chips. Tapping one sends it as the next prompt.
 *
 * Chips wrap horizontally (FlowRow) so short prompts sit side-by-side rather than
 * every chip taking its own full-width row — matching the GroupButtonItemViewHolder
 * chip-group layout from the legacy Android adapter.
 */
@Composable
private fun SuggestedPromptChips(
    prompts: List<String>,
    onPromptClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        Text(
            text = stringResource(Res.string.agent_suggestions_label),
            style = MaterialTheme.typography.labelMedium,
            color = taminColors.textMuted,
        )
        androidx.compose.foundation.layout.FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            prompts.forEach { prompt -> PromptChip(text = prompt, onClick = { onPromptClick(prompt) }) }
        }
    }
}

/** A prompt the user can send with one tap, in the app's blue chip style. */
@Composable
private fun PromptChip(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val taminColors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.chip)
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = taminColors.blueText,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
        modifier = modifier
            .clip(shape)
            .background(taminColors.blueBg)
            .border(Thickness.border, taminColors.blueBorder, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
    )
}

/**
 * Shown when the assistant service is unreachable. The cached conversation stays visible
 * behind it — only sending a new prompt is blocked.
 */
@Composable
private fun OfflineBanner(modifier: Modifier = Modifier) {
    val taminColors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.lg)
    Text(
        text = stringResource(Res.string.agent_offline_banner),
        style = MaterialTheme.typography.labelMedium,
        color = taminColors.dangerText,
        modifier = modifier
            .clip(shape)
            .background(taminColors.dangerBg)
            .border(Thickness.border, taminColors.dangerBorder, shape)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
    )
}

/**
 * Standalone "thinking" indicator shown floating at the top-center of the chat, separate
 * from the message list. Used while the agent is generating but has no processing card yet.
 */
@Composable
private fun FloatingTypingIndicator() {
    val taminColors = LocalTaminColors.current
    Box(
        modifier = Modifier
            .shadow(
                elevation = Elevation.md,
                shape = CircleShape,
                ambientColor = taminColors.shadowSubtle,
                spotColor = taminColors.shadowSubtle
            )
            .clip(CircleShape)
            .background(taminColors.bgSurface)
            .border(Thickness.border, taminColors.border, CircleShape)
            .padding(horizontal = Spacing.xlg, vertical = Spacing.smd)
    ) {
        TypingDotsIndicator()
    }
}

@Composable
private fun TypingDotsIndicator() {
    val taminColors = LocalTaminColors.current
    val transition = rememberInfiniteTransition(label = "typing")
    val dots = List(3) { index ->
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(400, delayMillis = index * 150),
                repeatMode = RepeatMode.Reverse
            ),
            label = "dot_alpha_$index"
        )
    }
    val density = LocalDensity.current
    val maxOffsetPx = with(density) { (-4).dp.toPx() }
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        dots.forEach { anim ->
            Box(
                modifier = Modifier
                    .size(6.dp)
                    // Lambda-based offset: reads the animated value at layout time so
                    // each frame only re-lays-out/redraws instead of recomposing.
                    .offset { IntOffset(x = 0, y = (maxOffsetPx * anim.value).roundToInt()) }
                    .clip(CircleShape)
                    .background(taminColors.blueText.copy(alpha = 0.4f + (anim.value * 0.6f)))
            )
        }
    }
}

// ─── Typewriter & Markdown ────────────────────────────────────────────────────

@Composable
private fun TypewriterText(
    text: String,
    style: androidx.compose.ui.text.TextStyle,
    onRequestScroll: () -> Unit = {},
    onAnimationFinished: () -> Unit = {}
) {
    // Parse markdown once, then stream the result word-by-word like an LLM response
    // (GPT-style) instead of fading whole lines in at once.
    val target = remember(text) { parseMarkdownBlock(text) }
    // Plain remember: the ViewModel is the authority on whether this bubble already
    // animated (see AgentIntent.OnTypingFinished), so no state needs saving here.
    var isFinished by remember(text) { mutableStateOf(false) }
    var visibleChars by remember(text) { mutableStateOf(0) }

    // "Thanos" materialize: the reveal resolves from a soft blur + low alpha into a
    // sharp, fully-opaque block. Runs once per bubble, concurrently with the typing.
    val materialize = remember(text) {
        androidx.compose.animation.core.Animatable(if (isFinished) 1f else 0f)
    }

    LaunchedEffect(text) {
        if (isFinished) {
            visibleChars = target.length
            onAnimationFinished()
            return@LaunchedEffect
        }
        launch {
            materialize.animateTo(1f, animationSpec = tween(durationMillis = 340, easing = LinearOutSlowInEasing))
        }
        val full = target.text
        // Scrolling is requested on a slow cadence rather than once per word: each call
        // launches an animateScrollToItem, and firing one every ~30ms both stacks up
        // coroutines and fights the user's own scrolling — the main source of jank.
        var wordsSinceScroll = 0
        while (visibleChars < full.length) {
            visibleChars = nextWordBoundary(full, visibleChars)
            if (++wordsSinceScroll >= WORDS_PER_SCROLL_REQUEST) {
                wordsSinceScroll = 0
                onRequestScroll()
            }
            delay(WORD_REVEAL_DELAY_MS)
        }
        visibleChars = full.length
        isFinished = true
        onAnimationFinished()
    }

    val shown = remember(visibleChars, target) {
        target.subSequence(0, visibleChars.coerceIn(0, target.length))
    }

    Text(
        text = shown,
        style = style,
        modifier = androidx.compose.ui.Modifier
            .graphicsLayer { this.alpha = 0.35f + 0.65f * materialize.value }
            .blur(((1f - materialize.value) * 5f).dp)
    )
}

/** Cadence of the GPT-style word streaming reveal. */
private const val WORD_REVEAL_DELAY_MS = 28L

/**
 * How many revealed words pass between auto-scroll requests while a bubble types.
 * At [WORD_REVEAL_DELAY_MS] this is roughly one scroll every 170ms — enough to keep the
 * newest text in view without launching a scroll animation on every frame.
 */
private const val WORDS_PER_SCROLL_REQUEST = 6

/** Advances the reveal cursor past the next run of whitespace and the word after it. */
private fun nextWordBoundary(s: String, from: Int): Int {
    if (from >= s.length) return s.length
    var i = from
    while (i < s.length && s[i].isWhitespace()) i++
    while (i < s.length && !s[i].isWhitespace()) i++
    return i
}

/** An assistant markdown answer whose links are routed like every other link in the chat. */
@Composable
private fun AgentMarkdown(
    text: String,
    isAnimating: Boolean,
    contentColor: androidx.compose.ui.graphics.Color,
    onIntent: (AgentIntent) -> Unit,
    onRequestScroll: () -> Unit,
    onAnimationFinished: () -> Unit,
) {
    val deepLinkHandler = LocalDeepLinkHandler.current
    MarkdownContent(
        text = text,
        isAnimating = isAnimating,
        contentColor = contentColor,
        onRequestScroll = onRequestScroll,
        onAnimationFinished = onAnimationFinished,
        onLinkClick = { link ->
            // A prompt link continues the conversation; everything else leaves through
            // the app's deep link gate, which applies the target's feature flag.
            when (val parsed = DeepLinkParser.parse(link, DeepLinkSource.AGENT)) {
                is ParsedDeepLink.Prompt -> onIntent(AgentIntent.SendTextPrompt(parsed.text))
                else -> deepLinkHandler.open(link, DeepLinkSource.AGENT)
            }
        },
    )
}

/**
 * Parses a single line of markdown into a styled AnnotatedString.
 *
 * Handles (in priority order):
 * - `### Header` / `## Header` / `# Header` → Bold + larger visual weight
 * - `> Blockquote` → italic + muted color prefix
 * - `1. …` numbered list → keeps number, indented
 * - `- `, `* `, `● `, `• ` bullet list → replaces with `● `
 * - `***bold+italic***` / `___bold+italic___`
 * - `**bold**` / `__bold__`
 * - `*italic*` / `_italic_`
 * - Inline `code` spans
 */
private fun parseMarkdownLine(line: String): androidx.compose.ui.text.AnnotatedString {
    return androidx.compose.ui.text.buildAnnotatedString {
        val trimmed = line.trimStart().toPersianDigits()

        // ── Block-level prefixes ──────────────────────────────────────────────
        val (processedLine, blockStyle) = when {
            trimmed.startsWith("### ") -> {
                trimmed.removePrefix("### ") to
                    androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold)
            }
            trimmed.startsWith("## ") -> {
                trimmed.removePrefix("## ") to
                    androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold)
            }
            trimmed.startsWith("# ") -> {
                trimmed.removePrefix("# ") to
                    androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold)
            }
            trimmed.startsWith("> ") -> {
                // Blockquote: keep a ▌ prefix and render the rest in italic
                "▌ " + trimmed.removePrefix("> ") to
                    androidx.compose.ui.text.SpanStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            }
            // Numbered list: 1. / 2. / … — keep the number, add indent
            Regex("^\\d+\\.\\s+").containsMatchIn(trimmed) -> {
                val match = Regex("^(\\d+\\.\\s+)").find(trimmed)
                val prefix = match?.value ?: ""
                "  $prefix" + trimmed.removePrefix(prefix) to null
            }
            // Bullet list: -, *, ●, •
            Regex("^[-*●•]\\s+").containsMatchIn(trimmed) -> {
                "● " + Regex("^[-*●•]\\s+").replace(trimmed, "") to null
            }
            else -> line to null
        }

        // Apply block-level style wrapping for headers/blockquotes
        if (blockStyle != null) {
            withStyle(blockStyle) {
                appendInlineStyles(processedLine)
            }
        } else {
            appendInlineStyles(processedLine)
        }
    }
}

/**
 * Applies inline markdown styles (bold+italic, bold, italic, code) to [text].
 * Called from [parseMarkdownLine] after block-level prefix handling.
 */
private fun androidx.compose.ui.text.AnnotatedString.Builder.appendInlineStyles(text: String) {
    // Regex order matters: bold+italic must come before bold and italic.
    val inlinePatterns = listOf(
        // ***bold+italic*** or ___bold+italic___
        Regex("(\\*\\*\\*|___)(.*?)\\1") to { _: String, content: String ->
            androidx.compose.ui.text.SpanStyle(
                fontWeight = FontWeight.Bold,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            ) to content
        },
        // **bold** or __bold__
        Regex("(\\*\\*|__)(.*?)\\1") to { _: String, content: String ->
            androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold) to content
        },
        // *italic* or _italic_  (but not ** or __)
        Regex("(?<!\\*)\\*(?!\\*)(.*?)(?<!\\*)\\*(?!\\*)|(?<!_)_(?!_)(.*?)(?<!_)_(?!_)") to { _: String, content: String ->
            androidx.compose.ui.text.SpanStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic) to content
        },
        // `inline code`
        Regex("`(.*?)`") to { _: String, content: String ->
            androidx.compose.ui.text.SpanStyle(
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                background = androidx.compose.ui.graphics.Color(0x18000000)
            ) to content
        }
    )

    // Build a flat list of (range, style, content) from all patterns
    data class Span(val start: Int, val end: Int, val style: androidx.compose.ui.text.SpanStyle, val content: String)

    val spans = mutableListOf<Span>()
    for ((regex, styleBuilder) in inlinePatterns) {
        for (match in regex.findAll(text)) {
            // Extract the actual content (group 2 for bold+italic/bold, or 1/2 for italic)
            val content = match.groupValues.drop(1).firstOrNull { it.isNotEmpty() } ?: continue
            val (style, _) = styleBuilder("", content)
            // Avoid overlapping spans from earlier (higher-priority) patterns
            val overlaps = spans.any { it.start < match.range.last + 1 && it.end > match.range.first }
            if (!overlaps) spans.add(Span(match.range.first, match.range.last + 1, style, content))
        }
    }
    spans.sortBy { it.start }

    var cursor = 0
    for (span in spans) {
        if (cursor < span.start) append(text.substring(cursor, span.start))
        withStyle(span.style) { append(span.content) }
        cursor = span.end
    }
    if (cursor < text.length) append(text.substring(cursor))
}

private fun parseMarkdownBlock(text: String): androidx.compose.ui.text.AnnotatedString {
    return androidx.compose.ui.text.buildAnnotatedString {
        val lines = text.split("\n")
        lines.forEachIndexed { index, line ->
            append(parseMarkdownLine(line))
            if (index < lines.size - 1) append("\n")
        }
    }
}

// ─── Chat Bubble ──────────────────────────────────────────────────────────────

@Composable
private fun ChatBubbleItem(
    item: ChatItem,
    typingDelay: Long = 1500L,
    onIntent: (AgentIntent) -> Unit = {},
    onRequestScroll: () -> Unit = {},
    isVoicePlaying: Boolean = false,
    voicePositionMs: Int = 0
) {
    val isUser = item.sender == ChatSender.User
    val currentLayoutDirection = LocalLayoutDirection.current

    var isAnimationFinished by rememberSaveable(item.id) { mutableStateOf(!item.isTypingAnimating) }

    // Shared renderer for all three layouts — only the wrapper (user surface / agent
    // full-width / processing box) differs. Content itself is drawn in the caller's
    // layout direction while the Row stays LTR for consistent bubble alignment.
    val renderContent: @Composable (Color) -> Unit = { contentColor ->
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            BubbleContentRenderer(
                content = item.content,
                isTypingAnimating = item.isTypingAnimating,
                typingDelay = typingDelay,
                onIntent = onIntent,
                contentColor = contentColor,
                onRequestScroll = onRequestScroll,
                onAnimationFinished = {
                    isAnimationFinished = true
                    // Tell the ViewModel too: it owns the "already animated" flag so the
                    // reveal never replays when this row is recycled during scrolling.
                    if (item.isTypingAnimating) onIntent(AgentIntent.OnTypingFinished(item.id))
                },
                itemId = item.id,
                isUser = isUser,
                isVoicePlaying = isVoicePlaying,
                voicePositionMs = voicePositionMs
            )
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
        ) {
            // Full-width bubbles: system notes and data views draw their own container.
            val isProcessingOrEmbedded = item.content is ChatBubbleContent.ProcessingSteps ||
                                         item.content is ChatBubbleContent.DataView

            when {
                // Voice draws its own container (with waveform + progress), so it must
                // not be wrapped in the plain text Surface — otherwise the bar is hidden.
                item.content is ChatBubbleContent.Voice -> {
                    Box { renderContent(MaterialTheme.colorScheme.onSurface) }
                }

                isProcessingOrEmbedded -> {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        renderContent(MaterialTheme.colorScheme.onSurface)
                    }
                }

                isUser -> {
                    // The user's words on the app's brand gradient, like its primary buttons; the
                    // corner nearest the screen edge is tucked in to point at the sender.
                    val taminColors = LocalTaminColors.current
                    Box(
                        modifier = Modifier
                            .widthIn(max = USER_BUBBLE_MAX_WIDTH)
                            .clip(
                                RoundedCornerShape(
                                    topStart = CornerRadius.xl,
                                    topEnd = CornerRadius.sm,
                                    bottomStart = CornerRadius.xl,
                                    bottomEnd = CornerRadius.xl,
                                )
                            )
                            .background(taminTopAppBarGradient())
                            .padding(vertical = Spacing.smPlus, horizontal = Spacing.smd)
                    ) {
                        renderContent(taminColors.onGradient)
                    }
                }

                else -> {
                    // Agent: full width, no Surface card background, no avatar padding
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.padding(vertical = Spacing.xs)) {
                            renderContent(LocalTaminColors.current.textPrimary)
                        }
                        // Follow-up suggestions belong to this reply, so they render inside
                        // the same bubble instead of forming their own chat row.
                        if (item.suggestedPrompts.isNotEmpty() && isAnimationFinished) {
                            SuggestedPromptChips(
                                prompts = item.suggestedPrompts,
                                onPromptClick = { onIntent(AgentIntent.SendTextPrompt(it)) },
                                modifier = Modifier.padding(top = Spacing.sm)
                            )
                        }
                        // Footer: only for agent bubbles, not SuggestedPrompts
                        if (item.content !is ChatBubbleContent.SuggestedPrompts && isAnimationFinished) {
                            AgentBubbleFooter(item = item, onIntent = onIntent)
                        }
                    }
                }
            }

        }
    }
}

// ─── Bubble Content Renderer ──────────────────────────────────────────────────

@Composable
private fun BubbleContentRenderer(
    content: ChatBubbleContent,
    isTypingAnimating: Boolean = false,
    typingDelay: Long = 1500L,
    onIntent: (AgentIntent) -> Unit = {},
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    onRequestScroll: () -> Unit = {},
    onAnimationFinished: () -> Unit = {},
    itemId: String = "",
    isUser: Boolean = false,
    isVoicePlaying: Boolean = false,
    voicePositionMs: Int = 0
) {
    val taminColors = LocalTaminColors.current

    when (content) {
        is ChatBubbleContent.Voice -> {
            onAnimationFinished()
            VoiceChatBubble(
                filePath = content.source,
                durationMs = content.durationMs ?: 0L,
                isPlaying = isVoicePlaying,
                positionMs = voicePositionMs,
                amplitudes = content.amplitudes,
                isUser = isUser,
                onToggle = { onIntent(AgentIntent.ToggleVoicePlayback(itemId, content.source)) },
                onSeek = { onIntent(AgentIntent.SeekVoicePlayback(itemId, it)) }
            )
        }

        // A plain message carrying a link (e.g. a general_response in CLIENT mode) is drawn as
        // markdown, so its links become buttons instead of raw `[label](@key)` text.
        is ChatBubbleContent.Text -> if (MarkdownParser.containsLink(content.message)) {
            AgentMarkdown(
                text = content.message,
                isAnimating = isTypingAnimating,
                contentColor = contentColor,
                onIntent = onIntent,
                onRequestScroll = onRequestScroll,
                onAnimationFinished = onAnimationFinished,
            )
        } else {
            val textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = contentColor,
                lineHeight = 22.sp
            )
            if (isTypingAnimating) {
                TypewriterText(text = content.message, style = textStyle, onRequestScroll = onRequestScroll, onAnimationFinished = onAnimationFinished)
            } else {
                Text(text = parseMarkdownBlock(content.message), style = textStyle)
                onAnimationFinished()
            }
        }

        is ChatBubbleContent.Markdown -> AgentMarkdown(
            text = content.text,
            isAnimating = isTypingAnimating,
            contentColor = contentColor,
            onIntent = onIntent,
            onRequestScroll = onRequestScroll,
            onAnimationFinished = onAnimationFinished,
        )

        is ChatBubbleContent.KeyValue -> {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                content.title?.let { title ->
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }

                content.items.forEachIndexed { lineIndex, (key, value) ->
                    key(lineIndex) {
                        // rememberSaveable: persist visible across scroll — only animates once
                        var visible by rememberSaveable(key, value) { mutableStateOf(!isTypingAnimating) }

                        LaunchedEffect(isTypingAnimating) {
                            if (!visible && isTypingAnimating) {
                                delay(lineIndex * 50L) // Fast stagger: 50ms per line
                                visible = true
                            }
                        }

                        if (visible) {
                            val alpha = remember { androidx.compose.animation.core.Animatable(if (!isTypingAnimating) 1f else 0f) }
                            LaunchedEffect(Unit) {
                                if (alpha.value < 1f) {
                                    alpha.animateTo(1f, animationSpec = tween(durationMillis = 300))
                                }
                            }

                            // Wrap with box to apply alpha fade
                            Box(modifier = androidx.compose.ui.Modifier.graphicsLayer { this.alpha = alpha.value }) {
                                if (key.startsWith("----") || key.startsWith("────")) {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                        thickness = 0.5.dp,
                                        modifier = Modifier.padding(vertical = 3.dp)
                                    )
                                } else {
                                    Text(
                                        text = androidx.compose.ui.text.buildAnnotatedString {
                                            withStyle(style = androidx.compose.ui.text.SpanStyle(color = taminColors.textSecondary)) {
                                                append("${key.toPersianDigits()}: ")
                                            }
                                            withStyle(style = androidx.compose.ui.text.SpanStyle(
                                                fontWeight = FontWeight.Medium,
                                                color = contentColor
                                            )) {
                                                append(value.toPersianDigits())
                                            }
                                        },
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = contentColor,
                                            lineHeight = 21.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        is ChatBubbleContent.DeepLink -> {
            val deepLinkHandler = LocalDeepLinkHandler.current
            OutlinedButton(
                onClick = { deepLinkHandler.open(content.destination.toAgentDeepLink(), DeepLinkSource.AGENT) },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            ) {
                Text(content.title)
            }
        }

        is ChatBubbleContent.WebLink -> {
            OutlinedButton(
                onClick = { /* open browser */ },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            ) {
                Text("🔗 ${content.title}")
            }
        }

        is ChatBubbleContent.SuggestedPrompts -> {
            var isVisible by remember { mutableStateOf(!isTypingAnimating) }
            if (isTypingAnimating) {
                LaunchedEffect(Unit) {
                    delay(typingDelay)
                    isVisible = true
                }
            }
            // Fallback path: suggestions normally ride inside the reply bubble (see
            // ChatItem.suggestedPrompts) and only land here when they arrive with no
            // reply to attach to. No enter animation — the chips just appear.
            if (isVisible) {
                SuggestedPromptChips(
                    prompts = content.prompts,
                    onPromptClick = { onIntent(AgentIntent.SendTextPrompt(it)) }
                )
            }
        }

        is ChatBubbleContent.ProcessingSteps -> {
            ExtensionCard(
                state = AgentProcessingState(content.steps, content.currentActiveIndex, content.isCompleted),
                modifier = Modifier.fillMaxWidth()
            )
        }

        is ChatBubbleContent.Image -> {
            onAnimationFinished()
            ImageBubble(content)
        }

        is ChatBubbleContent.Chart -> {
            onAnimationFinished()
            ChartBubble(content)
        }

        is ChatBubbleContent.Table -> {
            onAnimationFinished()
            TableBubble(content)
        }

        is ChatBubbleContent.RichText -> {
            onAnimationFinished()
            RichTextBubble(content = content, contentColor = contentColor)
        }

        is ChatBubbleContent.Video -> {
            onAnimationFinished()
            VideoBubble(content = content)
        }

        is ChatBubbleContent.DynamicForm -> {
            onAnimationFinished()
            // Rendered once the generative-form handlers land; until then the schema is
            // carried through untouched so nothing is lost.
            Text("📝 فرم پویا", color = MaterialTheme.colorScheme.primary)
        }

        is ChatBubbleContent.ServiceError -> {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .run {
                        when {
                            content.canRetryPrompt ->
                                clickable { onIntent(AgentIntent.OnRetryClick) }
                            content.actionKey != null ->
                                clickable { onIntent(AgentIntent.ExecuteServiceAction(content.actionKey, content.payload)) }
                            else -> this
                        }
                    }
                    .padding(4.dp)
            ) {
                if (content.canRetryPrompt || content.actionKey != null) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "تلاش مجدد",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("⚠️", fontSize = 16.sp)
                }
                Text(
                    text = content.message,
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error)
                )
            }
        }
    }
}

// ─── Input Bar ────────────────────────────────────────────────────────────────

@Composable
private fun AgentInputBar(
    isGenerating: Boolean,
    onSend: (String) -> Unit,
    onCancel: () -> Unit,
    onStartVoice: () -> Unit = {},
    /** False while the service is unreachable — the field stays visible but inert. */
    isEnabled: Boolean = true,
    /** The server decides per user whether voice prompts are allowed (`canSendVoice`). */
    isVoiceEnabled: Boolean = true
) {
    var text by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }
    val taminColors = LocalTaminColors.current
    val neon = rememberNeonFocus(isFocused)
    // Same box as TaminTextField — surface fill and hairline border — lit with the assistant's neon
    // (a glow and a turning gradient border) while it has focus.
    val shape = RoundedCornerShape(CornerRadius.lg)
    val send = {
        if (text.isNotBlank() && !isGenerating) {
            onSend(text.trim())
            text = ""
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = Spacing.lg, end = Spacing.lg, bottom = Spacing.lg, top = Spacing.sm)
            // Glow first: the shadow below clips whatever follows it, and the glow draws outside the box.
            .neonGlow(neon, color = taminColors.aiAssistantNeonStops.first(), cornerRadius = CornerRadius.lg)
            .shadow(elevation = Elevation.sm, shape = shape, ambientColor = taminColors.shadowSubtle, spotColor = taminColors.shadowSubtle)
            .clip(shape)
            .background(taminColors.bgSurface)
            .neonBorder(
                state = neon,
                neonColors = taminColors.aiAssistantNeonStops,
                restingColor = taminColors.border,
                cornerRadius = CornerRadius.lg,
                restingWidth = Thickness.border,
            )
            .padding(start = Spacing.md, end = Spacing.sm, top = Spacing.sm, bottom = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        BasicTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .weight(1f)
                .onFocusChanged { isFocused = it.isFocused },
            enabled = !isGenerating && isEnabled,
            maxLines = INPUT_MAX_LINES,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = taminColors.textPrimary),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { send() }),
            cursorBrush = androidx.compose.ui.graphics.SolidColor(taminColors.blueText),
            decorationBox = { innerTextField ->
                if (text.isEmpty()) {
                    Text(
                        text = stringResource(Res.string.agent_input_placeholder),
                        style = MaterialTheme.typography.bodyMedium,
                        color = taminColors.textMuted,
                    )
                }
                innerTextField()
            }
        )

        val isTyping = text.isNotBlank()
        val mode = when {
            isGenerating -> InputAction.Stop
            isTyping || !isVoiceEnabled -> InputAction.Send
            else -> InputAction.Voice
        }
        InputActionButton(
            mode = mode,
            enabled = isEnabled || isGenerating,
            onClick = {
                when (mode) {
                    InputAction.Stop -> onCancel()
                    InputAction.Send -> send()
                    InputAction.Voice -> onStartVoice()
                }
            },
        )
    }
}

private enum class InputAction { Send, Voice, Stop }

/**
 * The input's square action: send on the brand gradient, voice on the subtle icon tile, stop on
 * the danger tint — the same shapes as the app's icon tiles.
 */
@Composable
private fun InputActionButton(mode: InputAction, enabled: Boolean, onClick: () -> Unit) {
    val taminColors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.md)
    val background = when (mode) {
        InputAction.Send -> Modifier.background(taminTopAppBarGradient(), shape)
        InputAction.Voice -> Modifier.background(taminColors.iconBgSubtle, shape)
        // The danger tint alone barely shows on the white field, so stop also gets its border.
        InputAction.Stop -> Modifier
            .background(taminColors.dangerBg, shape)
            .border(Thickness.border, taminColors.dangerBorder, shape)
    }
    Box(
        modifier = Modifier
            .size(IconSize.largePlus)
            .alpha(if (enabled) 1f else taminColors.disabledAlpha)
            .clip(shape)
            .then(background)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(targetState = mode, label = "input_action") { target ->
            when (target) {
                InputAction.Send -> Icon(
                    painter = painterResource(Res.drawable.ic_send),
                    contentDescription = stringResource(Res.string.agent_send),
                    tint = taminColors.onGradient,
                    modifier = Modifier.size(IconSize.banner),
                )
                InputAction.Voice -> Icon(
                    imageVector = Icons.Rounded.Mic,
                    contentDescription = stringResource(Res.string.agent_record_voice),
                    tint = taminColors.blueText,
                    modifier = Modifier.size(IconSize.medium),
                )
                InputAction.Stop -> Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = stringResource(Res.string.agent_stop),
                    tint = taminColors.dangerText,
                    modifier = Modifier.size(IconSize.medium),
                )
            }
        }
    }
}

// ─── Empty State ──────────────────────────────────────────────────────────────

@Composable
private fun EmptyState(onPromptClick: (String) -> Unit, modifier: Modifier = Modifier) {
    val taminColors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.page),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(IconSize.xxxlarge)
                .clip(RoundedCornerShape(CornerRadius.card))
                .background(taminColors.aiAssistantGradient),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = taminColors.onGradient,
                modifier = Modifier.size(IconSize.large),
            )
        }
        Spacer(Modifier.height(Spacing.xlg))
        Text(
            text = stringResource(Res.string.agent_screen_title),
            style = MaterialTheme.typography.titleLarge,
            color = taminColors.textPrimary,
        )
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = stringResource(Res.string.agent_empty_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = taminColors.textSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Spacing.xl))
        AgentSuggestions(onPromptClick = onPromptClick)
    }
}

@Composable
private fun AgentSuggestions(onPromptClick: (String) -> Unit) {
    val suggestions = listOf(
        stringResource(Res.string.agent_suggestion_history),
        stringResource(Res.string.agent_suggestion_pension),
        stringResource(Res.string.agent_suggestion_prescription),
        stringResource(Res.string.agent_suggestion_early_retirement),
    )
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        suggestions.forEach { suggestion -> PromptChip(text = suggestion, onClick = { onPromptClick(suggestion) }) }
    }
}

// ─── Permission & Not Allowed ─────────────────────────────────────────────────

@Composable
private fun PermissionCheckingIndicator() {
    val taminColors = LocalTaminColors.current
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            CircularProgressIndicator(color = taminColors.blueText)
            Text(
                text = stringResource(Res.string.agent_checking_permission),
                style = MaterialTheme.typography.bodyMedium,
                color = taminColors.textSecondary,
            )
        }
    }
}

@Composable
private fun NotAllowedMessage(message: String?) {
    val taminColors = LocalTaminColors.current
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
            modifier = Modifier.padding(Spacing.xxl)
        ) {
            IconTile(
                icon = Icons.Rounded.Block,
                tint = taminColors.onGradient,
                background = taminColors.iconGradientDanger,
                size = IconSize.xxlarge,
            )
            Text(
                text = stringResource(Res.string.agent_not_allowed_title),
                style = MaterialTheme.typography.titleLarge,
                color = taminColors.textPrimary,
            )
            Text(
                text = message ?: stringResource(Res.string.agent_not_allowed_default),
                style = MaterialTheme.typography.bodyMedium,
                color = taminColors.textSecondary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

// ─── Agent Bubble Footer ──────────────────────────────────────────────────────

@Composable
private fun AgentBubbleFooter(item: ChatItem, onIntent: (AgentIntent) -> Unit) {
    val timeString = rememberSaveable {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        "${now.hour.toString().padStart(2, '0')}:${now.minute.toString().padStart(2, '0')}".toPersianDigits()
    }

    // State for like/dislike toggle
    var liked    by rememberSaveable { mutableStateOf<Boolean?>(null) }
    var copied   by rememberSaveable { mutableStateOf(false) }

    @Suppress("DEPRECATION")
                    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current

    val taminColors = LocalTaminColors.current
    val iconTint = taminColors.textMuted
    val activeTint = taminColors.blueText

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = timeString,
            style = MaterialTheme.typography.labelSmall,
            color = taminColors.textMuted,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FooterAction(
                icon = rememberVectorPainter(Icons.Outlined.ThumbUp),
                description = stringResource(Res.string.agent_like),
                tint = if (liked == true) activeTint else iconTint,
                onClick = { liked = if (liked == true) null else true },
            )
            FooterAction(
                icon = rememberVectorPainter(Icons.Outlined.ThumbDown),
                description = stringResource(Res.string.agent_dislike),
                tint = if (liked == false) taminColors.dangerText else iconTint,
                onClick = { liked = if (liked == false) null else false },
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = Spacing.xs)
                    .height(Spacing.md)
                    .width(Thickness.border)
                    .background(taminColors.divider)
            )
            FooterAction(
                icon = painterResource(Res.drawable.ic_tamin_copy),
                description = stringResource(Res.string.action_copy),
                tint = if (copied) activeTint else iconTint,
                onClick = {
                    copied = true
                    clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(extractTextFromItem(item)))
                },
            )
            FooterAction(
                icon = painterResource(Res.drawable.ic_share),
                description = stringResource(Res.string.agent_share),
                tint = iconTint,
                onClick = { onIntent(AgentIntent.ShareContent(extractTextFromItem(item))) },
            )
        }
    }
}

@Composable
private fun FooterAction(
    icon: androidx.compose.ui.graphics.painter.Painter,
    description: String,
    tint: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(FOOTER_ACTION_SIZE)
            .clip(RoundedCornerShape(CornerRadius.md))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(painter = icon, contentDescription = description, tint = tint, modifier = Modifier.size(IconSize.small))
    }
}

private fun extractTextFromItem(item: ChatItem): String {
    return when (val content = item.content) {
        is ChatBubbleContent.Text -> content.message
        is ChatBubbleContent.Markdown -> content.text
        is ChatBubbleContent.KeyValue -> {
            buildString {
                content.title?.let { appendLine(it) }
                content.items.forEach { (k, v) -> appendLine("$k: $v") }
            }
        }
        else -> ""
    }
}

private val USER_BUBBLE_MAX_WIDTH = 300.dp
private val FOOTER_ACTION_SIZE = 28.dp
private const val INPUT_MAX_LINES = 4
