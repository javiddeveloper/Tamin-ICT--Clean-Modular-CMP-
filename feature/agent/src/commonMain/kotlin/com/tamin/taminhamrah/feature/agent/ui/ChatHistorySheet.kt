package com.tamin.taminhamrah.feature.agent.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.agent.AgentSessionDN
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.util.PersianDateFormatter

/**
 * Saved conversations, the equivalent of old_Android's `HistoryCategoryBottomSheet`.
 *
 * The assistant always opens on an empty chat; this sheet is how the user gets back to
 * an earlier one. Each row can be opened, renamed or deleted.
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ChatHistorySheet(
    sessions: List<AgentSessionDN>,
    activeSessionId: String?,
    onDismiss: () -> Unit,
    onOpenSession: (String) -> Unit,
    onDeleteSession: (String) -> Unit,
    onRenameSession: (String, String) -> Unit,
    onStartNewChat: () -> Unit
) {
    val taminColors = LocalTaminColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var renaming by remember { mutableStateOf<AgentSessionDN?>(null) }
    var pendingDelete by remember { mutableStateOf<AgentSessionDN?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = taminColors.bgSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "گفتگوهای من",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = taminColors.textPrimary
            )
            Spacer(Modifier.height(12.dp))

            NewChatRow(onClick = onStartNewChat)
            Spacer(Modifier.height(8.dp))

            if (sessions.isEmpty()) {
                EmptyHistoryMessage()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sessions, key = { it.id }) { session ->
                        SessionRow(
                            session = session,
                            isActive = session.id == activeSessionId,
                            onOpen = { onOpenSession(session.id) },
                            onRename = { renaming = session },
                            onDelete = { pendingDelete = session }
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }

    renaming?.let { session ->
        RenameDialog(
            initialTitle = session.title,
            onDismiss = { renaming = null },
            onConfirm = { newTitle ->
                onRenameSession(session.id, newTitle)
                renaming = null
            }
        )
    }

    pendingDelete?.let { session ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("حذف گفتگو") },
            text = { Text("«${session.title}» و همه پیام‌هایش حذف شوند؟") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteSession(session.id)
                    pendingDelete = null
                }) {
                    Text("حذف", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("انصراف") }
            }
        )
    }
}

@Composable
private fun NewChatRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = "گفتگوی جدید",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.primary
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
    val taminColors = LocalTaminColors.current
    val background = if (isActive) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .clickable(onClick = onOpen)
            .padding(start = 12.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = session.title,
                style = MaterialTheme.typography.bodyMedium,
                color = taminColors.textPrimary,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "${PersianDateFormatter.formatTimestamp(session.lastMessageAt)} • ${session.messageCount} پیام",
                style = MaterialTheme.typography.labelSmall,
                color = taminColors.textMuted
            )
        }
        IconButton(onClick = onRename) {
            Icon(
                Icons.Default.Edit,
                contentDescription = "تغییر نام",
                tint = taminColors.textMuted,
                modifier = Modifier.size(18.dp)
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "حذف",
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun EmptyHistoryMessage() {
    val taminColors = LocalTaminColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "هنوز گفتگویی ذخیره نشده است.",
            style = MaterialTheme.typography.bodySmall,
            color = taminColors.textMuted
        )
    }
}

@Composable
private fun RenameDialog(
    initialTitle: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialTitle) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تغییر نام گفتگو") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(text) },
                enabled = text.isNotBlank()
            ) { Text("ذخیره") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("انصراف") }
        }
    )
}
