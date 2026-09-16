package com.tamin.taminhamrah.ui.deeplink

import androidx.compose.runtime.staticCompositionLocalOf
import com.tamin.taminhamrah.deeplink.DeepLinkSource

/**
 * Hands a link to the app's single deep link gate.
 *
 * Features never navigate to another feature themselves: they pass the link here and the host
 * resolves it through `ResolveDeepLinkUseCase`, which applies the feature flag. The default does
 * nothing, so a preview or test without a host cannot navigate anywhere.
 */
fun interface DeepLinkHandler {
    /** [onOpened] runs after the gate lets the link through, e.g. to close the screen that sent it. */
    fun open(uri: String, source: DeepLinkSource, onOpened: () -> Unit)

    fun open(uri: String, source: DeepLinkSource) = open(uri, source) {}
}

val LocalDeepLinkHandler = staticCompositionLocalOf { DeepLinkHandler { _, _, _ -> } }
