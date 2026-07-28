package com.tamin.taminhamrah.feature.agent.ui

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
import com.tamin.taminhamrah.feature.agent.audio.rememberMicPermission
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
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
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

/**
 * Navigation hand-off for [ChatBubbleContent.DeepLink] bubbles. The host supplies a
 * lambda that maps an `AgentDestination` id to a real route; the default is a no-op
 * so previews and tests render without navigation.
 */
val LocalAgentNavigator = staticCompositionLocalOf<(String) -> Unit> { {} }

// ─── AgentScreen ──────────────────────────────────────────────────────────────

@Composable
fun AgentScreen(
    viewModel: AgentViewModel = koinViewModel(),
    onNavigateToDestination: (String) -> Unit = {}
) {
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
                is AgentEvent.NavigateToDeepLink -> onNavigateToDestination(event.destination)
                is AgentEvent.NavigateToWebView -> { /* External navigation */ }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.sendIntent(AgentIntent.CheckPermission)
    }

    // Provided via a CompositionLocal rather than threaded through five layers of
    // composables, since only the leaf DeepLink bubble consumes it.
    CompositionLocalProvider(LocalAgentNavigator provides onNavigateToDestination) {
        AgentContent(
            uiState = uiState,
            listState = listState,
            onIntent = { viewModel.sendIntent(it) },
            onRequestScroll = requestScrollToBottom
        )
    }
}

