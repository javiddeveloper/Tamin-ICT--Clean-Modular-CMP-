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
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.IconSize
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.blur
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
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.blur.AppBarScrim
import com.tamin.taminhamrah.ui.blur.safeHazeEffect
import com.tamin.taminhamrah.ui.blur.safeHazeSource
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.info
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.TaminCardPurpleStart
import com.tamin.taminhamrah.ui.theme.TaminCardPurpleMid
import com.tamin.taminhamrah.ui.theme.TaminCardPurpleEnd
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.coloredShadow
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.ui.text.font.FontVariation.weight
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import kotlinx.coroutines.NonCancellable.start
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
        uiState.isCheckingPermission -> PermissionCheckingIndicator()
        uiState.isNotAllowed        -> NotAllowedMessage(message = uiState.notAllowedMessage)
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

    val isEmptyState = uiState.chatItems.isEmpty() && !uiState.isGenerating

    Box(
        modifier = Modifier
            .fillMaxSize()
            // The app runs edge-to-edge (MainActivity.enableEdgeToEdge()), so the
            // manifest's windowSoftInputMode="adjustResize" is not honored by the
            // system — Compose must consume the IME inset itself, same as every other
            // screen in the app (see the imePadding() usages elsewhere under feature/*).
            // Without this the keyboard simply overlaps the bottom bar/content instead
            // of pushing them up.
            .imePadding()
    ) {
        // Full-bleed backdrop (Figma 90:14), behind the top bar too — otherwise its
        // glass blur has nothing colorful to sample and washes out to the plain page.
        // Always drawn, not just for the empty/orb state, so the message list matches it.
        AgentBackground(
            modifier = Modifier
                .fillMaxSize()
                .safeHazeSource(state = hazeState)
        )

        // ── Scrolling content: the haze source, sitting behind both bars ──
        if (isEmptyState) {
            EmptyState(
                userFirstName = uiState.userFirstName,
                onSuggestionClick = { onIntent(AgentIntent.SendTextPrompt(it)) },
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

                // Both the bare 3-dot indicator and the processing card render inline,
                // directly under the last message, instead of floating elsewhere.
                if (uiState.isGenerating) {
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

        // ── Top toolbar: blurred, overlays the content, seen-through from the top ──
        AgentTopBar(
            isGenerating = uiState.isGenerating,
            isOffline = uiState.isOffline,
            sessionsCount = uiState.sessions.size,
            hazeState = hazeState,
            onIntent = onIntent,
            onNavigateBack = onNavigateBack,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .onSizeChanged { topBarHeightPx = it.height }
        )

        // ── Bottom input: global scrim gradient behind, solid pill on top ──
        // The typing/processing indicator is rendered once inside the LazyColumn above.
        //
        // Inset handling: the outer Box already consumes the IME inset via imePadding(),
        // so this bar must NOT add its own `ime` padding — doing so would apply the
        // keyboard height twice. Only the navigation bar is padded here; while the
        // keyboard is open that inset is 0 (the IME covers it), so the bar lands
        // directly on top of the keyboard.
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
                .windowInsetsPadding(WindowInsets.navigationBars)
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
                    hazeState = hazeState,
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

// Exact values pulled from the Figma node (90:87 "Background+Border+Shadow+OverlayBlur") —
// this card has no equivalent in the shared TaminColors palette, so its glass/gradient/shadow
// colors are reproduced literally rather than approximated from existing tokens.
private val TopBarCardRadius = 22.dp
private val TopBarCardShape = RoundedCornerShape(TopBarCardRadius)
private val TopBarTileShape = RoundedCornerShape(13.dp)
private val TopBarIconTint = Color(0xFFD5E1FA)
private val TopBarSubtitleColor = Color(0xFFA9BDE6)
private val TopBarShadowColor = Color(0xFF040A1E)
private val TopBarBadgeGradient = Brush.linearGradient(listOf(Color(0xFF7C5CFF), Color(0xFF3B6FD4)))

/** One blink half-cycle for the online/offline status dot (fade out, then back in). */
private const val TOP_BAR_STATUS_DOT_BLINK_DURATION_MS = 900

private val TopBarOnlineDotColor = Color(0xFF3DDC84)

/**
 * Rich persona header replacing the old plain title bar: assistant name + online status on
 * one side, history (with a saved-conversation-count badge) and new-chat actions on the
 * other, and a back chevron at the far edge — matches the Figma "یارا" top bar card
 * (node 90:87), reproduced at 1:1 spacing/color fidelity rather than approximated.
 */
@Composable
private fun AgentTopBar(
    isGenerating: Boolean,
    isOffline: Boolean,
    sessionsCount: Int,
    hazeState: HazeState,
    onIntent: (AgentIntent) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 14.dp, vertical = 5.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(67.dp)
                // Drop shadow from the Figma node ("Background+Border+Shadow+OverlayBlur").
                // Its color was already captured as TopBarShadowColor but never drawn, so
                // the card read as flat against the backdrop. Applied before clip() so the
                // blur spills outside the rounded bounds instead of being cut off by them.
                .coloredShadow(
                    color = TopBarShadowColor.copy(alpha = 0.55f),
                    borderRadius = TopBarCardRadius,
                    blurRadius = 24.dp,
                    offsetY = 10.dp
                )
                .clip(TopBarCardShape)
                .border(1.dp, Color.White.copy(alpha = 0.18f), TopBarCardShape)
        ) {
            // The card's own subtle glass sheen — 16% white fading to 5%, diagonal.
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.linearGradient(
                            listOf(Color.White.copy(alpha = 0.16f), Color.White.copy(alpha = 0.05f))
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                // Two groups pinned to the two edges — not one flat spacedBy row. The Figma
                // frame's fixed 10dp gaps only sum to exactly the card width at its own
                // reference size; on a real device's width, spacedBy alone would just pack
                // every icon to one side and leave a stray gap at the other (the "empty
                // space next to the history icon" bug) instead of distributing it. Pinning
                // the two logical clusters to opposite edges puts any leftover width where
                // the design already shows the one flexible-looking gap: between the persona
                // block and the add button.
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Right-edge cluster (RTL start): chevron, persona block.
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Chevron (back) — rightmost under RTL, matching the Figma layout exactly.
                    TopBarGlassTile(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = "بازگشت",
                        onClick = onNavigateBack
                    )

                    // Persona block: name + "AI" pill, then the online-status row beneath it.
                    Column(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            Text(
                                text = "یارا",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFA78BFA).copy(alpha = 0.22f), RoundedCornerShape(CornerRadius.lg))
                                    .border(1.dp, Color(0xFFA78BFA).copy(alpha = 0.40f), RoundedCornerShape(CornerRadius.lg))
                                    .padding(top = 2.dp, bottom = 0.dp, start = 8.dp, end = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "AI",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFD8CFFF)
                                    )
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            val statusDotBlinkAlpha by rememberInfiniteTransition(label = "top_bar_status_dot_blink").animateFloat(
                                initialValue = 1f,
                                targetValue = 0.25f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(TOP_BAR_STATUS_DOT_BLINK_DURATION_MS, easing = LinearEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "top_bar_status_dot_blink_alpha"
                            )
                            val statusDotColor = if (isOffline) Color.White.copy(alpha = 0.4f) else TopBarOnlineDotColor
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .graphicsLayer { alpha = statusDotBlinkAlpha }
                                    .coloredShadow(
                                        color = statusDotColor.copy(alpha = 0.9f),
                                        borderRadius = 3.dp,
                                        blurRadius = 8.dp
                                    )
                                    .clip(CircleShape)
                                    .background(statusDotColor)
                            )
                            Text(
                                text = if (isGenerating) "در حال پردازش..."
                                       else if (isOffline) "دستیار هوشمند · آفلاین"
                                       else "دستیار هوشمند · آنلاین",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    color = TopBarSubtitleColor
                                )
                            )
                        }
                    }
                }

                // Left-edge cluster (RTL end): new chat, history+badge.
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TopBarGlassTile(
                        icon = Icons.Default.Add,
                        contentDescription = "گفتگوی جدید",
                        onClick = { onIntent(AgentIntent.StartNewSession) }
                    )

                    // History — leftmost under RTL, with the saved-conversation-count badge.
                    Box {
                        TopBarGlassTile(
                            icon = Icons.Outlined.History,
                            contentDescription = "گفتگوهای من",
                            onClick = { onIntent(AgentIntent.OpenChatHistory) }
                        )
                        if (sessionsCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 3.dp, y = (-3).dp)
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(TopBarBadgeGradient)
                                    .border(1.dp, Color.White.copy(alpha = 0.35f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                NumericText(
                                    text = (if (sessionsCount > 9) "9+" else sessionsCount.toString()).toPersianDigits(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
    }
}

/** One 34dp glass icon tile — the history/new-chat/back buttons on the top bar card. */
@Composable
private fun TopBarGlassTile(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(34.dp)
            .clip(TopBarTileShape)
            .background(Color.White.copy(alpha = 0.10f))
            .border(1.dp, Color.White.copy(alpha = 0.18f), TopBarTileShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = TopBarIconTint,
            modifier = Modifier.size(18.dp)
        )
    }
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
                        // Transparent card straight on AgentBackground — the theme's
                        // blueText is too dark to read there, white keeps it legible.
                        color = if (state.isCompleted) taminColors.greenText else Color.White,
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
                        .background(Color.White.copy(alpha = 0.14f))
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
                    color = Color.White
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
                        .background(Color.White.copy(alpha = 0.35f))
                )
            }
        }

        Spacer(Modifier.width(10.dp))

        // Transparent card straight on AgentBackground — white-based tones instead
        // of the theme's textPrimary/textSecondary/textMuted, which are too dark
        // to read against that fixed-dark backdrop.
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall.copy(
                color = when {
                    isDone   -> Color.White.copy(alpha = 0.7f)
                    isActive -> Color.White
                    else     -> Color.White.copy(alpha = 0.4f)
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
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        Text(
            text = stringResource(Res.string.agent_suggestions_label),
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = 0.6f),
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

// Same three-stop purple used for a dependant's insurance card (see
// InsuranceCardComponents.kt) — reused here so each dot carries one stop of that
// gradient instead of a single flat tint.
private val TypingDotColors = listOf(TaminCardPurpleStart, TaminCardPurpleMid, TaminCardPurpleEnd)

@Composable
private fun TypingDotsIndicator() {
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
        dots.forEachIndexed { index, anim ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    // Lambda-based offset: reads the animated value at layout time so
                    // each frame only re-lays-out/redraws instead of recomposing.
                    .offset { IntOffset(x = 0, y = (maxOffsetPx * anim.value).roundToInt()) }
                    .clip(CircleShape)
                    .background(TypingDotColors[index].copy(alpha = 0.5f + (anim.value * 0.5f)))
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
                    // corner nearest the screen edge is tucked in slightly to point at the sender.
                    val taminColors = LocalTaminColors.current
                    val userBubbleShape = RoundedCornerShape(
                        topStart = CornerRadius.xl,
                        topEnd = CornerRadius.xl,
                        bottomStart = CornerRadius.xl,
                        bottomEnd = CornerRadius.sm,
                    )
                    val userBubbleBorderBrush = Brush.linearGradient(
                        colors = listOf(Color.White.copy(alpha = 0.05f), Color.White.copy(alpha = 0.3f))
                    )
                    Box(
                        modifier = Modifier
                            .widthIn(max = USER_BUBBLE_MAX_WIDTH)
                            .clip(userBubbleShape)
                            .background(taminTopAppBarGradient())
                            .border(Thickness.border, userBubbleBorderBrush, userBubbleShape)
                            .padding(vertical = Spacing.smPlus, horizontal = Spacing.smd)
                    ) {
                        Column {
                            renderContent(taminColors.onGradient)
                            Text(
                                text = rememberChatTimeString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = taminColors.onGradient.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .align(Alignment.Start)
                                    .padding(top = Spacing.sm)
                            )
                        }
                    }
                }

                else -> {
                    // Agent: full width, no Surface card background, no avatar padding.
                    // Renders directly on AgentBackground (no card of its own), so its
                    // text follows that backdrop's fixed-dark palette rather than the
                    // light/dark app theme's textPrimary — same reasoning as EmptyState.
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.padding(vertical = Spacing.xs)) {
                            renderContent(Color.White)
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
                lineHeight = 22.sp,
                fontWeight = if (isUser) FontWeight.Medium else FontWeight.Normal,
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

// ─── Input Bar ──────────────────────────────────────────────────────────────

// Exact values pulled from the Figma node (90:120 "Background+Border+Shadow+OverlayBlur") —
// same glass-card family as the top bar, reproduced literally rather than approximated.
private val InputBarCardShape = RoundedCornerShape(26.dp)
private val InputBarMutedIconTint = Color(0xFFBFD0F0)
private val InputBarPlaceholderColor = Color(0xFFE2ECFF)

/**
 * Bottom composer, matching the Figma "یارا" input bar card (node 90:120) at 1:1
 * spacing/color fidelity: a 60dp glass pill (26dp radius, diagonal white sheen, blurred
 * background, dark drop shadow) holding a fixed left-pointing send button (42dp, opaque
 * white glass — not RTL-mirrored, the design always points it left), then mic and image
 * buttons (38dp, more transparent, muted blue-white icon tint), then the message field.
 */
@Composable
private fun AgentInputBar(
    isGenerating: Boolean,
    hazeState: HazeState,
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
    val toaster = LocalToaster.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 14.dp, end = 14.dp, bottom = 16.dp, top = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .coloredShadow(
                    color = TopBarShadowColor.copy(alpha = 0.55f),
                    borderRadius = TopBarCardRadius,
                    blurRadius = 24.dp,
                    offsetY = 10.dp
                )
                .clip(InputBarCardShape)
                .border(1.dp, Color.White.copy(alpha = 0.18f), InputBarCardShape)
        ) {
            // The card's own subtle glass sheen — 15% white fading to 5%, diagonal.
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.linearGradient(
                            listOf(Color.White.copy(alpha = 0.15f), Color.White.copy(alpha = 0.05f))
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 10.dp, end = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {

                // Text field
                BasicTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.weight(1f).onFocusChanged { isFocused = it.isFocused },
                    enabled = !isGenerating && isEnabled,
                    maxLines = INPUT_MAX_LINES,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.5.sp,
                        color = Color(0xFFE2ECFF),
                        textAlign = TextAlign.Right
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (text.isNotBlank() && !isGenerating) {
                                onSend(text.trim())
                                text = ""
                            }
                        }
                    ),
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(Color.White),
                    decorationBox = { innerTextField ->
                        Box(modifier = Modifier.fillMaxWidth().padding(start = 10.dp)) {
                            if (text.isEmpty()) {
                                Text(
                                    text = "پیام خود را بنویسید…",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 13.5.sp,
                                        color = InputBarPlaceholderColor.copy(alpha = 0.45f)
                                    ),
                                    modifier = Modifier.align(Alignment.CenterStart)
                                )
                            }
                            // Same anchor as the placeholder above (CenterStart = right edge
                            // under the app's global RTL) — otherwise the placeholder sits on
                            // the right but typed text jumps to anchor on the left, appearing
                            // to start from the middle of the field the moment you type.
                            Box(modifier = Modifier.align(Alignment.CenterStart)) { innerTextField() }
                        }
                    }
                )

                // Image attach — matches the Figma layout; no attach flow exists yet, so it
                // just says so rather than being a button that silently does nothing.
                InputBarGlassButton(
                    icon = Icons.Default.Image,
                    contentDescription = null,
                    size = 38.dp,
                    backgroundAlpha = 0.09f,
                    borderAlpha = 0.14f,
                    iconTint = InputBarMutedIconTint,
                    onClick = { toaster.info("این امکان به‌زودی اضافه می‌شود") }
                )

                // Mic — always available on its own, independent of the send button. Hidden
                // behind isVoiceEnabled: the server decides per user whether voice prompts
                // are allowed at all.
                if (isVoiceEnabled) {
                    InputBarGlassButton(
                        icon = Icons.Default.Mic,
                        contentDescription = "ضبط صدا",
                        size = 38.dp,
                        backgroundAlpha = 0.09f,
                        borderAlpha = 0.14f,
                        iconTint = InputBarMutedIconTint,
                        onClick = onStartVoice,
                        enabled = !isGenerating && isEnabled
                    )
                }

                // Send/Cancel — a fixed, never-mirrored left arrow (the design's own send
                // glyph, not a "back" affordance), 42dp — larger and more opaque than the
                // other two. Only reacts once there is text to send or a request to cancel;
                // otherwise it sits at the design's neutral idle glass look.
                val isTyping = text.isNotBlank()
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isGenerating -> MaterialTheme.colorScheme.error.copy(alpha = 0.18f)
                                isTyping -> taminColors.aiAssistantTint
                                else -> Color.White.copy(alpha = 0.10f)
                            }
                        )
                        .border(
                            1.dp,
                            if (isGenerating || isTyping) Color.White.copy(alpha = 0.30f) else Color.White.copy(alpha = 0.20f),
                            CircleShape
                        )
                        .clickable(enabled = isGenerating || isTyping) {
                            if (isGenerating) onCancel() else if (isTyping) { onSend(text.trim()); text = "" }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(
                        targetState = if (isGenerating) 2 else 1,
                        label = "send_cancel_anim"
                    ) { state ->
                        when (state) {
                            2 -> Icon(Icons.Default.Close, contentDescription = "توقف", tint = MaterialTheme.colorScheme.error)
                            else -> Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "ارسال",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

/** One glass icon button on the input bar — the mic/image buttons (fixed 38dp per spec). */
@Composable
private fun InputBarGlassButton(
    icon: ImageVector,
    contentDescription: String?,
    size: Dp,
    backgroundAlpha: Float,
    borderAlpha: Float,
    iconTint: Color,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = backgroundAlpha))
            .border(1.dp, Color.White.copy(alpha = borderAlpha), CircleShape)
            .then(if (onClick != null) Modifier.clickable(enabled = enabled, onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(18.dp)
        )
    }
}

// ─── Empty State ──────────────────────────────────────────────────────────────

@Composable
private fun EmptyState(
    userFirstName: String?,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = Spacing.lg, end = Spacing.lg, top = Spacing.xxxxxl, bottom = Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Figma 90:40 - the sphere carries its own 16 dp bottom margin.
            AgentOrb()
            Spacer(Modifier.height(Spacing.xxl))
            Text(
                text = if (userFirstName.isNullOrBlank()) {
                    "سلام، من یارا هستم"
                } else {
                    "سلام $userFirstName، من یارا هستم"
                },
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = "دستیار هوشمند سازمان تأمین اجتماعی. دربارهٔ سوابق بیمه، مستمری، درمان و خدمات از من بپرسید.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = Spacing.lg)
            )
            Spacer(Modifier.height(Spacing.xl))
            AgentSuggestions(onSuggestionClick = onSuggestionClick)
        }
    }
}

/** One welcome-screen suggestion: its prompt text and the colored icon badge beside it. */
private data class AgentSuggestion(
    val text: String,
    val icon: ImageVector?,
    val iconTint: Color,
    val iconBackground: Color
)

@Composable
private fun AgentSuggestions(
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    val suggestions = remember(taminColors) {
        listOf(
            AgentSuggestion(
                text = "سابقهٔ بیمهٔ من چقدر است؟",
                icon = null,
                iconTint = Color.Black,
                iconBackground = taminColors.blueBg.copy(alpha = 0.1f)
            ),
            AgentSuggestion(
                text = "مستمری این ماه چه زمانی واریز می‌شود؟",
                icon = null,
                iconTint = Color.Black,
                iconBackground = taminColors.tealBg.copy(alpha = 0.1f)
            ),
            AgentSuggestion(
                text = "شرایط دریافت هدیهٔ ازدواج چیست؟",
                icon = null,
                iconTint = Color.Black,
                iconBackground = taminColors.fuchsiaBlueBg.copy(alpha = 0.1f)
            ),
            AgentSuggestion(
                text = "آخرین نسخهٔ الکترونیک من",
                icon = null,
                iconTint = androidx.compose.ui.graphics.Color.Black,
                iconBackground = taminColors.greenBg.copy(alpha = 0.1f)
            )
        )
    }
    Column(
        modifier = modifier.fillMaxWidth()
            .padding(vertical = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        suggestions.forEach { suggestion ->
            AgentSuggestionRow(
                suggestion = suggestion,
                onClick = { onSuggestionClick(suggestion.text) }
            )
        }
    }
}

@Composable
private fun AgentSuggestionRow(
    suggestion: AgentSuggestion,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(CornerRadius.xl)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, Color.White.copy(alpha = 0.14f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.md, vertical = Spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        Text(
            text = suggestion.text,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Start
            )
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.5f),
            modifier = Modifier.size(18.dp)
        )
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
private fun rememberChatTimeString(): String = rememberSaveable {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    "${now.hour.toString().padStart(2, '0')}:${now.minute.toString().padStart(2, '0')}".toPersianDigits()
}

@Composable
private fun AgentBubbleFooter(item: ChatItem, onIntent: (AgentIntent) -> Unit) {
    val timeString = rememberChatTimeString()

    // State for like/dislike toggle
    var liked    by rememberSaveable { mutableStateOf<Boolean?>(null) }
    var copied   by rememberSaveable { mutableStateOf(false) }

    @Suppress("DEPRECATION")
                    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current

    val taminColors = LocalTaminColors.current
    // Sits directly on AgentBackground (no card), so it follows that fixed-dark
    // backdrop's white-based palette rather than the theme's textMuted/blueText.
    val iconTint = Color.White.copy(alpha = 0.5f)
    val activeTint = Color.White

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
            color = Color.White.copy(alpha = 0.6f),
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
                    .background(Color.White.copy(alpha = 0.14f))
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

@PreviewRtlTheme
@Composable
private fun AgentTopBarOnlinePreview() {
    PreviewRtlThemeContent {
        AgentTopBar(
            isGenerating = false,
            isOffline = false,
            sessionsCount = 3,
            hazeState = remember { HazeState() },
            onIntent = {},
            onNavigateBack = {}
        )
    }
}

@PreviewRtlTheme
@Composable
private fun AgentTopBarOfflinePreview() {
    PreviewRtlThemeContent {
        AgentTopBar(
            isGenerating = true,
            isOffline = true,
            sessionsCount = 0,
            hazeState = remember { HazeState() },
            onIntent = {},
            onNavigateBack = {}
        )
    }
}

private val USER_BUBBLE_MAX_WIDTH = 300.dp
private val FOOTER_ACTION_SIZE = 28.dp
private const val INPUT_MAX_LINES = 4
