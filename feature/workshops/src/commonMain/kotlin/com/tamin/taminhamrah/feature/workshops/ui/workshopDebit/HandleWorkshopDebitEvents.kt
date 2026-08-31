package com.tamin.taminhamrah.feature.workshops.ui.workshopDebit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.getString

/**
 * A refusal always says something.
 *
 * The service's own wording is preferred when it sent any; the fallback exists because a blank
 * rejection message used to mean the user was shown nothing at all. Both now go to the app's
 * toast host — the snackbar this used to write to was never hosted, so neither was ever seen.
 */
@Composable
fun HandleWorkshopDebitEvents(
    events: Flow<WorkshopDebitEvent>,
    onOpenUrl: (String) -> Unit,
) {
    val toaster = LocalToaster.current
    LaunchedEffect(events, toaster) {
        events.collect { event ->
            when (event) {
                is WorkshopDebitEvent.OpenPaymentPage -> onOpenUrl(event.url)
                is WorkshopDebitEvent.ShowServerMessage -> toaster.error(event.message)
                is WorkshopDebitEvent.ShowMessage -> toaster.error(getString(event.message))
            }
        }
    }
}
