package com.tamin.taminhamrah.feature.inquiryEducation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.tamin.taminhamrah.feature.inquiryEducation.ui.contract.InquiryEducationUiState
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_error
import taminx.core.core_ui.inquiry_education_failure_title

@Composable
fun InquiryEducationFailureStep(
    state: InquiryEducationUiState,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Spacer(modifier = Modifier.height(Spacing.lg))

        Box(
            modifier = Modifier
                .size(IconSize.headerIconOuter)
                .coloredShadow(
                    color = colors.dangerText.copy(alpha = 0.25f),
                    borderRadius = CornerRadius.max,
                    blurRadius = Elevation.xxl,
                    offsetY = Spacing.smPlus,
                )
                .clip(CircleShape)
                .background(colors.iconGradientDanger),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_error),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(IconSize.xlarge),
            )
        }

        Spacer(modifier = Modifier.height(Spacing.sm))

        Text(
            text = stringResource(Res.string.inquiry_education_failure_title),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
            ),
            textAlign = TextAlign.Center,
        )

        if (state.failureMessage.isNotBlank()) {
            Text(
                text = state.failureMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textMuted,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(Spacing.lg))
    }
}
