package com.tamin.taminhamrah.feature.workshops.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsEvent
import com.tamin.taminhamrah.feature.workshops.ui.model.WorkshopAction
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.getString

/**
 * Turns the list's one-off events into navigation and messages.
 *
 * Event collection is its own composable so the screen stays stateless and the route stays a
 * wiring layer. The message text is resolved here, in the UI: a `StringResource` travels in the
 * event because resolving one inside a ViewModel hangs under test.
 */
@Composable
fun HandleWorkshopsEvents(
    events: Flow<WorkshopsEvent>,
    onOpenAction: (WorkshopAction, String, String, String) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    LaunchedEffect(events) {
        events.collect { event ->
            when (event) {
                is WorkshopsEvent.Navigate -> onOpenAction(
                    event.action,
                    event.workshopId,
                    event.branchCode,
                    event.workshopName,
                )

                is WorkshopsEvent.ShowMessage ->
                    snackbarHostState.showSnackbar(getString(event.message))

                is WorkshopsEvent.ShowToast -> {}
            }
        }
    }
}
