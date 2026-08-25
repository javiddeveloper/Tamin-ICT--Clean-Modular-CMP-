package com.tamin.taminhamrah.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.tamin.taminhamrah.ui.components.toast.AppToastHost
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Preview
annotation class PreviewRtlTheme

@Composable
fun PreviewRtlThemeContent(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    TaminHamrahTheme(darkTheme = darkTheme) {
        AppToastHost {
            content()
        }
    }
}
