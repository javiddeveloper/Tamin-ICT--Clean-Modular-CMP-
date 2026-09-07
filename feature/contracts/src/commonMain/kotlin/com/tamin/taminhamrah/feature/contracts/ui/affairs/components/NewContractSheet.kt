package com.tamin.taminhamrah.feature.contracts.ui.affairs.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.DarkTaminColors
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_affairs_new_contract_sheet_description
import taminx.core.core_ui.contract_affairs_new_contract_sheet_title
import taminx.core.core_ui.contract_type_freelance_desc
import taminx.core.core_ui.contract_type_freelance_title
import taminx.core.core_ui.contract_type_housewife_desc
import taminx.core.core_ui.contract_type_housewife_title
import taminx.core.core_ui.contract_type_optional_desc
import taminx.core.core_ui.contract_type_optional_title
import taminx.core.core_ui.contract_type_student_desc
import taminx.core.core_ui.contract_type_student_title
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_chevron_forward

private data class HardcodedContractOption(
    val flag: FeatureFlag,
    val titleRes: StringResource,
    val descRes: StringResource,
    val icon: ImageVector,
)

@Composable
fun NewContractSheet(
    onServiceClick: (FeatureFlag) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isDark = colors == DarkTaminColors

    val options = remember {
        listOf(
            HardcodedContractOption(
                flag = FeatureFlag.STUDENT_INSURANCE,
                titleRes = Res.string.contract_type_student_title,
                descRes = Res.string.contract_type_student_desc,
                icon = StudentCapIcon,
            ),
            HardcodedContractOption(
                flag = FeatureFlag.HOUSEWIFE_INSURANCE,
                titleRes = Res.string.contract_type_housewife_title,
                descRes = Res.string.contract_type_housewife_desc,
                icon = HousewifeIcon,
            ),
            HardcodedContractOption(
                flag = FeatureFlag.FREELANCE_INSURANCE,
                titleRes = Res.string.contract_type_freelance_title,
                descRes = Res.string.contract_type_freelance_desc,
                icon = FreelanceIcon,
            ),
            HardcodedContractOption(
                flag = FeatureFlag.OPTIONAL_INSURANCE,
                titleRes = Res.string.contract_type_optional_title,
                descRes = Res.string.contract_type_optional_desc,
                icon = OptionalShieldIcon,
            ),
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.bgPage,
        shape = RoundedCornerShape(topStart = Spacing.xl, topEnd = Spacing.xl),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.page)
                .padding(bottom = Spacing.xlg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            TaminText(
                text = stringResource(Res.string.contract_affairs_new_contract_sheet_title),
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = Spacing.xs),
            )

            Text(
                style = MaterialTheme.typography.bodySmall,
                text = stringResource(Res.string.contract_affairs_new_contract_sheet_description),
                color = colors.textSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = Spacing.xs),
            )

            options.forEach { option ->
                val shape = RoundedCornerShape(CornerRadius.lg)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape)
                        .background(colors.bgSurface, shape)
                        .border(1.dp, colors.border.copy(alpha = 0.5f), shape)
                        .clickable { onServiceClick(option.flag) }
                        .padding(horizontal = Spacing.lg, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(IconSize.large)
                            .shadow(
                                elevation = Elevation.md,
                                shape = RoundedCornerShape(CornerRadius.xl),
                                clip = false,
                                ambientColor = if (isDark) Color.Black else MaterialTheme.colorScheme.primary,
                                spotColor = if (isDark) Color.Black else MaterialTheme.colorScheme.primary,
                            )
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.surface,
                                        if (isDark) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer.copy(
                                            alpha = 0.7f
                                        )
                                    ),
                                    start = Offset.Zero,
                                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                                ),
                                shape = RoundedCornerShape(CornerRadius.xl)
                            )
                            .border(
                                width = 1.5.dp,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = if (isDark) 0.15f else 0.9f),
                                        Color.White.copy(alpha = if (isDark) 0.02f else 0.1f)
                                    )
                                ),
                                shape = RoundedCornerShape(CornerRadius.xl)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = if (isDark) 0.05f else 0.6f),
                                            Color.Transparent
                                        )
                                    ),
                                    shape = RoundedCornerShape(CornerRadius.xl)
                                )
                        )
                        Icon(
                            imageVector = option.icon,
                            contentDescription = "contract_icons",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(IconSize.large)
                                .padding(Spacing.sm)
                        )
                    }

                    Spacer(modifier = Modifier.width(Spacing.md))

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        TaminText(
                            text = stringResource(option.titleRes),
                            color = colors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                        )
                        Text(
                            text = stringResource(option.descRes),
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary,
                            fontSize = 12.sp,
                        )
                    }

                    Spacer(modifier = Modifier.width(Spacing.xs))

                    Icon(
                        imageVector = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                        contentDescription = null,
                        tint = colors.textMuted,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}

