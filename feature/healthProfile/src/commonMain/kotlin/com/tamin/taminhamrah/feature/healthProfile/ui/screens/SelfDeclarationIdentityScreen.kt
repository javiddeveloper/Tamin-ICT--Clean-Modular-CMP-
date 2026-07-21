package com.tamin.taminhamrah.feature.healthProfile.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.healthProfile.ui.components.*
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationUiState
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SelfDeclarationIdentityScreen(
    state: SelfDeclarationUiState,
    onIntent: (SelfDeclarationIntent) -> Unit,
    onBackClicked: () -> Unit
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            HealthTopAppBar(onBackClicked = onBackClicked)
        },
        bottomBar = {
            HealthIrritateNavigationBar(
                primaryText = "مرحلهٔ بعدی",
                onPrimaryClick = { onIntent(SelfDeclarationIntent.ChangeStep(SelfDeclarationStep.PERSONAL)) },
                secondaryText = "انصراف",
                onSecondaryClick = onBackClicked
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .background(taminColors.bgPage)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HealthProgressBar(currentStep = 1, totalSteps = 10)

            TaminText(
                text = "اطلاعات هویتی",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = taminColors.textPrimary
                )
            )

            TaminText(
                text = "لطفاً اطلاعات هویتی ثبت شدهٔ خود در سازمان تأمین اجتماعی را بررسی و در صورت صحت، تایید کنید.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = taminColors.textTertiary,
                    lineHeight = 22.sp
                )
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
                border = BorderStroke(1.dp, taminColors.border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    IdentityRow(label = "نام و نام خانوادگی:", value = "${state.patientName} ${state.patientFamily}")
                    IdentityRow(label = "نام پدر:", value = state.patientFather)
                    IdentityRow(label = "جنسیت:", value = state.patientGender)
                    IdentityRow(label = "تاریخ تولد:", value = state.patientBirthDate)
                    IdentityRow(label = "شماره بیمه:", value = state.insuranceNumber)
                    IdentityRow(label = "نوع بیمه:", value = state.insuranceType)
                    IdentityRow(label = "آخرین مراجعه:", value = state.lastVisitDate)
                }
            }

            InfoBanner(message = "این اطلاعات از پایگاه داده‌های سازمان تأمین اجتماعی استخراج شده و به دلایل امنیتی غیرقابل ویرایش است.")
            Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
        }
    }
}

@Composable
fun IdentityRow(label: String, value: String) {
    val taminColors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TaminText(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = taminColors.textTertiary
        )
        TaminText(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = taminColors.textPrimary
        )
    }
    HorizontalDivider(color = taminColors.divider, thickness = 1.dp)
}

@PreviewRtlTheme
@Preview
@Composable
fun SelfDeclarationIdentityScreenPreview() {
    PreviewRtlThemeContent {
        SelfDeclarationIdentityScreen(
            state = SelfDeclarationUiState(),
            onIntent = {},
            onBackClicked = {}
        )
    }
}
