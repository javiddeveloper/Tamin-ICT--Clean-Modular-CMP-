package com.tamin.taminhamrah.feature.treatment.ui.healthProfile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.treatment.ui.components.LifestyleItem
import com.tamin.taminhamrah.feature.treatment.ui.components.SubFlowHeader
import com.tamin.taminhamrah.feature.treatment.ui.contract.HealthProfileUiState
import com.tamin.taminhamrah.feature.treatment.ui.model.TreatmentMocks
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Spacing

@Composable
fun HealthProfileContent(
    state: HealthProfileUiState,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SubFlowHeader(title = "پرونده و خوداظهاری سلامت", onBack = onBack)

        if (state.isLoading && state.patientGeneral == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = Spacing.md, vertical = Spacing.xs),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                state.patientGeneral?.let { gen ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(CornerRadius.sm),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(Spacing.md)) {
                                Text(
                                    text = "مشخصات عمومی پرونده",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(Spacing.xs))
                                HorizontalDivider()
                                Spacer(modifier = Modifier.height(Spacing.xs))
                                Text(
                                    text = "نام بیمار: ${gen.patientName ?: ""} ${gen.patientFamily ?: ""}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "سن: ${gen.patientAge ?: "نامشخص"} سال",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "جنسیت: ${gen.patientGender ?: "نامشخص"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "تاریخ تولد: ${gen.patientBirthDate ?: ""}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "موبایل: ${gen.patientMobile ?: ""}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (!gen.patientAddress.isNullOrEmpty()) {
                                    Text(
                                        text = "آدرس: ${gen.patientAddress}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(CornerRadius.sm),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(Spacing.md)) {
                            Text(
                                text = "شاخص‌های سبک زندگی",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(Spacing.xs))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(Spacing.xs))

                            if (state.isSelfDeclarativeLoading) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(Spacing.md),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                }
                            } else {
                                val self = state.patientSelfDeclarative
                                if (self == null) {
                                    Text(
                                        text = "اطلاعات سبک زندگی ثبت نشده است.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                } else {
                                    LifestyleItem(label = "وضعیت دخانیات", valText = self.smokingStatusTitle, isWarning = self.smokingStatusTitle?.contains("سیگار") == true)
                                    LifestyleItem(label = "فعالیت ورزشی", valText = self.exerciseFreqTitle, isWarning = false)
                                    LifestyleItem(label = "مصرف الکل", valText = self.alcoholUsageTitle, isWarning = self.alcoholUsageTitle?.contains("دارد") == true)

                                    if (!self.lastUpdateDate.isNullOrEmpty()) {
                                        Spacer(modifier = Modifier.height(Spacing.xs))
                                        Text(
                                            text = "آخرین بروزرسانی: ${self.lastUpdateDate}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(CornerRadius.sm),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(Spacing.md)) {
                            Text(
                                text = "حساسیت‌های دارویی بیمار",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(Spacing.xs))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(Spacing.xs))

                            if (state.isDrugAllergiesLoading) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(Spacing.md),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                }
                            } else if (state.patientDrugAllergies.isEmpty()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "هیچ حساسیت دارویی ثبت نشده است.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            } else {
                                state.patientDrugAllergies.forEach { item ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = item.drugName ?: "داروی نامشخص",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFC62828)
                                            )
                                            if (!item.allergyComments.isNullOrEmpty()) {
                                                Text(
                                                    text = "توضیح: ${item.allergyComments}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = Color(0xFFC62828),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@PreviewRtlTheme
@Composable
fun HealthProfileContentPreview() {
    PreviewRtlThemeContent {
        HealthProfileContent(
            state = TreatmentMocks.healthProfileUiState,
            onBack = {}
        )
    }
}
