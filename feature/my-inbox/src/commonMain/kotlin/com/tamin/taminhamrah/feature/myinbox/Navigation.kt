package com.tamin.taminhamrah.feature.myinbox

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.tamin.taminhamrah.feature.myinbox.ui.MyInboxScreen
import kotlinx.serialization.Serializable

@Serializable
object MyInboxRoute

fun NavGraphBuilder.myInboxScreen(
    onNavigateBack: () -> Unit
) {
    composable<MyInboxRoute> {
        MyInboxScreen(
            onNavigateBack = onNavigateBack
        )
    }
}
