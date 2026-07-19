package com.tamin.taminhamrah.ui.components.khadamat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.ui.theme.Spacing

@Composable
fun ServiceGrid(
    services: List<MainServiceDN>,
    onServiceClick: (MainServiceDN) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = Spacing.sm, bottom = Spacing.xl),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        items(services, key = { it.id ?: 0 }) { service ->
            ServiceCard(
                service = service,
                onClick = { onServiceClick(service) }
            )
        }
    }
}
