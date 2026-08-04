package com.tamin.taminhamrah.feature.profile.ui.addDependent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.AddDependentEvent
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.AddDependentIntent
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.AddDependentState
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.DocType
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.StepperMode
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.UploadedDocument
import com.tamin.taminhamrah.model.addDependent.BranchPR
import com.tamin.taminhamrah.model.addDependent.FamilyRelationshipPR
import com.tamin.taminhamrah.model.addDependent.RegistryDataPR
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.rememberJellyOverscroll
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_back

@Composable
fun AddDependentRoute(
    viewModel: AddDependentViewModel,
    onBackClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(AddDependentIntent.InitData)
    }

    HandleAddDependentEvents(
        events = viewModel.events,
        onBackClicked = onBackClicked
    )

    AddDependentScreen(
        state = uiState,
        onIntent = viewModel::sendIntent,
        onBackClicked = onBackClicked
    )
}

@Composable
fun HandleAddDependentEvents(
    events: Flow<AddDependentEvent>,
    onBackClicked: () -> Unit
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            AddDependentEvent.NavigateBack -> onBackClicked()
            is AddDependentEvent.ShowToast -> {}
            is AddDependentEvent.ShowErrorDialog -> {}
            is AddDependentEvent.ShowSuccessDialog -> {}
        }
    }
}

@Composable
fun AddDependentScreen(
    modifier: Modifier = Modifier,
    state: AddDependentState,
    onIntent: (AddDependentIntent) -> Unit,
    onBackClicked: () -> Unit
) {
    val colors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bgPage)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            val stepTitle = when (state.currentStep) {
                1 -> "مرحله ۱ از ۳: استعلام اطلاعات"
                2 -> "مرحله ۲ از ۳: تایید شرایط"
                else -> "مرحله ۳ از ۳: مدارک و ثبت نهایی"
            }

            TaminTopAppBar(
                title = "افزودن فرد تبعی - $stepTitle",
                centerTitle = true,
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = "بازگشت",
                        onClick = onBackClicked
                    )
                }
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState, overscrollEffect = rememberJellyOverscroll())
                        .padding(Spacing.page),
                    verticalArrangement = Arrangement.spacedBy(Spacing.lg)
                ) {
                    when (state.currentStep) {
                        1 -> InquiryInfoStep(state = state, onIntent = onIntent)
                        2 -> VerificationStep(state = state, onIntent = onIntent)
                        3 -> UploadDocumentStep(state = state, onIntent = onIntent)
                    }
                }

                if (state.isLoading) {
                    LoadingStateOverlay()
                }
            }

            // Bottom Actions Container
            WizardBottomBar(state = state, onIntent = onIntent)
        }
    }
}

@Composable
private fun InquiryInfoStep(
    state: AddDependentState,
    onIntent: (AddDependentIntent) -> Unit
) {
    val colors = LocalTaminColors.current
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        com.tamin.taminhamrah.ui.components.TaminJalaliDatePicker(
            title = "انتخاب تاریخ تولد",
            onDismiss = { showDatePicker = false },
            onConfirm = { year, month, day ->
                val dateStr = "$year/${month.toString().padStart(2, '0')}/${day.toString().padStart(2, '0')}"
                val timestamp = com.tamin.taminhamrah.util.PersianDateFormatter.toEpochMillis(year, month, day).toString()
                onIntent(AddDependentIntent.OnBirthDateSelected(dateStr, dateStr, timestamp))
                showDatePicker = false
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.lg),
        colors = CardDefaults.cardColors(containerColor = colors.bgSurface)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Text(
                text = "اطلاعات متقاضی",
                color = colors.textPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            // National ID
            OutlinedTextField(
                value = state.dependentNationalId,
                onValueChange = { if (it.length <= 10) onIntent(AddDependentIntent.OnNationalIdChanged(it)) },
                label = { Text("کد ملی ۱۰ رقمی") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.teal,
                    unfocusedBorderColor = colors.border
                )
            )

            // Birth Date Selector Field
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(1.dp, colors.border, RoundedCornerShape(CornerRadius.md))
                    .clickable { showDatePicker = true }
                    .padding(horizontal = Spacing.md),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = state.birthDatePersian.ifBlank { "تاریخ تولد را انتخاب کنید" },
                    color = if (state.birthDatePersian.isBlank()) colors.textSecondary else colors.textPrimary,
                    fontSize = 14.sp
                )
            }

            // Relationship Selection
            RelationshipDropdown(
                selectedRelationship = state.selectedRelationship,
                onRelationshipSelected = { onIntent(AddDependentIntent.OnRelationshipSelected(it)) }
            )

            Button(
                onClick = { onIntent(AddDependentIntent.SubmitInquiryRegistry) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(CornerRadius.md),
                colors = ButtonDefaults.buttonColors(containerColor = colors.teal)
            ) {
                Text("استعلام اطلاعات ثبت احوال", color = colors.bgSurface, fontWeight = FontWeight.Bold)
            }
        }
    }

    // Inquired Registry Result Card
    state.registryData?.let { registry ->
        RegistryResultCard(registry = registry)

        // Complementary Location Fields
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(CornerRadius.lg),
            colors = CardDefaults.cardColors(containerColor = colors.bgSurface)
        ) {
            Column(
                modifier = Modifier.padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md)
            ) {
                Text(
                    text = "اطلاعات محل سکونت و شعبه",
                    color = colors.textPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                // Branch Dropdown
                BranchDropdown(
                    activeBranches = state.activeBranches,
                    selectedBranch = state.selectedBranch,
                    onBranchSelected = { onIntent(AddDependentIntent.OnBranchSelected(it)) }
                )
            }
        }
    }
}

