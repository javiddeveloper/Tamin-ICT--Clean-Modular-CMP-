package com.tamin.taminhamrah.feature.taminServices.inspection.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.contract.InspectionIntent
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.collapseAway
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.edict_search_title
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_search
import taminx.core.core_ui.inspection_subtitle
import taminx.core.core_ui.inspection_title

@Composable
internal fun InspectionHeader(
    onBackClicked: () -> Unit,
    onIntent: (InspectionIntent) -> Unit,
    modifier: Modifier = Modifier,
    collapseProgress: () -> Float = { 0f },
    onSearchClicked: () -> Unit = {},
) {
    val taminColors = LocalTaminColors.current
    val gradient = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = CornerRadius.x3l, bottomEnd = CornerRadius.x3l))
            .background(gradient),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TaminTopAppBar(
            title = stringResource(Res.string.inspection_title),
            background = gradient,
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = null,
                    onClick = onBackClicked,
                )
            },
            action = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_search),
                    contentDescription = stringResource(Res.string.edict_search_title),
                    onClick = onSearchClicked,
                )
            }
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .collapseAway(collapseProgress)
                        .padding(horizontal = Spacing.page, vertical = Spacing.smPlus),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    DecorativeBackgroundCircle(
                        size = 190.dp,
                        xOffset = 450.dp,
                        yOffset = (-150).dp
                    )
                    AnimatedRingHeaderIcon(icon = Icons.Outlined.Assignment)
                    Spacer(Modifier.height(Spacing.sm))
                    Text(
                        text = stringResource(Res.string.inspection_subtitle),
                        style = MaterialTheme.typography.labelLarge,
                        color = taminColors.textHeaderSubtitle,
                    )
                    /*
                                Spacer(Modifier.height(Spacing.sm))

                                CustomChip(
                                    text = stringResource(Res.string.inspection_count_format, count.toString().toPersianDigits()),
                                    containerColor = taminColors.chipBg,
                                    textColor = taminColors.textPrimary,
                                    border = BorderStroke(width = 1.dp, color = taminColors.hawkesBlue),
                                )

                                Spacer(Modifier.height(Spacing.lg))

                                TaminPrimaryButton(
                                    text = stringResource(Res.string.inspection_request_button),
                                    onClick = onRequestInspectionClicked,
                                    background = taminTopAppBarGradient(),
                                )*/
                }
            }
        }

    }
}

@PreviewRtlTheme
@Composable
private fun InspectionHeaderPreviewLight() {
    PreviewRtlThemeContent {
        InspectionHeader(
            onBackClicked = {},
            onIntent = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun InspectionHeaderPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        InspectionHeader(
            onBackClicked = {},
            onIntent = {},
        )
    }
}
