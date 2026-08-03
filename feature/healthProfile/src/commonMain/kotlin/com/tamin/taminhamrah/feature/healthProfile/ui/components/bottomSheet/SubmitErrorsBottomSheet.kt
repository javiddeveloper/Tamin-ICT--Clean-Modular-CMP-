package com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.healthProfile.ui.model.HealthProblemPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHamrahShapes
import com.tamin.taminhamrah.util.toPersianDigits

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitErrorsBottomSheet(
    problems: List<HealthProblemPR>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val taminColors = LocalTaminColors.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
        containerColor = taminColors.bgSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.page)
                .padding(bottom = Spacing.page),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(34.dp)
                        .clip(TaminHamrahShapes.medium)
                        .background(color = LocalTaminColors.current.divider)
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(Spacing.lg),
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = LocalTaminColors.current.textSecondary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(taminColors.dangerBorder.copy(alpha = 0.5f))
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = null,
                    tint = taminColors.dangerText,
                    modifier = Modifier.size(30.dp)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TaminText(
                    text = "خوداظهاری ثبت نشد",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = taminColors.textPrimary,
                    textAlign = TextAlign.Center
                )
                TaminText(
                    text = "سامانه پیام‌های زیر را برگرداند:",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp),
                    color = taminColors.textSecondary,
                    textAlign = TextAlign.Center
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(weight = 1f, fill = false)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                problems.forEachIndexed { index, problem ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(taminColors.bgSurface)
                            .border(1.dp, taminColors.dangerBorder, RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(taminColors.dangerBorder.copy(alpha = 0.6f)),
                                contentAlignment = Alignment.Center
                            ) {
                                TaminText(
                                    text = (index + 1).toString().toPersianDigits(),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    ),
                                    color = taminColors.dangerText
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            TaminText(
                                text = problem.message,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 20.sp
                                ),
                                color = taminColors.textPrimary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(taminColors.blueBg)
                    .border(
                        1.dp,
                        taminColors.blueText.copy(alpha = 0.25f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = taminColors.blueText,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    TaminText(
                        text = "این پیام‌ها عیناً از سامانه تأمین دریافت شده است.اطلاعات وارد شده شما پاک نشده است.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        ),
                        color = taminColors.blueText,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            TaminFilledButton(
                text = "بازگشت به فرم و بررسی",
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@PreviewRtlTheme
@Preview
@Composable
private fun SubmitErrorsBottomSheetPreview() {
    PreviewRtlThemeContent {
        SubmitErrorsBottomSheet(
            problems = listOf(
                HealthProblemPR(code = 1, message = "کد ملی وارد شده در سامانه استعلام یافت نشد."),
                HealthProblemPR(
                    code = 2,
                    message = "شمارهٔ موبایل با شمارهٔ ثبت‌شدهٔ بیمه‌شده مطابقت ندارد."
                ),
                HealthProblemPR(
                    code = 3,
                    message = "تاریخ تولد با مدارک هویتی ثبت‌شده هم‌خوانی ندارد."
                )
            ),
            onDismiss = {}
        )
    }
}
