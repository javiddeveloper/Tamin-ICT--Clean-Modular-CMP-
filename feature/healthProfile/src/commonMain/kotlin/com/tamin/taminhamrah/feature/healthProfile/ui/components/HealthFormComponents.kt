
package com.tamin.taminhamrah.feature.healthProfile.ui.components

import com.tamin.taminhamrah.ui.components.TaminText

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.applicationFont

/**
 * A beautiful segmented control switcher (typically Yes/No toggle).
 */
@Composable
fun SegmentedControl(
    modifier: Modifier = Modifier,
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit,
    activeColor: Color = LocalTaminColors.current.blueText,
    activeBgColor: Color = LocalTaminColors.current.bgSurface
) {
    val taminColors = LocalTaminColors.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .background(taminColors.divider, RoundedCornerShape(13.dp))
            .border(1.5.dp, taminColors.border.copy(alpha = 0.5f), RoundedCornerShape(13.dp))
            .padding(4.dp)
            .height(42.dp)
    ) {
        val width = maxWidth
        val tabWidth = width / options.size

        // Animated slider background capsule
        val indicatorOffset by animateDpAsState(
            targetValue = tabWidth * selectedIndex,
            animationSpec = tween(300),
            label = "tabSlide"
        )

        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(tabWidth)
                .fillMaxHeight()
                .background(activeBgColor, RoundedCornerShape(10.dp))
                .border(1.5.dp, taminColors.border, RoundedCornerShape(10.dp))
        )

        Row(modifier = Modifier.fillMaxSize()) {
            options.forEachIndexed { index, label ->
                val isSelected = index == selectedIndex
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) activeColor else taminColors.textTertiary,
                    label = "textColor"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onOptionSelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    TaminText(
                        text = label,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = textColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * Interactive filter choice chips wrapping nicely inside containers.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InteractiveChoiceChips(
    modifier: Modifier = Modifier,
    options: List<String>,
    selectedIndices: Set<Int>,
    onSelectionChanged: (Set<Int>) -> Unit,
    activeColor: Color = LocalTaminColors.current.blueText,
    activeBgColor: Color = LocalTaminColors.current.blueBg
) {
    val taminColors = LocalTaminColors.current

    // Custom Flow layout using Compose row wrapping
    // Multiplatform standard wrap
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEachIndexed { index, label ->
            val isSelected = selectedIndices.contains(index)
            val chipBgColor = if (isSelected) activeBgColor else Color.Transparent
            val chipBorderColor = if (isSelected) activeColor else taminColors.border
            val chipTextColor = if (isSelected) activeColor else taminColors.textPrimary

            Box(
                modifier = Modifier
                    .background(chipBgColor, RoundedCornerShape(100.dp))
                    .border(
                        BorderStroke(if (isSelected) 1.5.dp else 1.dp, chipBorderColor),
                        RoundedCornerShape(100.dp)
                    )
                    .clip(RoundedCornerShape(100.dp))
                    .clickable {
                        val newSelection = selectedIndices.toMutableSet()
                        if (newSelection.contains(index)) {
                            newSelection.remove(index)
                        } else {
                            newSelection.add(index)
                        }
                        onSelectionChanged(newSelection)
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                TaminText(
                    text = label,
                    fontSize = 12.5.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = chipTextColor
                )
            }
        }
    }
}

/**
 * Custom stylized text input with validation ticks, prefix icons, and error states.
 */
@Composable
fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector? = null,
    isValid: Boolean? = null,
    errorText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true
) {
    val taminColors = LocalTaminColors.current
    var isFocused by remember { mutableStateOf(false) }

    // Colors mapping based on focus and validation state
    val borderColor = when {
        isValid == false -> taminColors.dangerText
        isFocused -> taminColors.blueText
        else -> taminColors.border
    }

    val leadingIconColor = if (isFocused) taminColors.blueText else taminColors.textMuted

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
                .border(BorderStroke(1.5.dp, borderColor), RoundedCornerShape(13.dp))
                .onFocusChanged { isFocused = it.isFocused }
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading Icon
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = leadingIconColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }

            // Input field
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = singleLine,
                keyboardOptions = keyboardOptions,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = taminColors.textPrimary,
                    fontWeight = FontWeight.Medium,
                    fontFamily = applicationFont()
                ),
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        TaminText(
                            text = placeholder,
                            fontSize = 13.5.sp,
                            color = taminColors.textMuted,
                            fontWeight = FontWeight.Normal
                        )
                    }
                    innerTextField()
                }
            )

            // Suffix validation / status checkmark
            if (isValid == true) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "معتبر",
                    tint = taminColors.greenText,
                    modifier = Modifier.size(19.dp)
                )
            }
        }

        // Error message below
        if (isValid == false && !errorText.isNullOrEmpty()) {
            Row(
                modifier = Modifier.padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "خطا",
                    tint = taminColors.dangerText,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                TaminText(
                    text = errorText,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = taminColors.dangerText
                )
            }
        }
    }
}

/**
 * Informational banner with warning lock icon.
 */
