package com.tamin.taminhamrah.feature.workshops.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAttachment
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopDocumentType
import com.tamin.taminhamrah.feature.workshops.ui.theme.WorkshopDimens
import com.tamin.taminhamrah.ui.components.InputRestriction
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.LoadingButtonIconPosition
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.animatedErrorBorder
import com.tamin.taminhamrah.ui.components.document.TaminDocumentUploadCard
import com.tamin.taminhamrah.ui.components.document.TaminDocumentUploadState
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.paging.OnLoadMore
import com.tamin.taminhamrah.ui.paging.PagingFooter
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.toPersianDigits
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_info
import taminx.core.core_ui.ic_tamin_check
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_chevron_down
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ws_form_add_doc
import taminx.core.core_ui.ws_form_doc_type_title
import taminx.core.core_ui.ws_form_docs_count
import taminx.core.core_ui.ws_form_docs_title
import taminx.core.core_ui.ws_form_edit_info
import taminx.core.core_ui.ws_form_err_docs
import taminx.core.core_ui.ws_form_file_size
import taminx.core.core_ui.ws_form_group_count
import taminx.core.core_ui.ws_form_prev
import kotlin.time.Duration.Companion.milliseconds

/**
 * The parts every کارگاه form is assembled from.
 *
 * The design draws all three — ثبت اعتراض, ماده ۱۶ and نام‌نویسی — from one template: a stepper, a
 * titled section, collapsible review groups, an upload box, amber notes, tick-boxes, an error line
 * and a sticky footer. Each is a piece here, so a form is a list of them rather than a re-drawing.
 */

/** One rung of a form's progress: done, current, or still ahead. */
@Immutable
data class WorkshopFormStep(val label: String, val isDone: Boolean, val isCurrent: Boolean)

/** A label/value line inside a review group. */
@Immutable
data class WorkshopReviewRow(val label: String, val value: String, val isNumeric: Boolean = true)

/**
 * «هویت — محل و شغل — مدارک», with the finished rungs ticked.
 *
 * The connector sits between rungs, not after the last one, which is why it is drawn from the
 * previous step rather than as a trailing element of each.
 */
@Composable
fun WorkshopStepper(
    steps: ImmutableList<WorkshopFormStep>,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md)
            .padding(top = Spacing.lg, bottom = Spacing.xs),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Top,
    ) {
        steps.forEachIndexed { index, step ->
            if (index > 0) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = Spacing.xs, vertical = StepConnectorTopInset)
                        .width(StepConnectorWidth)
                        .height(StepConnectorHeight)
                        .clip(CircleShape)
                        .background(
                            if (step.isDone || step.isCurrent) colors.blueText else colors.border,
                        ),
                )
            }
            Column(
                modifier = Modifier.width(StepMaxWidth),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.tabSelector),
            ) {
                StepBadge(step = step, index = index)
                Text(
                    text = step.label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (step.isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = if (step.isCurrent) colors.textPrimary else colors.textMuted,
                    maxLines = 2,
                )
            }
        }
    }
}

@Composable
private fun StepBadge(step: WorkshopFormStep, index: Int) {
    val colors = LocalTaminColors.current
    val shape = CircleShape
    val done = step.isDone

    Box(
        modifier = Modifier
            .size(StepBadgeSize)
            .clip(shape)
            .then(
                when {
                    done -> Modifier
                        .background(colors.greenBg)
                        .border(StepBadgeBorder, colors.greenBorder, shape)

                    step.isCurrent -> Modifier.background(colors.buttonGradient, shape)
                    else -> Modifier
                        .background(colors.bgSurface)
                        .border(StepBadgeBorder, colors.border, shape)
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (done) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_check),
                contentDescription = null,
                tint = colors.greenText,
                modifier = Modifier.size(StepCheckSize),
            )
        } else {
            NumericText(
                text = (index + 1).toString().toPersianDigits(),
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.ExtraBold),
                color = if (step.isCurrent) colors.txtNameProfile else colors.textMuted,
            )
        }
    }
}