private val StudentCapIcon: ImageVector
    get() = ImageVector.Builder(
        name = "StudentCap",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.8f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    ) {
        moveTo(12f, 4f)
        lineTo(2f, 9f)
        lineTo(12f, 14f)
        lineTo(22f, 9f)
        close()
        moveTo(6f, 11.5f)
        verticalLineTo(16.5f)
        curveTo(6f, 16.5f, 8.5f, 19f, 12f, 19f)
        curveTo(15.5f, 19f, 18f, 16.5f, 18f, 16.5f)
        verticalLineTo(11.5f)
        moveTo(20f, 10.5f)
        verticalLineTo(17f)
    }.build()

private val HousewifeIcon: ImageVector
    get() = ImageVector.Builder(
        name = "Housewife",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.8f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    ) {
        moveTo(3f, 10f)
        lineTo(12f, 3f)
        lineTo(21f, 10f)
        moveTo(5f, 9f)
        verticalLineTo(19.5f)
        curveTo(5f, 20f, 5.5f, 20.5f, 6f, 20.5f)
        horizontalLineTo(18f)
        curveTo(18.5f, 20.5f, 19f, 20f, 19f, 19.5f)
        verticalLineTo(9f)
        moveTo(12f, 16.8f)
        curveTo(12f, 16.8f, 9.5f, 15f, 9.5f, 13.5f)
        curveTo(9.5f, 12.6f, 10.2f, 12f, 11f, 12f)
        curveTo(11.5f, 12f, 11.9f, 12.3f, 12f, 12.5f)
        curveTo(12.1f, 12.3f, 12.5f, 12f, 13f, 12f)
        curveTo(13.8f, 12f, 14.5f, 12.6f, 14.5f, 13.5f)
        curveTo(14.5f, 15f, 12f, 16.8f, 12f, 16.8f)
    }.build()

private val FreelanceIcon: ImageVector
    get() = ImageVector.Builder(
        name = "Briefcase",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.8f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    ) {
        moveTo(4f, 7f)
        horizontalLineTo(20f)
        curveTo(21.1f, 7f, 22f, 7.9f, 22f, 9f)
        verticalLineTo(18f)
        curveTo(22f, 19.1f, 21.1f, 20f, 20f, 20f)
        horizontalLineTo(4f)
        curveTo(2.9f, 20f, 2f, 19.1f, 2f, 18f)
        verticalLineTo(9f)
        curveTo(2f, 7.9f, 2.9f, 7f, 4f, 7f)
        close()
        moveTo(9f, 7f)
        verticalLineTo(5f)
        curveTo(9f, 4.4f, 9.4f, 4f, 10f, 4f)
        horizontalLineTo(14f)
        curveTo(14.6f, 4f, 15f, 4.4f, 15f, 5f)
        verticalLineTo(7f)
        moveTo(10f, 13f)
        horizontalLineTo(14f)
    }.build()

private val OptionalShieldIcon: ImageVector
    get() = ImageVector.Builder(
        name = "ShieldPlus",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).path(
        stroke = SolidColor(Color.Black),
        strokeLineWidth = 1.8f,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
    ) {
        moveTo(12f, 3f)
        lineTo(4f, 6f)
        verticalLineTo(12f)
        curveTo(4f, 16.5f, 7.5f, 20.5f, 12f, 21.5f)
        curveTo(16.5f, 20.5f, 20f, 16.5f, 20f, 12f)
        verticalLineTo(6f)
        lineTo(12f, 3f)
        close()
        moveTo(12f, 9f)
        verticalLineTo(15f)
        moveTo(9f, 12f)
        horizontalLineTo(15f)
    }.build()
