package com.tamin.taminhamrah.feature.profile.ui.addDependent

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.AddDependentState
import com.tamin.taminhamrah.ui.components.ListGroupView
import com.tamin.taminhamrah.ui.components.ListItemBadge
import com.tamin.taminhamrah.ui.components.ListItemColors
import com.tamin.taminhamrah.ui.components.ListItemData
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_check
import taminx.core.core_ui.ic_tamin_user
import taminx.core.core_ui.success_badge_text
import taminx.core.core_ui.success_dependent_fallback
import taminx.core.core_ui.success_desc
import taminx.core.core_ui.success_title

@Composable
fun AddDependentSuccessStep(
    state: AddDependentState
) {
    val colors = LocalTaminColors.current
    val dependentName = state.registryData
        ?.let { "${it.firstName} ${it.lastName}".trim() }
        ?.ifBlank { null }
        ?: stringResource(Res.string.success_dependent_fallback)
    val relationLabel = state.selectedRelationship?.relationDesc.orEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .coloredShadow(
                    color = colors.greenText.copy(alpha = 0.25f),
                    borderRadius = 50.dp,
                    blurRadius = 30.dp,
                    offsetY = 10.dp
                )
                .clip(CircleShape)
                .background(colors.iconGradientSuccess),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_tamin_check),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(Spacing.xxl))

        Text(
            text = stringResource(Res.string.success_title),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        Text(
            text = stringResource(Res.string.success_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textMuted,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(Spacing.xlg))

        ListGroupView(
            containerBackgroundColor = colors.verifiedContainerBg,
            containerBorder = BorderStroke(1.dp, colors.verifiedContainerBorder),
            items = persistentListOf(
                ListItemData(
                    title = dependentName,
                    subtitle = relationLabel.ifBlank { null },
                    leadingIconPainter = painterResource(Res.drawable.ic_tamin_user),
                    colors = ListItemColors(
                        leadingIconBackgroundColor = colors.verifiedIconBg,
                        leadingIconTintColor = colors.verifiedIconTint,
                        titleColor = colors.springGreenText
                    ),
                    showArrow = false,
                    badge = ListItemBadge(
                        text = stringResource(Res.string.success_badge_text),
                        backgroundColor = colors.verifiedBadgeBg,
                        textColor = colors.greenText
                    ),
                    titleStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            )
        )
    }
}
