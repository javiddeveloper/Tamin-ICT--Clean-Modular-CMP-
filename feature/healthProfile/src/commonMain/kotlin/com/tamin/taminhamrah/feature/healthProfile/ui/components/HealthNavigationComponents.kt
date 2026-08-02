package com.tamin.taminhamrah.feature.healthProfile.ui.components

import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminText

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Surface
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.topbars.TaminTopAppBar
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.draw.rotate
import com.tamin.taminhamrah.ui.components.IconPosition
import taminx.feature.healthprofile.generated.resources.*

val LocalIsEditMode = staticCompositionLocalOf { false }

/**
 * Static Top Bar for Health Profile screens with status bar inset padding.
 */
@Composable
fun HealthTopAppBar(
    title: String = "خوداظهاری سلامت",
    onBackClicked: () -> Unit,
    onCloseClicked: (() -> Unit)? = null,
    currentStep: Int? = null,
    totalSteps: Int = 10,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    Surface(
        color = taminColors.bgSurface,
        shadowElevation = 0.dp
    ) {
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
                        modifier = modifier.rotate(180f),
                        painter = painterResource(Res.drawable.ic_health_back),
                        contentDescription = "بازگشت",
                        tint = taminColors.textPrimary
                    )
                },
                onNavigationClick = onBackClicked,
                actionIcon = onCloseClicked?.let {
                    {
                        Icon(

                            painter = painterResource(Res.drawable.ic_health_close),
                            contentDescription = "بستن",
                            tint = taminColors.textPrimary
                        )
                    }
                },
                onActionClick = onCloseClicked
            )
            if (currentStep != null && currentStep > 0 && !LocalIsEditMode.current) {
                HealthProgressBar(
                    currentStep = currentStep,
                    totalSteps = totalSteps,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * A premium segmented progress bar indicating multi-step form progress.
 *
 * @param currentStep The current step index (1-based).
 * @param totalSteps The total number of steps.
 * @param showStepText Whether to display step text on top of the progress bar.
 */
@Composable
fun HealthProgressBar(
    modifier: Modifier = Modifier,
    currentStep: Int,
    totalSteps: Int = 9,
    showStepText: Boolean = true
) {
    val taminColors = LocalTaminColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (showStepText) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TaminText(
                    text = stringResource(
                        Res.string.health_step_format,
                        currentStep.toString().toPersianDigits(),
                        totalSteps.toString().toPersianDigits()
                    ),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = taminColors.blueText
                    )
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..totalSteps) {
                val isActive = i <= currentStep
                val segmentColor = if (isActive) taminColors.blueText else taminColors.border

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(segmentColor, RoundedCornerShape(100.dp))
                )
            }
        }
    }
}

/**
 * Reusable action navigation bar at the bottom of the screens.
 * Contains primary and secondary buttons, with glass-morphic background blur style.
 */
@Composable
fun HealthIrritateNavigationBar(
    modifier: Modifier = Modifier,
    primaryText: String,
    onPrimaryClick: () -> Unit,
    primaryEnabled: Boolean = true,
    showChevron: Boolean = true,
    secondaryText: String? = null,
    onSecondaryClick: (() -> Unit)? = null
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
        val isEditMode = LocalIsEditMode.current
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isEditMode) {
                if (onSecondaryClick != null) {
                    Box(
                        modifier = Modifier
                            .height(54.dp)
                            .border(1.5.dp, taminColors.border, RoundedCornerShape(15.dp))
                            .clip(RoundedCornerShape(15.dp))
                            .clickable { onSecondaryClick() }
                            .padding(horizontal = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        TaminText(
                            text = "انصراف",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = taminColors.textSecondary
                        )
                    }
                }

                TaminFilledButton(
                    text = "ثبت ویرایش",
                    onClick = onPrimaryClick,
                    enabled = primaryEnabled,
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Check,
                    iconPosition = IconPosition.End
                )
            } else {
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

                // Primary Solid Button
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
}


@Composable
fun HealthNavigationBar(
    modifier: Modifier = Modifier,
    primaryText: String,
    onPrimaryClick: () -> Unit,
    primaryEnabled: Boolean = true,
    showChevron: Boolean = true,
    primaryIconPainter: androidx.compose.ui.graphics.painter.Painter? = null,
    secondaryText: String? = null,
    onSecondaryClick: (() -> Unit)? = null
) {
    val taminColors = LocalTaminColors.current
    val navBarBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val imeBottom = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
    val bottomInset = maxOf(navBarBottom, imeBottom)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(
                start = 18.dp,
                top = 14.dp,
                end = 18.dp,
                bottom = 14.dp + bottomInset
            )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            TaminFilledButton(
                text = primaryText,
                onClick = onPrimaryClick,
                enabled = primaryEnabled,
                painter = primaryIconPainter,
                icon = if (primaryIconPainter == null && showChevron) Icons.AutoMirrored.Filled.KeyboardArrowRight else null
            )

            if (secondaryText != null && onSecondaryClick != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .border(1.5.dp, taminColors.border, RoundedCornerShape(15.dp))
                        .clip(RoundedCornerShape(15.dp))
                        .clickable { onSecondaryClick() },
                    contentAlignment = Alignment.Center
                ) {
                    TaminText(
                        text = secondaryText,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = taminColors.textTertiary
                        )
                    )
                }
            }
        }
    }
}


@PreviewRtlTheme
@Composable
private fun HealthIrritateNavigationComponentsPreview() {
    PreviewRtlThemeContent {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(LocalTaminColors.current.bgPage)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            TaminText("Progress Bar (Step 3 of 9)")
            HealthProgressBar(currentStep = 3, totalSteps = 9)

            TaminText("Navigation Bar (Primary Only)")
            HealthIrritateNavigationBar(
                primaryText = "تکمیل خوداظهاری سلامت",
                onPrimaryClick = {}
            )

            TaminText("Navigation Bar (Primary + Secondary)")
            HealthIrritateNavigationBar(
                primaryText = "مرحله بعدی",
                onPrimaryClick = {},
                secondaryText = "انصراف",
                onSecondaryClick = {}
            )



            TaminText("Navigation Vertical Bar (Primary + Secondary)")
            HealthNavigationBar(
                primaryText = "مرحله بعدی",
                onPrimaryClick = {},
                secondaryText = "انصراف",
                onSecondaryClick = {}
            )
        }
    }
}
