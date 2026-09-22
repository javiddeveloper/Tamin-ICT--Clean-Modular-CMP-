package com.tamin.taminhamrah.feature.agent.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.tamin.taminhamrah.model.agent.AgentSessionDN
import com.tamin.taminhamrah.ui.components.BackHandler
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.agent_history
import taminx.core.core_ui.agent_new_chat
import kotlin.math.roundToInt

// ─── Chat history drawer ──────────────────────────────────────────────────────
//
// Saved conversations, the equivalent of old_Android's `HistoryCategoryBottomSheet`, but
// laid out as a side drawer that slides in over the chat from the *physical right* edge.
// The app is hardcoded RTL, so `Alignment.End`/`CenterEnd` would be the *left* edge — every
// edge-dependent value below is deliberately absolute. The panel is drawn with the same
// frosted glass as the top and input bars, blurring whatever part of the chat it covers.
//
// The drawer is composed permanently and driven by [visible] — not `if (visible) { … }` —
// so the close animation actually plays. Once the exit transition ends AnimatedVisibility
// composes nothing, so a hidden drawer intercepts no touches.

/** Panel width as a fraction of the screen, capped so it stays a "panel" on tablets. */
private const val DRAWER_WIDTH_FRACTION = 0.84f
private val DrawerMaxWidth = 360.dp

private val DrawerCornerRadius = 26.dp

/** Only the open (left) edge is rounded — the right edge is flush with the screen. */
private val DrawerShape = AbsoluteRoundedCornerShape(
    topLeft = DrawerCornerRadius,
    bottomLeft = DrawerCornerRadius,
    topRight = 0.dp,
    bottomRight = 0.dp
)
private val DrawerTileShape = RoundedCornerShape(13.dp)
private val DrawerRowShape = RoundedCornerShape(16.dp)
private val DialogShape = RoundedCornerShape(22.dp)

private val DrawerScrimColor = Color.Black.copy(alpha = 0.55f)

/** Same purple → blue as the history badge on the top bar. */
private val DrawerAccentGradient = Brush.linearGradient(listOf(Color(0xFF7C5CFF), Color(0xFF3B6FD4)))
private val DrawerAccentSoft = Color(0xFFA78BFA)

/** Fill of the open conversation's row — the accent gradient, faded so the text stays legible. */
private val ActiveRowGradient = Brush.linearGradient(
    listOf(Color(0xFF7C5CFF).copy(alpha = 0.34f), Color(0xFF3B6FD4).copy(alpha = 0.26f))
)

private val NewChatButtonRadius = 25.dp
private val NewChatButtonShape = RoundedCornerShape(NewChatButtonRadius)

private const val DRAWER_ANIM_MS = 320
private const val SCRIM_ANIM_MS = 260
private const val DRAG_SETTLE_ANIM_MS = 220

/** Fraction of the panel width the user must drag it out before a release closes it. */
private const val DRAG_DISMISS_FRACTION = 0.35f

/**
 * Right-edge drawer listing saved conversations. Each row can be opened, renamed or deleted;
 * a gradient button pinned to the bottom starts a new chat. Dismissed by the scrim, the close tile, the system back
 * gesture, or swiping the panel back out to the right.
 *
 * Must be the last child of a full-screen `Box` that also contains the Haze *sources* the
 * blur samples (the agent backdrop and chat list), otherwise the glass has nothing to blur.
 */
