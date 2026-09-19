package com.tamin.taminhamrah.ui.components

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
import androidx.compose.material.icons.filled.Payment
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Maps a menu service's `icon` string (from `mockMenuData` / `menu.json`) to a Material icon.
 *
 * Single source shared by the خدمات list ([ServiceCard]) and the home «دسترسی سریع» / «خدمات ویژه»
 * tiles, so both render the same glyph for a given service. Unknown / null keys fall back to a
 * neutral help glyph.
 */
fun serviceIconFor(name: String?): ImageVector = when (name) {
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
