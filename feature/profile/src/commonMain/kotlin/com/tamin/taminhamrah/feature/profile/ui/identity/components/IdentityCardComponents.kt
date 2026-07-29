package com.tamin.taminhamrah.feature.profile.ui.identity.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.tamin.taminhamrah.feature.profile.ui.identity.IdentityDimens
import com.tamin.taminhamrah.ui.components.LoadAsyncImage
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.shrinkOnCollapse
import com.tamin.taminhamrah.ui.components.vanishOnCollapse
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Easing
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminIdentityCardEnd
import com.tamin.taminhamrah.ui.theme.TaminIdentityCardMid
import com.tamin.taminhamrah.ui.theme.TaminIdentityCardStart
import com.tamin.taminhamrah.ui.theme.TaminIdentityChipEnd
import com.tamin.taminhamrah.ui.theme.TaminIdentityChipMid
import com.tamin.taminhamrah.ui.theme.TaminIdentityChipStart
import com.tamin.taminhamrah.ui.theme.TaminIdentityChipTrace
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_ejtemaei_logo
import taminx.core.core_ui.ic_tamin_user
import taminx.core.core_ui.ic_tamin_verified
import taminx.core.core_ui.identity_card_org
import taminx.core.core_ui.identity_card_type
import taminx.core.core_ui.identity_field_birth_date
import taminx.core.core_ui.identity_field_national_code
import taminx.core.core_ui.identity_lineage
import taminx.core.core_ui.identity_ssn_label

/**
 * The insured-person card at the top of the identity screen, and the morph that folds it into the
 * app bar as the page scrolls.
 *
 * Every animated value is read inside a `layout {}` / `graphicsLayer {}` / `drawBehind {}` lambda,
 * so a frame of the fold costs a re-layout or a redraw and never a recomposition.
 */

/** Built once: a gradient rebuilt per draw would allocate on every frame of the fold. */
private val CardBackground = Brush.linearGradient(
    listOf(TaminIdentityCardStart, TaminIdentityCardMid, TaminIdentityCardEnd),
)

private val ChipBackground = Brush.linearGradient(
    listOf(TaminIdentityChipStart, TaminIdentityChipMid, TaminIdentityChipEnd),
)

/** Alphas for the card's own furniture, over its fixed blue. */
private const val SUBDUED = 0.68f
private const val GLASS = 0.16f
private const val GLASS_FILL = 0.12f
private const val BAND_TINT = 0.14f
private const val HAIRLINE = 0.10f

// The sheen circles: fractions of the card's width, kept soft enough to read as light rather
// than as shapes drawn on top of the gradient.
private const val SHEEN_OUTER = 0.38f
private const val SHEEN_INNER = 0.26f
private const val SHEEN_NEAR = 0.12f
private const val SHEEN_FAR = 0.88f

/** Brightness of the lit top edge, where the lamination catches the most light. */
private const val TOP_EDGE = 0.16f

/**
 * The diagonal gloss. Left at the default corner-to-corner span so it scales with whatever the
 * card measures to, and hoisted so the fold does not rebuild it per frame.
 */
private val CardShine = Brush.linearGradient(
    0.00f to Color.Transparent,
    0.40f to Color.White.copy(alpha = 0.10f),
    0.55f to Color.White.copy(alpha = 0.04f),
    1.00f to Color.Transparent,
)

/**
 * One insured person's card: organization branding, their photo and name, the social-security
 * number, and a foot band carrying the national code and date of birth.
 *
 * As it folds, the avatar, the name and the social-security number travel up into the app bar, so
 * the collapsed state still answers "whose card is this, and what is their number" rather than
 * becoming a blank strip. Everything that belongs only to the open card fades on the way.
 *
 * Takes the handful of strings it draws rather than the identity record, so a field the screen
 * reloads elsewhere cannot invalidate the card.
 */
