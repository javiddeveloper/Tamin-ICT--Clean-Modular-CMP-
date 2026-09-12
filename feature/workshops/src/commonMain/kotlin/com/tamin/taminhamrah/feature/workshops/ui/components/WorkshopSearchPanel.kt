package com.tamin.taminhamrah.feature.workshops.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tamin.taminhamrah.feature.workshops.ui.WorkshopConstants
import com.tamin.taminhamrah.ui.digitsOnly
import com.tamin.taminhamrah.ui.theme.Spacing
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
            onValueChange = { onWorkshopIdChange(it.digitsOnly()) },
            maxLength = WorkshopConstants.WORKSHOP_CODE_MAX_LENGTH,
        )
        WorkshopTextField(
            label = stringResource(Res.string.workshop_branch_code),
            value = branchCode,
            onValueChange = { onBranchCodeChange(it.digitsOnly()) },
            maxLength = WorkshopConstants.WORKSHOP_CODE_MAX_LENGTH,
        )
    }
}

/**
 * A search panel raised into a dialog.
 *
 * [panel] is whichever panel the screen already draws, unchanged — only the surface it sits on
 * differs, so a screen keeps its own fields and its own buttons. The content scrolls because
 * برگ پرداخت‌ها searches on six fields and would otherwise run off a short screen.
 *
 * The panel's own buttons close it: both جست‌وجو and همهٔ موارد apply and dismiss, so the dialog
 * has no separate close action of its own.
 */
@Composable
fun WorkshopSearchDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    panel: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.page)
                .verticalScroll(rememberScrollState()),
        ) {
            panel()
        }
    }
}
