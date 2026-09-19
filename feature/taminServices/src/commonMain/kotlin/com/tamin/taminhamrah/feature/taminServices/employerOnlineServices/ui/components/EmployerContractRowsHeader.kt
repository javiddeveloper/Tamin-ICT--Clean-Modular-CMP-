package com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.contract.ContractRowsUiState
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.employer_online_services_contract_rows_title
import taminx.core.core_ui.ic_tamin_chevron_back

/**
 * The navy gradient header of the "ردیف‌های پیمان کارگاه" drill-down — title + back only (no search,
 * no fold). The workshop identity card overlaps its bottom edge, placed by the screen the same way
 * the landing screen overlaps its identity card.
 */
@Composable
internal fun EmployerContractRowsHeader(
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
    contractRowsUiState: ContractRowsUiState
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
    ) {
        TaminTopAppBar(
            title = stringResource(Res.string.employer_online_services_contract_rows_title),
            background = gradient,
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = null,
                    onClick = onBackClicked,
                )
            },
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
             /*   DecorativeBackgroundCircle(
                    size = 190.dp,
                    xOffset = 450.dp,
                    yOffset = (-150).dp,
                    modifier = Modifier.align(Alignment.TopEnd),
                )*/

                EmployerWorkshopInfoCard(
                    workshopName = contractRowsUiState.workshopName,
                    workshopCodeLabel = contractRowsUiState.workshopCodeLabel,
                    modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
                )
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun EmployerContractRowsHeaderPreviewLight() {
    PreviewRtlThemeContent {
        EmployerContractRowsHeader(
            onBackClicked = {},
            contractRowsUiState = ContractRowsUiState()
        )
    }
}

@PreviewRtlTheme
@Composable
private fun EmployerContractRowsHeaderPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        EmployerContractRowsHeader(
            onBackClicked = {},
            contractRowsUiState = ContractRowsUiState()
        )
    }
}
