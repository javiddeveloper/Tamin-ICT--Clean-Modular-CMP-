package com.tamin.taminhamrah.ui.components.document

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.ui.components.LoadAsyncImage
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.LiquidWaveProgressBar
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_place
import taminx.core.core_ui.ic_tamin_check
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ic_warning
import taminx.core.core_ui.orotez_protez_document_optional
import taminx.core.core_ui.orotez_protez_document_required

enum class TaminDocumentUploadState {
    Empty, Uploading, Uploaded, Failed
}

@Composable
fun TaminDocumentUploadCard(
    title: String,
    state: TaminDocumentUploadState,
    modifier: Modifier = Modifier,
    statusText: String? = null,
    isRequired: Boolean? = null,
    thumbnailBase64: String? = null,
    onCardClick: (() -> Unit)? = null,
    onPreviewClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.card)
    var fillAnimationComplete by remember(title, state) {
        mutableStateOf(state == TaminDocumentUploadState.Uploaded)
    }

    LaunchedEffect(title, state == TaminDocumentUploadState.Uploading) {
        if (state == TaminDocumentUploadState.Uploading) {
            fillAnimationComplete = false
        }
    }

    val displayState = if (state == TaminDocumentUploadState.Uploaded && !fillAnimationComplete) {
        TaminDocumentUploadState.Uploading
    } else {
        state
    }

    val rowModifier = when (displayState) {
        TaminDocumentUploadState.Empty -> modifier
            .fillMaxWidth()
            .drawBehind {
                drawRoundRect(
                    color = colors.border,
                    style = Stroke(
                        width = Thickness.medium.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(CornerRadius.card.toPx()),
                )
            }
            .clip(shape)
            .then(if (onCardClick != null) Modifier.clickable(onClick = onCardClick) else Modifier)
            .background(colors.bgSurface)
            .padding(Spacing.lg)

        TaminDocumentUploadState.Uploaded -> modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.greenBg)
            .border(Thickness.border, colors.greenBorder, shape)
            .then(if (onCardClick != null) Modifier.clickable(onClick = onCardClick) else Modifier)
            .padding(Spacing.lg)

        TaminDocumentUploadState.Failed -> modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.dangerBg)
            .border(Thickness.border, colors.dangerBorder, shape)
            .then(if (onCardClick != null) Modifier.clickable(onClick = onCardClick) else Modifier)
            .padding(Spacing.lg)

        TaminDocumentUploadState.Uploading -> modifier
            .fillMaxWidth()
            .clip(shape)
            .border(Thickness.border, colors.border, shape)
            .then(if (onCardClick != null) Modifier.clickable(onClick = onCardClick) else Modifier)
            .padding(Spacing.lg)
    }

    Box {
        if (displayState == TaminDocumentUploadState.Uploading) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(shape)
                    .background(colors.bgSurface),
            )
            LiquidWaveProgressBar(
                modifier = Modifier
                    .matchParentSize()
                    .clip(shape),
                onFillComplete = { fillAnimationComplete = true },
            )
        }

        Column(modifier = rowModifier) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    TaminDocumentIconTile(
                        state = displayState,
                        thumbnailBase64 = thumbnailBase64,
                        onPreviewClick = onPreviewClick
                    )

                    Column {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = colors.textPrimary,
                            )
                            if (isRequired != null) {
                                StatusPill(
                                    text = stringResource(
                                        if (isRequired) {
                                            Res.string.orotez_protez_document_required
                                        } else {
                                            Res.string.orotez_protez_document_optional
                                        }
                                    ),
                                    containerColor = if (isRequired) colors.blueBg else colors.bgPage,
                                    contentColor = if (isRequired) colors.blueText else colors.textMuted,
                                )
                            }
                        }

                        if (statusText != null) {
                            Spacer(Modifier.height(Spacing.xxs))
                            Text(
                                text = statusText,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (displayState == TaminDocumentUploadState.Failed) {
                                    colors.dangerText
                                } else {
                                    colors.textMuted
                                },
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (onDeleteClick != null) {
                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier.size(IconSize.large),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null,
                                tint = colors.dangerText,
                                modifier = Modifier.size(IconSize.medium),
                            )
                        }
                    } else if (displayState != TaminDocumentUploadState.Uploading && onCardClick != null) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                            contentDescription = null,
                            tint = colors.textMuted,
                            modifier = Modifier.size(IconSize.small),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TaminDocumentIconTile(
    state: TaminDocumentUploadState,
    thumbnailBase64: String?,
    onPreviewClick: (() -> Unit)?
) {
    val colors = LocalTaminColors.current

    val tileColor = when {
        thumbnailBase64 != null -> if (state == TaminDocumentUploadState.Uploaded) colors.blueBg else colors.chipBg
        state == TaminDocumentUploadState.Uploaded -> colors.greenText
        state == TaminDocumentUploadState.Failed -> colors.dangerText
        else -> colors.chipBg
    }

    Box(
        modifier = Modifier
            .size(IconSize.xlarge)
            .clip(RoundedCornerShape(CornerRadius.iconTile))
            .background(tileColor)
            .then(
                if (thumbnailBase64 != null && onPreviewClick != null && state == TaminDocumentUploadState.Uploaded) {
                    Modifier.clickable(onClick = onPreviewClick)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (thumbnailBase64 != null && state == TaminDocumentUploadState.Uploaded) {
            LoadAsyncImage(
                model = thumbnailBase64,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            when (state) {
                TaminDocumentUploadState.Uploading -> Icon(
                    imageVector = vectorResource(Res.drawable.ic_place),
                    contentDescription = null,
                    tint = colors.blueText,
                    modifier = Modifier.size(IconSize.banner),
                )
                TaminDocumentUploadState.Uploaded -> Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_check),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(IconSize.banner),
                )
                TaminDocumentUploadState.Failed -> Icon(
                    imageVector = vectorResource(Res.drawable.ic_warning),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(IconSize.banner),
                )
                TaminDocumentUploadState.Empty -> Icon(
                    imageVector = Icons.Outlined.Image,
                    contentDescription = null,
                    tint = colors.textMuted,
                    modifier = Modifier.size(IconSize.medium),
                )
            }
        }
    }
}


