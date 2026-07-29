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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.tamin.taminhamrah.feature.profile.ui.identity.IdentityDimens
import com.tamin.taminhamrah.ui.components.LoadAsyncImage
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.shrinkOnCollapse
import com.tamin.taminhamrah.ui.components.vanishOnCollapse
import com.tamin.taminhamrah.ui.theme.Easing
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminIdentityAvatarGlass
import com.tamin.taminhamrah.ui.theme.TaminIdentityCardGradient
import com.tamin.taminhamrah.ui.theme.TaminIdentityCardMuted
import com.tamin.taminhamrah.ui.theme.TaminIdentityCardShadow
import com.tamin.taminhamrah.ui.theme.TaminIdentityCardShine
import com.tamin.taminhamrah.ui.theme.TaminIdentityChipGradient
import com.tamin.taminhamrah.ui.theme.TaminIdentityChipTrace
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_shield_check
import taminx.core.core_ui.ic_tamin_user
import taminx.core.core_ui.identity_card_org
import taminx.core.core_ui.identity_card_type
import taminx.core.core_ui.identity_field_birth_date
import taminx.core.core_ui.identity_field_national_code
import taminx.core.core_ui.identity_lineage
import taminx.core.core_ui.identity_ssn_label
import kotlin.math.roundToInt

/**
 * The insured-person card, and the morph that folds it into the app bar as the page scrolls.
 *
 * Every measurement here comes from the design's SVG export; see [IdentityDimens]. Every animated
 * value is read inside a `layout {}` / `graphicsLayer {}` / `drawBehind {}` lambda, so a frame of
 * the fold costs a re-layout or a redraw and never a recomposition.
 */


