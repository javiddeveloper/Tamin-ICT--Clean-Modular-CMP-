package com.tamin.taminhamrah.feature.pensionInquiry.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.pensionInquiry.ui.contract.PensionInquiryIntent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.contract.PensionInquiryUiState
import com.tamin.taminhamrah.model.common.BeneficiaryPR
import com.tamin.taminhamrah.model.pension.EdictPensionerPR
import com.tamin.taminhamrah.model.pension.PensionIdPR
import com.tamin.taminhamrah.model.pension.PensionInquiryPR
import com.tamin.taminhamrah.model.pension.PayRollPR
import com.tamin.taminhamrah.model.pension.RecipientPR
import com.tamin.taminhamrah.model.personal.DisabilityDependentPR
import com.tamin.taminhamrah.model.personal.DisabilityPersonalInfoPR
import com.tamin.taminhamrah.model.personal.AgePR
import com.tamin.taminhamrah.model.personal.PersonalInfoPR
import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoPR
import com.tamin.taminhamrah.model.personal.survivorList.ConfirmSurvivorPR
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PensionInquiryScreen(
    viewModel: PensionInquiryViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(PensionInquiryIntent.LoadPensionInquiry)
        viewModel.sendIntent(PensionInquiryIntent.LoadPensionerIds)
        viewModel.sendIntent(PensionInquiryIntent.LoadRecipients)
        viewModel.sendIntent(PensionInquiryIntent.LoadPersonalInfo)
        viewModel.sendIntent(PensionInquiryIntent.LoadBeneficiaryList)
        viewModel.sendIntent(PensionInquiryIntent.LoadDisabilityDependentInfo)
        viewModel.sendIntent(PensionInquiryIntent.LoadConfirmSurvivorsList)
        viewModel.sendIntent(PensionInquiryIntent.LoadAge(1379L))
        viewModel.sendIntent(PensionInquiryIntent.LoadUserAge)
        viewModel.sendIntent(PensionInquiryIntent.LoadPensionerPayRoll(emptyList()))
        viewModel.sendIntent(PensionInquiryIntent.LoadDisabilityPersonalInfo)
    }

    PensionInquiryContent(state)
}

@Composable
fun PensionInquiryContent(
    state: PensionInquiryUiState
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("استعلام مستمری") })
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading && state.pensionList.isEmpty() && state.pensionerIds.isEmpty() && state.recipients.isEmpty() && state.personalInfo == null && state.deceasedInfo == null && state.disabilityPersonalInfo == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null && state.pensionList.isEmpty() && state.pensionerIds.isEmpty() && state.recipients.isEmpty() && state.personalInfo == null && state.deceasedInfo == null && state.disabilityPersonalInfo == null) {
                Text(
                    text = state.error ?: "خطای ناشناخته",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (state.disabilityPersonalInfo != null) {
                        item {
                            Text(
                                text = "اطلاعات فردی (از کار افتادگی):",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            DisabilityPersonalInfoItem(state.disabilityPersonalInfo)
                        }
                    }

                    if (state.deceasedInfo != null) {
                        item {
                            Text(
                                text = "اطلاعات متوفی:",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            DeceasedInfoItem(state.deceasedInfo)
                        }
                    }

                    if (state.age != null) {
                        item {
                            Text(
                                text = "سن:",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            AgeItem(state.age)
                        }
                    }

                    if (state.disabilityDependentInfo.isNotEmpty()){
                        item {
                            Text(
                                text = "مستمری از کار افتادگی / تبعی ها:",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                        items(state.disabilityDependentInfo){
                            DependentDisabilityInfo(it)
                        }
                    }

                    if (state.personalInfo != null) {
                        item {
                            Text(
                                text = "اطلاعات فردی:",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            PersonalInfoItem(state.personalInfo)
                        }
                    }

                    if (state.recipients.isNotEmpty()) {
                        item {
                            Text(
                                text = "لیست دریافت‌کنندگان:",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(state.recipients) { recipient ->
                            RecipientItem(recipient)
                        }
                    }

                    if (state.edictPensioner != null) {
                        item {
                            Text(
                                text = "حکم مستمری:",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            EdictItem(state.edictPensioner)
                        }
                    }

                    if (state.pensionerIds.isNotEmpty()) {
                        item {
                            Text(
                                text = "شناسه‌های مستمری‌بگیر:",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )
                        }
                        items(state.pensionerIds) { idItem ->
                            PensionIdItem(idItem)
                        }
                    }

                    if (state.pensionList.isNotEmpty()) {
                        item {
                            Text(
                                text = "لیست استعلام مستمری:",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )
                        }
                        items(state.pensionList) { item ->
                            PensionItem(item)
                        }
                    }
                    if (state.beneficiaryList.isNotEmpty()){
                        item {
                            Text(
                                text = "لیست بانک ها:",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )
                        }
                        items(state.beneficiaryList) { item ->
                            BeneficiaryItem(item)
                        }
                    }

                    if (state.confirmSurvivorsList.isNotEmpty()) {
                        item {
                            Text(
                                text = "تایید لیست بازماندگان:",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )
                        }
                        items(state.confirmSurvivorsList) { item ->
                            ConfirmSurvivorItem(item)
                        }
                    }

                    if (state.payRoll != null) {
                        item {
                            Text(
                                text = "فیش حقوقی:",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            )
                            PayRollItem(state.payRoll)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConfirmSurvivorItem(item: ConfirmSurvivorPR) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "شناسه درخواست: ${item.request?.id}", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun AgeItem(age: AgePR) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "سن: ${age.age}", style = MaterialTheme.typography.titleMedium)
            Text(text = "تاریخ تولد: ${age.birthDate}")
        }
    }
}

@Composable
fun DisabilityPersonalInfoItem(info: DisabilityPersonalInfoPR) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val personal = info.personal
            if (personal != null) {
                Text(text = "نام: ${personal.firstName} ${personal.lastName}", style = MaterialTheme.typography.titleMedium)
                Text(text = "نام پدر: ${personal.fatherName}")
                Text(text = "کد ملی: ${personal.nationalId}")
                Text(text = "تاریخ تولد: ${personal.dateOfBirth}")
                Text(text = "جنسیت: ${personal.genderDesc}")
            }
            Text(text = "شماره بیمه: ${info.insuranceId}")
            Text(text = "شعبه: ${info.branchName} (${info.branch})")
            Text(text = "استان: ${info.provinceName}")
            Text(text = "شماره موبایل: ${info.mobileNumber}")
            val work = info.work
            if (work != null) {
                Text(text = "شغل: ${work.jobDescription}")
                Text(text = "کد کارگاه: ${work.workshopId}")
            }
            Text(text = "سن: ${info.strAge} (${info.yearsAge} سال، ${info.monthsAge} ماه، ${info.daysAge} روز)")
            Text(text = "وضعیت تایید: ${if (info.confirmed) "تایید شده" else "تایید نشده"}")
        }
    }
}

@Composable
fun PersonalInfoItem(info: PersonalInfoPR) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val personal = info.personal
            if (personal != null) {
                Text(text = "نام: ${personal.firstName} ${personal.lastName}", style = MaterialTheme.typography.titleMedium)
                Text(text = "نام پدر: ${personal.fatherName}")
                Text(text = "کد ملی: ${personal.nationalId}")
                Text(text = "شماره شناسنامه: ${personal.ssn}")
                Text(text = "جنسیت: ${personal.genderDesc}")
            }
            Text(text = "شماره بیمه: ${info.insuranceId}")
            Text(text = "شعبه: ${info.branch}")
            Text(text = "استان: ${info.provinceName}")
            Text(text = "شماره موبایل: ${info.mobileNumber}")
        }
    }
}

@Composable
fun DeceasedInfoItem(info: DeceasedInfoPR) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val personal = info.personal
            if (personal != null) {
                Text(text = "نام متوفی: ${personal.firstName} ${personal.lastName}", style = MaterialTheme.typography.titleMedium)
                Text(text = "نام پدر: ${personal.fatherName}")
                Text(text = "کد ملی: ${personal.nationalId}")
                Text(text = "تاریخ فوت: ${info.deadDate}")
            }
            Text(text = "شعبه: ${info.branchName} (${info.branchCode})")
            Text(text = "شماره مستمری: ${info.pensionerId}")
            Text(text = "سن: ${info.yearsAge} سال و ${info.monthsAge} ماه و ${info.daysAge} روز")
        }
    }
}

