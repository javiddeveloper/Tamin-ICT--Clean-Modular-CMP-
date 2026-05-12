package com.tamin.taminhamrah.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle
import platform.UIKit.UINotificationFeedbackGenerator
import platform.UIKit.UINotificationFeedbackType

@Composable
actual fun rememberHapticFeedback(): (MyHapticFeedbackType) -> Unit {
    return remember {
        { type ->
            when (type) {
                MyHapticFeedbackType.Confirm -> {
                    UINotificationFeedbackGenerator().notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeSuccess)
                }
                MyHapticFeedbackType.Error -> {
                    UINotificationFeedbackGenerator().notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeError)
                }
                MyHapticFeedbackType.LongPress -> {
                    UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium).impactOccurred()
                }
            }
        }
    }
}
