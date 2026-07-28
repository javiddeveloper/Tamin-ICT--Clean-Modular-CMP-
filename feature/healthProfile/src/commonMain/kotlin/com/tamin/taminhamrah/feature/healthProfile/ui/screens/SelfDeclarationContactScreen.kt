package com.tamin.taminhamrah.feature.healthProfile.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.tamin.taminhamrah.feature.healthProfile.ui.components.*
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.ContactStepState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep
import com.tamin.taminhamrah.feature.healthProfile.ui.model.LookupItemPR
import com.tamin.taminhamrah.util.ValidationUtils
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.BottomSheetConfig
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.BottomSheetItem
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.BottomSheetType
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.HealthBottomSheet
import org.jetbrains.compose.resources.stringResource
import taminx.feature.healthprofile.generated.resources.*
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
@Composable
fun SelfDeclarationContactScreen(
    state: ContactStepState,
    provinceOptions: List<LookupItemPR> = emptyList(),
    cityOptions: List<LookupItemPR> = emptyList(),
    isLoading: Boolean = false,
    isProvincesLoading: Boolean = false,
    isCitiesLoading: Boolean = false,
    onIntent: (HealthProfileIntent) -> Unit,
    onBackClicked: () -> Unit
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    var showProvinceBottomSheet by remember { mutableStateOf(false) }
    var showCityBottomSheet by remember { mutableStateOf(false) }

    var mobileHasFocused by remember { mutableStateOf(false) }
    var mobileTouched by remember { mutableStateOf(false) }

    var emailHasFocused by remember { mutableStateOf(false) }
    var emailTouched by remember { mutableStateOf(false) }

    var postcodeHasFocused by remember { mutableStateOf(false) }
    var postcodeTouched by remember { mutableStateOf(false) }

    val isMobileValid = ValidationUtils.isPhoneNumberValid(state.mobile)
    val isEmailValid = ValidationUtils.isEmailValid(state.email)
    val isPostcodeValid = ValidationUtils.isPostcodeValid(state.postcode)

    val isNextEnabled = isMobileValid && isEmailValid && isPostcodeValid

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
                onBackClicked = onBackClicked,
                title = stringResource(Res.string.health_contact_title)
            )
        },
        bottomBar = {
            HealthIrritateNavigationBar(
                primaryText = stringResource(Res.string.health_btn_next_step),
                primaryEnabled = isNextEnabled,
                onPrimaryClick = { onIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.EMERGENCY)) },
                secondaryText = stringResource(Res.string.health_btn_prev_step),
                onSecondaryClick = onBackClicked
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(taminColors.bgPage)
            ) {
                FormFieldsShimmerSkeleton(fieldCount = 6)
            }
        } else {
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
                    text = stringResource(Res.string.health_contact_desc),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = taminColors.textTertiary,
                        lineHeight = 22.sp
                    )
                )

                val showMobileError = mobileTouched && !isMobileValid
                StyledTextField(
                    value = state.mobile,
                    onValueChange = { mob ->
                        val filtered = ValidationUtils.validatePhoneNumber(mob)
                        onIntent(HealthProfileIntent.UpdateContact(state.copy(mobile = filtered)))
                    },
                    label = stringResource(Res.string.health_contact_mobile_label),
                    placeholder = stringResource(Res.string.health_contact_mobile_placeholder),
                    isValid = if (showMobileError) false else null,
                    errorText = if (showMobileError) stringResource(Res.string.health_contact_mobile_error) else null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isRequired = true,
                    onFocusChanged = { isFocused ->
                        if (isFocused) {
                            mobileHasFocused = true
                        } else if (mobileHasFocused) {
                            mobileTouched = true
                        }
                    }
                )

                val showEmailError = emailTouched && !isEmailValid
                StyledTextField(
                    value = state.email,
                    onValueChange = { email ->
                        onIntent(HealthProfileIntent.UpdateContact(state.copy(email = email)))
                    },
                    label = stringResource(Res.string.health_contact_email_label),
                    placeholder = stringResource(Res.string.health_contact_email_placeholder),
                    isValid = if (showEmailError) false else null,
                    errorText = if (showEmailError) stringResource(Res.string.health_contact_email_error) else null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    onFocusChanged = { isFocused ->
                        if (isFocused) {
                            emailHasFocused = true
                        } else if (emailHasFocused) {
                            emailTouched = true
                        }
                    }
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        StyledSelectField(
                            value = state.provinceLabel,
                            label = stringResource(Res.string.health_contact_province_label),
                            placeholder = stringResource(Res.string.health_contact_province_placeholder),
                            isLoading = isProvincesLoading,
                            onClick = { showProvinceBottomSheet = true }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        StyledSelectField(
                            value = state.cityLabel,
                            label = stringResource(Res.string.health_contact_city_label),
                            placeholder = stringResource(Res.string.health_contact_city_placeholder),
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
                    label = stringResource(Res.string.health_contact_address_label),
                    placeholder = stringResource(Res.string.health_contact_address_placeholder),
                    singleLine = false
                )

                val showPostcodeError = postcodeTouched && state.postcode.length != 10
                StyledTextField(
                    value = state.postcode,
                    onValueChange = { post ->
                        val filtered = ValidationUtils.validatePostcode(post)
                        onIntent(HealthProfileIntent.UpdateContact(state.copy(postcode = filtered)))
                    },
                    label = stringResource(Res.string.health_contact_postcode_label),
                    placeholder = stringResource(Res.string.health_contact_postcode_placeholder),
                    isValid = if (showPostcodeError) false else null,
                    errorText = if (showPostcodeError) stringResource(Res.string.health_contact_postcode_error) else null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onFocusChanged = { isFocused ->
                        if (isFocused) {
                            postcodeHasFocused = true
                        } else if (postcodeHasFocused) {
                            postcodeTouched = true
                        }
                    }
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
                    TaminText(
                        text = stringResource(Res.string.health_contact_map_button),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
            }
        }

        if (showProvinceBottomSheet) {
            HealthBottomSheet(
                config = BottomSheetConfig(
                    title = stringResource(Res.string.health_contact_province_bs_title),
                    subtitle = stringResource(Res.string.health_contact_province_bs_subtitle),
                    type = BottomSheetType.PROVINCE,
                    showSearchInput = true,
                    searchInputHint = stringResource(Res.string.health_contact_province_bs_search_hint),
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
                    title = stringResource(Res.string.health_contact_city_bs_title),
                    subtitle = stringResource(Res.string.health_contact_city_bs_subtitle),
                    type = BottomSheetType.CITY,
                    showSearchInput = true,
                    searchInputHint = stringResource(Res.string.health_contact_city_bs_search_hint),
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

}

@Composable
private fun StyledSelectField(
    value: String,
    label: String,
    placeholder: String,
    onClick: () -> Unit,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    isRequired: Boolean = false,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current

    val annotatedLabel = buildAnnotatedString {
        append(label)
        if (isRequired) {
            withStyle(SpanStyle(color = taminColors.dangerText)) {
                append(" *")
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        TaminText(
            text = annotatedLabel,
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
