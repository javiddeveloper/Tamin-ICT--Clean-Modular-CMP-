package com.tamin.taminhamrah.model.contractFlow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.contracts.RegistrationInfoPR

@Composable
fun ContractTermsStepContent(
    info: RegistrationInfoPR,
    isRulesConfirmed: Boolean,
    onRulesConfirmedChange: (Boolean) -> Unit,
    onShowRules: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            ),
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = Color(0xFFE65100),
                    modifier = Modifier.size(22.dp),
                )
                Text(
                    text = "تایید این مرحله به منزله مطالعه و پذیرش مقررات و ضوابط انعقاد قرارداد می‌باشد.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        Button(
            onClick = onShowRules,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("مشاهده ضوابط و مقررات")
        }

        Text(
            text = "تعهدنامه",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Checkbox(
                checked = isRulesConfirmed,
                onCheckedChange = onRulesConfirmedChange,
            )
            Text(
                text = buildCommitmentText(
                    genderTitle = info.genderTitle,
                    fullName = info.fullName,
                ),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }
}

private fun buildCommitmentText(
    genderTitle: String,
    fullName: String,
) = buildAnnotatedString {
    append("اینجانب $genderTitle ")
    withStyle(SpanStyle(color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)) {
        append(fullName)
    }
    append(
        " با آگاهی کامل و در صحت عقلی، شرایط و مقررات فوق را مطالعه و خود را در هنگام قرارداد " +
            "و در ادامه بیمه پردازی ملزم به رعایت آن می‌دانم در غیر اینصورت کلیه تبعات و مسئولیت‌های آن " +
            "متوجه اینجانب بوده و سازمان تأمین اجتماعی در این خصوص مسئولیتی نخواهد داشت.",
    )
}
