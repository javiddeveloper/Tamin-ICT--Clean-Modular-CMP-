package com.tamin.taminhamrah.model.studentContract

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent

@Composable
fun <T> SelectableField(
    label: String,
    options: List<T>,
    selectedCode: String,
    selectedName: String,
    optionCode: (T) -> String,
    optionName: (T) -> String,
    isLoading: Boolean,
    enabled: Boolean = true,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label) },
            placeholder = {
                Text(
                    when {
                        isLoading -> "در حال بارگذاری..."
                        !enabled -> "ابتدا مورد قبلی را انتخاب کنید"
                        else -> "انتخاب کنید"
                    },
                )
            },
            enabled = false,
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(enabled = enabled && !isLoading && options.isNotEmpty()) {
                    expanded = true
                },
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f),
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionName(option)) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    },
                )
            }
            if (options.isEmpty() && !isLoading) {
                DropdownMenuItem(
                    text = {
                        Text(
                            "موردی یافت نشد",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    onClick = { expanded = false },
                )
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun SelectableFieldPreview() {
    PreviewRtlThemeContent {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                SelectableField(
                    label = "انتخاب شهر",
                    options = listOf("تهران", "مشهد", "اصفهان"),
                    selectedCode = "",
                    selectedName = "",
                    optionCode = { it },
                    optionName = { it },
                    isLoading = false,
                    onSelected = {},
                )
                Spacer(modifier = Modifier.height(16.dp))
                SelectableField(
                    label = "در حال بارگذاری",
                    options = emptyList<String>(),
                    selectedCode = "",
                    selectedName = "",
                    optionCode = { it },
                    optionName = { it },
                    isLoading = true,
                    onSelected = {},
                )
                Spacer(modifier = Modifier.height(16.dp))
                SelectableField(
                    label = "غیر فعال",
                    options = emptyList<String>(),
                    selectedCode = "",
                    selectedName = "",
                    optionCode = { it },
                    optionName = { it },
                    isLoading = false,
                    enabled = false,
                    onSelected = {},
                )
            }
        }
    }
}
