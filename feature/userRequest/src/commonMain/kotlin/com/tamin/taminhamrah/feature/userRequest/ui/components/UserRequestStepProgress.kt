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
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import org.jetbrains.compose.resources.stringResource
import taminx.feature.userrequest.generated.resources.Res as UserRequestRes
import taminx.feature.userrequest.generated.resources.user_request_step_branch_delivery
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
    statusDesc: String,
    modifier: Modifier = Modifier,
) {
    val isCompleted = statusDesc.contains("تایید") || statusDesc.contains("مختومه") || statusDesc.contains("تکمیل")
    val isError = statusDesc.contains("عدم") || statusDesc.contains("نقص") || statusDesc.contains("خطا")

    val steps = if (isCompleted) {
        listOf(
            RequestStepItem(stringResource(UserRequestRes.string.user_request_step_preprocessing), StepState.COMPLETED),
            RequestStepItem(stringResource(UserRequestRes.string.user_request_step_branch_delivery), StepState.COMPLETED),
            RequestStepItem(stringResource(UserRequestRes.string.user_request_step_processing_complete), StepState.COMPLETED),
            RequestStepItem(stringResource(UserRequestRes.string.user_request_step_result), StepState.COMPLETED),
        )
    } else if (isError) {
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

    val barColor = if (isCompleted) Color(0xFF03794A) else if (isError) Color(0xFFDC2626) else Color(0xFF1F4FA3)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
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

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            steps.forEach { step ->
                TaminText(
                    text = step.title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (step.state == StepState.ERROR) Color(0xFFDC2626) else LocalTaminColors.current.textSecondary,
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
    val nodeSize = 22.dp

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
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
        StepState.IN_PROGRESS -> {
            Box(
                modifier = Modifier
                    .size(nodeSize)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(3.dp, barColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
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
                    .background(Color(0xFFDC2626)),
                contentAlignment = Alignment.Center
            ) {
                TaminText(
                    text = "!",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color.White,
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
                    .background(Color.White)
                    .border(2.dp, Color(0xFFCBD5E1), CircleShape)
            )
        }
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun UserRequestStepProgressPreview() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        UserRequestStepProgress(statusDesc = "نقص مدارک ارسالی")
    }
}