/** What this step is for: its heading, and the sentence under it. */
@Composable
fun WorkshopFormSection(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
) {
    val colors = LocalTaminColors.current
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.ExtraBold,
            color = colors.textPrimary,
        )
        if (description != null) {
            Text(
                text = description,
                style = MaterialTheme.typography.labelMedium,
                color = colors.textTertiary,
                lineHeight = SectionDescriptionLineHeight,
                modifier = Modifier.padding(top = Spacing.tabSelector),
            )
        }
    }
}

/**
 * A block of already-known values the user is asked to check before going on.
 *
 * Collapsible, because a form's first step is usually two of these and the second one is rarely
 * what the user came to read. [onEdit] adds the «ویرایش اطلاعات» footer for a group whose values
 * came from an earlier step.
 */
@Composable
fun WorkshopReviewGroup(
    title: String,
    rows: ImmutableList<WorkshopReviewRow>,
    isOpen: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    onEdit: (() -> Unit)? = null,
) {
    val colors = LocalTaminColors.current
    val rotation = animateFloatAsState(
        if (isOpen) WorkshopDimens.toggleHalfTurn else 0f,
        label = "review-group",
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(WorkshopDimens.cardCorner)
            .padding(horizontal = WorkshopDimens.cardHorizontalPadding),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = ReviewHeaderHeight)
                .clickable(onClick = onToggle),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(
                    Res.string.ws_form_group_count,
                    rows.size.toString().toPersianDigits(),
                ),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textMuted,
            )
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_chevron_down),
                contentDescription = null,
                tint = colors.chevron,
                modifier = Modifier
                    .size(WorkshopDimens.serviceRowChevronSize)
                    .graphicsLayer { rotationZ = rotation.value },
            )
        }

        if (!isOpen) return@Column

        rows.forEachIndexed { index, row ->
            if (index > 0) {
                Box(
                    Modifier.fillMaxWidth().height(Thickness.border).background(colors.divider),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = ReviewRowPadding),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = row.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textMuted,
                )
                if (row.isNumeric) {
                    NumericText(
                        text = row.value,
                        style = MaterialTheme.typography.labelLarge
                            .copy(fontWeight = FontWeight.Bold),
                        color = colors.textPrimary,
                    )
                } else {
                    Text(
                        text = row.value,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                    )
                }
            }
        }

        if (onEdit != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.xxs)
                    .dashedTopRule(colors.divider)
                    .clickable(onClick = onEdit)
                    .defaultMinSize(minHeight = WorkshopDimens.cardButtonHeight),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.ws_form_edit_info),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.blueText,
                )
            }
        }
        Box(Modifier.height(Spacing.xs))
    }
}

/**
 * The files attached so far, the control that adds another, and the sheet that names its type.
 *
 * The rows are core-ui's [TaminDocumentUploadCard] — the same card the occurrence report uses —
 * so an attached file looks and behaves the same everywhere in the app, wave animation and
 * thumbnail included. Only what the design draws around it is this feature's own: the titled
 * surface, the «۲ از ۵» badge, and the green add button.
 *
 * The type sheet and the picker live here too, because all three کارگاه forms drove them
 * identically — three copies of the same two pieces of state was three places to get the
 * pending type wrong.
 *
 * The add control disappears at the cap rather than failing on tap, which is how the design says
 * "that is all this request may carry".
 */
