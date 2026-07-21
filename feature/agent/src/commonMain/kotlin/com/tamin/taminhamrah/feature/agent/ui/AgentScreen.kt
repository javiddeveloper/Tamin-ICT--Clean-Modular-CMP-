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
import kotlinx.coroutines.delay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.ui.contract.AgentEvent
import com.tamin.taminhamrah.feature.agent.ui.contract.AgentIntent
import com.tamin.taminhamrah.feature.agent.ui.contract.AgentProcessingState
import com.tamin.taminhamrah.feature.agent.ui.contract.AgentUiState
import com.tamin.taminhamrah.feature.agent.ui.contract.ChatItem
import com.tamin.taminhamrah.feature.agent.ui.contract.ChatSender
import com.tamin.taminhamrah.ui.blur.safeHazeEffect
import com.tamin.taminhamrah.ui.blur.safeHazeSource
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import androidx.compose.material.icons.outlined.ContentCopy
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

// ─── AgentScreen ──────────────────────────────────────────────────────────────

@Composable
fun AgentScreen(
    viewModel: AgentViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Smart auto-scroll: follows new messages only when user is at the bottom.
    // In top-to-bottom layout the "bottom" is the LAST item, not index 0.
    val isAtBottom by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            totalItems == 0 || lastVisible >= totalItems - 1
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AgentEvent.ScrollToBottom -> {
                    val lastIndex = (uiState.chatItems.size).coerceAtLeast(0)
                    if (isAtBottom || uiState.chatItems.isEmpty()) {
                        coroutineScope.launch { listState.animateScrollToItem(lastIndex) }
                    }
                }
                is AgentEvent.ShowError -> { /* handled via bubble */ }
                is AgentEvent.NavigateToDeepLink -> { /* External navigation */ }
                is AgentEvent.NavigateToWebView -> { /* External navigation */ }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.sendIntent(AgentIntent.CheckPermission)
    }

    AgentContent(
        uiState = uiState,
        listState = listState,
        onIntent = { viewModel.sendIntent(it) }
    )
}

// ─── Content Root ─────────────────────────────────────────────────────────────

@Composable
private fun AgentContent(
    uiState: AgentUiState,
    listState: LazyListState,
    onIntent: (AgentIntent) -> Unit
) {
    // No background box — content fills the screen directly inheriting the host's background
    when {
        uiState.isCheckingPermission -> PermissionCheckingIndicator()
        uiState.isNotAllowed        -> NotAllowedMessage(message = uiState.notAllowedMessage)
        else -> ChatLayout(uiState = uiState, listState = listState, onIntent = onIntent)
    }
}

// ─── Chat Layout ──────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatLayout(
    uiState: AgentUiState,
    listState: LazyListState,
    onIntent: (AgentIntent) -> Unit
) {
    val hazeState = remember { HazeState() }

    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = Color.Transparent,
        topBar = {
            AgentTopBar(
                isGenerating = uiState.isGenerating,
                hazeState = hazeState,
                onIntent = onIntent
            )
        },
        bottomBar = {
            AgentInputBar(
                isGenerating = uiState.isGenerating,
                hazeState = hazeState,
                onSend = { onIntent(AgentIntent.SendTextPrompt(it)) },
                onCancel = { onIntent(AgentIntent.CancelGeneration) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.chatItems.isEmpty() && !uiState.isGenerating) {
                EmptyState(modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .safeHazeSource(state = hazeState),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    // Top-to-bottom: no reverseLayout, newest items at bottom
                ) {
                    itemsIndexed(uiState.chatItems, key = { _, it -> it.id }) { index, item ->
                        val showAvatar = item.sender != ChatSender.User &&
                            (index == 0 || uiState.chatItems[index - 1].sender == ChatSender.User)

                        val textLength = if (item.content is ChatBubbleContent.SuggestedPrompts) {
                            (uiState.chatItems.getOrNull(index - 1)?.content as? ChatBubbleContent.Text)?.message?.length ?: 0
                        } else 0
                        val typingDelay = if (item.isTypingAnimating && textLength > 0) (textLength * 15L) + 200L else 1500L

                        ChatBubbleItem(
                            item = item,
                            showAvatar = showAvatar,
                            typingDelay = typingDelay,
                            onIntent = onIntent
                        )
                    }

                    if (uiState.isGenerating) {
                        item { TypingIndicatorBubble(processingState = uiState.processingState) }
                    }
                }
            }
        }
    }
}

