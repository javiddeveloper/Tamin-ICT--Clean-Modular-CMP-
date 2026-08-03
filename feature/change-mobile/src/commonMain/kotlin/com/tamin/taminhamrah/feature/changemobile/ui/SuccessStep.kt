package com.tamin.taminhamrah.feature.changemobile.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.changemobile.ui.contract.ChangeMobileUiState
import com.tamin.taminhamrah.ui.components.ListGroupView
import com.tamin.taminhamrah.ui.components.ListItemBadge
import com.tamin.taminhamrah.ui.components.ListItemColors
import com.tamin.taminhamrah.ui.components.ListItemData
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.painterResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_mobile
import taminx.core.core_ui.ic_tamin_check

@Composable
fun SuccessStep(
    uiStateState: State<ChangeMobileUiState>,
    onFinish: () -> Unit
) {
    val taminColors = LocalTaminColors.current
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            val newMobile = uiStateState.value.newMobile
            Spacer(modifier = Modifier.height(Spacing.lg))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .coloredShadow(
                        color = taminColors.greenText.copy(alpha = 0.25f),
                        borderRadius = 50.dp,
                        blurRadius = 30.dp,
                        offsetY = 10.dp
                    )
                    .clip(CircleShape)
                    .background(taminColors.iconGradientSuccess),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_tamin_check),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xxl))

            Text(
                text = "شمارهٔ موبایل با موفقیت تغییر کرد",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = taminColors.textPrimary
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            Text(
                text = "از این پس اطلاعیه‌ها و کدهای ورود به شمارهٔ زیر پیامک می‌شود.",
                style = MaterialTheme.typography.bodyMedium,
                color = taminColors.textMuted,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(Spacing.xlg))

            ListGroupView(
                containerBackgroundColor = taminColors.verifiedContainerBg,
                containerBorder = BorderStroke(1.dp, taminColors.verifiedContainerBorder),
                items = persistentListOf(
                    ListItemData(
                        title = newMobile.toPersianDigits(),
                        leadingIconPainter = painterResource(Res.drawable.ic_mobile),
                        leadingIconElevation = Elevation.xs,
                        colors = ListItemColors(
                            leadingIconBackgroundGradient = taminColors.verifiedIconGradient,
                            leadingIconBackgroundColor = taminColors.verifiedIconBg,
                            leadingIconTintColor = taminColors.verifiedIconTint,
                            titleColor = taminColors.springGreenText
                        ),
                        showArrow = false,
                        badge = ListItemBadge(
                            text = "فعال",
                            backgroundColor = taminColors.verifiedBadgeBg,
                            textColor = taminColors.greenText
                        ),
                        titleStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                )
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            LoadingButton(
                text = "بازگشت به حساب کاربری",
                onClick = onFinish,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
