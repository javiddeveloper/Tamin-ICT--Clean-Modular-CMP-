package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing

@Composable
fun CustomSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeHolder: String = "",
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        textStyle = MaterialTheme.typography.titleMedium,
        placeholder = {
            Text(
                text = placeHolder,
                style = MaterialTheme.typography.bodyMedium.copy(MaterialTheme.colorScheme.onSurfaceVariant)
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = LocalTaminColors.current.textMuted
            )
        },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = MaterialTheme.shapes.extraLarge,
        modifier = modifier.fillMaxWidth()
    )
}

@PreviewRtlTheme
@Composable
private fun ProfileScreenPreview() {
    PreviewRtlThemeContent {
        CustomSearchBar(
            placeHolder = "جستجو در میان خدمات ...",
            query = "",
            onQueryChange = {}
        )
    }
}