/**
 * One insured person's card: organization branding, their photo and name, the social-security
 * number, and a footer carrying the national code and date of birth.
 *
 * As it folds, the photo, the name and the social-security number travel up into the app bar, so
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
    val shape = RoundedCornerShape(IdentityDimens.cardCorner)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = IdentityDimens.cardShadow,
                shape = shape,
                ambientColor = TaminIdentityCardShadow,
                spotColor = TaminIdentityCardShadow,
            )
            .clip(shape)
            .background(TaminIdentityCardGradient)
            .cardSheen(rtl)
            .footerRule(collapseProgress),
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
                    color = TaminIdentityCardMuted,
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
                CardValue(
                    text = nationalId.toPersianDigits(),
                    modifier = Modifier
                        .layoutId(CardSlot.CodeValue)
                        .vanishOnCollapse(collapseProgress, IdentityDimens.vanishRate),
                )
                CardCaption(
                    text = stringResource(Res.string.identity_field_birth_date),
                    modifier = Modifier
                        .layoutId(CardSlot.DateCaption)
                        .vanishOnCollapse(collapseProgress, IdentityDimens.vanishRate),
                )
                CardValue(
                    text = dateOfBirth.toPersianDigits(),
                    modifier = Modifier
                        .layoutId(CardSlot.DateValue)
                        .vanishOnCollapse(collapseProgress, IdentityDimens.vanishRate),
                )
            },
        ) { measurables, constraints ->
            val width = constraints.maxWidth
            // Every dp below is an artboard measurement, so scale it to the width we were
            // actually handed. `scaled` is the only way a design dp reaches this layout.
            val designScale = width / IdentityDimens.designCardWidth.toPx()
            fun scaled(value: Dp) = (value.toPx() * designScale).roundToInt()

            val pad = scaled(IdentityDimens.cardPadding)
            val inner = Constraints(maxWidth = (width - 2 * pad).coerceAtLeast(0))
            val avatarW = scaled(IdentityDimens.avatarWidth)
            val avatarH = scaled(IdentityDimens.avatarHeight)

            val brand = measurables.slot(CardSlot.Brand).measure(inner)
            val chip = measurables.slot(CardSlot.Chip).measure(Constraints())
            val avatar = measurables.slot(CardSlot.Avatar)
                .measure(Constraints.fixed(avatarW, avatarH))
            val ssnCaption = measurables.slot(CardSlot.SsnCaption).measure(inner)
            val ssnNumber = measurables.slot(CardSlot.SsnNumber).measure(inner)
            val codeCaption = measurables.slot(CardSlot.CodeCaption).measure(inner)
            val codeValue = measurables.slot(CardSlot.CodeValue).measure(inner)
            val dateCaption = measurables.slot(CardSlot.DateCaption).measure(inner)
            val dateValue = measurables.slot(CardSlot.DateValue).measure(inner)

            // The name and lineage share the strip beside the photo, which sits at the start.
            val textStart = pad + avatarW + scaled(IdentityDimens.avatarNameGap)
            val textC = Constraints(maxWidth = (width - pad - textStart).coerceAtLeast(0))
            val name = measurables.slot(CardSlot.Name).measure(textC)
            val lineage = measurables.slot(CardSlot.Lineage).measure(textC)

            val expandedH = scaled(IdentityDimens.cardExpandedHeight)
            // The folded bar is chrome, not artboard: it keeps its height on every screen.
            val barH = IdentityDimens.cardCollapsedHeight.roundToPx()
            val t = Easing.standard.transform(collapseProgress())

            // Expanded slots. placeRelative measures x from the start edge — the right in RTL —
            // so the photo at `pad` lands on the right, as the export shows.
            val avatarTop = scaled(IdentityDimens.avatarTop)
            val nameTop = avatarTop + (avatarH - name.height - lineage.height) / 2

            // Collapsed slots. The photo keeps its start inset and only rises and shrinks; the
            // name follows it, and the number crosses to the far end of the bar.
            val collapsedH = IdentityDimens.avatarCollapsedHeight.roundToPx()
            val collapsedW = (avatarW * collapsedH.toFloat() / avatarH).toInt()
            val avatarBarTop = (barH - collapsedH) / 2
            val nameBarX = pad + collapsedW + scaled(IdentityDimens.avatarNameGap)
            val nameBarTop = (barH - name.height) / 2
            // Positioned by the placeable's full size, not its scaled size: placeRelative moves
            // the whole placeable and the shrink only changes what is painted inside it.
            val ssnBarX = width - pad - ssnNumber.width
            val ssnBarTop = (barH - ssnNumber.height) / 2

            layout(width, lerp(expandedH, barH, t)) {
                // Pieces that belong only to the open card stay put and fade.
                brand.placeRelative(pad, scaled(IdentityDimens.brandTop))
                chip.placeRelative(width - pad - chip.width, scaled(IdentityDimens.chipTop))
                lineage.placeRelative(textStart, nameTop + name.height)
                ssnCaption.placeRelative(pad, scaled(IdentityDimens.ssnCaptionTop))
                codeCaption.placeRelative(pad, scaled(IdentityDimens.footerCaptionTop))
                codeValue.placeRelative(pad, scaled(IdentityDimens.footerValueTop))
                dateCaption.placeRelative(
                    width - pad - dateCaption.width,
                    scaled(IdentityDimens.footerCaptionTop),
                )
                dateValue.placeRelative(
                    width - pad - dateValue.width,
                    scaled(IdentityDimens.footerValueTop),
                )

                // Traveling pieces glide from their card slot to their slot in the bar.
                avatar.placeRelativeWithLayer(pad, lerp(avatarTop, avatarBarTop, t)) {
                    val scale = lerp(1f, collapsedH.toFloat() / avatarH, t)
                    scaleX = scale
                    scaleY = scale
                    // placeRelative anchors the start edge, so the scale must anchor there too:
                    // anchoring the layout-left in RTL walks the shrinking photo into the name.
                    transformOrigin = TransformOrigin(if (rtl) 1f else 0f, 0f)
                }
                name.placeRelative(lerp(textStart, nameBarX, t), lerp(nameTop, nameBarTop, t))
                ssnNumber.placeRelative(
                    lerp(pad, ssnBarX, t),
                    lerp(scaled(IdentityDimens.ssnNumberTop), ssnBarTop, t),
                )
            }
        }
    }
}

/** The card's pieces, addressed by name rather than by index into the measurables. */
private enum class CardSlot {
    Brand, Chip, Avatar, Name, Lineage,
    SsnCaption, SsnNumber, CodeCaption, CodeValue, DateCaption, DateValue,
}

private fun List<Measurable>.slot(id: CardSlot): Measurable = first { it.layoutId == id }

/** The holder's photo, as a pane of glass over the card rather than a tile cut into it. */
@Composable
private fun CardAvatar(photo: String?, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(IdentityDimens.avatarCorner)
    Box(
        modifier = modifier
            .background(TaminIdentityAvatarGlass, shape)
            .border(1.dp, Color.White.copy(alpha = IdentityDimens.avatarBorderAlpha), shape)
            .clip(shape),
        contentAlignment = Alignment.Center,
    ) {
        if (photo.isNullOrBlank()) {
            AvatarGlyph()
        } else {
            // The shared placeholder is gray-on-light and would sit oddly on the blue face, so a
            // photo that fails to load falls back to the same glyph as no photo at all.
            LoadAsyncImage(
                model = photo,
                modifier = Modifier.fillMaxSize(),
                errorContent = { AvatarGlyph() },
            )
        }
    }
}

