package com.tamin.taminhamrah.ui.components.topbars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.back_content_description
import taminx.core.core_ui.close_content_description
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.step_of_total_label

/**
 * A [TaminTopAppBar] for multi-step forms: title/back/close bar plus an optional
 * segmented step-progress row underneath. Shared by occurrence reporting and health
 * self-declaration, whose per-feature icon assets and step-label text differ — pass
 * [navigationIcon]/[closeIcon]/[stepLabel] to keep those differences without duplicating
 * the bar itself (see `.claude/rules/architecture.md`).
 *
 * [TaminStepProgressBar]'s compact segmented bar (rather than reusing [StepIndicator]) is
 * intentional here, not an oversight: occurrence reporting has 6 steps, more than double
 * [StepIndicator]'s current 3-step callers (change-mobile, addDependent, …), and that
 * component's per-step title-text layout wouldn't stay legible at that count.
 */
@Composable
fun TaminStepTopAppBar(
    title: String,
    onBackClicked: () -> Unit,
    onCloseClicked: (() -> Unit)? = null,
    currentStep: Int? = null,
    totalSteps: Int = 6,
    showProgress: Boolean = currentStep != null && currentStep > 0,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {
        val taminColors = LocalTaminColors.current
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_chevron_back),
            contentDescription = stringResource(Res.string.back_content_description),
            tint = taminColors.textPrimary,
            modifier = Modifier.size(24.dp)
        )
    },
    closeIcon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = stringResource(Res.string.close_content_description),
            tint = LocalTaminColors.current.textPrimary,
            modifier = Modifier.size(22.dp)
        )
    },
    stepLabel: @Composable (current: Int, total: Int) -> String = { current, total ->
        stringResource(
            Res.string.step_of_total_label,
            current.toString().toPersianDigits(),
            total.toString().toPersianDigits(),
        )
    },
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
                navigationIcon = navigationIcon,
                onNavigationClick = onBackClicked,
                actionIcon = onCloseClicked?.let { closeIcon },
                onActionClick = onCloseClicked
            )

            if (showProgress && currentStep != null) {
                TaminStepProgressBar(
                    currentStep = currentStep,
                    totalSteps = totalSteps,
                    stepLabel = stepLabel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.lg, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * The segmented progress row under [TaminStepTopAppBar] — extracted on its own so a
 * feature that only needs the bar (no top-bar chrome) can still reuse it directly.
 */
@Composable
fun TaminStepProgressBar(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
    showStepText: Boolean = true,
    stepLabel: @Composable (current: Int, total: Int) -> String = { current, total ->
        stringResource(
            Res.string.step_of_total_label,
            current.toString().toPersianDigits(),
            total.toString().toPersianDigits(),
        )
    },
) {
    val taminColors = LocalTaminColors.current
    Column(
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (showStepText) {
            TaminText(
                text = stepLabel(currentStep, totalSteps),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = taminColors.blueText
                )
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
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
