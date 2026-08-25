package com.tamin.taminhamrah.feature.taminServices.occurrence.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * A field's "show its error only after the user has focused-then-left it" state, bundled behind
 * one [rememberFieldTouchState] call instead of each field re-declaring its own
 * `XHasFocused`/`XTouched` `rememberSaveable` pair plus an inline `onFocusChanged` lambda (the
 * exact same five lines were copy-pasted per field across the wizard's steps).
 */
internal data class FieldTouchState(
    val touched: Boolean,
    val onFocusChanged: (Boolean) -> Unit,
)

@Composable
internal fun rememberFieldTouchState(): FieldTouchState {
    var hasFocused by rememberSaveable { mutableStateOf(false) }
    var touched by rememberSaveable { mutableStateOf(false) }
    return FieldTouchState(
        touched = touched,
        onFocusChanged = { isFocused ->
            if (isFocused) {
                hasFocused = true
            } else if (hasFocused) {
                touched = true
            }
        },
    )
}
