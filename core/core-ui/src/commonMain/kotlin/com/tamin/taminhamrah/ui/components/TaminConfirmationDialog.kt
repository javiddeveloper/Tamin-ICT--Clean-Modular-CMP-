package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.LocalTaminColors

@Composable
fun TaminConfirmationDialog(
    title: String,
    description: String,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    onDismissRequest: () -> Unit,
    icon: ImageVector? = null,
    /** Defaults keep the informational blue every existing caller expects. */
    iconTint: Color = LocalTaminColors.current.blueText,
    iconBackground: Color = LocalTaminColors.current.blueBg,
    iconBackgroundBrush: Brush? = null,
    /**
     * Optional block between the description and the buttons, for anything the description cannot
     * be: a value to copy, a field to read back. Omitted by every caller that only needs prose.
     */
    content: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (icon != null) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(iconBackgroundBrush ?: SolidColor(iconBackground)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                TaminText(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = taminColors.textPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                TaminText(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        lineHeight = 21.sp
                    ),
                    color = taminColors.textSecondary,
                    textAlign = TextAlign.Center
                )

                if (content != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    content()
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    confirmButton()
                    dismissButton()
                }
            }
        }
    }
}

@PreviewRtlTheme
@Preview
@Composable
private fun TaminConfirmationDialogPreview() {
    PreviewRtlThemeContent {
        TaminConfirmationDialog(
            title = "ثبت نهایی اطلاعات",
            description = "آیا از صحت اطلاعات واردشده اطمینان دارید؟ پس از ثبت نهایی، امکان ویرایش برخی اطلاعات محدود خواهد بود.",
            confirmButton = {
                TaminFilledButton(
                    text = "تأیید و ثبت نهایی",
                    icon = Icons.Default.Check,
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    height = 50.dp,
                    shape = RoundedCornerShape(14.dp)
                )
            },
            dismissButton = {
                TaminOutlinedButton(
                    text = "انصراف",
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    height = 50.dp,
                    shape = RoundedCornerShape(14.dp)
                )
            },
            onDismissRequest = {},
            icon = Icons.Outlined.Lock
        )
    }
}
