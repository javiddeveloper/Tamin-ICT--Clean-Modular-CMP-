package com.tamin.taminhamrah.feature.workshops.ui.workshopRecentlyAddedMembers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.workshop.WorkshopNewMemberPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import kotlinx.collections.immutable.persistentListOf
import org.koin.compose.koinInject

@Composable
fun WorkshopRecentlyAddedMembersScreen(
    workshopId: String,
    branchCode: String,
    viewModel: WorkshopRecentlyAddedMembersViewModel = koinInject()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(workshopId, branchCode) {
        viewModel.sendIntent(WorkshopRecentlyAddedMembersIntent.Load(workshopId, branchCode))
    }

    WorkshopRecentlyAddedMembersContent(state = state)
}

@Composable
fun WorkshopRecentlyAddedMembersContent(state: WorkshopRecentlyAddedMembersUiState) {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (state.isLoading) CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        else if (!state.error.isNullOrEmpty()) Text("خطا: ${state.error}", color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
        else {
            if (state.list.isEmpty()) Text("لیست خالی است", modifier = Modifier.align(Alignment.Center))
            else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.list) { item ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("شناسه بیمه: ${item.insuranceId ?: "ندارد"}")
                                Text("شغل: ${item.job ?: "ندارد"}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun WorkshopRecentlyAddedMembersScreenPreview() {
    PreviewRtlThemeContent {
        WorkshopRecentlyAddedMembersContent(
            state = WorkshopRecentlyAddedMembersUiState(
                isLoading = false,
                list = persistentListOf(
                    WorkshopNewMemberPR(
                        id = 1,
                        dateOfStart = null,
                        insuranceId = "12345678",
                        relationWithTamin = null,
                        organizationId = null,
                        workshopId = "9900020917749",
                        job = "توسعه دهنده"
                    )
                ),
                error = null
            )
        )
    }
}