// ─── Top Bar ──────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgentTopBar(
    isGenerating: Boolean,
    hazeState: HazeState,
    onIntent: (AgentIntent) -> Unit
) {
    val taminColors = LocalTaminColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .safeHazeEffect(
                state = hazeState,
                style = HazeStyle(
                    blurRadius = 28.dp,
                    noiseFactor = 0.03f,
                    tint = HazeTint(
                        color = Color(0xFF3B1E86).copy(alpha = 0.55f)
                    )
                ),
                fallbackColor = Color(0xFF3B1E86).copy(alpha = 0.9f)
            )
    ) {
        // Gradient overlay on top of the blur
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(taminColors.aiAssistantGradient.let { brush ->
                    // Apply at reduced opacity so blur shows through
                    androidx.compose.ui.graphics.Brush.linearGradient(
                        colorStops = arrayOf(
                            0f to Color(0xFF3B1E86).copy(alpha = 0.60f),
                            0.5f to Color(0xFF3F5BD9).copy(alpha = 0.50f),
                            1f to Color(0xFF1F4FA3).copy(alpha = 0.55f)
                        )
                    )
                })
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
    val infiniteTransition = rememberInfiniteTransition(label = "agent_pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = if (isActive) 0.5f else 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )
    Box(
        modifier = Modifier
            .padding(start = 12.dp)
            .size(36.dp)
            .alpha(if (isActive) alpha else 1f)
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
                        showLine = showLine
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
    showLine: Boolean = false
) {
    val taminColors = LocalTaminColors.current
    val infiniteTransition = rememberInfiniteTransition(label = "step_shimmer")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )

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
            Box(modifier = Modifier.size(28.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(taminColors.aiAssistantGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text("AI", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(8.dp))
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
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
        dots.forEach { anim ->
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .offset(y = (-4).dp * anim.value)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f + (anim.value * 0.6f)))
            )
        }
    }
}

// ─── Typewriter & Markdown ────────────────────────────────────────────────────

