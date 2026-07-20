package com.tamin.taminhamrah.feature.agent.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.graphics.Brush
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

// ─── Agent System Colors ──────────────────────────────────────────────────────
private val AgentPrimary = Color(0xFF1A73E8)
private val AgentSurface = Color(0xFF0D1117)
private val AgentSurfaceVariant = Color(0xFF161B22)
private val AgentOnSurface = Color(0xFFF0F6FF)
private val AgentBubbleUser = Color(0xFF1A73E8)
private val AgentBubbleAgent = Color(0xFF21262D)
private val AgentBubbleBorder = Color(0xFF30363D)
private val AgentAccent = Color(0xFF58A6FF)
private val AgentError = Color(0xFFFF6B6B)
private val AgentGradientStart = Color(0xFF0D1117)
private val AgentGradientEnd = Color(0xFF161B22)

@Composable
fun AgentScreen(
    viewModel: AgentViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Listen to events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AgentEvent.ScrollToBottom -> {
                    if (uiState.chatItems.isNotEmpty()) {
                        coroutineScope.launch {
                            listState.animateScrollToItem(0)
                        }
                    }
                }
                is AgentEvent.ShowError -> { /* Handled by snackbar */ }
                is AgentEvent.NavigateToDeepLink -> { /* External navigation */ }
                is AgentEvent.NavigateToWebView -> { /* External navigation */ }
            }
        }
    }

    // Initial permission check
    LaunchedEffect(Unit) {
        viewModel.sendIntent(AgentIntent.CheckPermission)
    }

    AgentContent(
        uiState = uiState,
        listState = listState,
        onIntent = { viewModel.sendIntent(it) }
    )
}