@Composable
fun EdictItem(edict: EdictPensionerPR) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val fullName = "${edict.edictInfo?.firstName ?: ""} ${edict.edictInfo?.lastName ?: ""}".trim()
            Text(text = "نام و نام خانوادگی: $fullName", style = MaterialTheme.typography.titleMedium)
            Text(text = "شعبه: ${edict.branchName}")
            Text(text = "سال: ${edict.edictYear} ماه: ${edict.edictMonth}")
            Text(text = "مبلغ کل: ${edict.edictInfo?.totalAmount ?: "0"}")
            Text(text = "قابل پرداخت: ${edict.edictInfo?.payableMonthly ?: "0"}")
            Text(text = "به حروف: ${edict.edictInfo?.lettersPayableMonthly ?: ""}")
        }
    }
}

@Composable
fun PensionIdItem(item: PensionIdPR) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "شناسه مستمری‌بگیر: ${item.pensionerId}")
        }
    }
}
@Composable
fun DependentDisabilityInfo(item: DisabilityDependentPR) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "نام: ${item.firstName}")
            Text(text = "نسبت: ${item.relation}")
        }
    }
}

@Composable
fun RecipientItem(item: RecipientPR) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "نام دریافت‌کننده: ${item.recipientName}")
            Text(text = "کد دریافت‌کننده: ${item.recipientCode}")
        }
    }
}

@Composable
fun PensionItem(item: PensionInquiryPR) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "نام: ${item.fullName}", style = MaterialTheme.typography.titleMedium)
            Text(text = "کد شعبه: ${item.branchCode}")
            Text(text = "شماره بیمه: ${item.insuranceNumber}")
            Text(text = "مبلغ پرداختی: ${item.paymentAmount}")
            Text(text = "وضعیت: ${item.statusDesc}")
        }
    }
}

@Composable
fun BeneficiaryItem(item: BeneficiaryPR) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "نام بانک: ${item.bankName}", style = MaterialTheme.typography.titleMedium)
            Text(text = "کد بانک: ${item.bankCode}")
        }
    }
}

@Composable
fun PayRollItem(item: PayRollPR) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "توضیحات: ${item.tprDesc}", style = MaterialTheme.typography.titleMedium)
            Text(text = "مبلغ کل: ${item.sumAmount}")
            Text(text = "مبلغ قابل پرداخت: ${item.sumPay}")
            Text(text = "سال: ${item.hisYear} ماه: ${item.hisMon}")
            Text(text = "نوع: ${item.clpType}")
        }
    }
}
