package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.shimmer

@Composable
fun UserAvatar(
    model: String?,
    size: Dp = IconSize.xxlarge,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    val taminColors = LocalTaminColors.current
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(CornerRadius.card))
            .border(1.dp, taminColors.bgIconProfile, RoundedCornerShape(CornerRadius.card)),
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .shimmer(
                        colorBase = Color.White.copy(alpha = 0.15f),
                        colorHighlight = Color.White.copy(alpha = 0.05f)
                    )
            )
        } else {
            LoadAsyncImage(
                model = model,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}


@PreviewRtlTheme
@Composable
private fun UserAvatarLight() {
    PreviewRtlThemeContent(darkTheme = false) {
        UserAvatar(
            model = null,
        )
    }
}


@PreviewRtlTheme
@Composable
private fun UserAvatarDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        UserAvatar(
            model = null,
        )
    }
}
