package com.tamin.taminhamrah.ui.system

import androidx.compose.runtime.Composable

/**
 * Deliberately does nothing on iOS.
 *
 * UIKit resolves the status bar style from the hosting `UIViewController`'s
 * `preferredStatusBarStyle`, not from the Compose tree, so a Compose-side call has nothing
 * to act on. Making it light there is a change in the iOS host: override
 * `preferredStatusBarStyle` on the controller that hosts `ComposeUIViewController`, or set
 * `UIViewControllerBasedStatusBarAppearance` to `NO` and configure it app-wide.
 *
 * It stays declared here so screens can state their intent in shared code, and so the iOS
 * side has one obvious place to grow a real implementation.
 */
@Composable
actual fun StatusBarIcons(darkIcons: Boolean) {
    // No-op — see the KDoc above.
}
