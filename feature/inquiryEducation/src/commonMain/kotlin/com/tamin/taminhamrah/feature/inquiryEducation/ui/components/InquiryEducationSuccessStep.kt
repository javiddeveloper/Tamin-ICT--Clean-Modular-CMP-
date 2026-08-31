package com.tamin.taminhamrah.feature.inquiryEducation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.tamin.taminhamrah.feature.inquiryEducation.ui.contract.InquiryEducationUiState
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_info
import taminx.core.core_ui.ic_tamin_check
import taminx.core.core_ui.inquiry_education_label_inquiry_code
import taminx.core.core_ui.inquiry_education_label_inquiry_date
import taminx.core.core_ui.inquiry_education_label_national_id
import taminx.core.core_ui.inquiry_education_label_student_name
import taminx.core.core_ui.inquiry_education_label_university
import taminx.core.core_ui.inquiry_education_success_note
import taminx.core.core_ui.inquiry_education_success_title

@Composable
fun InquiryEducationSuccessStep(
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

        InquiryEducationStatusIcon(
            icon = painterResource(Res.drawable.ic_tamin_check),
            shadowColor = colors.greenText.copy(alpha = 0.25f),
            iconBackground = colors.iconGradientSuccess,
        )

        Spacer(modifier = Modifier.height(Spacing.sm))

        Text(
            text = stringResource(Res.string.inquiry_education_success_title),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
            ),
            textAlign = TextAlign.Center,
        )

        if (state.successMessage.isNotBlank()) {
            Text(
                text = state.successMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textMuted,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.height(Spacing.xs))

        InquiryEducationSuccessDetailsCard(state = state)

        InquiryEducationSuccessNoteBox()

        Spacer(modifier = Modifier.height(Spacing.lg))
    }
}

@Composable
private fun InquiryEducationStatusIcon(
    icon: Painter,
    shadowColor: Color,
    iconBackground: Brush,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(IconSize.headerIconOuter)
            .coloredShadow(
                color = shadowColor,
                borderRadius = CornerRadius.max,
                blurRadius = Elevation.xxl,
                offsetY = Spacing.smPlus,
            )
            .clip(CircleShape)
            .background(iconBackground),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(IconSize.xlarge),
        )
    }
}

@Composable
private fun InquiryEducationSuccessDetailsCard(
    state: InquiryEducationUiState,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.card)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.verifiedContainerBg)
            .border(BorderStroke(Thickness.border, colors.verifiedContainerBorder), shape)
            .padding(Spacing.md),
    ) {
        DetailRow(
            label = stringResource(Res.string.inquiry_education_label_student_name),
            value = state.studentName,
            numeric = false,
            verticalPadding = Spacing.sm,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.inquiry_education_label_national_id),
            value = state.studentNationalId,
            verticalPadding = Spacing.sm,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.inquiry_education_label_university),
            value = state.universityName,
            numeric = false,
            verticalPadding = Spacing.sm,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.inquiry_education_label_inquiry_code),
            value = state.inquiryCode,
            verticalPadding = Spacing.sm,
        )
        TaminDivider()
        DetailRow(
            label = stringResource(Res.string.inquiry_education_label_inquiry_date),
            value = state.inquiryDate,
            verticalPadding = Spacing.sm,
        )
    }
}

@Composable
private fun InquiryEducationSuccessNoteBox(
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.listRow)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.greenBg, shape)
            .border(Thickness.border, colors.greenText.copy(alpha = 0.2f), shape)
            .padding(Spacing.smd),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_info),
            contentDescription = null,
            tint = colors.greenText,
        )
        Text(
            text = stringResource(Res.string.inquiry_education_success_note),
            style = MaterialTheme.typography.bodySmall,
            color = colors.greenText,
            modifier = Modifier.weight(1f),
        )
    }
}
