package com.tamin.taminhamrah.ui

import androidx.compose.runtime.Composable
import com.tamin.taminhamrah.ui.navigation.TaminHamrahNavGraph
import com.tamin.taminhamrah.ui.theme.TaminHamrahTheme

@Composable
fun MainApp() {
    TaminHamrahTheme {
        TaminHamrahNavGraph()
    }
}
