package com.tamin.taminhamrah.feature.taminServices.occurrence.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.topbars.TaminTopAppBar
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.applicationFont
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_back

// ─────────────────────────────────────────────────────────────────────────────
// StyledTextField (copied from healthProfile HealthFormComponents)
// ─────────────────────────────────────────────────────────────────────────────

enum class InputRestriction {
    None,
    LettersOnly,
    DigitsOnly,
    LettersAndDigits
}

@Composable
fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector? = null,
    leadingIconPainter: androidx.compose.ui.graphics.painter.Painter? = null,
    trailingIcon: ImageVector? = null,
    trailingIconPainter: androidx.compose.ui.graphics.painter.Painter? = null,
    isValid: Boolean? = null,
    errorText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    readOnly: Boolean = false,
    isRequired: Boolean = false,
    onClick: (() -> Unit)? = null,
    onFocusChanged: ((Boolean) -> Unit)? = null,
    maxLength: Int? = null,
    inputRestriction: InputRestriction = InputRestriction.None,
) {
    val taminColors = LocalTaminColors.current
    var isFocused by remember { mutableStateOf(false) }

    val borderColor = when {
        isValid == false -> taminColors.dangerText
        isFocused -> taminColors.blueText
        else -> taminColors.border
    }

    val leadingIconColor = if (isFocused) taminColors.blueText else taminColors.textMuted
    val trailingIconColor = if (isFocused) taminColors.blueText else taminColors.textSecondary

    val annotatedLabel = buildAnnotatedString {
        append(label)
        if (isRequired) {
            withStyle(SpanStyle(color = taminColors.dangerText)) {
                append(" *")
            }
        }
    }

    Box(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
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
                    .border(BorderStroke(1.5.dp, borderColor), RoundedCornerShape(13.dp))
                    .onFocusChanged { focusState ->
                        isFocused = focusState.isFocused
                        onFocusChanged?.invoke(focusState.isFocused)
                    }
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingIconPainter != null) {
                    Icon(painter = leadingIconPainter, contentDescription = null, tint = leadingIconColor, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                } else if (leadingIcon != null) {
                    Icon(imageVector = leadingIcon, contentDescription = null, tint = leadingIconColor, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                }

                BasicTextField(
                    value = value,
                    onValueChange = { newValue ->
                        val restricted = when (inputRestriction) {
                            InputRestriction.None -> newValue
                            InputRestriction.LettersOnly -> newValue.filter { it.isLetter() || it.isWhitespace() }
                            InputRestriction.DigitsOnly -> newValue.filter { it.isDigit() }
                            InputRestriction.LettersAndDigits -> newValue.filter { it.isLetter() || it.isDigit() || it.isWhitespace() }
                        }
                        val limited = maxLength?.let { restricted.take(it) } ?: restricted
                        onValueChange(limited)
                    },
                    singleLine = singleLine,
                    readOnly = readOnly || onClick != null,
                    keyboardOptions = keyboardOptions,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = taminColors.textPrimary,
                        fontWeight = FontWeight.Medium,
                        fontFamily = applicationFont(),
                    ),
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        if (value.isEmpty()) {
                            TaminText(text = placeholder, fontSize = 13.5.sp, color = taminColors.textMuted, fontWeight = FontWeight.Normal)
                        }
                        innerTextField()
                    }
                )

                if (isValid == true) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = taminColors.greenText, modifier = Modifier.size(19.dp))
                } else if (trailingIconPainter != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(painter = trailingIconPainter, contentDescription = null, tint = trailingIconColor, modifier = Modifier.size(20.dp))
                } else if (trailingIcon != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = trailingIcon, contentDescription = null, tint = trailingIconColor, modifier = Modifier.size(20.dp))
                }
            }

            if (isValid == false && !errorText.isNullOrEmpty()) {
                Row(modifier = Modifier.padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Error, contentDescription = null, tint = taminColors.dangerText, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    TaminText(text = errorText, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = taminColors.dangerText)
                }
            }
        }

        if (onClick != null) {
            Box(modifier = Modifier.matchParentSize().clip(RoundedCornerShape(13.dp)).clickable { onClick() })
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// OccurrenceTopAppBar (adapted from HealthTopAppBar)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun OccurrenceTopAppBar(
    title: String,
    onBackClicked: () -> Unit,
    onCloseClicked: (() -> Unit)? = null,
    currentStep: Int? = null,
    totalSteps: Int = 6,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    Surface(color = taminColors.bgSurface, shadowElevation = 0.dp) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            TaminTopAppBar(
                title = {
                    TaminText(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                },
                navigationIcon = {
                    Icon(
                        imageVector = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = "بازگشت",
                        tint = taminColors.textPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                },
                onNavigationClick = onBackClicked,
                actionIcon = onCloseClicked?.let {
                    {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن",
                            tint = taminColors.textPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                onActionClick = onCloseClicked
            )

            if (currentStep != null && currentStep > 0) {
                OccurrenceProgressBar(
                    currentStep = currentStep,
                    totalSteps = totalSteps,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.lg, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun OccurrenceProgressBar(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    Column(
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        TaminText(
            text = "مرحله ${currentStep.toString().toPersianDigits()} از ${totalSteps.toString().toPersianDigits()}",
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = taminColors.blueText
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..totalSteps) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(
                            if (i <= currentStep) taminColors.blueText else taminColors.border,
                            RoundedCornerShape(100.dp)
                        )
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// OccurrenceNavigationBar (adapted from HealthIrritateNavigationBar)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun OccurrenceNavigationBar(
    primaryText: String,
    onPrimaryClick: () -> Unit,
    primaryEnabled: Boolean = true,
    showChevron: Boolean = true,
    secondaryText: String? = null,
    onSecondaryClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val navBarBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val imeBottom = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
    val bottomInset = maxOf(navBarBottom, imeBottom)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(taminColors.glassSolid)
            .padding(
                start = 12.dp,
                top = 14.dp,
                end = 12.dp,
                bottom = 14.dp + bottomInset
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Optional Secondary outlined button
            if (secondaryText != null && onSecondaryClick != null) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .border(1.5.dp, taminColors.border, RoundedCornerShape(15.dp))
                        .clip(RoundedCornerShape(15.dp))
                        .clickable { onSecondaryClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            TaminFilledButton(
                text = primaryText,
                onClick = onPrimaryClick,
                enabled = primaryEnabled,
                modifier = Modifier.weight(1f),
                icon = if (showChevron) Icons.AutoMirrored.Filled.KeyboardArrowRight else null
            )
        }
    }
}


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

