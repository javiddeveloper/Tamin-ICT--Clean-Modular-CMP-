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
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_user
import taminx.core.core_ui.identity_field_birth_date
import taminx.core.core_ui.identity_field_father_name
import taminx.core.core_ui.identity_field_first_name
import taminx.core.core_ui.identity_field_last_name
import taminx.core.core_ui.identity_field_national_code
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
    firstName: String,
    lastName: String,
    fullName: String,
    fatherName: String,
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
                CardNameRow(
                    fullName = fullName,
                    collapseProgress = collapseProgress,
                    modifier = Modifier.layoutId(CardSlot.Name),
                )
                CardTopInfo(
                    lastName = lastName,
                    fatherName = fatherName,
                    dateOfBirth = dateOfBirth,
                    modifier = Modifier
                        .layoutId(CardSlot.TopInfo)
                        .vanishOnCollapse(collapseProgress, IdentityDimens.VANISH_RATE),
                )
                CardAvatar(
                    photo = photo,
                    modifier = Modifier.layoutId(CardSlot.Avatar),
                )
                CardNationalId(
                    nationalId = nationalId,
                    modifier = Modifier
                        .layoutId(CardSlot.NationalId)
                        .vanishOnCollapse(collapseProgress, IdentityDimens.VANISH_RATE),
                )
                CardSsn(
                    ssn = ssn,
                    collapseProgress = collapseProgress,
                    modifier = Modifier.layoutId(CardSlot.SsnNumber),
                )
            },
        ) { measurables, constraints ->
            val width = constraints.maxWidth
            val designScale =
                width / IdentityDimens.designCardWidth.toPx() * IdentityDimens.cardScale
            fun scaled(value: Dp) = (value.toPx() * designScale).roundToInt()

            val pad = scaled(IdentityDimens.cardPadding)
            val inner = Constraints(maxWidth = (width - 2 * pad).coerceAtLeast(0))
            val avatarW = scaled(IdentityDimens.avatarWidth)
            val avatarH = scaled(IdentityDimens.avatarHeight)
            val gap = scaled(IdentityDimens.avatarNameGap)

            val textC = Constraints(maxWidth = (width - 2 * pad - avatarW - gap).coerceAtLeast(0))

            val name = measurables.slot(CardSlot.Name).measure(textC)
            val topInfo = measurables.slot(CardSlot.TopInfo).measure(textC)
            val avatar = measurables.slot(CardSlot.Avatar).measure(Constraints.fixed(avatarW, avatarH))
            val nationalIdPlaceable = measurables.slot(CardSlot.NationalId).measure(inner)
            val ssnPlaceable = measurables.slot(CardSlot.SsnNumber).measure(inner)

            val expandedH = scaled(IdentityDimens.cardExpandedHeight)
            val barH = IdentityDimens.cardCollapsedHeight.roundToPx()
            val t = Easing.standard.transform(collapseProgress())

            // Expanded positions
            val expAvatarX = if (rtl) width - pad - avatarW else pad
            val expAvatarY = scaled(IdentityDimens.avatarTop)
            val expTopInfoX = if (rtl) pad else avatarW + gap + pad
            val expNameY = scaled(IdentityDimens.topInfoTop)
            val expTopInfoY = expNameY + name.height + 4.dp.roundToPx()
            val expFooterY = scaled(IdentityDimens.footerTop)
            val expNationalIdX = if (rtl) pad else width - pad - nationalIdPlaceable.width
            val expSsnX = if (rtl) width - pad - ssnPlaceable.width else pad

            // Collapsed positions
            val avatarScale = IdentityDimens.avatarCollapsedHeight.toPx() / avatarH
            val avatarHCollapsed = IdentityDimens.avatarCollapsedHeight.roundToPx()
            val avatarWCollapsed = (avatarW * avatarScale).toInt()
            val collAvatarY = (barH - avatarHCollapsed) / 2
            val collAvatarX = if (rtl) pad else width - pad - avatarWCollapsed
            val collNameX = if (rtl) pad + avatarWCollapsed + gap else width - pad - avatarWCollapsed - gap - name.width
            val collNameY = (barH - name.height) / 2
            val collSsnX = if (rtl) width - pad - ssnPlaceable.width else pad
            val collSsnY = (barH - ssnPlaceable.height) / 2

            layout(width, lerp(expandedH, barH, t)) {
                // Secondary fields fade out smoothly
                topInfo.placeRelativeWithLayer(expTopInfoX, expTopInfoY) {
                    alpha = (1f - t * 2f).coerceIn(0f, 1f)
                }
                nationalIdPlaceable.placeRelativeWithLayer(expNationalIdX, expFooterY) {
                    alpha = (1f - t * 2f).coerceIn(0f, 1f)
                }

                // Avatar glides and scales
                avatar.placeRelativeWithLayer(
                    lerp(expAvatarX, collAvatarX, t),
                    lerp(expAvatarY, collAvatarY, t),
                ) {
                    val s = lerp(1f, avatarScale, t)
                    scaleX = s
                    scaleY = s
                    transformOrigin = TransformOrigin(if (rtl) 1f else 0f, 0f)
                }

                // Name glides smoothly from top-right to collapsed position
                name.placeRelative(
                    lerp(expTopInfoX, collNameX, t),
                    lerp(expNameY, collNameY, t),
                )

                // SSN glides smoothly from bottom-left to collapsed position
                ssnPlaceable.placeRelative(
                    lerp(expSsnX, collSsnX, t),
                    lerp(expFooterY, collSsnY, t),
                )
            }
        }
    }
}

