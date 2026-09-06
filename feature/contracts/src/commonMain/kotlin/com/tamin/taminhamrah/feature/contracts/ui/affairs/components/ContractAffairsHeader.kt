package com.tamin.taminhamrah.feature.contracts.ui.affairs.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
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
import taminx.core.core_ui.contract_affairs_search_title
import taminx.core.core_ui.contract_affairs_subtitle
import taminx.core.core_ui.contract_affairs_title
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_search

/**
 * Collapsing gradient header for امور قراردادها و پرداخت — mirrors
 * [com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.InspectionHeader]:
 * back button + search action, a ripple document icon and the subtitle line that folds away on scroll.
 */
@Composable
internal fun ContractAffairsHeader(
    onBackClicked: () -> Unit,
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
            title = stringResource(Res.string.contract_affairs_title),
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
                    contentDescription = stringResource(Res.string.contract_affairs_search_title),
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
                    AnimatedRingHeaderIcon(icon = Icons.Outlined.Description)
                    Spacer(Modifier.height(Spacing.sm))
                    Text(
                        text = stringResource(Res.string.contract_affairs_subtitle),
                        style = MaterialTheme.typography.labelLarge,
                        color = taminColors.textHeaderSubtitle,
                    )
                }
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ContractAffairsHeaderPreviewLight() {
    PreviewRtlThemeContent {
        ContractAffairsHeader(
            onBackClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractAffairsHeaderPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        ContractAffairsHeader(
            onBackClicked = {},
        )
    }
}
