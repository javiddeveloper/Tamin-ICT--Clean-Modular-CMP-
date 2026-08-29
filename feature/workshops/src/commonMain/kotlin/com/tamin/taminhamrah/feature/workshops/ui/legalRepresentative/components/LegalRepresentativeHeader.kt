package com.tamin.taminhamrah.feature.workshops.ui.legalRepresentative.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.GlassIconTile
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.legal_representative_title

/** The gradient hero bar shared by every screen in this flow, title + back button fixed. */
@Composable
internal fun LegalRepresentativeHeader(
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    TaminTopAppBar(
        title = stringResource(Res.string.legal_representative_title),
        modifier = modifier,
        navigationIcon = {
            TaminTopAppBarButton(
                icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                contentDescription = null,
                onClick = onBackClicked,
                bordered = true,
            )
        },
        content = { content() },
    )
}

/** The hub screen's own header content: an icon tile over a muted caption. */
@Composable
internal fun LegalRepresentativeHeroSubtitle(text: String) {
    val taminColors = LocalTaminColors.current
    Spacer(Modifier.height(Spacing.smPlus))
    GlassIconTile(icon = Icons.Filled.Person)
    Spacer(Modifier.height(Spacing.sm))
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = taminColors.textHeaderSubtitle,
    )
}

/** A small card summarizing the workshop, embedded in the header on every later screen. */
@Composable
internal fun LegalRepresentativeWorkshopSummaryCard(
    workshopName: String,
    subtitle: String,
) {
    val taminColors = LocalTaminColors.current
    Spacer(Modifier.height(Spacing.smPlus))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.card))
            .background(taminColors.glassIconTileBg)
            .padding(Spacing.md),
    ) {
        Text(
            text = workshopName,
            style = MaterialTheme.typography.titleSmall,
            color = Color.White,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelMedium,
            color = taminColors.textHeaderSubtitle,
        )
    }
}
