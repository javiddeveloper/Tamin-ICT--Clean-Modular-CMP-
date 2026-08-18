package com.tamin.taminhamrah.ui.blur

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush

import androidx.compose.ui.unit.dp

@Composable
fun NavigationBarItemContent(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    label: @Composable (() -> Unit),
    containerBrush : Brush,
    radius : Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .background(
                brush = containerBrush,
                shape = RoundedCornerShape(radius) // Large pill shape
            ).padding(vertical = 4.dp)
            // Adjust padding to control the size of the pill wrapper

    ) {
        icon()
        label()
    }
}