@Composable
internal fun ChatHistoryDrawer(
    visible: Boolean,
    sessions: List<AgentSessionDN>,
    activeSessionId: String?,
    hazeState: HazeState,
    onDismiss: () -> Unit,
    onOpenSession: (String) -> Unit,
    onDeleteSession: (String) -> Unit,
    onRenameSession: (String, String) -> Unit,
    onStartNewChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    var renaming by remember { mutableStateOf<AgentSessionDN?>(null) }
    var pendingDelete by remember { mutableStateOf<AgentSessionDN?>(null) }

    // A dialog on top of the drawer is its own window and takes the back press itself, so
    // this only fires while the drawer alone is showing.
    BackHandler(enabled = visible, onBack = onDismiss)

    // Any in-flight rename/delete prompt belongs to the drawer session that opened it.
    LaunchedEffect(visible) {
        if (!visible) {
            renaming = null
            pendingDelete = null
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        // Resolved here, not with fillMaxWidth(fraction).widthIn(max): in that order the
        // fraction's min constraint wins and the cap is silently ignored on wide screens.
        val panelWidth = (maxWidth * DRAWER_WIDTH_FRACTION).coerceAtMost(DrawerMaxWidth)

        // ── Scrim: dims the chat and closes the drawer on tap ──
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(SCRIM_ANIM_MS)),
            exit = fadeOut(tween(SCRIM_ANIM_MS))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DrawerScrimColor)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss
                    )
            )
        }

        // ── Panel: slides in from the physical right. slideIn/OutHorizontally take raw
        // pixel offsets and are not mirrored for RTL, so `+width` is always "off the right
        // edge". ──
        AnimatedVisibility(
            visible = visible,
            modifier = Modifier.align(AbsoluteAlignment.CenterRight),
            enter = slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(DRAWER_ANIM_MS, easing = FastOutSlowInEasing)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(DRAWER_ANIM_MS, easing = FastOutSlowInEasing)
            )
        ) {
            DrawerPanel(
                width = panelWidth,
                sessions = sessions,
                activeSessionId = activeSessionId,
                hazeState = hazeState,
                onDismiss = onDismiss,
                onOpenSession = onOpenSession,
                onRename = { renaming = it },
                onDelete = { pendingDelete = it },
                onStartNewChat = onStartNewChat
            )
        }
    }

    renaming?.let { session ->
        RenameSessionDialog(
            initialTitle = session.title,
            onDismiss = { renaming = null },
            onConfirm = { newTitle ->
                onRenameSession(session.id, newTitle)
                renaming = null
            }
        )
    }

    pendingDelete?.let { session ->
        DeleteSessionDialog(
            title = session.title,
            onDismiss = { pendingDelete = null },
            onConfirm = {
                onDeleteSession(session.id)
                pendingDelete = null
            }
        )
    }
}

// ─── Panel ────────────────────────────────────────────────────────────────────

@Composable
private fun DrawerPanel(
    width: Dp,
    sessions: List<AgentSessionDN>,
    activeSessionId: String?,
    hazeState: HazeState,
    onDismiss: () -> Unit,
    onOpenSession: (String) -> Unit,
    onRename: (AgentSessionDN) -> Unit,
    onDelete: (AgentSessionDN) -> Unit,
    onStartNewChat: () -> Unit
) {
    val scope = rememberCoroutineScope()
    // Swipe-to-close: the panel follows the finger to the right (never to the left — it is
    // already flush with the edge) and on release either snaps back or hands off to
    // onDismiss. The exit slide then starts from wherever the finger left it.
    val dragOffsetPx = remember { Animatable(0f) }
    var panelWidthPx by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(width)
            .onSizeChanged { panelWidthPx = it.width }
            .offset { IntOffset(dragOffsetPx.value.roundToInt(), 0) }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        val threshold = panelWidthPx * DRAG_DISMISS_FRACTION
                        if (panelWidthPx > 0 && dragOffsetPx.value > threshold) {
                            onDismiss()
                        } else {
                            scope.launch { dragOffsetPx.animateTo(0f, tween(DRAG_SETTLE_ANIM_MS)) }
                        }
                    },
                    onDragCancel = {
                        scope.launch { dragOffsetPx.animateTo(0f, tween(DRAG_SETTLE_ANIM_MS)) }
                    },
                    onHorizontalDrag = { change, delta ->
                        // Raw pointer delta: positive is rightwards regardless of layout direction.
                        val next = (dragOffsetPx.value + delta).coerceAtLeast(0f)
                        scope.launch { dragOffsetPx.snapTo(next) }
                        change.consume()
                    }
                )
            }
            // Shadow spills leftwards, off the open edge, over the dimmed chat.
            .coloredShadow(
                color = AgentGlass.shadowColor.copy(alpha = 0.6f),
                borderRadius = DrawerCornerRadius,
                blurRadius = 28.dp,
                offsetX = (-6).dp
            )
            // Same frosted glass as the top/input bars — see AgentGlass.
            .agentFrostedGlassCard(DrawerShape, hazeState)
            // Swallow taps on the panel body so they do not fall through to the scrim's
            // dismiss. A gesture detector rather than clickable {} so no bogus "click"
            // action is announced to accessibility services.
            .pointerInput(Unit) { detectTapGestures { } }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 14.dp)
        ) {
            Spacer(Modifier.height(12.dp))
            DrawerHeader(sessionsCount = sessions.size, onClose = onDismiss)
            Spacer(Modifier.height(16.dp))

            // The list (or the empty note) takes whatever height is left, so the
            // new-chat button below stays pinned to the bottom edge.
            if (sessions.isEmpty()) {
                EmptyHistoryMessage(modifier = Modifier.weight(1f))
            } else {
                Text(
                    text = "گفتگوهای اخیر",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AgentGlass.textSecondary
                    ),
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(sessions, key = { it.id }) { session ->
                        SessionRow(
                            session = session,
                            isActive = session.id == activeSessionId,
                            onOpen = { onOpenSession(session.id) },
                            onRename = { onRename(session) },
                            onDelete = { onDelete(session) }
                        )
                    }
                }
            }

            // ── Pinned footer: hairline + the new-chat button ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(AgentGlass.borderColor)
            )
            Spacer(Modifier.height(14.dp))
            NewChatButton(onClick = onStartNewChat)
            Spacer(Modifier.height(14.dp))
        }
    }
}

