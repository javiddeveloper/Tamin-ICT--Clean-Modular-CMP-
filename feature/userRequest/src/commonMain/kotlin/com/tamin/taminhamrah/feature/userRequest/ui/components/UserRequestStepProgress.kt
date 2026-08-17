package com.tamin.taminhamrah.feature.userRequest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.tamin.taminhamrah.model.userRequest.UserRequestProgressPhase
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import taminx.feature.userrequest.generated.resources.Res as UserRequestRes
import taminx.feature.userrequest.generated.resources.user_request_step_branch_delivery
import taminx.feature.userrequest.generated.resources.user_request_step_error_indicator
import taminx.feature.userrequest.generated.resources.user_request_step_preprocessing
import taminx.feature.userrequest.generated.resources.user_request_step_processing_complete
import taminx.feature.userrequest.generated.resources.user_request_step_result

enum class StepState {
    COMPLETED,
    IN_PROGRESS,
    ERROR,
    UNREACHED,
}

data class RequestStepItem(
    val title: String,
    val state: StepState,
)

@Composable
fun UserRequestStepProgress(
    phase: UserRequestProgressPhase,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme

    val steps = if (phase == UserRequestProgressPhase.COMPLETED) {
        listOf(
            RequestStepItem(stringResource(UserRequestRes.string.user_request_step_preprocessing), StepState.COMPLETED),
            RequestStepItem(stringResource(UserRequestRes.string.user_request_step_branch_delivery), StepState.COMPLETED),
            RequestStepItem(stringResource(UserRequestRes.string.user_request_step_processing_complete), StepState.COMPLETED),
            RequestStepItem(stringResource(UserRequestRes.string.user_request_step_result), StepState.COMPLETED),
        )
    } else if (phase == UserRequestProgressPhase.ERROR) {
        listOf(
            RequestStepItem(stringResource(UserRequestRes.string.user_request_step_preprocessing), StepState.COMPLETED),
            RequestStepItem(stringResource(UserRequestRes.string.user_request_step_branch_delivery), StepState.COMPLETED),
            RequestStepItem(stringResource(UserRequestRes.string.user_request_step_processing_complete), StepState.COMPLETED),
            RequestStepItem(stringResource(UserRequestRes.string.user_request_step_result), StepState.ERROR),
        )
    } else {
        listOf(
            RequestStepItem(stringResource(UserRequestRes.string.user_request_step_preprocessing), StepState.COMPLETED),
            RequestStepItem(stringResource(UserRequestRes.string.user_request_step_branch_delivery), StepState.IN_PROGRESS),
            RequestStepItem(stringResource(UserRequestRes.string.user_request_step_processing_complete), StepState.UNREACHED),
            RequestStepItem(stringResource(UserRequestRes.string.user_request_step_result), StepState.UNREACHED),
        )
    }

    val barColor = colorScheme.primary

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.xl),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Spacing.xxs + Thickness.border)
                    .background(barColor)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                steps.forEach { step ->
                    StepNode(step = step, barColor = barColor)
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.tabSelector))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            steps.forEach { step ->
                TaminText(
                    text = step.title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (step.state == StepState.ERROR) colorScheme.error else colorScheme.primary,
                        fontWeight = if (step.state == StepState.ERROR) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StepNode(
    step: RequestStepItem,
    barColor: Color,
) {
    val taminColors = LocalTaminColors.current
    val colorScheme = MaterialTheme.colorScheme
    val nodeSize = IconSize.banner

    when (step.state) {
        StepState.COMPLETED -> {
            Box(
                modifier = Modifier
                    .size(nodeSize)
                    .clip(CircleShape)
                    .background(barColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = colorScheme.onPrimary,
                    modifier = Modifier.size(Spacing.smd)
                )
            }
        }
        StepState.IN_PROGRESS -> {
            Box(
                modifier = Modifier
                    .size(nodeSize)
                    .clip(CircleShape)
                    .background(taminColors.bgSurface)
                    .border(Spacing.xxs + Thickness.border, barColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(Spacing.sm)
                        .clip(CircleShape)
                        .background(barColor)
                )
            }
        }
        StepState.ERROR -> {
            Box(
                modifier = Modifier
                    .size(nodeSize)
                    .clip(CircleShape)
                    .background(colorScheme.error),
                contentAlignment = Alignment.Center
            ) {
                TaminText(
                    text = stringResource(UserRequestRes.string.user_request_step_error_indicator),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = colorScheme.onError,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
        StepState.UNREACHED -> {
            Box(
                modifier = Modifier
                    .size(nodeSize)
                    .clip(CircleShape)
                    .background(taminColors.bgSurface)
                    .border(Thickness.medium, taminColors.outerBorder, CircleShape)
            )
        }
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun UserRequestStepProgressPreview() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        UserRequestStepProgress(phase = UserRequestProgressPhase.ERROR)
    }
}

