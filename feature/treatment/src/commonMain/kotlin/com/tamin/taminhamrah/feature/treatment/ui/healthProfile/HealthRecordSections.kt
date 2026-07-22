package com.tamin.taminhamrah.feature.treatment.ui.healthProfile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.health.PatientHospitalizationsPR
import com.tamin.taminhamrah.model.health.PatientImagingPR
import com.tamin.taminhamrah.model.health.PatientLabPR
import com.tamin.taminhamrah.model.health.PatientVisitPR
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Spacing

/** Titled card wrapper shared by the patient-record sections (hospitalizations, visits, labs, imaging). */
@Composable
private fun HealthRecordCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.sm),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(Spacing.xs))
            content()
        }
    }
}

@Composable
private fun RecordEntry(title: String, vararg lines: String?) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            text = title.ifBlank { "نامشخص" },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        lines.filterNot { it.isNullOrBlank() }.forEach {
            Text(
                text = it!!,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyRecord(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.outline
    )
}

@Composable
fun PatientHospitalizationsSection(list: List<PatientHospitalizationsPR>) {
    HealthRecordCard(title = "سوابق بستری") {
        if (list.isEmpty()) {
            EmptyRecord("سابقه بستری ثبت نشده است.")
        } else {
            list.forEach { item ->
                RecordEntry(
                    item.healthcareProvider ?: "مرکز درمانی نامشخص",
                    item.doctorName?.let { "پزشک: $it" },
                    listOfNotNull(item.hospitalizedStartDate, item.hospitalizedEndDate)
                        .joinToString(" تا ").ifBlank { null }?.let { "تاریخ: $it" },
                    item.finalDiagDesc?.let { "تشخیص: $it" }
                )
            }
        }
    }
}

@Composable
fun PatientVisitsSection(list: List<PatientVisitPR>) {
    HealthRecordCard(title = "سوابق ویزیت") {
        if (list.isEmpty()) {
            EmptyRecord("سابقه ویزیت ثبت نشده است.")
        } else {
            list.forEach { item ->
                RecordEntry(
                    item.serviceName ?: item.doctorName ?: "ویزیت",
                    item.docSpeciality?.let { "تخصص: $it" },
                    item.visitDate?.let { "تاریخ: $it" },
                    item.diagDesc?.let { "تشخیص: $it" }
                )
            }
        }
    }
}

@Composable
fun PatientLabsSection(list: List<PatientLabPR>) {
    HealthRecordCard(title = "آزمایش‌ها") {
        if (list.isEmpty()) {
            EmptyRecord("سابقه آزمایش ثبت نشده است.")
        } else {
            list.forEach { item ->
                RecordEntry(
                    item.examName ?: "آزمایش",
                    item.doctorName?.let { "پزشک: $it" },
                    item.visitDate?.let { "تاریخ: $it" },
                    item.resultDesc?.let { "نتیجه: $it" }
                )
            }
        }
    }
}

@Composable
fun PatientImagingSection(list: List<PatientImagingPR>) {
    HealthRecordCard(title = "تصویربرداری") {
        if (list.isEmpty()) {
            EmptyRecord("سابقه تصویربرداری ثبت نشده است.")
        } else {
            list.forEach { item ->
                RecordEntry(
                    item.imagingName ?: "تصویربرداری",
                    item.modality?.let { "نوع: $it" },
                    item.doctorName?.let { "پزشک: $it" },
                    item.visitDate?.let { "تاریخ: $it" },
                    item.resultDesc?.let { "نتیجه: $it" }
                )
            }
        }
    }
}
