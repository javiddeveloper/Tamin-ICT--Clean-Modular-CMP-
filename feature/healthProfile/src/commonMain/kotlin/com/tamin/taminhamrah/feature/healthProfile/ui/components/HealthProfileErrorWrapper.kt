package com.tamin.taminhamrah.feature.healthProfile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors

@Composable
fun HealthProfileErrorView(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(taminColors.bgPage)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TaminText(
            text = error,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            TaminText("تلاش مجدد")
        }
    }
}

@Composable
fun HealthProfileErrorWrapper(
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit,
    shimmerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    if (isLoading) {
        shimmerContent()
    } else if (!error.isNullOrEmpty()) {
        HealthProfileErrorView(error = error, onRetry = onRetry, modifier = modifier)
    } else {
        content()
    }
}
