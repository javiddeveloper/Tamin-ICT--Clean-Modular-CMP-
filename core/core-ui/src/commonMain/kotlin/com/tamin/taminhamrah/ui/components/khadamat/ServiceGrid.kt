package com.tamin.taminhamrah.ui.components.khadamat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.common.MainServiceDN

@Composable
fun ServiceGrid(
    services: List<MainServiceDN>,
    onServiceClick: (MainServiceDN) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(services, key = { it.id ?: 0 }) { service ->
            ServiceCard(
                service = service,
                onClick = { onServiceClick(service) }
            )
        }
    }
}
