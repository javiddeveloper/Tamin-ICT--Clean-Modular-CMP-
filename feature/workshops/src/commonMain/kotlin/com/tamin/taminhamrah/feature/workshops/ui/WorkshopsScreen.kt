package com.tamin.taminhamrah.feature.workshops.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.model.workshop.EmployerWorkshopPR
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.model.workshop.EmployerAgreementPR
import org.koin.compose.viewmodel.koinViewModel

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsIntent
import com.tamin.taminhamrah.feature.workshops.ui.contract.WorkshopsUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkshopsScreen(
    viewModel: WorkshopsViewModel = koinViewModel(),
    navigateToPaymentSheets: (String, String) -> Unit = { _, _ -> },
    navigateToWorkshopDebit: (String, String) -> Unit = { _, _ -> },
    navigateToWorkshopDebtInquiry: (String, String) -> Unit = { _, _ -> },
    navigateToEmployerAgreement: (String, String) -> Unit = { _, _ -> },
    navigateToManagementDebit: (String, String) -> Unit = { _, _ -> },
    navigateToWorkshopMembers: (String, String) -> Unit = { _, _ -> },
    navigateToWorkshopStackholders: (String, String) -> Unit = { _, _ -> },
    navigateToWorkshopRecentlyAddedMembers: (String, String) -> Unit = { _, _ -> },
    navigateToObjectionableDebit: (String, String) -> Unit = { _, _ -> }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var workshopId by remember { mutableStateOf("") }
    var branchCode by remember { mutableStateOf("") }
    var workshopStatus by remember { mutableStateOf("") }
    var selectedWorkshop by remember { mutableStateOf<Pair<String, String>?>(null) }

    WorkshopsContent(
        uiState = uiState,
        workshopId = workshopId,
        onWorkshopIdChange = { workshopId = it },
        branchCode = branchCode,
        onBranchCodeChange = { branchCode = it },
        workshopStatus = workshopStatus,
        onWorkshopStatusChange = { workshopStatus = it },
        onLoadClick = {
            viewModel.sendIntent(
                WorkshopsIntent.LoadWorkshops(
                    workshopId = workshopId.takeIf { it.isNotBlank() },
                    branchCode = branchCode.takeIf { it.isNotBlank() },
                    workshopStatus = workshopStatus.takeIf { it.isNotBlank() }
                )
            )
        },
        onWorkshopClick = { wId, bCode ->
            selectedWorkshop = wId to bCode
        }
    )

    if (selectedWorkshop != null) {
        val (wId, bCode) = selectedWorkshop!!
        ModalBottomSheet(
            onDismissRequest = { selectedWorkshop = null }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "لیست برگ پرداخت",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedWorkshop = null
                            navigateToPaymentSheets(wId, bCode)
                        }
                        .padding(16.dp)
                )
                Text(
                    text = "لیست بدهی کارگاه",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedWorkshop = null
                            navigateToWorkshopDebit(wId, bCode)
                        }
                        .padding(16.dp)
                )
                Text(
                    text = "استعلام بدهی کارگاه",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedWorkshop = null
                            navigateToWorkshopDebtInquiry(wId, bCode)
                        }
                        .padding(16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun WorkshopsContent(
    uiState: WorkshopsUiState,
    workshopId: String,
    onWorkshopIdChange: (String) -> Unit,
    branchCode: String,
    onBranchCodeChange: (String) -> Unit,
    workshopStatus: String,
    onWorkshopStatusChange: (String) -> Unit,
    onLoadClick: () -> Unit,
    onWorkshopClick: (String, String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = workshopId,
            onValueChange = onWorkshopIdChange,
            label = { Text("کد کارگاه (workshopId)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = branchCode,
            onValueChange = onBranchCodeChange,
            label = { Text("کد شعبه (branchCode)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = workshopStatus,
            onValueChange = onWorkshopStatusChange,
            label = { Text("وضعیت کارگاه (workshopStatus)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLoadClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("ارسال و دریافت اطلاعات")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text(
                    text = "خطا: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                if (uiState.agreements.isEmpty()) {
                    Text(
                        text = "هیچ کارگاهی یافت نشد",
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.agreements) { agreement ->
                            WorkshopItem(
                                agreement = agreement,
                                onClick = onWorkshopClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WorkshopItem(
    agreement: EmployerAgreementPR,
    onClick: (String, String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                val wId = agreement.workshop?.workshopId ?: ""
                val bCode = agreement.workshop?.branchCode ?: ""
                if (wId.isNotBlank() && bCode.isNotBlank()) {
                    onClick(wId, bCode)
                }
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "کارگاه: ${agreement.workshop?.workshopName ?: "نامشخص"}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "کد کارگاه: ${agreement.workshop?.workshopId ?: "ندارد"}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "نام کارفرما: ${agreement.workshop?.employerName ?: "ندارد"}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun WorkshopsContentPreview() {
    PreviewRtlThemeContent {
        WorkshopsContent(
            uiState = WorkshopsUiState(
                isLoading = false,
                agreements = listOf(
                    EmployerAgreementPR(
                        pymseq = null,
                        regno = null,
                        firstname = null,
                        emailaddr = null,
                        nationalno = null,
                        mobileno = null,
                        startdate = null,
                        mastcusttype = null,
                        createdt = null,
                        masttyp = null,
                        logicalDeleted = null,
                        regemailseq = null,
                        lastname = null,
                        special = null,
                        risuid = null,
                        nationalcode = null,
                        enddate = null,
                        letDate = null,
                        regdate = null,
                        roletype = null,
                        dname = null,
                        letNo = null,
                        createuid = null,
                        workshop = EmployerWorkshopPR(
                            sswn = null,
                            branchTitle = "شعبه نمونه",
                            workshopApproveDate = null,
                            inclusionDate = null,
                            brhCode = null,
                            activityName = null,
                            workshopRegisterDate = null,
                            branchCode = "123",
                            workshopName = "کارگاه کامپیوتر توکلی",
                            employerName = "علی توکلی",
                            actitvityCode = null,
                            userId = null,
                            workshopId = "9900020917749",
                            workshopUnemployedStat = null
                        )
                    )
                ),
                error = null
            ),
            workshopId = "9900020917749",
            onWorkshopIdChange = {},
            branchCode = "123",
            onBranchCodeChange = {},
            workshopStatus = "",
            onWorkshopStatusChange = {},
            onLoadClick = {},
            onWorkshopClick = { _, _ -> }
        )
    }
}
