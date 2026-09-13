package com.tamin.taminhamrah.feature.fractionContract.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.ui.components.BackHandler

/**
 * Placeholder until design arrives (Phase 2). Opens from [com.tamin.taminhamrah.model.common.FeatureFlag.FRACTION_CONTRACT].
 */
@Composable
fun FractionContractScreen(
    onBack: () -> Unit,
) {
    BackHandler(onBack = onBack)
    Box(modifier = Modifier.fillMaxSize())
}
