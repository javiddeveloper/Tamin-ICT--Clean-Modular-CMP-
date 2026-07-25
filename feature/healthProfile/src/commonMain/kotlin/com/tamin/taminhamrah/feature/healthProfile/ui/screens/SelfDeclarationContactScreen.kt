package com.tamin.taminhamrah.feature.healthProfile.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.healthProfile.components.bottomsheet.BottomSheetConfig
import com.tamin.taminhamrah.feature.healthProfile.components.bottomsheet.BottomSheetItem
import com.tamin.taminhamrah.feature.healthProfile.components.bottomsheet.BottomSheetType
import com.tamin.taminhamrah.feature.healthProfile.components.bottomsheet.HealthBottomSheet
import com.tamin.taminhamrah.feature.healthProfile.ui.components.*
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.ContactStepState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep
import com.tamin.taminhamrah.feature.healthProfile.ui.model.LookupItemPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SelfDeclarationContactScreen(
    state: ContactStepState,
    provinceOptions: List<LookupItemPR> = emptyList(),
    cityOptions: List<LookupItemPR> = emptyList(),
    isProvincesLoading: Boolean = false,
    isCitiesLoading: Boolean = false,
    onIntent: (HealthProfileIntent) -> Unit,
    onBackClicked: () -> Unit
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    var showProvinceBottomSheet by remember { mutableStateOf(false) }
    var showCityBottomSheet by remember { mutableStateOf(false) }

    val isNextEnabled = state.mobile.length >= 10 && state.cityLabel.isNotEmpty() && state.provinceLabel.isNotEmpty() && state.address.isNotEmpty()


    // If province is set but cityOptions are not yet loaded, load cities
    LaunchedEffect(state.provinceId) {
        state.provinceId?.let { provinceId ->
            if (cityOptions.isEmpty() && !isCitiesLoading) {
                onIntent(HealthProfileIntent.LoadCitiesForProvince(provinceId))
            }
        }
    }

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
                    StyledSelectField(
                        value = state.provinceLabel,
                        label = "استان",
                        placeholder = "انتخاب استان",
                        isLoading = isProvincesLoading,
                        onClick = { showProvinceBottomSheet = true }
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    StyledSelectField(
                        value = state.cityLabel,
                        label = "شهر",
                        placeholder = "انتخاب شهر",
                        isLoading = isCitiesLoading,
                        onClick = {
                            if (state.provinceId == null) {
                                showProvinceBottomSheet = true
                            } else {
                                if (cityOptions.isEmpty() && !isCitiesLoading) {
                                    onIntent(HealthProfileIntent.LoadCitiesForProvince(state.provinceId))
                                }
                                showCityBottomSheet = true
                            }
                        }
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

    if (showProvinceBottomSheet) {
        HealthBottomSheet(
            config = BottomSheetConfig(
                title = "انتخاب استان",
                subtitle = "استان مورد نظر خود را انتخاب کنید",
                type = BottomSheetType.PROVINCE,
                showSearchInput = true,
                searchInputHint = "جستجوی استان...",
                singleSelection = true,
                isLoading = isProvincesLoading,
                items = provinceOptions.map {
                    BottomSheetItem(
                        id = it.id,
                        title = it.label,
                        isSelected = it.id == state.provinceId
                    )
                }
            ),
            onDismissRequest = { showProvinceBottomSheet = false },
            onSubmit = { result ->
                val selectedId = result.selectedItemIds.firstOrNull()
                if (selectedId != null) {
                    val selectedOption = provinceOptions.firstOrNull { it.id == selectedId }
                    selectedOption?.let { prov ->
                        onIntent(
                            HealthProfileIntent.UpdateContact(
                                state.copy(
                                    provinceId = prov.id,
                                    provinceLabel = prov.label,
                                    cityId = null,
                                    cityLabel = ""
                                )
                            )
                        )
                        onIntent(HealthProfileIntent.LoadCitiesForProvince(prov.id))
                    }
                }
                showProvinceBottomSheet = false
            }
        )
    }

    if (showCityBottomSheet) {
        HealthBottomSheet(
            config = BottomSheetConfig(
                title = "انتخاب شهر",
                subtitle = "شهر مورد نظر خود را انتخاب کنید",
                type = BottomSheetType.CITY,
                showSearchInput = true,
                searchInputHint = "جستجوی شهر...",
                singleSelection = true,
                isLoading = isCitiesLoading,
                items = cityOptions.map {
                    BottomSheetItem(
                        id = it.id,
                        title = it.label,
                        isSelected = it.id == state.cityId
                    )
                }
            ),
            onDismissRequest = { showCityBottomSheet = false },
            onSubmit = { result ->
                val selectedId = result.selectedItemIds.firstOrNull()
                if (selectedId != null) {
                    val selectedOption = cityOptions.firstOrNull { it.id == selectedId }
                    selectedOption?.let { city ->
                        onIntent(
                            HealthProfileIntent.UpdateContact(
                                state.copy(
                                    cityId = city.id,
                                    cityLabel = city.label
                                )
                            )
                        )
                    }
                }
                showCityBottomSheet = false
            }
        )
    }
}

@Composable
private fun StyledSelectField(
    value: String,
    label: String,
    placeholder: String,
    onClick: () -> Unit,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current

    Column(modifier = modifier.fillMaxWidth()) {
        TaminText(
            text = label,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold,
            color = taminColors.textTertiary,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(taminColors.bgSurface, RoundedCornerShape(13.dp))
                .border(BorderStroke(1.5.dp, taminColors.border), RoundedCornerShape(13.dp))
                .clip(RoundedCornerShape(13.dp))
                .clickable(enabled = enabled, onClick = onClick)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (value.isNotEmpty()) {
                TaminText(
                    text = value,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = taminColors.textPrimary
                )
            } else {
                TaminText(
                    text = placeholder,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Normal,
                    color = taminColors.textMuted
                )
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = taminColors.blueText
                )
            } else {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = taminColors.textSecondary,
                    modifier = Modifier.size(20.dp)
                )
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
            state = ContactStepState(cityLabel = "تهران", provinceLabel = "تهران", address = "خیابان آزادی"),
            provinceOptions = listOf(LookupItemPR(1, "تهران"), LookupItemPR(2, "اصفهان")),
            cityOptions = listOf(LookupItemPR(10, "تهران"), LookupItemPR(11, "ری")),
            onIntent = {},
            onBackClicked = {}
        )
    }
}
