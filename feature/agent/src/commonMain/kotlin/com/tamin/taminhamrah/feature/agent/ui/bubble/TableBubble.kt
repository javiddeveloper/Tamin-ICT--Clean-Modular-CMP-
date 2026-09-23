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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.ui.AgentGlass
import com.tamin.taminhamrah.feature.agent.ui.agentGlassCard
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Spacing

/**
 * A responsive table.
 *
 * The layout adapts to how much room the bubble actually gets: when every column can
 * have at least [MIN_COLUMN_WIDTH] the columns share the full width, which looks right
 * for the two- and three-column answers that make up most replies. Wider tables keep
 * their columns readable and scroll horizontally instead of squeezing text into
 * unreadable slivers — the chat itself never scrolls sideways.
 *
 * @param renderCell turns a header or cell string into the text drawn, e.g. inline markdown with
 *   tappable links; plain by default
 * @param cellContent draws a body cell itself instead, e.g. a button for a link; null draws text
 * @param framed draws the table on its own surface; false when it already sits inside a card
 */
@Composable
fun TableBubble(
    content: ChatBubbleContent.Table,
    modifier: Modifier = Modifier,
    framed: Boolean = true,
    renderCell: (String) -> AnnotatedString = { AnnotatedString(it) },
    cellContent: (@Composable (cell: String) -> Unit)? = null,
) {
    if (content.columns.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (framed) {
                    Modifier
                        .agentGlassCard(RoundedCornerShape(CornerRadius.xl))
                        .padding(Spacing.md)
                } else {
                    Modifier
                }
            )
    ) {
        content.title?.takeIf { it.isNotBlank() }?.let { title ->
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = AgentGlass.textPrimary
            )
            Spacer(Modifier.height(Spacing.smPlus))
        }

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val columnCount = content.columns.size
            val fitsWidth = maxWidth >= MIN_COLUMN_WIDTH * columnCount
            // When it fits, columns split the available width; otherwise each keeps a
            // readable minimum and the whole grid scrolls.
            val columnWidth: Dp? = if (fitsWidth) null else MIN_COLUMN_WIDTH

            // The header is rendered through [renderCell], which for a markdown table runs the
            // inline parser over each column title — worth doing once per table rather than on
            // every recomposition of the bubble it sits in. Body cells already come through
            // [cellContent], which memoizes per cell.
            val headerCells = remember(content.columns, renderCell) {
                content.columns.map(renderCell)
            }

            val grid: @Composable () -> Unit = {
                Column {
                    TableHeader(headerCells, columnWidth)
                    content.rows.forEachIndexed { index, row ->
                        TableBodyRow(
                            cells = row.cells,
                            renderCell = renderCell,
                            cellContent = cellContent,
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
private fun TableHeader(columns: List<AnnotatedString>, columnWidth: Dp?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.md))
            .background(AgentGlass.tileFill)
            .padding(vertical = Spacing.sm)
    ) {
        columns.forEach { column ->
            Text(
                text = column,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = AgentGlass.accent,
                textAlign = TextAlign.Start,
                maxLines = 2,
                modifier = Modifier
                    .cellWidth(columnWidth, this@Row)
                    .padding(horizontal = Spacing.sm)
            )
        }
    }
}

@Composable
private fun TableBodyRow(
    cells: List<String>,
    renderCell: (String) -> AnnotatedString,
    cellContent: (@Composable (cell: String) -> Unit)?,
    columnCount: Int,
    columnWidth: Dp?,
    isStriped: Boolean
) {
    val background = if (isStriped) AgentGlass.tileFillSubtle else androidx.compose.ui.graphics.Color.Transparent
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.md))
            .background(background)
            .padding(vertical = Spacing.sm),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        // Pad short rows so cells stay aligned with their headers.
        repeat(columnCount) { index ->
            val cell = cells.getOrElse(index) { "-" }
            val cellModifier = Modifier
                .cellWidth(columnWidth, this@Row)
                .padding(horizontal = Spacing.sm)
            if (cellContent != null) {
                Box(modifier = cellModifier) { cellContent(cell) }
            } else {
                Text(
                    text = renderCell(cell),
                    style = MaterialTheme.typography.bodySmall,
                    color = AgentGlass.textPrimary,
                    maxLines = MAX_CELL_LINES,
                    modifier = cellModifier,
                )
            }
        }
    }
}

/** Fixed width when scrolling, otherwise an equal share of the row. */
private fun Modifier.cellWidth(
    columnWidth: Dp?,
    scope: androidx.compose.foundation.layout.RowScope
): Modifier = if (columnWidth != null) width(columnWidth) else with(scope) { weight(1f) }

/** Enough for a short link label or a long value; a cell rarely needs more. */
private const val MAX_CELL_LINES = 4

/** Narrower than this and Persian labels start wrapping into unreadable columns. */
private val MIN_COLUMN_WIDTH = 96.dp
