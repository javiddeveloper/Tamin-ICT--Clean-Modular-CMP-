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
fun LoadAvatarImage(
    encodedImage: String?,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    val processedModel = remember(encodedImage) {
        if (encodedImage is String && encodedImage.isBase64Raw()) {
            "data:image/png;base64,$encodedImage"
        } else {
            encodedImage
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
            ImageErrorPlaceholder(modifier = Modifier.fillMaxSize())
        }
    )
}

private fun String.isBase64Raw(): Boolean {
    return !this.startsWith("http") && !this.startsWith("data:") && this.length > 100
}
