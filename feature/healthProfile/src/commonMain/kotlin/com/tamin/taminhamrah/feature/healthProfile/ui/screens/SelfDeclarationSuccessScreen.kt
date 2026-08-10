package com.tamin.taminhamrah.feature.healthProfile.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.healthProfile.ui.components.*
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.stringResource
import taminx.feature.healthprofile.generated.resources.*

@Composable
fun SelfDeclarationSuccessScreen(
    onFinish: (Boolean) -> Unit
) {
    val taminColors = LocalTaminColors.current

    Scaffold(
        topBar = {
            HealthTopAppBar(title = stringResource(Res.string.health_success_title), onBackClicked = { onFinish(false) })
        },
        bottomBar = {
            HealthIrritateNavigationBar(
                primaryText = stringResource(Res.string.health_success_btn_enter),
                showChevron = false,
                onPrimaryClick = { onFinish(true) }, // true means enter health profile
                secondaryText = stringResource(Res.string.health_success_btn_back),
                onSecondaryClick = { onFinish(false) } // false means close flow
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(taminColors.bgPage),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(24.dp)
            ) {
                // Success Tick Icon
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .background(taminColors.greenBg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = taminColors.greenText,
                        modifier = Modifier.size(54.dp)
                    )
                }

                TaminText(
                    text = stringResource(Res.string.health_success_title),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = taminColors.textPrimary
                    )
                )

                TaminText(
                    text = stringResource(Res.string.health_success_desc),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = taminColors.textTertiary,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center
                    )
                )

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
                    border = BorderStroke(1.dp, taminColors.border),
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TaminText(
                            text = stringResource(Res.string.health_success_tracking_code),
                            style = MaterialTheme.typography.bodyMedium,
                            color = taminColors.textSecondary
                        )
                    }
                }
            }
        }
    }
}

@PreviewRtlTheme
@Preview
@Composable
fun SelfDeclarationSuccessScreenPreview() {
    PreviewRtlThemeContent {
        SelfDeclarationSuccessScreen(
            onFinish = {}
        )
    }
}
