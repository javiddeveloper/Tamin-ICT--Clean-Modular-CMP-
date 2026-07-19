package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme

@Composable
fun SectionHeaderTitle(
    title: String,
    modifier: Modifier = Modifier,
    color: Color = LocalTaminColors.current.textMuted,
    textStyle: TextStyle = MaterialTheme.typography.titleSmall,
    contentPadding: PaddingValues = PaddingValues(
        start = Spacing.sm,
        bottom = Spacing.sm
    ),
) {
    Text(
        text = title,
        color = color,
        style = textStyle,
        modifier = modifier.padding(contentPadding)
    )
}

@PreviewRtlTheme
@Composable
private fun SectionHeaderTitlePreviewLight() {
    TaminHamrahTheme(darkTheme = false) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(LocalTaminColors.current.bgPage)
                .padding(Spacing.lg)
        ) {
            SectionHeaderTitle(title = "اطلاعات شخصی")
        }
    }
}

@PreviewRtlTheme
@Composable
private fun SectionHeaderTitlePreviewDark() {
    TaminHamrahTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(LocalTaminColors.current.bgPage)
                .padding(Spacing.lg)
        ) {
            SectionHeaderTitle(title = "اطلاعات شخصی")
        }
    }
}