@Composable
fun InfoBanner(
    message: String,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(taminColors.blueBg, RoundedCornerShape(13.dp))
            .border(1.dp, taminColors.blueText.copy(alpha = 0.2f), RoundedCornerShape(13.dp))
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "قفل",
            tint = taminColors.blueText,
            modifier = Modifier
                .size(18.dp)
                .align(Alignment.Top)
        )
        Spacer(modifier = Modifier.width(10.dp))
        TaminText(
            text = message,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Medium,
            color = taminColors.blueText,
            lineHeight = 18.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * A beautiful dashed bordered button for adding dynamically managed items.
 */
@Composable
fun DashedAddButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    val dashedBorderColor = taminColors.blueText.copy(alpha = 0.5f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .drawBehind {
                drawRoundRect(
                    color = dashedBorderColor,
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(15.dp.toPx())
                )
            }
            .clip(RoundedCornerShape(15.dp))
            .clickable { onClick() }
            .background(taminColors.bgSurface)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(taminColors.greenBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "افزودن",
                    tint = taminColors.greenText,
                    modifier = Modifier.size(17.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            TaminText(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = taminColors.textPrimary
            )
        }
    }
}

/**
 * Added item list representation card (such as drug allergies list item).
 */
@Composable
fun DynamicItemCard(
    title: String,
    description: String,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
        border = BorderStroke(1.dp, taminColors.border),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.MedicalServices,
                    contentDescription = null,
                    tint = taminColors.teal,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                TaminText(
                    text = title,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = taminColors.textPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            TaminText(
                text = description,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium,
                color = taminColors.textTertiary,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = taminColors.divider, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onDelete() }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "حذف",
                    tint = taminColors.dangerText,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(modifier = Modifier.width(7.dp))
                TaminText(
                    text = "حذف",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = taminColors.dangerText
                )
            }
        }
    }
}

/**
 * A reusable dialog modal for cancel/submit confirmation.
 */
@Composable
fun ActionDialog(
    showDialog: Boolean,
    title: String,
    description: String,
    confirmLabel: String,
    cancelLabel: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    if (showDialog) {
        val taminColors = LocalTaminColors.current

        Dialog(
            onDismissRequest = onCancel,
            properties = DialogProperties(
                usePlatformDefaultWidth = true,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Warning Shield Icon
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(taminColors.orangeBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = taminColors.orangeText,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TaminText(
                        text = title,
                        fontSize = 16.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = taminColors.textPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TaminText(
                        text = description,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = taminColors.textTertiary,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Confirm (gradient/solid block)
                        Button(
                            onClick = onConfirm,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = taminColors.blueText,
                                contentColor = Color.White
                            )
                        ) {
                            TaminText(
                                text = confirmLabel,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Cancel
                        OutlinedButton(
                            onClick = onCancel,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, taminColors.border),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = taminColors.textTertiary
                            )
                        ) {
                            TaminText(
                                text = cancelLabel,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun HealthFormComponentsPreview() {
    PreviewRtlThemeContent {
        var switcherIndex by remember { mutableStateOf(0) }
        var chipSelection by remember { mutableStateOf(setOf<Int>()) }
        var textValue by remember { mutableStateOf("09123456789") }
        var dialogOpen by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(LocalTaminColors.current.bgPage)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TaminText("Segmented Switcher", fontWeight = FontWeight.Bold, color = LocalTaminColors.current.textPrimary)
            SegmentedControl(
                options = listOf("بله", "خیر"),
                selectedIndex = switcherIndex,
                onOptionSelected = { switcherIndex = it }
            )

            TaminText("Interactive Choice Chips", fontWeight = FontWeight.Bold, color = LocalTaminColors.current.textPrimary)
            InteractiveChoiceChips(
                options = listOf("فشار خون بالا", "دیابت", "چربی خون بالا", "بیماری قلبی"),
                selectedIndices = chipSelection,
                onSelectionChanged = { chipSelection = it }
            )

            TaminText("Styled TextField (Phone)", fontWeight = FontWeight.Bold, color = LocalTaminColors.current.textPrimary)
            StyledTextField(
                value = textValue,
                onValueChange = { textValue = it },
                label = "شمارهٔ تلفن همراه",
                placeholder = "وارد کنید",
                leadingIcon = Icons.Default.Phone,
                isValid = textValue.length == 11,
                errorText = if (textValue.length != 11) "شماره همراه معتبر نیست" else null
            )

            TaminText("Info Banner", fontWeight = FontWeight.Bold, color = LocalTaminColors.current.textPrimary)
            InfoBanner(message = "این اطلاعات از قبل ثبت شده و قابل ویرایش نیست. در صورت نیاز به اصلاح با پشتیبانی تماس بگیرید.")

            TaminText("Dashed Add Button", fontWeight = FontWeight.Bold, color = LocalTaminColors.current.textPrimary)
            DashedAddButton(
                label = "افزودن حساسیت دارویی",
                onClick = { dialogOpen = true }
            )

            TaminText("Dynamic Item Card", fontWeight = FontWeight.Bold, color = LocalTaminColors.current.textPrimary)
            DynamicItemCard(
                title = "پنی‌سیلین",
                description = "باعث ایجاد خارش شدید پوستی و تنگی نفس خفیف می‌شود.",
                onDelete = {}
            )

            ActionDialog(
                showDialog = dialogOpen,
                title = "انصراف از تکمیل فرم",
                description = "آیا مطمئن هستید؟ اطلاعات وارد شده ذخیره نخواهند شد.",
                confirmLabel = "بله، خروج",
                cancelLabel = "ادامه تکمیل فرم",
                onConfirm = { dialogOpen = false },
                onCancel = { dialogOpen = false }
            )
        }
    }
}