@Composable
private fun AgentContent(
    uiState: AgentUiState,
    listState: LazyListState,
    onIntent: (AgentIntent) -> Unit,
    onRequestScroll: () -> Unit
) {
    when {
        uiState.isCheckingPermission -> PermissionCheckingIndicator()
        uiState.isNotAllowed        -> NotAllowedMessage(message = uiState.notAllowedMessage)
        else -> ChatLayout(uiState = uiState, listState = listState, onIntent = onIntent, onRequestScroll = onRequestScroll)
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
    onRequestScroll: () -> Unit
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

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // ── Scrolling content: the haze source, sitting behind both bars ──
        if (uiState.chatItems.isEmpty() && !uiState.isGenerating) {
            EmptyState(
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
            hazeState = hazeState,
            onIntent = onIntent,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .onSizeChanged { topBarHeightPx = it.height }
        )

        // ── Bottom input: global scrim gradient behind, solid pill on top ──
        // The typing/processing indicator is rendered once inside the LazyColumn above.
        //
        // Inset handling: the window is resized above the IME by the system, so this bar must
        // NOT add any `ime` padding itself — doing so applies the keyboard height twice and
        // pushes the bar a full keyboard above the keyboard. Only the navigation bar is
        // padded here; while the keyboard is open that inset is 0 (the IME covers it), so the
        // bar lands directly on top of the keyboard.
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
                    isEnabled = !uiState.isOffline,
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

@Composable
private fun AgentTopBar(
    isGenerating: Boolean,
    hazeState: HazeState,
    onIntent: (AgentIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    // Single tint color derived from the theme's AI-assistant gradient family (its first stop).
    val topBarTint = taminColors.aiAssistantTint
    Box(
        modifier = modifier
            .fillMaxWidth()
            .safeHazeEffect(
                state = hazeState,
                style = HazeStyle(
                    blurRadius = 28.dp,
                    noiseFactor = 0.03f,
                    tint = HazeTint(
                        color = topBarTint.copy(alpha = 0.55f)
                    )
                ),
                fallbackColor = topBarTint.copy(alpha = 0.9f)
            )
    ) {
        // Gradient overlay on top of the blur — reuse the theme token so it stays
        // consistent across light/dark. Alpha lets the blur show through.
        Box(
            modifier = Modifier
                .matchParentSize()
                .alpha(0.55f)
                .background(taminColors.aiAssistantGradient)
        )
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = Color.White,
                actionIconContentColor = Color.White,
                navigationIconContentColor = Color.White
            ),
            title = {
                Column {
                    Text(
                        text = "دستیار هوشمند تأمین",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    AnimatedVisibility(visible = isGenerating) {
                        Text(
                            text = "در حال پردازش...",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        )
                    }
                }
            },
            navigationIcon = {
                PulsingAgentIcon(isActive = isGenerating)
            },
            actions = {
                IconButton(onClick = { onIntent(AgentIntent.OpenChatHistory) }) {
                    Icon(
                        Icons.Outlined.History,
                        contentDescription = "گفتگوهای من",
                        tint = Color.White.copy(alpha = 0.9f)
                    )
                }
                IconButton(onClick = { onIntent(AgentIntent.StartNewSession) }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "گفتگوی جدید",
                        tint = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        )
    }
}

@Composable
private fun PulsingAgentIcon(isActive: Boolean) {
    // Only run the infinite clock while active — avoids a permanently-running
    // animation (and its recompositions) when the agent is idle.
    val alpha = if (isActive) {
        val infiniteTransition = rememberInfiniteTransition(label = "agent_pulse")
        val animated by infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_alpha"
        )
        animated
    } else 1f
    Box(
        modifier = Modifier
            .padding(start = 12.dp)
            .size(36.dp)
            .alpha(alpha)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.18f)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "AI", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                    text = if (state.isCompleted) "پردازش تمام شد" else "در حال پردازش...",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = if (state.isCompleted) taminColors.greenText
                                else MaterialTheme.colorScheme.primary,
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
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                )
            }
            androidx.compose.animation.AnimatedVisibility(
                visible = isDone,
                enter = fadeIn(tween(400)),
                exit = fadeOut(tween(400))
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "انجام شد",
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
                    color = MaterialTheme.colorScheme.primary
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
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
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
 * Rendered inside the reply bubble it belongs to, so an answer and its suggestions stay
 * a single chat item rather than two rows with two timestamps.
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
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "پیشنهادات:",
            style = MaterialTheme.typography.labelSmall.copy(color = taminColors.textMuted)
        )
        prompts.forEach { prompt ->
            SuggestionChip(
                onClick = { onPromptClick(prompt) },
                label = {
                    Text(
                        text = prompt,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                border = SuggestionChipDefaults.suggestionChipBorder(
                    enabled = true,
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
            )
        }
    }
}

/**
 * Shown when the assistant service is unreachable. The cached conversation stays visible
 * behind it — only sending a new prompt is blocked.
 */
@Composable
private fun OfflineBanner(modifier: Modifier = Modifier) {
    val taminColors = LocalTaminColors.current
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "اتصال به دستیار برقرار نشد. گفتگوی قبلی شما نمایش داده می‌شود.",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
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
                elevation = 6.dp,
                shape = CircleShape,
                ambientColor = Color.Black.copy(alpha = 0.10f),
                spotColor = Color.Black.copy(alpha = 0.10f)
            )
            .clip(CircleShape)
            .background(taminColors.bgSurface)
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                shape = CircleShape
            )
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        TypingDotsIndicator()
    }
}

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
        dots.forEach { anim ->
            Box(
                modifier = Modifier
                    .size(6.dp)
                    // Lambda-based offset: reads the animated value at layout time so
                    // each frame only re-lays-out/redraws instead of recomposing.
                    .offset { IntOffset(x = 0, y = (maxOffsetPx * anim.value).roundToInt()) }
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f + (anim.value * 0.6f)))
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

private fun parseMarkdownLine(line: String): androidx.compose.ui.text.AnnotatedString {
    return androidx.compose.ui.text.buildAnnotatedString {
        var processedLine = line
        if (processedLine.trimStart().startsWith("- ") || processedLine.trimStart().startsWith("* ")) {
            processedLine = processedLine.replaceFirst(Regex("^\\s*[-*]\\s+"), "•  ")
        }
        var currentIndex = 0
        val boldRegex = "\\*\\*(.*?)\\*\\*".toRegex()
        for (match in boldRegex.findAll(processedLine)) {
            append(processedLine.substring(currentIndex, match.range.first))
            withStyle(style = androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold)) {
                append(match.groupValues[1])
            }
            currentIndex = match.range.last + 1
        }
        append(processedLine.substring(currentIndex))
    }
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
        CompositionLocalProvider(LocalLayoutDirection provides currentLayoutDirection) {
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
            val isProcessingOrEmbedded = item.content is ChatBubbleContent.ProcessingSteps ||
                                         item.content is ChatBubbleContent.EmbeddedModel

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
                    Column(modifier = Modifier.widthIn(max = 300.dp)) {
                        Surface(
                            shape = RoundedCornerShape(
                                topStart    = 20.dp,
                                topEnd      = 4.dp,
                                bottomStart = 20.dp,
                                bottomEnd   = 20.dp
                            ),
                            color = MaterialTheme.colorScheme.primary,
                        ) {
                            Box(modifier = Modifier.padding(vertical = 10.dp, horizontal = 14.dp)) {
                                renderContent(MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }
                }

                else -> {
                    // Agent: full width, no Surface card background, no avatar padding
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.padding(vertical = 4.dp)) {
                            renderContent(MaterialTheme.colorScheme.onSurface)
                        }
                        // Follow-up suggestions belong to this reply, so they render inside
                        // the same bubble instead of forming their own chat row.
                        if (item.suggestedPrompts.isNotEmpty() && isAnimationFinished) {
                            SuggestedPromptChips(
                                prompts = item.suggestedPrompts,
                                onPromptClick = { onIntent(AgentIntent.SendTextPrompt(it)) },
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        // Footer: only for agent bubbles, not SuggestedPrompts
                        if (item.content !is ChatBubbleContent.SuggestedPrompts && isAnimationFinished) {
                            AgentBubbleFooter()
                        }
                    }
                }
            }

            // User avatar
            if (isUser) {
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👤", fontSize = 14.sp)
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
                filePath = content.path,
                durationMs = content.durationMs ?: 0L,
                isPlaying = isVoicePlaying,
                positionMs = voicePositionMs,
                amplitudes = content.amplitudes,
                isUser = isUser,
                onToggle = { onIntent(AgentIntent.ToggleVoicePlayback(itemId, content.path)) },
                onSeek = { onIntent(AgentIntent.SeekVoicePlayback(itemId, it)) }
            )
        }

        is ChatBubbleContent.Text -> {
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
                                                append("$key: ")
                                            }
                                            withStyle(style = androidx.compose.ui.text.SpanStyle(
                                                fontWeight = FontWeight.Medium,
                                                color = contentColor
                                            )) {
                                                append(value)
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
            val navigate = LocalAgentNavigator.current
            OutlinedButton(
                onClick = { navigate(content.destination) },
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
            Text("🖼 تصویر: ${content.caption ?: "بدون توضیح"}", color = MaterialTheme.colorScheme.primary)
        }

        is ChatBubbleContent.Chart -> {
            Text("📊 نمودار آماری", color = MaterialTheme.colorScheme.primary)
        }

        is ChatBubbleContent.DynamicForm -> {
            Text("📝 فرم پویا", color = MaterialTheme.colorScheme.primary)
        }

        is ChatBubbleContent.EmbeddedModel -> {
            Text("🧩 کامپوننت سفارشی", color = MaterialTheme.colorScheme.primary)
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
    isEnabled: Boolean = true
) {
    var text by remember { mutableStateOf("") }
    val taminColors = LocalTaminColors.current
    val pillShape = RoundedCornerShape(32.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                // Solid, opaque pill (no blur) so the field reads clearly in light theme.
                // Soft shadow gives it lift over the scrim; a defined border shapes it.
                .shadow(
                    elevation = 8.dp,
                    shape = pillShape,
                    ambientColor = Color.Black.copy(alpha = 0.10f),
                    spotColor = Color.Black.copy(alpha = 0.10f)
                )
                .clip(pillShape)
                .background(taminColors.bgSurface)
                .border(
                    BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    shape = pillShape
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {


                // Text field
                BasicTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.weight(1f).padding(vertical = 12.dp),
                    enabled = !isGenerating && isEnabled,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = taminColors.textPrimary),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (text.isNotBlank() && !isGenerating) {
                                onSend(text.trim())
                                text = ""
                            }
                        }
                    ),
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { innerTextField ->
                        if (text.isEmpty()) {
                            Text(
                                text = "هر چیزی بپرسید...",
                                style = MaterialTheme.typography.bodyMedium.copy(color = taminColors.textMuted)
                            )
                        }
                        innerTextField()
                    }
                )

                // Send/Mic button
                val isTyping = text.isNotBlank()
                IconButton(
                    onClick = {
                        if (isGenerating) {
                            onCancel()
                        } else if (isTyping) {
                            onSend(text.trim())
                            text = ""
                        } else {
                            onStartVoice()
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (isGenerating) MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                            else if (isTyping) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                        )
                ) {
                    AnimatedContent(
                        targetState = when {
                            isGenerating -> 2
                            isTyping -> 1
                            else -> 0
                        },
                        label = "send_mic_anim"
                    ) { state ->
                        when (state) {
                            2 -> Icon(Icons.Default.Close, contentDescription = "توقف", tint = MaterialTheme.colorScheme.error)
                            1 -> Icon(Icons.Default.ArrowUpward, contentDescription = "ارسال", tint = Color.White)
                            0 -> Icon(Icons.Default.Mic, contentDescription = "ضبط صدا", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }
    }
}

// ─── Empty State ──────────────────────────────────────────────────────────────

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    val taminColors = LocalTaminColors.current
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(taminColors.aiAssistantGradient),
            contentAlignment = Alignment.Center
        ) {
            Text("AI", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = "دستیار هوشمند تأمین",
            style = MaterialTheme.typography.titleLarge.copy(
                color = taminColors.textPrimary,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "سوالات خود درباره بیمه، سوابق و حقوق بازنشستگی را بپرسید",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = taminColors.textSecondary,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(Modifier.height(28.dp))
        AgentSuggestions()
    }
}

@Composable
private fun AgentSuggestions() {
    val suggestions = listOf(
        "تاریخچه بیمه‌ام را نشان بده",
        "حقوق بازنشستگی ماهانه‌ام",
        "آخرین نسخه پزشکی من",
        "قوانین بازنشستگی پیش از موعد"
    )
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        suggestions.forEach { suggestion ->
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                modifier = Modifier.clickable { /* onIntent */ }
            ) {
                Text(
                    text = suggestion,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}

// ─── Permission & Not Allowed ─────────────────────────────────────────────────

@Composable
private fun PermissionCheckingIndicator() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Text(
                text = "در حال بررسی دسترسی...",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text("🚫", fontSize = 48.sp)
            Text(
                text = "دسترسی محدود",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = taminColors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = message ?: "دستیار هوشمند برای شما فعال نیست.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = taminColors.textSecondary,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

// ─── Agent Bubble Footer ──────────────────────────────────────────────────────

@Composable
private fun AgentBubbleFooter() {
    val timeString = rememberSaveable {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        "${now.hour.toString().padStart(2, '0')}:${now.minute.toString().padStart(2, '0')}"
    }

    // State for like/dislike toggle
    var liked    by rememberSaveable { mutableStateOf<Boolean?>(null) }
    var copied   by rememberSaveable { mutableStateOf(false) }

    val iconTint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
    val activeTint = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, start = 2.dp, end = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Timestamp
        Text(
            text = timeString,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
        )

        // Action icons
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { liked = if (liked == true) null else true },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ThumbUp,
                    contentDescription = "پسندیدن",
                    tint = if (liked == true) activeTint else iconTint,
                    modifier = Modifier.size(14.dp)
                )
            }

            IconButton(
                onClick = { liked = if (liked == false) null else false },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ThumbDown,
                    contentDescription = "نپسندیدن",
                    tint = if (liked == false) MaterialTheme.colorScheme.error.copy(alpha = 0.8f) else iconTint,
                    modifier = Modifier.size(14.dp)
                )
            }

            // Divider
            Box(
                modifier = Modifier
                    .height(12.dp)
                    .width(1.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            )

            IconButton(
                onClick = { copied = true },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ContentCopy,
                    contentDescription = "کپی",
                    tint = if (copied) activeTint else iconTint,
                    modifier = Modifier.size(14.dp)
                )
            }

            IconButton(
                onClick = { },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Share,
                    contentDescription = "اشتراک‌گذاری",
                    tint = iconTint,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

