/** Title + saved-count pill on the start (right) side, close tile on the end (left) side. */
@Composable
private fun DrawerHeader(sessionsCount: Int, onClose: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(DrawerTileShape)
                    .background(DrawerAccentGradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.History,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = stringResource(Res.string.agent_history),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                )
                Text(
                    text = if (sessionsCount == 0) "بدون گفتگوی ذخیره‌شده"
                           else "${sessionsCount.toString().toPersianDigits()} گفتگوی ذخیره‌شده",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        color = AgentGlass.textSecondary
                    )
                )
            }
        }
        GlassTile(
            icon = Icons.Rounded.Close,
            contentDescription = "بستن",
            onClick = onClose
        )
    }
}

/** One 34dp glass icon tile, the same look as the top bar's history/new-chat/back buttons. */
@Composable
private fun GlassTile(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 34.dp,
    iconSize: Dp = 18.dp,
    tint: Color = AgentGlass.iconTint
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(DrawerTileShape)
            .background(AgentGlass.tileFill)
            .border(AgentGlass.borderWidth, AgentGlass.borderColor, DrawerTileShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(iconSize)
        )
    }
}

/** Full-width gradient pill pinned to the drawer's bottom edge — the primary action here. */
@Composable
private fun NewChatButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .coloredShadow(
                color = Color(0xFF5B46E4).copy(alpha = 0.45f),
                borderRadius = NewChatButtonRadius,
                blurRadius = 16.dp,
                offsetY = 6.dp
            )
            .clip(NewChatButtonShape)
            .background(DrawerAccentGradient)
            .border(AgentGlass.borderWidth, Color.White.copy(alpha = 0.22f), NewChatButtonShape)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = stringResource(Res.string.agent_new_chat),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        )
    }
}

@Composable
private fun SessionRow(
    session: AgentSessionDN,
    isActive: Boolean,
    onOpen: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    // The open conversation gets a tinted fill, a brighter/thicker border, an accent strip
    // on its leading edge and a "جاری" pill — one cue alone was too easy to miss.
    val background: Brush = if (isActive) ActiveRowGradient else SolidColor(AgentGlass.tileFillSubtle)
    val borderColor = if (isActive) DrawerAccentSoft.copy(alpha = 0.75f) else AgentGlass.borderColor
    val borderWidth = if (isActive) 1.5.dp else AgentGlass.borderWidth

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(DrawerRowShape)
            .background(background)
            .border(borderWidth, borderColor, DrawerRowShape)
            .clickable(onClick = onOpen)
            .padding(end = 8.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Leading (right-edge) accent strip; kept as a transparent spacer on inactive rows
        // so titles stay aligned across the list.
        Box(
            modifier = Modifier
                .padding(start = 6.dp)
                .width(3.dp)
                .height(34.dp)
                .clip(CircleShape)
                .background(if (isActive) DrawerAccentGradient else SolidColor(Color.Transparent))
        )
        Spacer(Modifier.width(9.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.5.sp,
                        fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium,
                        color = if (isActive) Color.White else AgentGlass.textPrimary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (isActive) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(CornerRadius.lg))
                            .background(DrawerAccentSoft.copy(alpha = 0.28f))
                            .border(1.dp, DrawerAccentSoft.copy(alpha = 0.55f), RoundedCornerShape(CornerRadius.lg))
                            .padding(horizontal = 7.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "جاری",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFE6DEFF)
                            )
                        )
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                NumericText(
                    text = PersianDateFormatter.formatTimestamp(session.lastMessageAt),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                    color = AgentGlass.textSecondary
                )
                Text(
                    text = "•",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                    color = AgentGlass.textSecondary
                )
                Text(
                    text = "${session.messageCount.toString().toPersianDigits()} پیام",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                    color = AgentGlass.textSecondary
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        GlassTile(
            icon = Icons.Outlined.Edit,
            contentDescription = "تغییر نام",
            onClick = onRename,
            size = 30.dp,
            iconSize = 15.dp
        )
        Spacer(Modifier.width(6.dp))
        GlassTile(
            icon = Icons.Outlined.Delete,
            contentDescription = "حذف",
            onClick = onDelete,
            size = 30.dp,
            iconSize = 15.dp,
            tint = AgentGlass.danger
        )
    }
}

