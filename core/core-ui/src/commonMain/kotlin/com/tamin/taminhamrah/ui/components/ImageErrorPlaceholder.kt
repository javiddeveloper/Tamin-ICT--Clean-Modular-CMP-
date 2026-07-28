package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.tamin.taminhamrah.ui.theme.IconSize
import org.jetbrains.compose.resources.painterResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_person_profile

@Composable
fun ImageErrorPlaceholder(
    modifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
) {
    Box(
        modifier = modifier.background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_person_profile),
            contentDescription = null,
            modifier = Modifier.size(IconSize.large),
            tint = iconTint,
        )
    }
}