@Composable
internal fun IdentityCard(
    fullName: String,
    fatherName: String,
    gender: String,
    nationality: String,
    ssn: String,
    nationalId: String,
    dateOfBirth: String,
    photo: String?,
    collapseProgress: () -> Float,
    modifier: Modifier = Modifier,
) {
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(IdentityDimens.cardCorner))
            .background(CardBackground)
            .cardSheen(rtl)
            .footBand(collapseProgress),
    ) {
        Layout(
            content = {
                CardBrandRow(
                    modifier = Modifier
                        .layoutId(CardSlot.Brand)
                        .vanishOnCollapse(collapseProgress, IdentityDimens.vanishRate),
                )
                ContactChip(
                    modifier = Modifier
                        .layoutId(CardSlot.Chip)
                        .vanishOnCollapse(collapseProgress, IdentityDimens.vanishRate),
                )
                CardAvatar(photo = photo, modifier = Modifier.layoutId(CardSlot.Avatar))
                Text(
                    text = fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .layoutId(CardSlot.Name)
                        .shrinkOnCollapse(
                            progress = collapseProgress,
                            minScale = IdentityDimens.nameCollapsedScale,
                            rtl = rtl,
                        ),
                )
                Text(
                    text = stringResource(Res.string.identity_lineage, fatherName, gender, nationality),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = SUBDUED),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .layoutId(CardSlot.Lineage)
                        .vanishOnCollapse(collapseProgress, IdentityDimens.vanishRate),
                )
                CardCaption(
                    text = stringResource(Res.string.identity_ssn_label),
                    modifier = Modifier
                        .layoutId(CardSlot.SsnCaption)
                        .vanishOnCollapse(collapseProgress, IdentityDimens.vanishRate),
                )
                // The one figure that survives the fold, so the bar still identifies the holder.
                NumericText(
                    text = ssn.toPersianDigits(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    modifier = Modifier
                        .layoutId(CardSlot.SsnNumber)
                        .shrinkOnCollapse(
                            progress = collapseProgress,
                            minScale = IdentityDimens.ssnCollapsedScale,
                            rtl = !rtl,
                        ),
                )
                CardCaption(
                    text = stringResource(Res.string.identity_field_national_code),
                    modifier = Modifier
                        .layoutId(CardSlot.CodeCaption)
                        .vanishOnCollapse(collapseProgress, IdentityDimens.vanishRate),
                )
                NumericText(
                    text = nationalId.toPersianDigits(),
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    modifier = Modifier
                        .layoutId(CardSlot.CodeNumber)
                        .vanishOnCollapse(collapseProgress, IdentityDimens.vanishRate),
                )
                CardCaptionedNumber(
                    caption = stringResource(Res.string.identity_field_birth_date),
                    number = dateOfBirth.toPersianDigits(),
                    alignment = Alignment.End,
                    modifier = Modifier
                        .layoutId(CardSlot.DateBlock)
                        .vanishOnCollapse(collapseProgress, IdentityDimens.vanishRate),
                )
            },
        ) { measurables, constraints ->
            val width = constraints.maxWidth
            val pad = Spacing.lg.roundToPx()
            val gap = Spacing.sm.roundToPx()
            val inner = Constraints(maxWidth = (width - 2 * pad).coerceAtLeast(0))
            val avatarPx = IdentityDimens.avatarSize.roundToPx()

            val brand = measurables.slot(CardSlot.Brand).measure(inner)
            val chip = measurables.slot(CardSlot.Chip).measure(Constraints())
            val avatar = measurables.slot(CardSlot.Avatar)
                .measure(Constraints.fixed(avatarPx, avatarPx))
            val ssnCaption = measurables.slot(CardSlot.SsnCaption).measure(inner)
            val ssnNumber = measurables.slot(CardSlot.SsnNumber).measure(inner)
            val codeCaption = measurables.slot(CardSlot.CodeCaption).measure(inner)
            val codeNumber = measurables.slot(CardSlot.CodeNumber).measure(inner)
            val dateBlock = measurables.slot(CardSlot.DateBlock).measure(inner)
            // The name and lineage share the strip between the start edge and the avatar.
            val textWidth = (width - 2 * pad - avatarPx - gap).coerceAtLeast(0)
            val textC = Constraints(maxWidth = textWidth)
            val name = measurables.slot(CardSlot.Name).measure(textC)
            val lineage = measurables.slot(CardSlot.Lineage).measure(textC)

            val expandedH = IdentityDimens.cardExpandedHeight.roundToPx()
            val barH = IdentityDimens.cardCollapsedHeight.roundToPx()
            val t = Easing.standard.transform(collapseProgress())

            // Expanded slots. placeRelative measures x from the start edge — the right in RTL — so
            // the avatar sitting at the far end lands on the left, as the design shows.
            val identityTop = IdentityDimens.identityTop.roundToPx()
            val avatarEndX = width - pad - avatarPx
            val nameBlockH = name.height + lineage.height
            val nameY = identityTop + (avatarPx - nameBlockH) / 2

            // The foot band's contents are centred in the band rather than measured off the card's
            // bottom edge, so the band and what it carries can never drift apart.
            val bandTop = expandedH - IdentityDimens.footerBandHeight.roundToPx()
            val codeBlockH = codeCaption.height + codeNumber.height
            val codeTop = bandTop + (IdentityDimens.footerBandHeight.roundToPx() - codeBlockH) / 2
            val dateTop = bandTop + (IdentityDimens.footerBandHeight.roundToPx() - dateBlock.height) / 2

            // Collapsed slots: avatar, name and social-security number in one row inside the
            // bar. The avatar keeps the card's own start inset, so the folded bar lines up with
            // the open card's rhythm instead of floating in from nowhere.
            val collapsedAvatar = IdentityDimens.avatarCollapsedSize.roundToPx()
            val avatarBarX = pad
            val avatarBarY = (barH - collapsedAvatar) / 2
            val nameBarX = avatarBarX + collapsedAvatar + gap
            val nameBarY = (barH - name.height) / 2
            // Positioned by the placeable's full size, not its scaled size: placeRelative moves
            // the whole placeable, and the shrink only changes what is painted inside it. Sizing
            // this from the scaled width walks the number off the far edge of the bar.
            val ssnBarX = width - pad - ssnNumber.width
            val ssnBarY = (barH - ssnNumber.height) / 2

            layout(width, lerp(expandedH, barH, t)) {
                // Pieces that belong only to the open card stay put and fade.
                brand.placeRelative(pad, IdentityDimens.brandTop.roundToPx())
                chip.placeRelative(width - pad - chip.width, IdentityDimens.brandTop.roundToPx())
                lineage.placeRelative(pad, nameY + name.height)
                ssnCaption.placeRelative(pad, IdentityDimens.ssnTop.roundToPx())
                codeCaption.placeRelative(pad, codeTop)
                codeNumber.placeRelative(pad, codeTop + codeCaption.height)
                dateBlock.placeRelative(width - pad - dateBlock.width, dateTop)

                // Traveling pieces glide from their card slot to their slot in the bar.
                avatar.placeRelativeWithLayer(
                    lerp(avatarEndX, avatarBarX, t),
                    lerp(identityTop, avatarBarY, t),
                ) {
                    val scale = lerp(1f, collapsedAvatar.toFloat() / avatarPx, t)
                    scaleX = scale
                    scaleY = scale
                    // placeRelative anchors the start edge, so the scale must anchor there too:
                    // anchoring the layout-left in RTL walks the shrinking avatar into the name.
                    transformOrigin = TransformOrigin(if (rtl) 1f else 0f, 0f)
                }
                name.placeRelative(lerp(pad, nameBarX, t), lerp(nameY, nameBarY, t))
                ssnNumber.placeRelative(
                    lerp(pad, ssnBarX, t),
                    lerp(IdentityDimens.ssnTop.roundToPx() + ssnCaption.height, ssnBarY, t),
                )
            }
        }
    }
}