@Composable
private fun RegistryResultCard(registry: RegistryDataPR) {
    val colors = LocalTaminColors.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.lg),
        colors = CardDefaults.cardColors(containerColor = colors.greenBg)
    ) {
        Column(
            modifier = Modifier.padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            Text(
                text = "اطلاعات تایید شده ثبت احوال",
                color = colors.greenText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(text = "نام: ${registry.firstName}", color = colors.textPrimary, fontSize = 14.sp)
            Text(text = "نام خانوادگی: ${registry.lastName}", color = colors.textPrimary, fontSize = 14.sp)
            Text(text = "نام پدر: ${registry.fatherName}", color = colors.textPrimary, fontSize = 14.sp)
        }
    }
}

@Composable
private fun VerificationStep(
    state: AddDependentState,
    onIntent: (AddDependentIntent) -> Unit
) {
    val colors = LocalTaminColors.current

    when (state.stepperMode) {
        StepperMode.SON_MODE -> {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(CornerRadius.lg),
                colors = CardDefaults.cardColors(containerColor = colors.bgSurface)
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    Text(
                        text = "استعلام وضعیت تحصیلی (فرزند پسر بالای ۱۹ سال)",
                        color = colors.textPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = state.educationCode,
                        onValueChange = { onIntent(AddDependentIntent.OnEducationCodeChanged(it)) },
                        label = { Text("کد استعلام تحصیلی") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Button(
                        onClick = { onIntent(AddDependentIntent.SubmitInquiryEducation) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(CornerRadius.md),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.teal)
                    ) {
                        Text("استعلام کد تحصیلی", color = colors.bgSurface, fontWeight = FontWeight.Bold)
                    }

                    if (state.universityName.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colors.greenBg, RoundedCornerShape(CornerRadius.md))
                                .padding(Spacing.md)
                        ) {
                            Text(
                                text = "دانشگاه/مدرسه: ${state.universityName}",
                                color = colors.greenText,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
        StepperMode.DAUGHTER_MODE -> {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(CornerRadius.lg),
                colors = CardDefaults.cardColors(containerColor = colors.bgSurface)
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    Text(
                        text = "تعهدنامه فرزند دختر (بالای ۱۸ سال)",
                        color = colors.textPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            onIntent(AddDependentIntent.OnDaughterCommitmentToggled(!state.isDaughterCommitmentChecked))
                        }
                    ) {
                        Checkbox(
                            checked = state.isDaughterCommitmentChecked,
                            onCheckedChange = { onIntent(AddDependentIntent.OnDaughterCommitmentToggled(it)) },
                            colors = CheckboxDefaults.colors(checkedColor = colors.teal)
                        )
                        Spacer(modifier = Modifier.width(Spacing.xs))
                        Text(
                            text = "اینجانب عدم ازدواج و عدم اشتغال فرزند دختر خود را تایید مینمایم.",
                            color = colors.textPrimary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
        StepperMode.DEFAULT_MODE -> {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(CornerRadius.lg),
                colors = CardDefaults.cardColors(containerColor = colors.bgSurface)
            ) {
                Box(
                    modifier = Modifier.padding(Spacing.lg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "این نسبت نیازی به تایید استعلام تکمیلی ندارد. لطفا به مرحله بعد بروید.",
                        color = colors.textSecondary,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun UploadDocumentStep(
    state: AddDependentState,
    onIntent: (AddDependentIntent) -> Unit
) {
    val colors = LocalTaminColors.current

    if (state.requiredDocTypes.isEmpty() || state.requiredDocTypes.all { it.isDisabled }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(CornerRadius.lg),
            colors = CardDefaults.cardColors(containerColor = colors.greenBg)
        ) {
            Box(
                modifier = Modifier.padding(Spacing.lg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "اطلاعات ثبت احوال تایید شده است. نیازی به بارگذاری مدرک نیست.",
                    color = colors.greenText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
            Text(
                text = "بارگذاری مدارک الزامی",
                color = colors.textPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            state.requiredDocTypes.filter { !it.isDisabled }.forEach { docType ->
                val uploaded = state.uploadedDocuments.find { it.docType == docType.code }
                DocumentSlotCard(
                    docType = docType,
                    uploaded = uploaded,
                    onUploadClicked = {
                        onIntent(
                            AddDependentIntent.UploadDocument(
                                fileBytes = ByteArray(100),
                                fileName = "${docType.code}.jpg",
                                docType = docType.code
                            )
                        )
                    },
                    onDeleteClicked = { onIntent(AddDependentIntent.DeleteDocument(docType.code)) }
                )
            }
        }
    }
}

@Composable
private fun DocumentSlotCard(
    docType: DocType,
    uploaded: UploadedDocument?,
    onUploadClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    val colors = LocalTaminColors.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.lg),
        colors = CardDefaults.cardColors(containerColor = colors.bgSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = docType.title, color = colors.textPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    text = if (uploaded != null) "بارگذاری شده: ${uploaded.fileName}" else "مدرک بارگذاری نشده است",
                    color = if (uploaded != null) colors.greenText else colors.textSecondary,
                    fontSize = 12.sp
                )
            }

            if (uploaded == null) {
                OutlinedButton(
                    onClick = onUploadClicked,
                    shape = RoundedCornerShape(CornerRadius.md)
                ) {
                    Text("انتخاب فایل", fontSize = 12.sp)
                }
            } else {
                OutlinedButton(
                    onClick = onDeleteClicked,
                    shape = RoundedCornerShape(CornerRadius.md)
                ) {
                    Text("حذف", color = colors.dangerText, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun RelationshipDropdown(
    selectedRelationship: FamilyRelationshipPR?,
    onRelationshipSelected: (FamilyRelationshipPR) -> Unit
) {
    val colors = LocalTaminColors.current
    var expanded by remember { mutableStateOf(false) }

    val sampleRelationships = listOf(
        FamilyRelationshipPR(id = 1, relationCode = "01", relationDesc = "همسر"),
        FamilyRelationshipPR(id = 2, relationCode = "02", relationDesc = "فرزند پسر"),
        FamilyRelationshipPR(id = 3, relationCode = "03", relationDesc = "فرزند دختر")
    )

    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(1.dp, colors.border, RoundedCornerShape(CornerRadius.md))
                .clickable { expanded = true }
                .padding(horizontal = Spacing.md),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = selectedRelationship?.relationDesc ?: "نسبت خانوادگی را انتخاب کنید",
                color = if (selectedRelationship == null) colors.textSecondary else colors.textPrimary,
                fontSize = 14.sp
            )
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            sampleRelationships.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.relationDesc.orEmpty()) },
                    onClick = {
                        onRelationshipSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun BranchDropdown(
    activeBranches: List<BranchPR>,
    selectedBranch: BranchPR?,
    onBranchSelected: (BranchPR) -> Unit
) {
    val colors = LocalTaminColors.current
    var expanded by remember { mutableStateOf(false) }

    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(1.dp, colors.border, RoundedCornerShape(CornerRadius.md))
                .clickable { if (activeBranches.size > 1) expanded = true }
                .padding(horizontal = Spacing.md),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = selectedBranch?.branchName?.ifBlank { selectedBranch?.branchCode } ?: "شعبه تامین اجتماعی را انتخاب کنید",
                color = if (selectedBranch == null) colors.textSecondary else colors.textPrimary,
                fontSize = 14.sp
            )
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            activeBranches.forEach { branch ->
                DropdownMenuItem(
                    text = { Text(branch.branchName.ifBlank { branch.branchCode }) },
                    onClick = {
                        onBranchSelected(branch)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun WizardBottomBar(
    state: AddDependentState,
    onIntent: (AddDependentIntent) -> Unit
) {
    val colors = LocalTaminColors.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.page),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        if (state.currentStep > 1) {
            OutlinedButton(
                onClick = { onIntent(AddDependentIntent.OnPreviousStepClicked) },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(CornerRadius.lg)
            ) {
                Text("مرحله قبل", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Button(
            onClick = { onIntent(AddDependentIntent.OnNextStepClicked) },
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(CornerRadius.lg),
            colors = ButtonDefaults.buttonColors(containerColor = colors.teal)
        ) {
            Text(
                text = if (state.currentStep == 3) "ثبت نهایی درخواست" else "ادامه",
                color = colors.bgSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
