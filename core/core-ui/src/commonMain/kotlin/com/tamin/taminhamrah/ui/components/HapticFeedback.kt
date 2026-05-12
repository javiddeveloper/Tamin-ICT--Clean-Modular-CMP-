package com.tamin.taminhamrah.ui.components

import androidx.compose.runtime.Composable

enum class MyHapticFeedbackType {
    Confirm,
    Error,
    LongPress,
}

@Composable
expect fun rememberHapticFeedback(): (MyHapticFeedbackType) -> Unit
