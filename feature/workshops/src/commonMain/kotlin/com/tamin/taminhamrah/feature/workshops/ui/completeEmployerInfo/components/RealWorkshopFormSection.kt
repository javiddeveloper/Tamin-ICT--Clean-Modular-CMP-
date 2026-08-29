package com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.common.ProvincePR
import com.tamin.taminhamrah.model.contracts.BranchPR
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.employer_info_branch
import taminx.core.core_ui.employer_info_btn_send_otp
import taminx.core.core_ui.employer_info_email
import taminx.core.core_ui.employer_info_real_code_label
import taminx.core.core_ui.employer_info_real_hint
import taminx.core.core_ui.ic_tamin_chevron_back

private val ButtonGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF3B6FD4), Color(0xFF173D7E)),
)

@Composable
fun RealWorkshopFormSection(
    workshopCode: String,
    onWorkshopCodeChanged: (String) -> Unit,
    userEmail: String,
    selectedProvince: ProvincePR?,
    onOpenProvincePicker: () -> Unit,
    selectedCity: CityPR?,
    onOpenCityPicker: () -> Unit,
    selectedBranch: BranchPR?,
    onOpenBranchPicker: () -> Unit,
    errorMessage: String?,
    hasAttemptedSubmit: Boolean,
    isLoading: Boolean,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val isCodeError = hasAttemptedSubmit && workshopCode.filter { it.isDigit() }.length != 10
    val isProvinceError = hasAttemptedSubmit && selectedProvince == null
    val isCityError = hasAttemptedSubmit && selectedCity == null
    val isBranchError = hasAttemptedSubmit && selectedBranch == null

    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        // Workshop Code & Email card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(18.dp),
                    ambientColor = Color(0x0A0F172A),
                    spotColor = Color(0x0A0F172A),
                )
                .clip(RoundedCornerShape(18.dp))
                .background(colors.bgSurface)
                .border(1.dp, colors.border, RoundedCornerShape(18.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            // Code Input
            Column {
                Text(
                    text = stringResource(Res.string.employer_info_real_code_label),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary,
                        fontSize = 11.sp,
                    ),
                    modifier = Modifier.padding(bottom = 5.dp),
                )
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    BasicTextField(
                        value = workshopCode,
                        onValueChange = { onWorkshopCodeChanged(it.filter { c -> c.isDigit() }.take(10)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                        ),
                        cursorBrush = SolidColor(colors.blueText),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(13.dp))
                                    .background(colors.bgPage)
                                    .border(
                                        width = 1.dp,
                                        color = if (isCodeError) colors.dangerText else colors.border,
                                        shape = RoundedCornerShape(13.dp),
                                    )
                                    .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                if (workshopCode.isEmpty()) {
                                    Text(
                                        text = "۱۰ رقم".toPersianDigits(),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = colors.textMuted,
                                        ),
                                    )
                                }
                                innerTextField()
                            }
                        },
                    )
                }
            }

            // Email (Read-only from Profile)
            Column {
                Text(
                    text = stringResource(Res.string.employer_info_email),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary,
                        fontSize = 11.sp,
                    ),
                    modifier = Modifier.padding(bottom = 5.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(Color(0xFFF1F4F9))
                        .border(1.dp, colors.border, RoundedCornerShape(13.dp))
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Text(
                            text = userEmail.ifBlank { "tamin@tamin.ir" },
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = colors.textMuted,
                        modifier = Modifier.size(15.dp),
                    )
                }
            }
        }

        // Location Pickers Card (Province -> City -> Branch)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(18.dp),
                    ambientColor = Color(0x0A0F172A),
                    spotColor = Color(0x0A0F172A),
                )
                .clip(RoundedCornerShape(18.dp))
                .background(colors.bgSurface)
                .border(1.dp, colors.border, RoundedCornerShape(18.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            // Province & City 2-column row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                // Province
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "استان",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textSecondary,
                            fontSize = 11.sp,
                        ),
                        modifier = Modifier.padding(bottom = 5.dp),
                    )
                    SelectChip(
                        text = selectedProvince?.provinceName ?: "انتخاب استان",
                        isSelected = selectedProvince != null,
                        isError = isProvinceError,
                        onClick = onOpenProvincePicker,
                    )
                }

                // City (enabled only when province selected)
                val isCityEnabled = selectedProvince != null
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "شهر",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textSecondary,
                            fontSize = 11.sp,
                        ),
                        modifier = Modifier.padding(bottom = 5.dp),
                    )
                    SelectChip(
                        text = selectedCity?.cityName ?: if (isCityEnabled) "انتخاب شهر" else "ابتدا استان",
                        isSelected = selectedCity != null,
                        isEnabled = isCityEnabled,
                        isError = isCityError,
                        onClick = { if (isCityEnabled) onOpenCityPicker() },
                    )
                }
            }

            // Branch (enabled only when city selected)
            val isBranchEnabled = selectedCity != null
            Column {
                Text(
                    text = stringResource(Res.string.employer_info_branch),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary,
                        fontSize = 11.sp,
                    ),
                    modifier = Modifier.padding(bottom = 5.dp),
                )
                SelectChip(
                    text = selectedBranch?.name ?: if (isBranchEnabled) "انتخاب شعبه" else "ابتدا شهر را انتخاب کنید",
                    isSelected = selectedBranch != null,
                    isEnabled = isBranchEnabled,
                    isError = isBranchError,
                    onClick = { if (isBranchEnabled) onOpenBranchPicker() },
                )
            }
        }

        // Error message line
        if (!errorMessage.isNullOrBlank()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = colors.dangerText,
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = colors.dangerText,
                        fontSize = 11.sp,
                    ),
                )
            }
        }

        // Notice hint
        Text(
            text = stringResource(Res.string.employer_info_real_hint),
            style = MaterialTheme.typography.bodySmall.copy(
                color = colors.textSecondary,
                lineHeight = 22.sp,
                fontSize = 11.sp,
            ),
            textAlign = TextAlign.Justify,
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Submit Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(15.dp),
                    ambientColor = Color(0x30173D7E),
                    spotColor = Color(0x30173D7E),
                )
                .clip(RoundedCornerShape(15.dp))
                .background(ButtonGradient)
                .clickable(enabled = !isLoading, onClick = onSubmit),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp,
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Email,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(17.dp),
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = stringResource(Res.string.employer_info_btn_send_otp),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.5.sp,
                    ),
                )
            }
        }
    }
}

@Composable
private fun SelectChip(
    text: String,
    isSelected: Boolean,
    isEnabled: Boolean = true,
    isError: Boolean = false,
    onClick: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val bg = if (!isEnabled) Color(0xFFF1F4F9) else colors.bgPage
    val fg = if (isSelected) colors.textPrimary else colors.textMuted
    val weight = if (isSelected) FontWeight.Bold else FontWeight.Medium

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .alpha(if (isEnabled) 1f else 0.6f)
            .clip(RoundedCornerShape(13.dp))
            .background(bg)
            .border(
                width = 1.dp,
                color = if (isError) colors.dangerText else colors.border,
                shape = RoundedCornerShape(13.dp),
            )
            .clickable(enabled = isEnabled, onClick = onClick)
            .padding(horizontal = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = weight,
                color = fg,
                fontSize = 11.5.sp,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_chevron_back),
            contentDescription = null,
            tint = colors.textMuted,
            modifier = Modifier.size(13.dp).rotate(-90f),
        )
    }
}