@Composable
fun WorkshopDocumentsPanel(
    attachments: ImmutableList<WorkshopAttachment>,
    types: ImmutableList<WorkshopDocumentType>,
    capacity: Int,
    onAdd: (fileName: String, bytes: ByteArray, typeCode: String) -> Unit,
    onRemove: (Int) -> Unit,
    modifier: Modifier = Modifier,
    /** While true the add control is inert — one upload at a time. */
    isUploading: Boolean = false,
    /**
     * Traces the error border round the whole panel.
     *
     * «بارگذاری حداقل یک مدرک» is answered by pressing افزودن مدرک, so the border traces that
     * button rather than the whole panel — it points at what to do, the way a field's border
     * points at the field to fill in.
     */
    isError: Boolean = false,
) {
    val colors = LocalTaminColors.current
    val scope = rememberCoroutineScope()
    var isTypeSheetOpen by remember { mutableStateOf(false) }
    // Never cleared: a canceled pick hands back a null file, which is what the callback tests.
    var pendingType by remember { mutableStateOf<WorkshopDocumentType?>(null) }

    // The wave outlives the upload by [WAVE_TAIL_MILLIS], the way step 6 of the occurrence report
    // does it — a fast upload otherwise flashes the card and is gone before it reads as progress.
    var isWaving by remember { mutableStateOf(false) }
    var countAtUploadStart by remember { mutableStateOf(attachments.size) }
    LaunchedEffect(isUploading) {
        if (isUploading) {
            countAtUploadStart = attachments.size
            isWaving = true
        } else {
            delay(WAVE_TAIL_MILLIS.milliseconds)
            isWaving = false
        }
    }
    // While the wave plays out on the newest file, that file is not also listed as settled. A
    // failed upload adds nothing, so the count decides rather than the wave alone.
    val hasLanded = isWaving && !isUploading && attachments.size > countAtUploadStart
    val settled = remember(attachments, hasLanded) {
        if (hasLanded) attachments.dropLast(1) else attachments
    }

    val filePicker = rememberFilePickerLauncher(type = FileKitType.Image) { file ->
        val type = pendingType
        if (file == null || type == null) return@rememberFilePickerLauncher
        scope.launch { onAdd(file.name, file.readBytes(), type.code) }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(WorkshopDimens.cardCorner)
            .padding(WorkshopDimens.panelPadding),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.ws_form_docs_title),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
            )
            NumericText(
                text = stringResource(
                    Res.string.ws_form_docs_count,
                    attachments.size.toString().toPersianDigits(),
                    capacity.toString().toPersianDigits(),
                ),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.blueText,
                modifier = Modifier
                    .clip(DocsBadgeShape)
                    .background(colors.blueBg)
                    .border(Thickness.border, colors.blueBorder, DocsBadgeShape)
                    .padding(
                        horizontal = DocCountHorizontalPadding,
                        vertical = WorkshopDimens.countBadgeVerticalPadding,
                    ),
            )
        }

        settled.forEachIndexed { index, attachment ->
            TaminDocumentUploadCard(
                title = stringResource(attachment.type.label),
                state = TaminDocumentUploadState.Uploaded,
                statusText = stringResource(Res.string.ws_form_file_size, attachment.size),
                onDeleteClick = { onRemove(index) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = WorkshopDimens.cardButtonsTopMargin),
            )
        }

        if (isWaving) {
            // Replaces the upload button until the wave finishes, so only one upload runs at a
            // time and the card cannot be covered up while playing.
            ShimmerBlock(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(WorkshopDimens.panelButtonHeight)
                    .padding(top = WorkshopDimens.cardButtonsTopMargin),
                cornerRadius = CornerRadius.chip,
            )
        } else if (attachments.size < capacity) {
            TaminOutlinedButton(
                text = stringResource(Res.string.ws_form_add_doc),
                onClick = { isTypeSheetOpen = true },
                icon = Icons.Default.Add,
                shape = BannerShape,
                height = WorkshopDimens.panelButtonHeight,
                borderWidth = WorkshopDimens.panelButtonBorderWidth,
                borderColor = if (isError) colors.dangerText else colors.blueBorder,
                containerColor = colors.bgSurface,
                contentColor = if (isError) colors.dangerText else colors.blueText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = WorkshopDimens.cardButtonsTopMargin)
                    .animatedErrorBorder(
                        isError = isError,
                        errorColor = colors.dangerText,
                        normalColor = colors.blueBorder,
                        borderWidth = WorkshopDimens.panelButtonBorderWidth,
                        cornerRadius = CornerRadius.chip,
                    ),
            )
        }

        if (isError) {
            Text(
                text = stringResource(Res.string.ws_form_err_docs),
                style = MaterialTheme.typography.labelSmall,
                color = colors.dangerText,
                modifier = Modifier.padding(top = Spacing.xs),
            )
        }
    }

    if (isTypeSheetOpen) {
        WorkshopDocumentTypeSheet(
            types = types,
            onDismiss = { isTypeSheetOpen = false },
            onSelect = { type ->
                pendingType = type
                isTypeSheetOpen = false
                filePicker.launch()
            },
        )
    }
}

