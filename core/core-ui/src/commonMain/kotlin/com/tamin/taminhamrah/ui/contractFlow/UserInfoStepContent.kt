package com.tamin.taminhamrah.ui.contractFlow

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
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.contractFlow.UploadImagePR
import com.tamin.taminhamrah.model.contractFlow.UserInfoFormPR
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.bs_city
import taminx.core.core_ui.contract_field_address
import taminx.core.core_ui.contract_field_phone
import taminx.core.core_ui.contract_user_info_edit_hint
import taminx.core.core_ui.identity_field_mobile
import taminx.core.core_ui.occurrence_field_postal_code
import taminx.core.core_ui.occurrence_field_postal_code_error

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
    val colors = LocalTaminColors.current

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
                    text = stringResource(Res.string.contract_user_info_edit_hint),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        SelectableField(
            label = stringResource(Res.string.bs_city),
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
            label = { Text(stringResource(Res.string.contract_field_address)) },
            minLines = 3,
        )

        OutlinedTextField(
            value = userInfo.zipCode,
            onValueChange = onZipCodeChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(Res.string.occurrence_field_postal_code)) },
            singleLine = true,
            isError = userInfo.zipCode.isNotEmpty() && userInfo.zipCode.length < 10,
            supportingText = {
                if (userInfo.zipCode.isNotEmpty() && userInfo.zipCode.length < 10) {
                    Text(
                        stringResource(Res.string.occurrence_field_postal_code_error),
                        color = colors.dangerText,
                    )
                }
            },
        )

        OutlinedTextField(
            value = userInfo.phoneNumber,
            onValueChange = onPhoneNumberChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(Res.string.contract_field_phone)) },
            singleLine = true,
        )

        if (userInfo.showMobile) {
            OutlinedTextField(
                value = userInfo.mobileNumber,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(Res.string.identity_field_mobile)) },
                singleLine = true,
                enabled = false,
            )
        }
    }
}
