package com.tamin.taminhamrah.ui.contractFlow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.contractFlow.ContractApplicantType
import org.jetbrains.compose.resources.stringResource

@Composable
fun ContractApplicantStepContent(
    selectedType: ContractApplicantType,
    onTypeSelected: (ContractApplicantType) -> Unit,
    availableTypes: List<ContractApplicantType> = listOf(ContractApplicantType.PERSONAL),
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        availableTypes.forEach { type ->
            androidx.compose.foundation.layout.Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = selectedType == type,
                        onClick = { onTypeSelected(type) },
                        role = Role.RadioButton,
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                RadioButton(
                    selected = selectedType == type,
                    onClick = null,
                )
                Text(
                    text = stringResource(type.labelRes),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