/** How long the upload wave keeps playing after the file has actually landed. */
private const val WAVE_TAIL_MILLIS = 1600L

/** A rule the user must know before submitting, in the design's amber. */
@Composable
fun WorkshopFormNote(text: String, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(NoteShape)
            .background(colors.orangeBg)
            .border(Thickness.border, colors.orangeText.copy(alpha = NoteBorderAlpha), NoteShape)
            .padding(horizontal = NoteHorizontalPadding, vertical = NoteVerticalPadding),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_info),
            contentDescription = null,
            tint = colors.orangeText,
            modifier = Modifier.size(IconSize.small),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary,
            lineHeight = NoteLineHeight,
        )
    }
}

/** A declaration the user has to tick before the form will submit. */
@Composable
fun WorkshopFormCheck(
    label: String,
    isChecked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(CheckCardShape)
            .taminSurface(CornerRadius.xl)
            .clickable(onClick = onToggle)
            .padding(horizontal = CheckHorizontalPadding, vertical = CheckVerticalPadding),
        horizontalArrangement = Arrangement.spacedBy(Spacing.smPlus),
    ) {
        Box(
            modifier = Modifier
                .size(CheckBoxSize)
                .clip(CheckBoxShape)
                .then(
                    if (isChecked) {
                        Modifier.background(colors.buttonGradient, CheckBoxShape)
                    } else {
                        Modifier
                            .background(colors.bgSurface)
                            .border(CheckBoxBorder, colors.outerBorder, CheckBoxShape)
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (isChecked) {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_check),
                    contentDescription = null,
                    tint = colors.txtNameProfile,
                    modifier = Modifier.size(CheckGlyphSize),
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = colors.textSecondary,
            lineHeight = CheckLineHeight,
        )
    }
}

/** Why the form will not submit yet. */
@Composable
fun WorkshopFormError(text: String, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.tabSelector),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_info),
            contentDescription = null,
            tint = colors.dangerText,
            modifier = Modifier.size(WorkshopDimens.toggleChevronSize),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.dangerText,
        )
    }
}

/** Free text a form asks for — «شرح اعتراض». */
@Composable
fun WorkshopFormTextArea(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val textStyle = MaterialTheme.typography.labelLarge.copy(
        color = colors.textPrimary,
        lineHeight = TextAreaLineHeight,
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = colors.textSecondary,
            modifier = Modifier.padding(bottom = WorkshopDimens.fieldLabelGap),
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = textStyle,
            cursorBrush = SolidColor(colors.blueText),
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = TextAreaHeight)
                .clip(TextAreaShape)
                .background(colors.bgPage)
                .border(Thickness.border, colors.border, TextAreaShape)
                .padding(TextAreaPadding),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.textMuted,
                        lineHeight = TextAreaLineHeight,
                    )
                }
                innerTextField()
            },
        )
    }
}

/**
 * The bar a form is driven from, pinned to the bottom of the page.
 *
 * «مرحلهٔ بعد» is the wider of the two — `flex:1.4` against the previous step's `flex:1` — because
 * going on is what the user is here to do.
 */
