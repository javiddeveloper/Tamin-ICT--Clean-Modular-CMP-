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
                .padding(paddingValues)
                .background(taminColors.bgPage)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HealthProgressBar(currentStep = 3, totalSteps = 10)

            TaminText(
                text = "تماس و سکونت",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = taminColors.textPrimary
                )
            )

            StyledTextField(
                value = state.mobile,
                onValueChange = { valStr ->
                    onIntent(SelfDeclarationIntent.UpdateState { copy(mobile = valStr) })
                },
                label = "شمارهٔ تلفن همراه",
                placeholder = "مثلاً 09123456789",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isValid = state.mobile.startsWith("09") && state.mobile.length == 11,
                errorText = if (state.mobile.isNotEmpty() && (!state.mobile.startsWith("09") || state.mobile.length != 11)) "شماره همراه معتبر نیست" else null
            )

            StyledTextField(
                value = state.email,
                onValueChange = { valStr ->
                    onIntent(SelfDeclarationIntent.UpdateState { copy(email = valStr) })
                },
                label = "آدرس ایمیل (اختیاری)",
                placeholder = "example@mail.com",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StyledTextField(
                    value = state.province,
                    onValueChange = { valStr ->
                        onIntent(SelfDeclarationIntent.UpdateState { copy(province = valStr) })
                    },
                    label = "استان",
                    placeholder = "وارد کنید",
                    modifier = Modifier.weight(1f)
                )
                StyledTextField(
                    value = state.city,
                    onValueChange = { valStr ->
                        onIntent(SelfDeclarationIntent.UpdateState { copy(city = valStr) })
                    },
                    label = "شهر",
                    placeholder = "وارد کنید",
                    modifier = Modifier.weight(1f)
                )
            }

            StyledTextField(
                value = state.address,
                onValueChange = { valStr ->
                    onIntent(SelfDeclarationIntent.UpdateState { copy(address = valStr) })
                },
                label = "آدرس دقیق محل سکونت",
                placeholder = "خیابان، کوچه، پلاک، واحد"
            )

            StyledTextField(
                value = state.postcode,
                onValueChange = { valStr ->
                    onIntent(SelfDeclarationIntent.UpdateState { copy(postcode = valStr) })
                },
                label = "کد پستی",
                placeholder = "کد پستی ۱۰ رقمی",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Map selection placeholder
            OutlinedButton(
                onClick = {},
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, taminColors.blueText),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = taminColors.blueText)
            ) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                TaminText("انتخاب موقعیت روی نقشه (جهت ثبت آدرس دقیق)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
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