/** The card's pieces, addressed by name rather than by index into the measurables. */
private enum class CardSlot {
    Brand, Chip, Avatar, Name, Lineage, SsnCaption, SsnNumber, CodeCaption, CodeNumber, DateBlock
}

private fun List<Measurable>.slot(id: CardSlot): Measurable = first { it.layoutId == id }

/**
 * The holder's photo, as a pane of glass over the card rather than a light tile cut into it.
 *
 * Falls back to the person glyph only when there is no [photo]; it is the same picture the
 * profile screen shows.
 */
@Composable
private fun CardAvatar(photo: String?, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(IdentityDimens.avatarCorner)
    val innerShape = RoundedCornerShape(IdentityDimens.avatarInnerCorner)
    Box(
        modifier = modifier
            .size(IdentityDimens.avatarSize)
            .background(Color.White.copy(alpha = GLASS_FILL), shape)
            .border(1.dp, Color.White.copy(alpha = GLASS), shape)
            .padding(IdentityDimens.avatarRim),
        contentAlignment = Alignment.Center,
    ) {
        if (photo.isNullOrBlank()) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_user),
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.fillMaxSize().clip(innerShape),
            )
        } else {
            LoadAsyncImage(
                model = photo,
                modifier = Modifier.fillMaxSize().clip(innerShape),
            )
        }
    }
}

