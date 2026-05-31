package com.tamin.taminhamrah.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Preview
annotation class PreviewRtlTheme

@Composable
fun PreviewRtlThemeContent(
    content: @Composable () -> Unit
) {
    TaminHamrahTheme {
        content()
    }
}
