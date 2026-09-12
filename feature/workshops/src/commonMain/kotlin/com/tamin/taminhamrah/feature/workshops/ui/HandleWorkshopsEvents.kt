package com.tamin.taminhamrah.feature.workshops.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsEvent
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.getString

/**
 * Turns the list's one-off events into navigation and messages.
 *
 * Event collection is its own composable so the screen stays stateless and the route stays a
 * wiring layer. The message text is resolved here, in the UI: a `StringResource` travels in the
 * event because resolving one inside a ViewModel hangs under test.
 *
 * Messages go to the app's toast host. They used to go to a `SnackbarHostState` created here and
 * hosted nowhere, which meant «لیست بدهی برای این کارگاه یافت نشد» was shown to no one.
 */
@Composable
fun HandleWorkshopsEvents(
    events: Flow<WorkshopsEvent>,
    onOpenAction: (WorkshopsEvent.Navigate) -> Unit,
) {
    val toaster = LocalToaster.current
    LaunchedEffect(events, toaster) {
        events.collect { event ->
            when (event) {
                is WorkshopsEvent.Navigate -> onOpenAction(event)

                is WorkshopsEvent.ShowMessage -> toaster.error(getString(event.message))
                is WorkshopsEvent.ShowToast -> toaster.error(event.message)
            }
        }
    }
}
