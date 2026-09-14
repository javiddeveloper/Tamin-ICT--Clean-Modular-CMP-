package com.tamin.taminhamrah.feature.taminServices.workersPayment.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.workers_payment_info_sheet_body
import taminx.core.core_ui.workers_payment_info_sheet_dismiss
import taminx.core.core_ui.workers_payment_info_sheet_title

@Composable
internal fun WorkersPaymentInfoBottomSheet(
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val taminColors = LocalTaminColors.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = CornerRadius.sheet, topEnd = CornerRadius.sheet),
        containerColor = taminColors.bgSurface,
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.page)
                .padding(bottom = Spacing.page),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            TaminText(
                text = stringResource(Res.string.workers_payment_info_sheet_title),
                style = MaterialTheme.typography.titleMedium,
                color = taminColors.textPrimary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
            )
            TaminText(
                text = stringResource(Res.string.workers_payment_info_sheet_body),
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 26.sp,
                color = taminColors.textSecondary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Justify,
            )
            TaminOutlinedButton(
                text = stringResource(Res.string.workers_payment_info_sheet_dismiss),
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.sm),
                borderWidth = 0.dp,
                borderColor = Color.Transparent,
                containerColor = taminColors.divider,
                contentColor = taminColors.textPrimary,
            )
        }
    }
}


@PreviewRtlTheme
@Composable
private fun WorkersPaymentInfoBottomSheetPreview() {
    PreviewRtlThemeContent {
        WorkersPaymentInfoBottomSheet(onDismiss = {})
    }
}
