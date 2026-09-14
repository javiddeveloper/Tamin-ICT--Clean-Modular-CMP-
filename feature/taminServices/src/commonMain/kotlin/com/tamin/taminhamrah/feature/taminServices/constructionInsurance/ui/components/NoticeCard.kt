package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.notice_point_1
import taminx.core.core_ui.notice_point_2
import taminx.core.core_ui.notice_point_3
import taminx.core.core_ui.notice_point_4
import taminx.core.core_ui.notice_title

@Composable
fun NoticeCard(modifier: Modifier = Modifier) {
    BoxContainer(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(Res.string.notice_title),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = LocalTaminColors.current.orangeText,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            BulletPoint(stringResource(Res.string.notice_point_1))
            Spacer(modifier = Modifier.height(8.dp))
            BulletPoint(stringResource(Res.string.notice_point_2))
            Spacer(modifier = Modifier.height(8.dp))
            BulletPoint(stringResource(Res.string.notice_point_3))
            Spacer(modifier = Modifier.height(8.dp))
            BulletPoint(stringResource(Res.string.notice_point_4))
        }
    }
}

@Composable
private fun BoxContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val taminColors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(taminColors.orangeBg.copy(alpha = 0.3f))
            .border(1.dp, taminColors.orangeText, RoundedCornerShape(16.dp))
    ) {
        content()
    }
}

@Composable
private fun BulletPoint(text: String) {
    val taminColors = LocalTaminColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            color = taminColors.orangeText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = taminColors.orangeText,
            lineHeight = 18.sp
        )
    }
}