@Composable
private fun AgentContent(
    uiState: AgentUiState,
    listState: LazyListState,
    onIntent: (AgentIntent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(AgentGradientStart, AgentGradientEnd)
                )
            )
    ) {
        when {
            uiState.isCheckingPermission -> PermissionCheckingIndicator()
            uiState.isNotAllowed -> NotAllowedMessage(message = uiState.notAllowedMessage)
            else -> ChatLayout(
                uiState = uiState,
                listState = listState,
                onIntent = onIntent
            )
        }
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
    Scaffold(
        containerColor = Color.Transparent,
        topBar = { AgentTopBar(isGenerating = uiState.isGenerating, onIntent = onIntent) },
        bottomBar = {
            AgentInputBar(
                isGenerating = uiState.isGenerating,
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
            // Chat list
            if (uiState.chatItems.isEmpty() && !uiState.isGenerating) {
                EmptyState(modifier = Modifier.weight(1f))
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Bottom),
                    reverseLayout = true
                ) {
                    if (uiState.isGenerating) {
                        item {
                            TypingIndicatorBubble(processingState = uiState.processingState)
                        }
                    }
                    // reverseLayout = true means index 0 is at the BOTTOM.
                    // reversedItems[0] is the LATEST message, shown at bottom.
                    val reversedItems = uiState.chatItems.reversed()
                    itemsIndexed(reversedItems, key = { _, it -> it.id }) { index, item ->
                        // showAvatar: only for agent. Show if this is the first agent msg in a
                        // consecutive agent group (i.e. the item BEFORE it in visual order is
                        // not an agent — note: index 0 is bottom, so index-1 is visually above).
                        val showAvatar = item.sender != ChatSender.User &&
                            (index == 0 || reversedItems[index - 1].sender == ChatSender.User)

                        // Compute typing delay for SuggestedPrompts based on preceding Text bubble
                        val textLength = if (item.content is ChatBubbleContent.SuggestedPrompts) {
                            (reversedItems.getOrNull(index + 1)?.content as? ChatBubbleContent.Text)?.message?.length ?: 0
                        } else 0
                        val typingDelay = if (item.isTypingAnimating && textLength > 0) (textLength * 15L) + 200L else 1500L

                        ChatBubbleItem(
                            item = item,
                            showAvatar = showAvatar,
                            typingDelay = typingDelay,
                            onIntent = onIntent
                        )
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
    onIntent: (AgentIntent) -> Unit
) {
    Surface(
        color = AgentSurfaceVariant,
        tonalElevation = 0.dp
    ) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = AgentOnSurface
            ),
            title = {
                Column {
                    Text(
                        text = "Tamin Intelligent Assistant",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AgentOnSurface
                        )
                    )
                    AnimatedVisibility(visible = isGenerating) {
                        Text(
                            text = "Processing...",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AgentAccent
                            )
                        )
                    }
                }
            },
            navigationIcon = {
                // AI icon with pulse animation
                PulsingAgentIcon(isActive = isGenerating)
            },
            actions = {
                IconButton(onClick = { onIntent(AgentIntent.StartNewSession) }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "New Session",
                        tint = AgentOnSurface.copy(alpha = 0.7f)
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
            .background(
                Brush.radialGradient(
                    colors = listOf(AgentPrimary, AgentPrimary.copy(alpha = 0.5f))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "AI", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

// ─── Extension Card ───────────────────────────────────────────────────────────

/**
 * Extension Card — Displays AI processing steps (section 7 of agent.md)
 *
 * Each step shows a loader while executing.
 * Shows a green checkmark upon completion.
 */
@Composable
private fun ExtensionCard(
    state: AgentProcessingState,
    modifier: Modifier = Modifier
) {
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
            // Header row
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
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = if (state.isCompleted) "پردازش تمام شد" else "در حال پردازش...",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = if (state.isCompleted) Color(0xFF4CAF50) else AgentAccent,
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
                    // Show connector line only between steps that are not yet done,
                    // and not after the last step
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
        // Icon column with optional connector line
        Box(
            modifier = Modifier.width(20.dp),
            contentAlignment = Alignment.Center
        ) {
            // Vertical connector line to next step
            if (showLine) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(24.dp)
                        .offset(y = 16.dp)
                        .background(AgentBubbleBorder)
                )
            }
            // Step state icon
            androidx.compose.animation.AnimatedVisibility(
                visible = isDone,
                enter = fadeIn(tween(400)),
                exit = fadeOut(tween(400))
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Done",
                    tint = Color(0xFF4CAF50),
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
                    color = AgentAccent
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
                        .background(AgentBubbleBorder)
                )
            }
        }

        Spacer(Modifier.width(10.dp))

        // Step label - fills remaining space
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall.copy(
                color = when {
                    isDone -> AgentOnSurface.copy(alpha = 0.6f)
                    isActive -> AgentOnSurface
                    else -> AgentOnSurface.copy(alpha = 0.35f)
                }
            )
        )

        // Arrow icon for completed steps
        if (isDone) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = null,
                tint = Color(0xFF4CAF50).copy(alpha = 0.7f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// ─── Chat Bubbles ─────────────────────────────────────────────────────────────

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
            val alpha = 1f - (diff.toFloat() / petalCount) * 0.8f

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
                color = color.copy(alpha = alpha),
                start = start,
                end = end,
                strokeWidth = petalWidth,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        }
    }
}

@Composable
private fun TypingIndicatorBubble(processingState: AgentProcessingState?) {
    val currentLayoutDirection = LocalLayoutDirection.current
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            // Avatar
            Box(modifier = Modifier.size(28.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(AgentPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text("AI", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.width(8.dp))

            CompositionLocalProvider(LocalLayoutDirection provides currentLayoutDirection) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    if (processingState != null) {
                        // Show step progress card instead of dots
                        ExtensionCard(
                            state = processingState,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        // Only show dots when not in a processing step
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
                    .background(AgentOnSurface.copy(alpha = 0.4f + (anim.value * 0.6f)))
            )
        }
    }
}

@Composable
private fun TypewriterText(text: String, style: androidx.compose.ui.text.TextStyle) {
    val lines = remember(text) { text.split("\n") }
    var revealedLineIndex by rememberSaveable(text) { mutableStateOf(0) }
    var isFinished by rememberSaveable(text) { mutableStateOf(false) }

    LaunchedEffect(text) {
        if (!isFinished) {
            for (i in revealedLineIndex until lines.size) {
                revealedLineIndex = i
                val delayTime = (lines[i].length * 15L).coerceAtLeast(150L)
                delay(delayTime)
            }
            isFinished = true
        }
    }

    Column {
        lines.forEachIndexed { index, line ->
            AnimatedVisibility(
                visible = isFinished || index <= revealedLineIndex,
                enter = fadeIn(tween(500))
            ) {
                Text(text = parseMarkdownLine(line), style = style)
            }
        }
    }
}

private fun parseMarkdownLine(line: String): androidx.compose.ui.text.AnnotatedString {
    return androidx.compose.ui.text.buildAnnotatedString {
        var processedLine = line
        val isBullet = processedLine.trimStart().startsWith("- ") || processedLine.trimStart().startsWith("* ")
        if (isBullet) {
            processedLine = processedLine.replaceFirst(Regex("^\\s*[-*]\\s+"), "•  ")
        }

        var currentIndex = 0
        val boldRegex = "\\*\\*(.*?)\\*\\*".toRegex()
        val matches = boldRegex.findAll(processedLine)
        for (match in matches) {
            val range = match.range
            append(processedLine.substring(currentIndex, range.first))
            withStyle(style = androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold)) {
                append(match.groupValues[1])
            }
            currentIndex = range.last + 1
        }
        append(processedLine.substring(currentIndex))
    }
}

private fun parseMarkdownBlock(text: String): androidx.compose.ui.text.AnnotatedString {
    return androidx.compose.ui.text.buildAnnotatedString {
        val lines = text.split("\n")
        lines.forEachIndexed { index, line ->
            append(parseMarkdownLine(line))
            if (index < lines.size - 1) {
                append("\n")
            }
        }
    }
}

@Composable
private fun ChatBubbleItem(
    item: ChatItem,
    showAvatar: Boolean = true,
    typingDelay: Long = 1500L,
    onIntent: (AgentIntent) -> Unit = {}
) {
    val isUser = item.sender == ChatSender.User
    val currentLayoutDirection = LocalLayoutDirection.current

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
        ) {
            if (!isUser) {
                Box(modifier = Modifier.size(28.dp)) {
                    if (showAvatar) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(AgentPrimary),
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
                // Full-width cards with no bubble background
                CompositionLocalProvider(LocalLayoutDirection provides currentLayoutDirection) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 36.dp) // align with avatar-less content
                    ) {
                        BubbleContentRenderer(
                            content = item.content,
                            isTypingAnimating = item.isTypingAnimating,
                            typingDelay = typingDelay,
                            onIntent = onIntent
                        )
                    }
                }
            } else {
                // Standard chat bubble
                Surface(
                    shape = RoundedCornerShape(
                        topStart = if (isUser) 20.dp else 4.dp,
                        topEnd = if (isUser) 4.dp else 20.dp,
                        bottomStart = 20.dp,
                        bottomEnd = 20.dp
                    ),
                    color = if (isUser) AgentBubbleUser else AgentSurfaceVariant,
                    modifier = Modifier.widthIn(max = 300.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(
                            vertical = 10.dp,
                            horizontal = if (isUser) 14.dp else 12.dp
                        )
                    ) {
                        CompositionLocalProvider(LocalLayoutDirection provides currentLayoutDirection) {
                            BubbleContentRenderer(
                                content = item.content,
                                isTypingAnimating = item.isTypingAnimating,
                                typingDelay = typingDelay,
                                onIntent = onIntent
                            )
                        }
                    }
                }
            }

            if (isUser) {
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(AgentOnSurface.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👤", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun BubbleContentRenderer(
    content: ChatBubbleContent,
    isTypingAnimating: Boolean = false,
    typingDelay: Long = 1500L,
    onIntent: (AgentIntent) -> Unit = {}
) {
    when (content) {
        is ChatBubbleContent.Text -> {
            if (isTypingAnimating) {
                TypewriterText(
                    text = content.message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = AgentOnSurface,
                        lineHeight = 22.sp
                    )
                )
            } else {
                Text(
                    text = parseMarkdownBlock(content.message),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = AgentOnSurface,
                        lineHeight = 22.sp
                    )
                )
            }
        }

        is ChatBubbleContent.KeyValue -> {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                content.title?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = AgentAccent,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    HorizontalDivider(color = AgentBubbleBorder, thickness = 0.5.dp)
                }
                content.items.forEach { (key, value) ->
                    Text(
                        text = androidx.compose.ui.text.buildAnnotatedString {
                            withStyle(
                                style = androidx.compose.ui.text.SpanStyle(color = AgentOnSurface.copy(alpha = 0.6f))
                            ) {
                                append("$key: ")
                            }
                            withStyle(
                                style = androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Medium)
                            ) {
                                append(value)
                            }
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AgentOnSurface,
                            lineHeight = 22.sp
                        )
                    )
                }
            }
        }

        is ChatBubbleContent.DeepLink -> {
            OutlinedButton(
                onClick = { /* navigate */ },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = AgentAccent
                ),
                border = BorderStroke(1.dp, AgentAccent.copy(alpha = 0.5f))
            ) {
                Text(content.title)
            }
        }

        is ChatBubbleContent.WebLink -> {
            OutlinedButton(
                onClick = { /* open browser */ },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = AgentAccent
                ),
                border = BorderStroke(1.dp, AgentAccent.copy(alpha = 0.5f))
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
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AgentOnSurface.copy(alpha = 0.5f)
                        )
                    )
                    content.prompts.forEach { prompt ->
                        SuggestionChip(
                            onClick = { /* onSendPrompt(prompt) */ },
                            label = {
                                Text(
                                    text = prompt,
                                    style = MaterialTheme.typography.bodySmall.copy(color = AgentAccent)
                                )
                            },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = AgentPrimary.copy(alpha = 0.1f)
                            ),
                            border = SuggestionChipDefaults.suggestionChipBorder(
                                enabled = true,
                                borderColor = AgentAccent.copy(alpha = 0.3f)
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
            Text("🎵 Voice Message (${content.durationMs ?: "Unknown"} ms)", color = AgentAccent)
        }

        is ChatBubbleContent.Image -> {
            Text("🖼 Image: ${content.caption ?: "No caption"}", color = AgentAccent)
        }

        is ChatBubbleContent.Chart -> {
            Text("📊 Embedded Chart Model", color = AgentAccent)
        }

        is ChatBubbleContent.DynamicForm -> {
            Text("📝 Embedded Dynamic Form", color = AgentAccent)
        }

        is ChatBubbleContent.EmbeddedModel -> {
            Text("🧩 Embedded Custom Component", color = AgentAccent)
        }

        is ChatBubbleContent.ServiceError -> {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .run {
                        if (content.canRetryPrompt) {
                            clickable { onIntent(AgentIntent.OnRetryClick) }
                        } else if (content.actionKey != null) {
                            clickable { onIntent(AgentIntent.ExecuteServiceAction(content.actionKey, content.payload)) }
                        } else {
                            this
                        }
                    }
                    .padding(4.dp)
            ) {
                if (content.canRetryPrompt || content.actionKey != null) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Retry",
                        tint = AgentError,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("⚠️", fontSize = 16.sp)
                }
                Text(
                    text = content.message,
                    style = MaterialTheme.typography.bodySmall.copy(color = AgentError)
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
    onCancel: () -> Unit
) {
    var text by remember { mutableStateOf("") }

    Surface(
        color = AgentSurfaceVariant,
        border = BorderStroke(1.dp, AgentBubbleBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .navigationBarsPadding()
                .imePadding(),
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
                        text = "Ask your question...",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = AgentOnSurface.copy(alpha = 0.4f)
                        )
                    )
                },
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = AgentOnSurface
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AgentAccent.copy(alpha = 0.7f),
                    unfocusedBorderColor = AgentBubbleBorder,
                    cursorColor = AgentAccent,
                    focusedContainerColor = AgentSurface,
                    unfocusedContainerColor = AgentSurface
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

            // Send / Cancel button
            AnimatedContent(
                targetState = isGenerating,
                label = "send_cancel_btn",
                transitionSpec = {
                    scaleIn(tween(200)) togetherWith scaleOut(tween(200))
                }
            ) { generating ->
                if (generating) {
                    IconButton(
                        onClick = onCancel,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(AgentError.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Cancel",
                            tint = AgentError
                        )
                    }
                } else {
                    val sendEnabled = text.isNotBlank()
                    IconButton(
                        onClick = {
                            if (sendEnabled) {
                                onSend(text.trim())
                                text = ""
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if (sendEnabled) AgentPrimary else AgentBubbleBorder
                            )
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Send",
                            tint = if (sendEnabled) Color.White else AgentOnSurface.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}

// ─── Empty + Error States ─────────────────────────────────────────────────────

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🤖", fontSize = 64.sp)
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Social Security Intelligent Assistant",
            style = MaterialTheme.typography.titleLarge.copy(
                color = AgentOnSurface,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Ask your questions about insurance, history, and pensions",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = AgentOnSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(Modifier.height(24.dp))
        // Initial suggestions
        AgentSuggestions()
    }
}

@Composable
private fun AgentSuggestions() {
    val suggestions = listOf(
        "What is my insurance history?",
        "Show my monthly pension",
        "What is my last medical prescription?",
        "Early retirement laws"
    )
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        suggestions.forEach { suggestion ->
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = AgentSurfaceVariant,
                border = BorderStroke(1.dp, AgentBubbleBorder.copy(alpha = 0.5f)),
                modifier = Modifier.clickable { /* onIntent */ }
            ) {
                Text(
                    text = suggestion,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.bodySmall.copy(color = AgentAccent)
                )
            }
        }
    }
}

@Composable
private fun PermissionCheckingIndicator() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CircularProgressIndicator(color = AgentAccent)
            Text(
                text = "Checking access...",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AgentOnSurface.copy(alpha = 0.6f)
                )
            )
        }
    }
}

@Composable
private fun NotAllowedMessage(message: String?) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text("🚫", fontSize = 48.sp)
            Text(
                text = "Limited Access",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = AgentOnSurface,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = message ?: "The intelligent assistant is not enabled for you.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AgentOnSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}
