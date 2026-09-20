package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.file_unit
import taminx.core.core_ui.label_full_name
import taminx.core.core_ui.label_national_code

@Composable
fun ConstructionUserInfoCard(
    userName: String,
    nationalCode: String,
    itemCount: Int,
    modifier: Modifier = Modifier
) {
    if (userName.isBlank() && nationalCode.isBlank()) return

    val taminColors = LocalTaminColors.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .coloredShadow(
                color = taminColors.shadowSubtle,
                borderRadius = CornerRadius.lg,
                blurRadius = Elevation.md,
                offsetY = Spacing.xs
            ),
        shape = RoundedCornerShape(CornerRadius.lg),
        color = taminColors.bgSurface,
        tonalElevation = Elevation.none
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            InfoColumn(
                value = userName,
                label = stringResource(Res.string.label_full_name)
            )

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(Spacing.xxl)
                    .background(taminColors.divider)
            )

            InfoColumn(
                value = nationalCode,
                label = stringResource(Res.string.label_national_code)
            )

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(Spacing.xxl)
                    .background(taminColors.divider)
            )

            InfoColumn(
                value = "$itemCount",
                label = stringResource(Res.string.file_unit)
            )
        }
    }
}

@Composable
private fun InfoColumn(
    value: String,
    label: String
) {
    val taminColors = LocalTaminColors.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = taminColors.textPrimary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = taminColors.textMuted
        )
    }
}




@PreviewRtlTheme
@Composable
private fun ConstructionUserInfoCardPreview() {
    PreviewRtlThemeContent {
        ConstructionUserInfoCard(
            userName = "حسین توکلی کرمانی",
            nationalCode = "۴۴۷۹۸۹۰۸۸۲",
            itemCount = 24,
            modifier = Modifier.padding(Spacing.page)
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ConstructionUserInfoCardPreviewDark() {
    TaminHamrahTheme(darkTheme = true) {
        ConstructionUserInfoCard(
            userName = "حسین توکلی کرمانی",
            nationalCode = "۴۴۷۹۸۹۰۸۸۲",
            itemCount = 24,
            modifier = Modifier.padding(Spacing.page)
        )
    }
}