/** Stands in for the holder's photo: none on file, or one that would not load. */
@Composable
private fun AvatarGlyph() {
    Icon(
        imageVector = vectorResource(Res.drawable.ic_tamin_user),
        contentDescription = null,
        tint = Color.White.copy(alpha = IdentityDimens.avatarGlyphAlpha),
        modifier = Modifier.fillMaxSize().padding(Spacing.sm),
    )
}

/** The small light-blue caption above every figure on the card. */
@Composable
private fun CardCaption(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = TaminIdentityCardMuted,
        modifier = modifier,
    )
}

/** A figure in the card's footer. */
@Composable
private fun CardValue(text: String, modifier: Modifier = Modifier) {
    NumericText(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = Color.White,
        modifier = modifier,
    )
}

@Composable
private fun CardBrandRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        val tileShape = RoundedCornerShape(IdentityDimens.brandTileCorner)
        Box(
            modifier = Modifier
                .size(IdentityDimens.brandTileSize)
                .background(Color.White.copy(alpha = IdentityDimens.brandTileFillAlpha), tileShape)
                .border(1.dp, Color.White.copy(alpha = IdentityDimens.brandTileBorderAlpha), tileShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_shield_check),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(IdentityDimens.brandIconSize),
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = stringResource(Res.string.identity_card_org),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
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
            .background(TaminIdentityChipGradient)
            .drawBehind {
                // One vertical trace and one horizontal, as the export draws them.
                val stroke = 1.dp.toPx()
                drawLine(
                    TaminIdentityChipTrace,
                    Offset(size.width * 0.49f, size.height * 0.115f),
                    Offset(size.width * 0.49f, size.height * 0.885f),
                    stroke,
                )
                drawLine(
                    TaminIdentityChipTrace,
                    Offset(size.width * 0.086f, size.height * 0.48f),
                    Offset(size.width * 0.914f, size.height * 0.48f),
                    stroke,
                )
            },
    )
}

/**
 * The hairline above the footer, inset to the card's own padding, fading out as the card folds so
 * the collapsed bar reads as one flat surface.
 */
private fun Modifier.footerRule(progress: () -> Float): Modifier = drawBehind {
    val fade = (1f - progress() * IdentityDimens.vanishRate).coerceIn(0f, 1f)
    if (fade <= 0f) return@drawBehind
    val pad = IdentityDimens.cardPadding.toPx()
    val top = IdentityDimens.footerRuleTop.toPx()
    if (top >= size.height) return@drawBehind
    drawRect(
        color = Color.White.copy(alpha = IdentityDimens.footerRuleAlpha * fade),
        topLeft = Offset(pad, top),
        size = Size(size.width - 2 * pad, 1.dp.toPx()),
    )
}

/**
 * The laminated look: thin arcs of light and one narrow diagonal streak, plus the lit top edge.
 *
 * Rings, not filled circles — the export strokes them at a single pixel, which is what keeps the
 * card reading as glass rather than as colored blobs painted over the gradient.
 */
private fun Modifier.cardSheen(rtl: Boolean): Modifier = drawBehind {
    val stroke = Stroke(1.dp.toPx())
    val nearX = if (rtl) size.width - IdentityDimens.ringNearInset.toPx() else IdentityDimens.ringNearInset.toPx()
    val farX = if (rtl) IdentityDimens.ringFarInset.toPx() else size.width - IdentityDimens.ringFarInset.toPx()

    drawCircle(
        color = Color.White.copy(alpha = IdentityDimens.ringOuterAlpha),
        radius = IdentityDimens.ringOuterRadius.toPx(),
        center = Offset(nearX, IdentityDimens.ringNearTop.toPx()),
        style = stroke,
    )
    drawCircle(
        color = Color.White.copy(alpha = IdentityDimens.ringInnerAlpha),
        radius = IdentityDimens.ringInnerRadius.toPx(),
        center = Offset(nearX, IdentityDimens.ringNearTop.toPx()),
        style = stroke,
    )
    drawCircle(
        color = Color.White.copy(alpha = IdentityDimens.ringFootAlpha),
        radius = IdentityDimens.ringFootRadius.toPx(),
        center = Offset(farX, size.height),
        style = stroke,
    )

    drawRect(brush = TaminIdentityCardShine)
    drawRect(
        color = Color.White.copy(alpha = IdentityDimens.topEdgeAlpha),
        size = Size(size.width, 1.dp.toPx()),
    )
}
