package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_close

@Composable
internal fun ConstructionNoticeBottomSheet(onDismiss: () -> Unit) {
    val taminColors = LocalTaminColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = taminColors.bgSurface,
        shape = RoundedCornerShape(topStart = CornerRadius.sheet, topEnd = CornerRadius.sheet),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = Spacing.md)
                    .size(width = 32.dp, height = 4.dp)
                    .background(taminColors.border, RoundedCornerShape(50)),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.page)
                .navigationBarsPadding()
                .padding(bottom = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            NoticeCard()
            TaminOutlinedButton(
                text = stringResource(Res.string.action_close),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                borderColor = taminColors.border,
                containerColor = taminColors.chipBg,
                contentColor = taminColors.textSecondary,
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ConstructionNoticeBottomSheetPreview() {
    PreviewRtlThemeContent {
        ConstructionNoticeBottomSheet(onDismiss = {})
    }
}

@PreviewRtlTheme
@Composable
private fun ConstructionNoticeBottomSheetPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        ConstructionNoticeBottomSheet(onDismiss = {})
    }
}
