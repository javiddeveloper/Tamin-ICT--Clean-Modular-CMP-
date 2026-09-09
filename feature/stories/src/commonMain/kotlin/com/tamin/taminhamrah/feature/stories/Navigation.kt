package com.tamin.taminhamrah.feature.stories

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.toRoute
import com.tamin.taminhamrah.feature.stories.ui.viewer.StoryViewerScreen
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.ui.composableWithFadeTransitions
import kotlinx.serialization.Serializable

/**
 * The viewer is a destination of its own rather than an overlay on the home page, which is what
 * makes the system back button close it, and what takes the floating navigation bar off the screen
 * while a story is up — the host graph hides that bar on every route outside the four tabs.
 *
 * [channelIndex] is the position in the catalogue the rail was tapped on. A position rather than a
 * key because the viewer walks forward through the whole list from there.
 */
@Serializable
data class StoryViewerRoute(val channelIndex: Int = 0)

fun NavController.navigateToStoryViewer(channelIndex: Int, navOptions: NavOptions? = null) {
    navigate(StoryViewerRoute(channelIndex), navOptions)
}

/**
 * @param onOpenFeature where a slide's call to action leads. Takes a [FeatureFlag] rather than
 *   navigating from here: the story feature must not import another feature, and the host is also
 *   where the server's answer about that service is checked.
 */
fun NavGraphBuilder.storyViewerScreen(
    onClose: () -> Unit,
    onOpenFeature: (FeatureFlag) -> Unit,
) {
    composableWithFadeTransitions<StoryViewerRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<StoryViewerRoute>()
        StoryViewerScreen(
            channelIndex = route.channelIndex,
            onClose = onClose,
            onOpenFeature = onOpenFeature,
        )
    }
}
