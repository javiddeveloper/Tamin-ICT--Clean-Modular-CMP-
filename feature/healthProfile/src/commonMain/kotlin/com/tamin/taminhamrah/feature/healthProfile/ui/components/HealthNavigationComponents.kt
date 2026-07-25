package com.tamin.taminhamrah.feature.healthProfile.ui.components

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

/**
 * Static Top Bar for Health Profile screens with status bar inset padding.
 */
@Composable
fun HealthTopAppBar(
    title: String = "خوداظهاری سلامت",
    onBackClicked: () -> Unit,
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
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "بازگشت"
                    )
                },
                onNavigationClick = onBackClicked
            )
            if (currentStep != null && currentStep > 0) {
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
 */
@Composable
fun HealthProgressBar(
    modifier: Modifier = Modifier,
    currentStep: Int,
    totalSteps: Int = 9
) {
    val taminColors = LocalTaminColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
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

            // Primary Solid Button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .background(
                        brush = if (primaryEnabled) taminColors.heroGradient else Brush.linearGradient(listOf(taminColors.border, taminColors.border)),
                        shape = RoundedCornerShape(15.dp)
                    )
                    .clip(RoundedCornerShape(15.dp))
                    .clickable(enabled = primaryEnabled) { onPrimaryClick() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    TaminText(
                        text = primaryText,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (primaryEnabled) Color.White else taminColors.textMuted
                        )
                    )
                    if (showChevron) {
                        Spacer(modifier = Modifier.width(9.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = if (primaryEnabled) Color.White else taminColors.textMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .background(
                        brush = if (primaryEnabled) taminColors.heroGradient else Brush.linearGradient(listOf(taminColors.border, taminColors.border)),
                        shape = RoundedCornerShape(15.dp)
                    )
                    .clip(RoundedCornerShape(15.dp))
                    .clickable(enabled = primaryEnabled) { onPrimaryClick() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    TaminText(
                        text = primaryText,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (primaryEnabled) Color.White else taminColors.textMuted
                        )
                    )
                    if (primaryIconPainter != null) {
                        Spacer(modifier = Modifier.width(9.dp))
                        Icon(
                            painter = primaryIconPainter,
                            contentDescription = null,
                            tint = if (primaryEnabled) Color.White else taminColors.textMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    } else if (showChevron) {
                        Spacer(modifier = Modifier.width(9.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = if (primaryEnabled) Color.White else taminColors.textMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

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
