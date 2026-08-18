package com.tamin.taminhamrah.feature.workshops.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.digitsOnly
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.workshop_all_items
import taminx.core.core_ui.workshop_branch_code
import taminx.core.core_ui.workshop_code
import taminx.core.core_ui.workshop_search

/**
 * The two code fields the list is searched by.
 *
 * Both are digits only, and both are held as typed ASCII: they go into query parameters, where the
 * Persian digits the rest of the screen shows would not match anything. Anything non-numeric is
 * dropped as it is typed rather than accepted and answered with an empty list.
 */
@Composable
fun WorkshopSearchPanel(
    workshopId: String,
    branchCode: String,
    onWorkshopIdChange: (String) -> Unit,
    onBranchCodeChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(CornerRadius.lg)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        OutlinedTextField(
            value = workshopId,
            onValueChange = { onWorkshopIdChange(it.digitsOnly().take(CODE_MAX_LENGTH)) },
            label = { Text(stringResource(Res.string.workshop_code)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = branchCode,
            onValueChange = { onBranchCodeChange(it.digitsOnly().take(CODE_MAX_LENGTH)) },
            label = { Text(stringResource(Res.string.workshop_branch_code)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            TaminPrimaryButton(
                text = stringResource(Res.string.workshop_search),
                onClick = onSearch,
                modifier = Modifier.weight(1f),
            )
            TaminOutlinedButton(
                text = stringResource(Res.string.workshop_all_items),
                onClick = onClear,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/** The service's own cap on both codes. */
private const val CODE_MAX_LENGTH = 20