/** The small muted caption that sits above every figure on the card. */
@Composable
private fun CardCaption(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = Color.White.copy(alpha = SUBDUED),
        modifier = modifier,
    )
}

/** A caption above a figure, for the blocks that move as one. */
@Composable
private fun CardCaptionedNumber(
    caption: String,
    number: String,
    modifier: Modifier = Modifier,
    alignment: Alignment.Horizontal = Alignment.Start,
) {
    Column(modifier = modifier, horizontalAlignment = alignment) {
        CardCaption(text = caption)
        NumericText(
            text = number,
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
        )
    }
}

@Composable
private fun CardBrandRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Box(
            modifier = Modifier
                .size(IdentityDimens.brandTileSize)
                .background(
                    Color.White.copy(alpha = GLASS),
                    RoundedCornerShape(CornerRadius.avatarTile),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_ejtemaei_logo),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(IdentityDimens.brandIconSize),
            )
        }
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
            ) {
                Text(
                    text = stringResource(Res.string.identity_card_org),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_verified),
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(IdentityDimens.verifiedIconSize),
                )
            }
            CardCaption(text = stringResource(Res.string.identity_card_type))
        }
    }
}

/** The gold contact plate every physical insurance card carries. */
@Composable
private fun ContactChip(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(width = IdentityDimens.chipWidth, height = IdentityDimens.chipHeight)
            .clip(RoundedCornerShape(IdentityDimens.chipCorner))
            .background(ChipBackground)
            .drawBehind {
                val stroke = 0.5.dp.toPx()
                listOf(0.35f, 0.65f).forEach { x ->
                    drawLine(
                        TaminIdentityChipTrace,
                        Offset(size.width * x, 0f),
                        Offset(size.width * x, size.height),
                        stroke,
                    )
                }
                drawLine(
                    TaminIdentityChipTrace,
                    Offset(0f, size.height * 0.45f),
                    Offset(size.width, size.height * 0.45f),
                    stroke,
                )
            },
    )
}

/**
 * A darker band across the card's foot, ruled off by a hairline, fading out as the card folds so
 * the collapsed bar reads as one flat surface.
 */
private fun Modifier.footBand(progress: () -> Float): Modifier = drawBehind {
    val fade = (1f - progress() * IdentityDimens.vanishRate).coerceIn(0f, 1f)
    if (fade <= 0f) return@drawBehind
    val bandHeight = IdentityDimens.footerBandHeight.toPx().coerceAtMost(size.height)
    val top = size.height - bandHeight
    drawRect(
        color = Color.Black.copy(alpha = BAND_TINT * fade),
        topLeft = Offset(0f, top),
        size = Size(size.width, bandHeight),
    )
    drawRect(
        color = Color.White.copy(alpha = HAIRLINE * fade),
        topLeft = Offset(0f, top),
        size = Size(size.width, 1.dp.toPx()),
    )
}

/**
 * The laminated look: a diagonal band of light across the face, a lit top edge, and one soft
 * circle behind it — a card catching a highlight rather than the treatment cards' flat colour
 * blobs, which is what tells the two apart at a glance.
 */
private fun Modifier.cardSheen(rtl: Boolean): Modifier = drawBehind {
    val nearEdge = size.width * if (rtl) SHEEN_NEAR else SHEEN_FAR
    drawCircle(
        color = Color.White.copy(alpha = 0.045f),
        radius = size.width * SHEEN_OUTER,
        center = Offset(nearEdge, 0f),
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.035f),
        radius = size.width * SHEEN_INNER,
        center = Offset(nearEdge, 0f),
    )
    // Corner to corner by default, so the band follows the card without measuring it — and
    // without building a brush on every frame of the fold.
    drawRect(brush = CardShine)
    drawRect(
        color = Color.White.copy(alpha = TOP_EDGE),
        size = Size(size.width, 1.dp.toPx()),
    )
}
