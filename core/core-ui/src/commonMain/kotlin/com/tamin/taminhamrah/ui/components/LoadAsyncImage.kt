package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.SubcomposeAsyncImage
import com.tamin.taminhamrah.ui.theme.shimmer

@Composable
fun LoadAsyncImage(
    model: String?,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    /**
     * What to draw when the image cannot be loaded. Defaults to the app's gray placeholder, which
     * suits a light surface; a caller drawing on a dark one should pass its own.
     */
    errorContent: (@Composable () -> Unit)? = null,
) {
    val processedModel = remember(model) {
        when {
            model is String && model.isBase64Raw() -> "data:image/png;base64,$model"
            else -> model
        }
    }

    SubcomposeAsyncImage(
        model = processedModel,
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier,
        loading = {
            Box(modifier = Modifier.fillMaxSize().shimmer())
        },
        error = {
            if (errorContent != null) {
                errorContent()
            } else {
                ImageErrorPlaceholder(modifier = Modifier.fillMaxSize())
            }
        }
    )
}

private fun String.isBase64Raw(): Boolean {
    return !this.startsWith("http") && !this.startsWith("data:") && this.length > 100
}
