package com.tamin.taminhamrah.feature.healthProfile.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
fun SelfDeclarationContactScreen(
    state: SelfDeclarationUiState,
    onIntent: (SelfDeclarationIntent) -> Unit,
    onBackClicked: () -> Unit
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    val isNextEnabled = state.mobile.length >= 10 && state.city.isNotEmpty() && state.province.isNotEmpty() && state.address.isNotEmpty()

    Scaffold(
        topBar = {
            HealthTopAppBar(onBackClicked = onBackClicked)
        },
        bottomBar = {
            HealthIrritateNavigationBar(
                primaryText = "مرحلهٔ بعدی",
                primaryEnabled = isNextEnabled,
                onPrimaryClick = { onIntent(SelfDeclarationIntent.ChangeStep(SelfDeclarationStep.EMERGENCY)) },
                secondaryText = "مرحلهٔ قبلی",
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
            HealthProgressBar(currentStep = 3, totalSteps = 10)

            TaminText(
                text = "اطلاعات تماس و سکونت",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = taminColors.textPrimary
                )
            )

            TaminText(
                text = "اطلاعات تماس جهت ارتباط‌های بعدی و موارد اضطراری استفاده خواهد شد.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = taminColors.textTertiary,
                    lineHeight = 22.sp
                )
            )

            StyledTextField(
                value = state.mobile,
                onValueChange = { mob ->
                    onIntent(SelfDeclarationIntent.UpdateState { copy(mobile = mob) })
                },
                label = "شماره تلفن همراه",
                placeholder = "۰۹۱۲۳۴۵۶۷۸۹"
            )

            StyledTextField(
                value = state.landline,
                onValueChange = { land ->
                    onIntent(SelfDeclarationIntent.UpdateState { copy(landline = land) })
                },
                label = "تلفن ثابت (به همراه کد استان)",
                placeholder = "۰۲۱۲۲۳۳۴۴۵۵"
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    StyledTextField(
                        value = state.province,
                        onValueChange = { prov ->
                            onIntent(SelfDeclarationIntent.UpdateState { copy(province = prov) })
                        },
                        label = "استان",
                        placeholder = "مثلاً تهران"
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    StyledTextField(
                        value = state.city,
                        onValueChange = { c ->
                            onIntent(SelfDeclarationIntent.UpdateState { copy(city = c) })
                        },
                        label = "شهر",
                        placeholder = "مثلاً تهران"
                    )
                }
            }

            StyledTextField(
                value = state.address,
                onValueChange = { addr ->
                    onIntent(SelfDeclarationIntent.UpdateState { copy(address = addr) })
                },
                label = "نشانی کامل محل سکونت",
                placeholder = "خیابان، کوچه، پلاک، واحد",
                singleLine = false
            )

            StyledTextField(
                value = state.postalCode,
                onValueChange = { post ->
                    onIntent(SelfDeclarationIntent.UpdateState { copy(postalCode = post) })
                },
                label = "کد پستی ۱۰ رقمی",
                placeholder = "۱۲۳۴۵۶۷۸۹۰"
            )

            OutlinedButton(
                onClick = { /* Open map dialog */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = taminColors.blueText),
                border = BorderStroke(1.dp, taminColors.blueText)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                TaminText("انتخاب موقعیت روی نقشه (جهت ثبت آدرس دقیق)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
        }
    }
}

@PreviewRtlTheme
@Preview
@Composable
fun SelfDeclarationContactScreenPreview() {
    PreviewRtlThemeContent {
        SelfDeclarationContactScreen(
            state = SelfDeclarationUiState(city = "تهران", province = "تهران", address = "خیابان آزادی"),
            onIntent = {},
            onBackClicked = {}
        )
    }
}
