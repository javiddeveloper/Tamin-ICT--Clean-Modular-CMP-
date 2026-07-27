package com.tamin.taminhamrah.feature.taminServices.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.common.MenuServiceStatusDN
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.*

@Composable
fun ServiceCard(
    service: MainServiceDN,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val isDisabled = service.status == MenuServiceStatusDN.DISABLED ||
        service.status == MenuServiceStatusDN.TEMPORARY_DISABLED ||
        service.status == MenuServiceStatusDN.COMPLETELY_DISABLED

    val cardAlpha = if (isDisabled) 0.2f else 1.0f
    val shadowElevation = if (isDisabled) Elevation.none else Elevation.md

    val showRedDot = service.status == MenuServiceStatusDN.TEMPORARY_DISABLED ||
        service.status == MenuServiceStatusDN.DISABLED ||
        service.status == MenuServiceStatusDN.COMPLETELY_DISABLED ||
        service.status == MenuServiceStatusDN.ENABLED_WITH_ERROR

    Box(
        modifier = modifier
            .wrapContentSize()
            .padding(top = Spacing.xxs, end = Spacing.xxs),
        contentAlignment = Alignment.TopStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(16.dp)
                )
                .clip(RoundedCornerShape(16.dp))
                .background(color = MaterialTheme.colorScheme.surface)
                .clickable(enabled = !isDisabled) { onClick() }
                .padding(Spacing.lg)
                .alpha(cardAlpha),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(IconSize.xlarge)
                    .shadow(
                        elevation = shadowElevation,
                        shape = RoundedCornerShape(CornerRadius.xl),
                        clip = false,
                        ambientColor = if (isDark) Color.Black else MaterialTheme.colorScheme.primary,
                        spotColor = if (isDark) Color.Black else MaterialTheme.colorScheme.primary,
                    )
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface,
                                if (isDark) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                            ),
                            start = Offset.Zero,
                            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        ),
                        shape = RoundedCornerShape(CornerRadius.xl)
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = if (isDark) 0.15f else 0.9f),
                                Color.White.copy(alpha = if (isDark) 0.02f else 0.1f)
                            )
                        ),
                        shape = RoundedCornerShape(CornerRadius.xl)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = if (isDark) 0.05f else 0.6f),
                                    Color.Transparent
                                )
                            ),
                            shape = RoundedCornerShape(CornerRadius.xl)
                        )
                )
                Icon(
                    imageVector = getIconForName(service.icon),
                    contentDescription = service.name,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(IconSize.large)
                        .padding(Spacing.sm)
                )
            }

            Spacer(modifier = Modifier.width(Spacing.sm))

            AutoResizeText(
                text = service.name ?: "",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Right,
                ),
                maxLines = 2,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.xxs)
            )
        }

        if (showRedDot) {
            Box(
                modifier = Modifier
                    .size(IconSize.statIcon)
                    .align(Alignment.TopEnd)
                    .offset(
                        x = 2.dp / 4,
                        y = -(2.dp / 4)
                    )
                    .background(
                        MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        CircleShape
                    )
            )
        }
    }
}

@Composable
private fun AutoResizeText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    minFontSize: TextUnit = 8.sp,
) {
    var resizedTextStyle by remember(text) { mutableStateOf(style) }
    var readyToDraw by remember(text) { mutableStateOf(false) }

    Text(
        text = text,
        modifier = modifier.drawWithContent {
            if (readyToDraw) drawContent()
        },
        style = resizedTextStyle,
        softWrap = true,
        maxLines = maxLines,
        onTextLayout = { result ->
            if (result.didOverflowHeight && resizedTextStyle.fontSize > minFontSize) {
                resizedTextStyle = resizedTextStyle.copy(
                    fontSize = resizedTextStyle.fontSize * 0.95f
                )
            } else {
                readyToDraw = true
            }
        }
    )
}

internal fun getIconForName(name: String?): ImageVector {
    return when (name) {
        "user" -> Icons.Default.Person
        "relation" -> Icons.Default.Link
        "credit-card" -> Icons.Default.CreditCard
        "camera" -> Icons.Default.PhotoCamera
        "relationship" -> Icons.Default.People
        "inbox" -> Icons.Default.Inbox
        "bill" -> Icons.Default.Receipt
        "budget" -> Icons.Default.AttachMoney
        "paper-plane" -> Icons.Default.Send
        "protest" -> Icons.Default.Gavel
        "list" -> Icons.Default.List
        "obligation" -> Icons.Default.Assignment
        "love" -> Icons.Default.Favorite
        "crutch" -> Icons.Default.Accessibility
        "medical" -> Icons.Default.LocalHospital
        "death" -> Icons.Default.LocalFlorist
        "cctv" -> Icons.Default.Visibility
        "wedding-presents" -> Icons.Default.CardGiftcard
        "medicine" -> Icons.Default.Healing
        "scan" -> Icons.Default.QrCodeScanner
        "calc" -> Icons.Default.Calculate
        "first-aid-kit" -> Icons.Default.MedicalServices
        "folder" -> Icons.Default.Folder
        "agreement-freelance" -> Icons.Default.Handshake
        "student" -> Icons.Default.School
        "contract_payment" -> Icons.Default.Payment
        "woman_agreement-freelance" -> Icons.Default.Face
        "optional-insurance" -> Icons.Default.VerifiedUser
        "student_inquiry" -> Icons.Default.Search
        "survivors" -> Icons.Default.FamilyRestroom
        "ticket" -> Icons.Default.ConfirmationNumber
        "objecting_history_bugs" -> Icons.Default.ReportProblem
        "insurance" -> Icons.Default.Policy
        "announcement" -> Icons.Default.Campaign
        "stamp" -> Icons.Default.AppRegistration
        "document" -> Icons.Default.Description
        "agreement" -> Icons.Default.AssignmentTurnedIn
        "disability" -> Icons.Default.WheelchairPickup
        "workshop" -> Icons.Default.Business
        "contract" -> Icons.Default.BorderColor
        "ic_assigner" -> Icons.Default.TransferWithinAStation
        "employer_info" -> Icons.Default.Info
        "onlineServiceReq" -> Icons.Default.CloudQueue
        "update" -> Icons.Default.Update
        else -> Icons.Default.HelpOutline
    }
}

@PreviewRtlTheme
@Composable
private fun TaminServicesTagPreview() {
    PreviewRtlThemeContent {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(Spacing.lg),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    ServiceCard(
                        service = MainServiceDN(
                            name = "بیمه دانشجویی",
                            icon = "insurance",
                            status = MenuServiceStatusDN.ACTIVE
                        ),
                        onClick = { }
                    )
                    ServiceCard(
                        service = MainServiceDN(
                            name = "کارگاه‌ها",
                            icon = "workshop",
                            status = MenuServiceStatusDN.ENABLED_WITH_ERROR
                        ),
                        onClick = { }
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.lg))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    ServiceCard(
                        service = MainServiceDN(
                            name = "بیمه مشاغل آزاد",
                            icon = "calc",
                            status = MenuServiceStatusDN.DISABLED
                        ),
                        onClick = { }
                    )
                }
            }
        }
    }
}