/** The card's pieces, addressed by name rather than by index into the measurables. */
private enum class CardSlot {
    Name, TopInfo, Avatar, NationalId, SsnNumber
}

private fun List<Measurable>.slot(id: CardSlot): Measurable = first { it.layoutId == id }

/** The holder's photo, as a pane of glass over the card rather than a tile cut into it. */
@Composable
private fun CardAvatar(photo: String?, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(IdentityDimens.avatarCorner)
    Box(
        modifier = modifier
            .background(TaminIdentityAvatarGlass, shape)
            .border(1.dp, Color.White.copy(alpha = 0.25f), shape)
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
        tint = Color.White.copy(alpha = IdentityDimens.AVATAR_GLYPH_ALPHA),
        modifier = Modifier.fillMaxSize().padding(Spacing.sm),
    )
}

@Composable
private fun CardNameRow(
    fullName: String,
    collapseProgress: () -> Float,
    modifier: Modifier = Modifier,
) {
    val fade = (1f - collapseProgress() * 2f).coerceIn(0f, 1f)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (fade > 0f) {
            Text(
                text = stringResource(Res.string.identity_field_first_name),
                style = MaterialTheme.typography.labelSmall,
                color = TaminIdentityCardMuted.copy(alpha = fade),
            )
        }
        Text(
            text = fullName,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun CardTopInfo(
    lastName: String,
    fatherName: String,
    dateOfBirth: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        CardFieldRow(
            label = stringResource(Res.string.identity_field_last_name),
            value = lastName,
        )
        CardFieldRow(
            label = stringResource(Res.string.identity_field_father_name),
            value = fatherName,
        )
        CardFieldRow(
            label = stringResource(Res.string.identity_field_birth_date),
            value = dateOfBirth.toPersianDigits(),
        )
    }
}

@Composable
private fun CardFieldRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TaminIdentityCardMuted,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun CardNationalId(
    nationalId: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = stringResource(Res.string.identity_field_national_code),
            style = MaterialTheme.typography.labelSmall,
            color = TaminIdentityCardMuted,
        )
        NumericText(
            text = nationalId.toPersianDigits(),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
        )
    }
}

@Composable
private fun CardSsn(
    ssn: String,
    collapseProgress: () -> Float,
    modifier: Modifier = Modifier,
) {
    val fade = (1f - collapseProgress() * 2f).coerceIn(0f, 1f)
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
    ) {
        if (fade > 0f) {
            Text(
                text = stringResource(Res.string.identity_ssn_label),
                style = MaterialTheme.typography.labelSmall,
                color = TaminIdentityCardMuted.copy(alpha = fade),
            )
        }
        NumericText(
            text = ssn.toPersianDigits(),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
        )
    }
}

/**
 * The hairline above the footer, inset to the card's own padding, fading out as the card folds so
 * the collapsed bar reads as one flat surface.
 */
private fun Modifier.footerRule(progress: () -> Float): Modifier = drawBehind {
    val fade = (1f - progress() * IdentityDimens.VANISH_RATE).coerceIn(0f, 1f)
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
 * The laminated look: thin arcs of light and the lit top edge.
 */
private fun Modifier.cardSheen(rtl: Boolean): Modifier = drawBehind {
    val stroke = Stroke(1.dp.toPx())
    val topArcX = if (rtl) size.width * 0.1f else size.width * 0.9f
    val bottomArcX = if (rtl) size.width * 0.9f else size.width * 0.1f

    drawCircle(
        color = Color.White.copy(alpha = 0.05f),
        radius = size.width * 0.6f,
        center = Offset(topArcX, size.height * -0.1f),
        style = stroke,
    )

    drawCircle(
        color = Color.White.copy(alpha = 0.04f),
        radius = size.width * 0.5f,
        center = Offset(bottomArcX, size.height * 1.1f),
        style = stroke,
    )

    drawRect(brush = TaminIdentityCardShine)
    drawRect(
        color = Color.White.copy(alpha = IdentityDimens.topEdgeAlpha),
        size = Size(size.width, 1.dp.toPx()),
    )
}
