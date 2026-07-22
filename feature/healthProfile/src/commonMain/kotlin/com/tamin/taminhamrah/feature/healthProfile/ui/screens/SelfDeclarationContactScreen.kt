package com.tamin.taminhamrah.feature.healthProfile.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.healthProfile.ui.components.*
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.ContactStepState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import androidx.compose.ui.tooling.preview.Preview
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileIntent

@Composable
fun SelfDeclarationContactScreen(
    state: ContactStepState,
    onIntent: (HealthProfileIntent) -> Unit,
    onBackClicked: () -> Unit
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    val isNextEnabled = state.mobile.length >= 10 && state.cityLabel.isNotEmpty() && state.provinceLabel.isNotEmpty() && state.address.isNotEmpty()

    Scaffold(
        topBar = {
            HealthTopAppBar(
                currentStep = 3,
                totalSteps = 10,
                onBackClicked = onBackClicked
            )
        },
        bottomBar = {
            HealthIrritateNavigationBar(
                primaryText = "مرحلهٔ بعدی",
                primaryEnabled = isNextEnabled,
                onPrimaryClick = { onIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.EMERGENCY)) },
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
                    onIntent(HealthProfileIntent.UpdateContact(state.copy(mobile = mob)))
                },
                label = "شماره تلفن همراه",
                placeholder = "۰۹۱۲۳۴۵۶۷۸۹"
            )

            StyledTextField(
                value = state.landline,
                onValueChange = { land ->
                    onIntent(HealthProfileIntent.UpdateContact(state.copy(landline = land)))
                },
                label = "تلفن ثابت (به همراه کد استان)",
                placeholder = "۰۲۱۲۲۳۳۴۴۵۵"
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    StyledTextField(
                        value = state.provinceLabel,
                        onValueChange = { prov ->
                            onIntent(HealthProfileIntent.UpdateContact(state.copy(provinceLabel = prov)))
                        },
                        label = "استان",
                        placeholder = "مثلاً تهران"
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    StyledTextField(
                        value = state.cityLabel,
                        onValueChange = { c ->
                            onIntent(HealthProfileIntent.UpdateContact(state.copy(cityLabel = c)))
                        },
                        label = "شهر",
                        placeholder = "مثلاً تهران"
                    )
                }
            }

            StyledTextField(
                value = state.address,
                onValueChange = { addr ->
                    onIntent(HealthProfileIntent.UpdateContact(state.copy(address = addr)))
                },
                label = "نشانی کامل محل سکونت",
                placeholder = "خیابان، کوچه، پلاک، واحد",
                singleLine = false
            )

            StyledTextField(
                value = state.postcode,
                onValueChange = { post ->
                    onIntent(HealthProfileIntent.UpdateContact(state.copy(postcode = post)))
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
            state = ContactStepState(cityLabel = "تهران", provinceLabel = "تهران", address = "خیابان آزادی"),
            onIntent = {},
            onBackClicked = {}
        )
    }
}

