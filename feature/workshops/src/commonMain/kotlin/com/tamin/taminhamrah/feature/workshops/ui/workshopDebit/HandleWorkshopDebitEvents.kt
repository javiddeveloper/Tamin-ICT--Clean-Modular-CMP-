package com.tamin.taminhamrah.feature.workshops.ui.workshopDebit

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.getString

/**
 * A refusal always says something.
 *
 * The service's own wording is preferred when it sent any; the fallback exists because a blank
 * rejection message used to mean the user was shown nothing at all.
 */
@Composable
fun HandleWorkshopDebitEvents(
    events: Flow<WorkshopDebitEvent>,
    onOpenUrl: (String) -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    LaunchedEffect(events) {
        events.collect { event ->
            when (event) {
                is WorkshopDebitEvent.OpenPaymentPage -> onOpenUrl(event.url)
                is WorkshopDebitEvent.ShowServerMessage ->
                    snackbarHostState.showSnackbar(event.message)

                is WorkshopDebitEvent.ShowMessage ->
                    snackbarHostState.showSnackbar(getString(event.message))
            }
        }
    }
}
