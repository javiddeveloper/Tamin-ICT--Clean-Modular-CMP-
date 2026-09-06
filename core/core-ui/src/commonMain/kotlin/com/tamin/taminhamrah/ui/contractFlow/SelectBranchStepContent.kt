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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.common.ProvincePR
import com.tamin.taminhamrah.model.contractFlow.BranchSelectionFormPR
import com.tamin.taminhamrah.model.contracts.BranchPR
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheetType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.bs_branch
import taminx.core.core_ui.bs_city
import taminx.core.core_ui.bs_province
import taminx.core.core_ui.contract_branch_notice_1
import taminx.core.core_ui.contract_branch_notice_2

val ContractBranchNotices: ImmutableList<StringResource> = persistentListOf(
    Res.string.contract_branch_notice_1,
    Res.string.contract_branch_notice_2,
)

@Composable
fun SelectBranchStepContent(
    branchSelection: BranchSelectionFormPR,
    provinces: List<ProvincePR>,
    cities: List<CityPR>,
    branches: List<BranchPR>,
    isProvincesLoading: Boolean,
    isCitiesLoading: Boolean,
    isBranchesLoading: Boolean,
    onProvinceSelected: (ProvincePR) -> Unit,
    onCitySelected: (CityPR) -> Unit,
    onBranchSelected: (BranchPR) -> Unit,
    modifier: Modifier = Modifier,
    notices: ImmutableList<StringResource> = ContractBranchNotices,
    provincesError: String? = null,
    citiesError: String? = null,
    branchesError: String? = null,
    onRetryProvinces: (() -> Unit)? = null,
    onRetryCities: (() -> Unit)? = null,
    onRetryBranches: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        notices.forEach { noticeRes ->
            InfoCard(text = stringResource(noticeRes))
        }

        SelectableField(
            label = stringResource(Res.string.bs_province),
            options = provinces,
            selectedCode = branchSelection.provinceCode,
            selectedName = branchSelection.provinceName,
            optionCode = { it.provinceCode },
            optionName = { it.provinceName },
            isLoading = isProvincesLoading,
            onSelected = onProvinceSelected,
            errorMessage = provincesError,
            onRetry = onRetryProvinces,
            sheetType = TaminBottomSheetType.PROVINCE,
        )

        SelectableField(
            label = stringResource(Res.string.bs_city),
            options = cities,
            selectedCode = branchSelection.cityCode,
            selectedName = branchSelection.cityName,
            optionCode = { it.cityCode },
            optionName = { it.cityName },
            isLoading = isCitiesLoading,
            enabled = branchSelection.provinceCode.isNotBlank(),
            onSelected = onCitySelected,
            errorMessage = citiesError,
            onRetry = onRetryCities,
            sheetType = TaminBottomSheetType.CITY,
        )

        SelectableField(
            label = stringResource(Res.string.bs_branch),
            options = branches,
            selectedCode = branchSelection.branchCode,
            selectedName = branchSelection.branchName,
            optionCode = { it.code },
            optionName = { it.name },
            isLoading = isBranchesLoading,
            enabled = branchSelection.cityCode.isNotBlank(),
            onSelected = onBranchSelected,
            errorMessage = branchesError,
            onRetry = onRetryBranches,
            sheetType = TaminBottomSheetType.BRANCH,
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