@Composable
private fun TypewriterText(text: String, style: androidx.compose.ui.text.TextStyle) {
    val lines = remember(text) { text.split("\n") }
    var revealedLineIndex by rememberSaveable(text) { mutableStateOf(0) }
    var isFinished by rememberSaveable(text) { mutableStateOf(false) }

    LaunchedEffect(text) {
        if (!isFinished) {
            for (i in revealedLineIndex until lines.size) {
                revealedLineIndex = i
                delay(30L) // Fast typing stagger
            }
            isFinished = true
        }
    }

    Column {
        lines.forEachIndexed { index, line ->
            val isVisible = isFinished || index <= revealedLineIndex
            val alpha by animateFloatAsState(
                targetValue = if (isVisible) 1f else 0f,
                animationSpec = tween(durationMillis = 800) // Thanos fade effect
            )

            // Render always but fade alpha (prevents layout lag and jumpy scroll)
            Text(
                text = parseMarkdownLine(line), 
                style = style,
                modifier = Modifier.alpha(alpha)
            )
        }
    }
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
    showAvatar: Boolean = true,
    typingDelay: Long = 1500L,
    onIntent: (AgentIntent) -> Unit = {}
) {
    val isUser = item.sender == ChatSender.User
    val currentLayoutDirection = LocalLayoutDirection.current
    val taminColors = LocalTaminColors.current

    val bubbleColor   = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor  = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
        ) {
            // Agent avatar
            if (!isUser) {
                Box(modifier = Modifier.size(28.dp)) {
                    if (showAvatar) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(taminColors.aiAssistantGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("AI", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(Modifier.width(8.dp))
            }

            val isProcessingOrEmbedded = item.content is ChatBubbleContent.ProcessingSteps ||
                                         item.content is ChatBubbleContent.EmbeddedModel

            if (isProcessingOrEmbedded) {
                CompositionLocalProvider(LocalLayoutDirection provides currentLayoutDirection) {
                    Box(modifier = Modifier.fillMaxWidth().padding(start = 36.dp)) {
                        BubbleContentRenderer(
                            content = item.content,
                            isTypingAnimating = item.isTypingAnimating,
                            typingDelay = typingDelay,
                            onIntent = onIntent,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            } else {
                if (isUser) {
                    Column(modifier = Modifier.widthIn(max = 300.dp)) {
                        Surface(
                            shape = RoundedCornerShape(
                                topStart    = 20.dp,
                                topEnd      = 4.dp,
                                bottomStart = 20.dp,
                                bottomEnd   = 20.dp
                            ),
                            color = bubbleColor,
                        ) {
                            Box(modifier = Modifier.padding(vertical = 10.dp, horizontal = 14.dp)) {
                                CompositionLocalProvider(LocalLayoutDirection provides currentLayoutDirection) {
                                    BubbleContentRenderer(
                                        content = item.content,
                                        isTypingAnimating = item.isTypingAnimating,
                                        typingDelay = typingDelay,
                                        onIntent = onIntent,
                                        contentColor = contentColor
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Agent: Full width, no Surface card background
                    Column(modifier = Modifier.fillMaxWidth().padding(end = 16.dp)) {
                        Box(modifier = Modifier.padding(vertical = 4.dp)) {
                            CompositionLocalProvider(LocalLayoutDirection provides currentLayoutDirection) {
                                BubbleContentRenderer(
                                    content = item.content,
                                    isTypingAnimating = item.isTypingAnimating,
                                    typingDelay = typingDelay,
                                    onIntent = onIntent,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        // Footer: only for agent bubbles, not SuggestedPrompts
                        if (item.content !is ChatBubbleContent.SuggestedPrompts) {
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
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val taminColors = LocalTaminColors.current

    when (content) {
        is ChatBubbleContent.Text -> {
            val textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = contentColor,
                lineHeight = 22.sp
            )
            if (isTypingAnimating) {
                TypewriterText(text = content.message, style = textStyle)
            } else {
                Text(text = parseMarkdownBlock(content.message), style = textStyle)
            }
        }

        is ChatBubbleContent.KeyValue -> {
            Column(
                modifier = Modifier.animateContentSize(animationSpec = tween(durationMillis = 300)),
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

                        val alpha by animateFloatAsState(
                            targetValue = if (visible) 1f else 0f,
                            animationSpec = tween(durationMillis = 800) // Thanos fade
                        )

                        // Wrap with box to apply alpha fade without AnimatedVisibility
                        Box(modifier = Modifier.alpha(alpha)) {
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

        is ChatBubbleContent.DeepLink -> {
            OutlinedButton(
                onClick = { /* navigate */ },
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
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(tween(500))
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "پیشنهادات:",
                        style = MaterialTheme.typography.labelSmall.copy(color = taminColors.textMuted)
                    )
                    content.prompts.forEach { prompt ->
                        SuggestionChip(
                            onClick = { /* onSendPrompt(prompt) */ },
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
        }

        is ChatBubbleContent.ProcessingSteps -> {
            ExtensionCard(
                state = AgentProcessingState(content.steps, content.currentActiveIndex, content.isCompleted),
                modifier = Modifier.fillMaxWidth()
            )
        }

        is ChatBubbleContent.Voice -> {
            Text("🎵 پیام صوتی", color = MaterialTheme.colorScheme.primary)
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
    hazeState: HazeState,
    onSend: (String) -> Unit,
    onCancel: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    val taminColors = LocalTaminColors.current
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()

    Surface(
        color = Color.Transparent,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .safeHazeEffect(
                    state = hazeState,
                    style = HazeStyle(
                        blurRadius = 24.dp,
                        noiseFactor = 0.02f,
                        tint = HazeTint(
                            color = if (isDark)
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)
                            else
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                        )
                    ),
                    fallbackColor = MaterialTheme.colorScheme.surface
                )
        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                enabled = !isGenerating,
                placeholder = {
                    Text(
                        text = "سوال خود را بپرسید...",
                        style = MaterialTheme.typography.bodyMedium.copy(color = taminColors.textMuted)
                    )
                },
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = taminColors.textPrimary),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    cursorColor          = MaterialTheme.colorScheme.primary,
                    focusedContainerColor   = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                ),
                shape = RoundedCornerShape(20.dp),
                maxLines = 4,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (text.isNotBlank() && !isGenerating) {
                            onSend(text.trim())
                            text = ""
                        }
                    }
                )
            )

            AnimatedContent(
                targetState = isGenerating,
                label = "send_cancel_btn",
                transitionSpec = { scaleIn(tween(200)) togetherWith scaleOut(tween(200)) }
            ) { generating ->
                if (generating) {
                    IconButton(
                        onClick = onCancel,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.12f))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "لغو", tint = MaterialTheme.colorScheme.error)
                    }
                } else {
                    val sendEnabled = text.isNotBlank()
                    IconButton(
                        onClick = {
                            if (sendEnabled) { onSend(text.trim()); text = "" }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if (sendEnabled) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                            )
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "ارسال",
                            tint = if (sendEnabled) Color.White
                                   else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
        } // end haze Box
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
