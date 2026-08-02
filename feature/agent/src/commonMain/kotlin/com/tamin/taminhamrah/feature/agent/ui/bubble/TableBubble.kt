package com.tamin.taminhamrah.feature.agent.ui.bubble

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.ui.theme.LocalTaminColors

/**
 * A responsive table.
 *
 * The layout adapts to how much room the bubble actually gets: when every column can
 * have at least [MIN_COLUMN_WIDTH] the columns share the full width, which looks right
 * for the two- and three-column answers that make up most replies. Wider tables keep
 * their columns readable and scroll horizontally instead of squeezing text into
 * unreadable slivers — the chat itself never scrolls sideways.
 */
@Composable
fun TableBubble(
    content: ChatBubbleContent.Table,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    if (content.columns.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(taminColors.bgSurface)
            .padding(12.dp)
    ) {
        content.title?.takeIf { it.isNotBlank() }?.let { title ->
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = taminColors.textPrimary
            )
            Spacer(Modifier.height(10.dp))
        }

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val columnCount = content.columns.size
            val fitsWidth = maxWidth >= MIN_COLUMN_WIDTH * columnCount
            // When it fits, columns split the available width; otherwise each keeps a
            // readable minimum and the whole grid scrolls.
            val columnWidth: Dp? = if (fitsWidth) null else MIN_COLUMN_WIDTH

            val grid: @Composable () -> Unit = {
                Column {
                    TableHeader(content.columns, columnWidth)
                    content.rows.forEachIndexed { index, row ->
                        TableBodyRow(
                            cells = row.cells,
                            columnCount = columnCount,
                            columnWidth = columnWidth,
                            isStriped = index % 2 == 1
                        )
                    }
                }
            }

            if (fitsWidth) {
                grid()
            } else {
                Box(modifier = Modifier.horizontalScroll(rememberScrollState())) { grid() }
            }
        }
    }
}

@Composable
private fun TableHeader(columns: List<String>, columnWidth: Dp?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
            .padding(vertical = 8.dp)
    ) {
        columns.forEach { column ->
            Text(
                text = column,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Start,
                maxLines = 2,
                modifier = Modifier
                    .cellWidth(columnWidth, this@Row)
                    .padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun TableBodyRow(
    cells: List<String>,
    columnCount: Int,
    columnWidth: Dp?,
    isStriped: Boolean
) {
    val taminColors = LocalTaminColors.current
    val background = if (isStriped) {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f)
    } else {
        androidx.compose.ui.graphics.Color.Transparent
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .padding(vertical = 8.dp)
    ) {
        // Pad short rows so cells stay aligned with their headers.
        repeat(columnCount) { index ->
            Text(
                text = cells.getOrElse(index) { "-" },
                style = MaterialTheme.typography.bodySmall,
                color = taminColors.textPrimary,
                maxLines = 3,
                modifier = Modifier
                    .cellWidth(columnWidth, this@Row)
                    .padding(horizontal = 8.dp)
            )
        }
    }
}

/** Fixed width when scrolling, otherwise an equal share of the row. */
private fun Modifier.cellWidth(
    columnWidth: Dp?,
    scope: androidx.compose.foundation.layout.RowScope
): Modifier = if (columnWidth != null) width(columnWidth) else with(scope) { weight(1f) }

/** Narrower than this and Persian labels start wrapping into unreadable columns. */
private val MIN_COLUMN_WIDTH = 96.dp
