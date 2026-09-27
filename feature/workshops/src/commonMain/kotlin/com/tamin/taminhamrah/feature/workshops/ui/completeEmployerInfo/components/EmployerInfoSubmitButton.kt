package com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.Spacing

private val ButtonHeight = 52.dp
private val ButtonCorner = 15.dp
private val ButtonElevation = 6.dp
private val ButtonIconSize = 17.dp
private val ButtonTextSize = 13.5.sp

/**
 * The one submit button the three steps share.
 *
 * While the request is in flight the whole button becomes a shimmer of its own size rather than
 * growing a spinner: the page says "loading" one way everywhere, and the control does not change
 * shape underneath a thumb that is still on it.
 *
 * [enabled] false paints the disabled gradient and stops the click, so a form that cannot be sent
 * says so before it is pressed. The reason it cannot be sent is printed above it by the caller.
 */
@Composable
fun EmployerInfoSubmitButton(
    text: String,
    icon: ImageVector,
    enabled: Boolean,
    isSubmitting: Boolean,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    if (isSubmitting) {
        ShimmerBlock(
            modifier = modifier.fillMaxWidth().height(ButtonHeight),
            cornerRadius = ButtonCorner,
        )
        return
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(ButtonHeight)
            .shadow(
                elevation = if (enabled) ButtonElevation else 0.dp,
                shape = RoundedCornerShape(ButtonCorner),
                ambientColor = colors.shadowPrimary,
                spotColor = colors.shadowPrimary,
            )
            .clip(RoundedCornerShape(ButtonCorner))
            .background(if (enabled) colors.buttonGradient else colors.buttonDisabledGradient)
            .clickable(enabled = enabled, onClick = onSubmit),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.onGradient,
            modifier = Modifier.size(ButtonIconSize),
        )
        Spacer(modifier = Modifier.size(Spacing.sm))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = colors.onGradient,
                fontSize = ButtonTextSize,
            ),
        )
    }
}
