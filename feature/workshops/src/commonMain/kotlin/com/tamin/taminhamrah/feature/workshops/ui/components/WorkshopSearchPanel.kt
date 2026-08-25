package com.tamin.taminhamrah.feature.workshops.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.feature.workshops.ui.WorkshopConstants
import com.tamin.taminhamrah.ui.digitsOnly
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.workshop_branch_code
import taminx.core.core_ui.workshop_code

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
    WorkshopSearchCard(onSearch = onSearch, onClear = onClear, modifier = modifier) {
        WorkshopTextField(
            label = stringResource(Res.string.workshop_code),
            value = workshopId,
            onValueChange = { onWorkshopIdChange(it.digitsOnly().take(WorkshopConstants.WORKSHOP_CODE_MAX_LENGTH)) },
        )
        WorkshopTextField(
            label = stringResource(Res.string.workshop_branch_code),
            value = branchCode,
            onValueChange = { onBranchCodeChange(it.digitsOnly().take(WorkshopConstants.WORKSHOP_CODE_MAX_LENGTH)) },
        )
    }
}

