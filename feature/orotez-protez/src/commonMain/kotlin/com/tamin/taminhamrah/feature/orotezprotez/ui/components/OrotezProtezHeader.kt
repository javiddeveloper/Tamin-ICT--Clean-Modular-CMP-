package com.tamin.taminhamrah.feature.orotezprotez.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import com.tamin.taminhamrah.ui.components.GlassIconTile
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_help
import taminx.core.core_ui.ic_info
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.orotez_protez_help_dialog_confirm
import taminx.core.core_ui.orotez_protez_help_dialog_description
import taminx.core.core_ui.orotez_protez_help_dialog_title
import taminx.core.core_ui.orotez_protez_subtitle
import taminx.core.core_ui.orotez_protez_title

@Composable
internal fun OrotezProtezHeader(
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val gradient = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }
    var showHelpDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = CornerRadius.x3l, bottomEnd = CornerRadius.x3l))
            .background(gradient)
            .padding(bottom = Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TaminTopAppBar(
            title = stringResource(Res.string.orotez_protez_title),
            background = gradient,
            navigationIcon = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    contentDescription = null,
                    onClick = onBackClicked,
                    bordered = true,
                )
            },
            action = {
                TaminTopAppBarButton(
                    icon = vectorResource(Res.drawable.ic_help),
                    contentDescription = null,
                    onClick = { showHelpDialog = true },
                    bordered = true,
                )
            },
        )

        Spacer(Modifier.height(Spacing.smPlus))
        GlassIconTile(icon = Icons.Filled.Accessibility)
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = stringResource(Res.string.orotez_protez_subtitle),
            style = MaterialTheme.typography.labelLarge,
            color = taminColors.textHeaderSubtitle,
        )
    }

    if (showHelpDialog) {
        TaminConfirmationDialog(
            title = stringResource(Res.string.orotez_protez_help_dialog_title),
            description = stringResource(Res.string.orotez_protez_help_dialog_description),
            icon = vectorResource(Res.drawable.ic_info),
            onDismissRequest = { showHelpDialog = false },
            confirmButton = {
                TaminFilledButton(
                    text = stringResource(Res.string.orotez_protez_help_dialog_confirm),
                    onClick = { showHelpDialog = false },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            dismissButton = {},
        )
    }
}
