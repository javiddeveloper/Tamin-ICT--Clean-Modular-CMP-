package com.tamin.taminhamrah.ui.components.khadamat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
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
    val isDisabled = service.status == MenuServiceStatusDN.DISABLED ||
            service.status == MenuServiceStatusDN.TEMPORARY_DISABLED ||
            service.status == MenuServiceStatusDN.COMPLETELY_DISABLED

    val cardAlpha = if (isDisabled) 0.5f else 1.0f
    val shadowElevation = if (isDisabled) Elevation.none else Elevation.md

    val showRedDot = service.status == MenuServiceStatusDN.TEMPORARY_DISABLED ||
            service.status == MenuServiceStatusDN.DISABLED ||
            service.status == MenuServiceStatusDN.COMPLETELY_DISABLED ||
            service.status == MenuServiceStatusDN.ENABLED_WITH_ERROR

    Box(
        modifier = modifier.wrapContentSize(),
        contentAlignment = Alignment.TopStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = MaterialTheme.shapes.medium
                )
                .clip(MaterialTheme.shapes.medium)
                .clickable(
                    enabled = true,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onClick() }
                .padding(Spacing.sm)
                .alpha(cardAlpha),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(IconSize.large)
                    .shadow(
                        elevation = shadowElevation,
                        shape = RoundedCornerShape(CornerRadius.xl),
                        clip = false
                    )
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.primaryContainer
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(1f, 1f)
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
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0f)
                                )
                            ),
                            shape = RoundedCornerShape(CornerRadius.xl)
                        )
                )

                Icon(
                    imageVector = getIconForName(service.icon),
                    contentDescription = service.name,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(IconSize.medium)
                )
            }

            Spacer(modifier = Modifier.width(Spacing.sm))

            Text(
                text = service.name ?: "",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End,
                ),
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.xxs)
            )
        }

        if (showRedDot) {
            val layoutDirection = LocalLayoutDirection.current
            val badgeAlignment =
                if (layoutDirection == LayoutDirection.Rtl) {
                    Alignment.TopEnd
                } else {
                    Alignment.TopStart
                }

            Box(
                modifier = Modifier
                    .size(IconSize.statIcon)
                    .align(badgeAlignment)
                    .absoluteOffset(x = (-4).dp, y = (-4).dp)
                    .background(
                        MaterialTheme.colorScheme.error,
                        androidx.compose.foundation.shape.CircleShape
                    )
            )
        }
    }
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
private fun KhadamatTagPreview() {
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
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ServiceCard(
                        service = MainServiceDN(
                            name = "بیمه دانشجویی",
                            icon = "insurance",
                            status = MenuServiceStatusDN.ACTIVE
                        ),
                        onClick = { }
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.lg))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
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