@Composable
private fun EmptyHistoryMessage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(AgentGlass.tileFill)
                .border(AgentGlass.borderWidth, AgentGlass.borderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.History,
                contentDescription = null,
                tint = AgentGlass.iconTint.copy(alpha = 0.7f),
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = "هنوز گفتگویی ذخیره نشده است.",
            style = MaterialTheme.typography.bodySmall.copy(color = AgentGlass.textSecondary),
            textAlign = TextAlign.Center
        )
    }
}

// ─── Dialogs ──────────────────────────────────────────────────────────────────
//
// Plain `Dialog` windows with a glass card inside, instead of Material's AlertDialog: that
// one is themed for the app's light/dark surfaces and looked like a stray white box over
// the assistant's fixed-dark backdrop. A dialog window cannot blur what is under it, so
// the card uses the frost's opaque fallback colour plus the usual sheen and border.

@Composable
private fun GlassDialogCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .coloredShadow(
                color = AgentGlass.shadowColor.copy(alpha = 0.6f),
                borderRadius = 22.dp,
                blurRadius = 28.dp,
                offsetY = 10.dp
            )
            .clip(DialogShape)
            .background(AgentGlass.frostFallbackColor)
            .background(AgentGlass.sheen)
            .border(AgentGlass.borderWidth, AgentGlass.borderColor, DialogShape)
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        content = content
    )
}

@Composable
private fun DialogTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
    )
}

/** Secondary (cancel) action — a glass pill with muted text. */
@Composable
private fun DialogGlassButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(42.dp)
            .clip(DrawerTileShape)
            .background(AgentGlass.tileFill)
            .border(AgentGlass.borderWidth, AgentGlass.borderColor, DrawerTileShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = AgentGlass.textPrimary
            )
        )
    }
}

/** Primary action — filled with [brush]; dims (and stops reacting) when [enabled] is false. */
@Composable
private fun DialogFilledButton(
    label: String,
    brush: Brush,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .height(42.dp)
            .alpha(if (enabled) 1f else 0.45f)
            .clip(DrawerTileShape)
            .background(brush)
            .border(AgentGlass.borderWidth, Color.White.copy(alpha = 0.22f), DrawerTileShape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
    }
}

@Composable
private fun RenameSessionDialog(
    initialTitle: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialTitle) }
    val focusRequester = remember { FocusRequester() }
    val canSave = text.isNotBlank()

    Dialog(onDismissRequest = onDismiss) {
        GlassDialogCard {
            // Inside the dialog's own composition, so the field is attached before focus is
            // requested — outside it this throws "FocusRequester is not initialized".
            LaunchedEffect(Unit) { focusRequester.requestFocus() }

            DialogTitle("تغییر نام گفتگو")

            // Same field styling as the composer's BasicTextField on the input bar.
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.5.sp,
                    color = AgentGlass.textPrimary,
                    textAlign = TextAlign.Right
                ),
                cursorBrush = SolidColor(Color.White),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { if (canSave) onConfirm(text) }),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(DrawerTileShape)
                            .background(AgentGlass.tileFillSubtle)
                            .border(AgentGlass.borderWidth, AgentGlass.borderColor, DrawerTileShape)
                            .padding(horizontal = 12.dp, vertical = 12.dp)
                    ) {
                        if (text.isEmpty()) {
                            Text(
                                text = "عنوان گفتگو",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.5.sp,
                                    color = AgentGlass.textSecondary.copy(alpha = 0.6f)
                                ),
                                modifier = Modifier.align(Alignment.CenterStart)
                            )
                        }
                        // Same anchor as the placeholder (CenterStart = right edge under RTL) so
                        // typed text does not jump to the other side of the field.
                        Box(modifier = Modifier.align(Alignment.CenterStart)) { innerTextField() }
                    }
                }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DialogFilledButton(
                    label = "ذخیره",
                    brush = DrawerAccentGradient,
                    enabled = canSave,
                    onClick = { onConfirm(text) },
                    modifier = Modifier.weight(1f)
                )
                DialogGlassButton(
                    label = "انصراف",
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DeleteSessionDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        GlassDialogCard {
            DialogTitle("حذف گفتگو")
            Text(
                text = "«$title» و همه پیام‌هایش حذف شوند؟",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.sp,
                    color = AgentGlass.textSecondary
                )
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DialogFilledButton(
                    label = "حذف",
                    brush = SolidColor(AgentGlass.danger.copy(alpha = 0.85f)),
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f)
                )
                DialogGlassButton(
                    label = "انصراف",
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
