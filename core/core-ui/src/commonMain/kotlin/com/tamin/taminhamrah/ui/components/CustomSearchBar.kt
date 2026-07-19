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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.TaminLightTextPrimary
import com.tamin.taminhamrah.ui.theme.TaminLightTextSecondary
import com.tamin.taminhamrah.ui.theme.TaminNavy700
import com.tamin.taminhamrah.ui.theme.TaminRed

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
                color = TaminLightTextSecondary,
                style = MaterialTheme.typography.titleMedium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = TaminNavy700
            )
        },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFEFF3F8),
            unfocusedContainerColor = Color(0xFFEFF3F8),
            disabledContainerColor = Color(0xFFEFF3F8),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = TaminNavy700,
            focusedTextColor = TaminLightTextPrimary,
            unfocusedTextColor = TaminLightTextPrimary
        ),
        shape = MaterialTheme.shapes.large,
        modifier = modifier.fillMaxWidth()
    )
}

@PreviewRtlTheme
@Composable
private fun ProfileScreenPreview() {
    PreviewRtlThemeContent {
        CustomSearchBar(
            query = "هدیه ازدواج",
            onQueryChange = {}
        )
    }
}

