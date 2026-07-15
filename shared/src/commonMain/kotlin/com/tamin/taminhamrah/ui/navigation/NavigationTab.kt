package com.tamin.taminhamrah.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationTab(val title: String, val isSelected: Boolean, val icon: ImageVector, val onClick: () -> Unit)
