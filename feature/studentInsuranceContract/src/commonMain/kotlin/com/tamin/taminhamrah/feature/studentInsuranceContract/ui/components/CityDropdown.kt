package com.tamin.taminhamrah.feature.studentInsuranceContract.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.CityOptionPR

@Composable
fun CityDropdown(
    cities: List<CityOptionPR>,
    selectedCityCode: String,
    selectedCityName: String,
    isLoading: Boolean,
    onCitySelected: (CityOptionPR) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedCityName,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("شهر محل سکونت") },
            placeholder = {
                Text(if (isLoading) "در حال بارگذاری..." else "انتخاب کنید")
            },
            enabled = false,
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(enabled = !isLoading && cities.isNotEmpty()) { expanded = true },
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f),
        ) {
            cities.forEach { city ->
                DropdownMenuItem(
                    text = { Text(city.name) },
                    onClick = {
                        onCitySelected(city)
                        expanded = false
                    },
                )
            }
            if (cities.isEmpty() && !isLoading) {
                DropdownMenuItem(
                    text = { Text("شهری یافت نشد", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    onClick = { expanded = false },
                )
            }
        }
    }
}
