package com.tamin.taminhamrah.ui.components.khadamat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BorderColor
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.TransferWithinAStation
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WheelchairPickup
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.model.common.MainServiceDN
import com.tamin.taminhamrah.model.common.MenuServiceStatusDN
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.TaminLightBgPage
import com.tamin.taminhamrah.ui.theme.TaminLightTextPrimary
import com.tamin.taminhamrah.ui.theme.TaminTeal700

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

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                enabled = !isDisabled,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .alpha(cardAlpha),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(55.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(17.dp),
                    clip = false
                )
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFFFFF),
                            Color(0xFFE9F1FF)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(1f, 1f)
                    ),
                    shape = RoundedCornerShape(17.dp)
                ),
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.5f),
                                Color.White.copy(alpha = 0f)
                            )
                        ),
                        shape = RoundedCornerShape(17.dp)
                    )
            )


            Icon(
                imageVector = getIconForName(service.icon),
                contentDescription = service.name,
                tint = Color(0xFF18468C),
                modifier = Modifier.size(33.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = service.name ?: "",
            style = MaterialTheme.typography.titleSmall.copy(
                color = TaminLightTextPrimary,
                textAlign = TextAlign.Center,
            ),
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp)
        )
    }
}

private fun getIconForName(name: String?): ImageVector {
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
                .background(TaminLightBgPage)
                .padding(16.dp),
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

                Spacer(modifier = Modifier.height(16.dp))

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