@Composable
fun WorkshopFormFooter(
    nextLabel: String,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    onPrev: (() -> Unit)? = null,
    /** While true the forward action shows the app's spinner and refuses further taps. */
    isBusy: Boolean = false,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = colors.divider,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = Thickness.border.toPx(),
                )
            }
            .background(colors.bgPage)
            .padding(
                start = Spacing.page,
                end = Spacing.page,
                top = Spacing.md,
                bottom = Spacing.xlg,
            ),
        horizontalArrangement = Arrangement.spacedBy(Spacing.cardGap),
    ) {
        if (onPrev != null) {
            TaminOutlinedButton(
                text = stringResource(Res.string.ws_form_prev),
                onClick = onPrev,
                icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                shape = FooterButtonShape,
                height = FooterButtonHeight,
                borderWidth = FooterButtonBorder,
                borderColor = colors.blueBorder,
                containerColor = colors.bgSurface,
                contentColor = colors.blueText,
                modifier = Modifier.weight(PrevButtonWeight),
            )
        }
        // The app's own submit button, so an upload or a submission in flight shows the same
        // spinner here as everywhere else — and a second tap cannot start a second request.
        LoadingButton(
            text = nextLabel,
            onClick = onNext,
            isLoading = isBusy,
            enabled = !isBusy,
            // Points the way on: autoMirrored, so under RTL it draws "‹" as the design has it.
            icon = vectorResource(Res.drawable.ic_tamin_chevron_forward),
            iconPosition = LoadingButtonIconPosition.TRAILING,
            height = FooterButtonHeight,
            shape = FooterButtonShape,
            modifier = Modifier.weight(NextButtonWeight),
        )
    }
}

/** The dashed rule the design puts above a card's footer control. */
private fun Modifier.dashedTopRule(color: Color): Modifier =
    drawBehind {
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = Thickness.border.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(WorkshopDimens.toggleDashOn.toPx(), WorkshopDimens.toggleDashOff.toPx()),
            ),
        )
    }

// ---------------------------------------------------------------- stepper
private val StepBadgeSize = 30.dp
private val StepBadgeBorder = 1.5.dp
private val StepCheckSize = 15.dp
private val StepMaxWidth = 96.dp
private val StepConnectorWidth = 44.dp
private val StepConnectorHeight = 2.dp
private val StepConnectorTopInset = 14.dp

// ---------------------------------------------------------------- section
private val SectionDescriptionLineHeight = 22.sp

// ----------------------------------------------------------- review group
private val ReviewHeaderHeight = 48.dp
private val ReviewRowPadding = 10.dp

// ------------------------------------------------------------- documents
private val DocCountHorizontalPadding = 9.dp

// ----------------------------------------------------------------- notes
private val NoteHorizontalPadding = 12.dp
private val NoteVerticalPadding = 10.dp
private val NoteLineHeight = 22.sp
private const val NoteBorderAlpha = 0.35f

// ---------------------------------------------------------------- checks
private val CheckBoxSize = 21.dp
private val CheckBoxCorner = 7.dp
private val CheckBoxBorder = 1.5.dp
private val CheckGlyphSize = 13.dp
private val CheckHorizontalPadding = 13.dp
private val CheckVerticalPadding = 12.dp
private val CheckLineHeight = 22.sp

// -------------------------------------------------------------- text area
private val TextAreaCorner = 15.dp
private val TextAreaPadding = 13.dp
private val TextAreaHeight = 108.dp
private val TextAreaLineHeight = 24.sp

// ---------------------------------------------------------------- footer
private val FooterButtonHeight = 52.dp
private val FooterButtonCorner = 15.dp
private val FooterButtonBorder = 1.5.dp
private const val PrevButtonWeight = 1f
private const val NextButtonWeight = 1.4f

