package com.tamin.taminhamrah.feature.taminServices.occurrence.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.lock_content_description

@Composable
fun InfoBanner(
    message: String,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(taminColors.blueBg, RoundedCornerShape(13.dp))
            .border(1.dp, taminColors.blueText.copy(alpha = 0.2f), RoundedCornerShape(13.dp))
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = stringResource(Res.string.lock_content_description),
            tint = taminColors.blueText,
            modifier = Modifier
                .size(18.dp)
                .align(Alignment.Top)
        )
        Spacer(modifier = Modifier.width(10.dp))
        TaminText(
            text = message,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Medium,
            color = taminColors.blueText,
            lineHeight = 18.sp,
            modifier = Modifier.weight(1f)
        )
    }
}
