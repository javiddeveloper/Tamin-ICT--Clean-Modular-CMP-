package com.tamin.taminhamrah.feature.studentInsuranceContract.ui.step

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.components.SelectableField
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.BranchSelectionFormPR
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.CityOptionPR

@Composable
fun SelectBranchStepContent(
    branchSelection: BranchSelectionFormPR,
    provinces: List<CityOptionPR>,
    cities: List<CityOptionPR>,
    branches: List<CityOptionPR>,
    isProvincesLoading: Boolean,
    isCitiesLoading: Boolean,
    isBranchesLoading: Boolean,
    onProvinceSelected: (CityOptionPR) -> Unit,
    onCitySelected: (CityOptionPR) -> Unit,
    onBranchSelected: (CityOptionPR) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        InfoCard(
            text = "متقاضی محترم، پس از ثبت قرارداد، امکان تغییر شعبه وجود ندارد؛ لطفا در انتخاب شعبه دقت نمایید.",
        )
        InfoCard(
            text = "در صورت عدم ارائه خدمات الکترونیکی، ممکن است نیاز به مراجعه حضوری به شعبه انتخابی داشته باشید.",
        )

        SelectableField(
            label = "استان",
            options = provinces,
            selectedCode = branchSelection.provinceCode,
            selectedName = branchSelection.provinceName,
            isLoading = isProvincesLoading,
            onSelected = onProvinceSelected,
        )

        SelectableField(
            label = "شهر",
            options = cities,
            selectedCode = branchSelection.cityCode,
            selectedName = branchSelection.cityName,
            isLoading = isCitiesLoading,
            enabled = branchSelection.provinceCode.isNotBlank(),
            onSelected = onCitySelected,
        )

        SelectableField(
            label = "شعبه تأمین اجتماعی",
            options = branches,
            selectedCode = branchSelection.branchCode,
            selectedName = branchSelection.branchName,
            isLoading = isBranchesLoading,
            enabled = branchSelection.cityCode.isNotBlank(),
            onSelected = onBranchSelected,
        )
    }
}

@Composable
private fun InfoCard(
    text: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                text = text,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
