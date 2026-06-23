package com.tamin.taminhamrah.feature.studentInsuranceContract.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.studentContract.UserInfoFormPR
import com.tamin.taminhamrah.model.common.CityPR

@Composable
fun UserInfoStepContent(
    userInfo: UserInfoFormPR,
    cities: List<CityPR>,
    isCitiesLoading: Boolean,
    onCitySelected: (CityPR) -> Unit,
    onAddressChange: (String) -> Unit,
    onZipCodeChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
            ),
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp),
                )
                Text(
                    text = "متقاضی محترم، در صورت نادرست بودن اطلاعات، می‌توانید از طریق فرم زیر نسبت به ویرایش اطلاعات خود اقدام نمایید.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        SelectableField(
            label = "شهر",
            options = cities,
            selectedCode = userInfo.cityCode,
            selectedName = userInfo.cityName,
            optionCode = { it.cityCode },
            optionName = { it.cityName },
            isLoading = isCitiesLoading,
            onSelected = onCitySelected,
        )

        OutlinedTextField(
            value = userInfo.address,
            onValueChange = onAddressChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("آدرس محل سکونت") },
            minLines = 3,
        )

        OutlinedTextField(
            value = userInfo.zipCode,
            onValueChange = onZipCodeChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("کد پستی") },
            singleLine = true,
            isError = userInfo.zipCode.isNotEmpty() && userInfo.zipCode.length < 10,
            supportingText = {
                if (userInfo.zipCode.isNotEmpty() && userInfo.zipCode.length < 10) {
                    Text("کد پستی باید ۱۰ رقم باشد", color = Color(0xFFB00020))
                }
            },
        )

        OutlinedTextField(
            value = userInfo.phoneNumber,
            onValueChange = onPhoneNumberChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("شماره تلفن") },
            singleLine = true,
        )

        if (userInfo.showMobile) {
            OutlinedTextField(
                value = userInfo.mobileNumber,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                label = { Text("شماره موبایل") },
                singleLine = true,
                enabled = false,
            )
        }
    }
}
