package com.tamin.taminhamrah.feature.taminServices.funeralAllowance.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.theme.LocalTaminColors

@Composable
internal fun FuneralAllowanceEligibilitySuccessDialog(
    onContinue: () -> Unit,
    onDismiss: () -> Unit,
) {
    val taminColors = LocalTaminColors.current

    TaminConfirmationDialog(
        title = "شرایط برخورداری احراز شد",
        description = "بر اساس استعلام انجام‌شده، شما واجد شرایط دریافت کمک‌هزینه مراسم ترحیم هستید. در ادامه اطلاعات متوفی و حساب واریز را تأیید و مدارک را بارگذاری کنید.",
        confirmButton = {
            TaminFilledButton(
                text = "ادامه درخواست",
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )
        },
        dismissButton = {},
        onDismissRequest = onDismiss,
        icon = Icons.Default.Check,
        iconTint = taminColors.greenText,
        iconBackground = taminColors.greenBg,
    )
}