/**
 * «نوع مدرک را انتخاب کنید» — what a file is filed under, asked before the picker opens.
 *
 * The type comes first because the service files by type, not by file: picking an image and then
 * being asked what it is invites the wrong answer.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkshopDocumentTypeSheet(
    types: ImmutableList<WorkshopDocumentType>,
    onDismiss: () -> Unit,
    onSelect: (WorkshopDocumentType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.bgSurface,
        modifier = modifier,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.page)
                .padding(
                    bottom = WindowInsets.navigationBars.asPaddingValues()
                        .calculateBottomPadding() + Spacing.lg,
                ),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            item(key = "title") {
                Text(
                    text = stringResource(Res.string.ws_form_doc_type_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    modifier = Modifier.padding(bottom = Spacing.sm),
                )
            }
            items(types, key = { it.code }) { type ->
                Text(
                    text = stringResource(type.label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(DocTypeItemShape)
                        .clickable { onSelect(type) }
                        .background(colors.chipBg)
                        .padding(horizontal = Spacing.lg, vertical = Spacing.md),
                )
            }
        }
    }
}

/**
 * A searchable list of values a field is chosen from — a city, a job.
 *
 * Searched rather than scrolled: both lookups run to thousands of rows, and the service is asked
 * again as the query changes rather than every row being pulled down once.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> WorkshopLookupSheet(
    title: String,
    query: String,
    onQueryChange: (String) -> Unit,
    options: ImmutableList<T>,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    canLoadMore: Boolean = false,
    isLoadingMore: Boolean = false,
    onLoadMore: (() -> Unit)? = null,
    label: (T) -> String = { it.toString() },
) {
    val colors = LocalTaminColors.current
    val listState = rememberLazyListState()

    if (onLoadMore != null) {
        listState.OnLoadMore(
            enabled = canLoadMore && !isLoadingMore,
            onLoadMore = onLoadMore,
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.bgSurface,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.page)
                .padding(
                    bottom = WindowInsets.navigationBars.asPaddingValues()
                        .calculateBottomPadding() + Spacing.lg,
                ),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
            )
            WorkshopTextField(
                label = title,
                value = query,
                onValueChange = onQueryChange,
                keyboardType = KeyboardType.Text,
                inputRestriction = InputRestriction.None,
            )
            if (isLoading) {
                // Row-shaped blocks rather than a spinner, so the sheet does not jump when the
                // real options replace them — the same wait every other list in the app draws.
                repeat(LookupShimmerRows) {
                    ShimmerBlock(
                        modifier = Modifier.fillMaxWidth().height(LookupShimmerRowHeight),
                        cornerRadius = CornerRadius.md,
                    )
                }
            }
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                items(
                    count = options.size,
                    key = { index ->
                        val item = options[index]
                        (item as? com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers.PickedOption)?.code
                            ?: (item as? WorkshopDocumentType)?.code
                            ?: index
                    },
                ) { index ->
                    val option = options[index]
                    Text(
                        text = label(option),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textPrimary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(OptionItemShape)
                            .clickable { onSelect(option) }
                            .background(colors.chipBg)
                            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
                    )
                }

                if (isLoadingMore) {
                    item(key = "lookup_sheet_loading_more") {
                        PagingFooter(isLoadingNextPage = true, error = null, onRetry = {})
                    }
                }
            }
        }
    }
}

/** Something the user needs to know before starting, in the design's blue. */
@Composable
fun WorkshopFormBanner(text: String, modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(BannerShape)
            .background(colors.blueBg)
            .border(Thickness.border, colors.blueBorder, BannerShape)
            .padding(horizontal = NoteHorizontalPadding, vertical = NoteVerticalPadding),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_info),
            contentDescription = null,
            tint = colors.blueText,
            modifier = Modifier.size(IconSize.small),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary,
            lineHeight = NoteLineHeight,
        )
    }
}

/** A lookup waits as four row-shaped blocks — about a sheet's worth before it scrolls. */
private const val LookupShimmerRows = 4
private val LookupShimmerRowHeight = 44.dp

private val DocsBadgeShape = RoundedCornerShape(CornerRadius.max)
private val NoteShape = RoundedCornerShape(CornerRadius.chip)
private val CheckCardShape = RoundedCornerShape(CornerRadius.xl)
private val CheckBoxShape = RoundedCornerShape(CheckBoxCorner)
private val TextAreaShape = RoundedCornerShape(TextAreaCorner)
private val FooterButtonShape = RoundedCornerShape(FooterButtonCorner)
private val DocTypeItemShape = RoundedCornerShape(CornerRadius.md)
private val OptionItemShape = RoundedCornerShape(CornerRadius.md)
private val BannerShape = RoundedCornerShape(CornerRadius.chip)

