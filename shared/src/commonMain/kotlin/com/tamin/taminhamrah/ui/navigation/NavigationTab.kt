package com.tamin.taminhamrah.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.DrawableResource

data class NavigationTab(val title: String, val isSelected: Boolean, val icon: DrawableResource, val onClick: () -> Unit)
