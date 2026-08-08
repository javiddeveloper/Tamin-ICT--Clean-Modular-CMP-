package com.tamin.taminhamrah.mapper.contactUs

import com.tamin.taminhamrah.model.contactUs.ContactDetailDN
import com.tamin.taminhamrah.model.contactUs.ContactDetailPR
import com.tamin.taminhamrah.model.contactUs.ContactDetailType
import com.tamin.taminhamrah.model.contactUs.ContactDetailTypePR
import com.tamin.taminhamrah.model.contactUs.ContactUsInfoDN
import com.tamin.taminhamrah.model.contactUs.ContactUsPR
import com.tamin.taminhamrah.model.contactUs.HotlineDN
import com.tamin.taminhamrah.model.contactUs.HotlinePR
import com.tamin.taminhamrah.model.contactUs.SocialChannelDN
import com.tamin.taminhamrah.model.contactUs.SocialChannelPR
import com.tamin.taminhamrah.model.contactUs.SocialChannelType
import com.tamin.taminhamrah.model.contactUs.SocialChannelTypePR
import kotlinx.collections.immutable.toImmutableList

fun ContactUsInfoDN.toPresentation(): ContactUsPR = ContactUsPR(
    hotline = hotline.toPresentation(),
    socialChannels = socialChannels.map { it.toPresentation() }.toImmutableList(),
    contactDetails = contactDetails.map { it.toPresentation() }.toImmutableList(),
    footerTitle = footerTitle,
    footerSubtitle = footerSubtitle
)

fun HotlineDN.toPresentation(): HotlinePR = HotlinePR(
    title = title,
    number = number,
    dialNumber = dialNumber
)

fun SocialChannelDN.toPresentation(): SocialChannelPR = SocialChannelPR(
    id = id,
    title = title,
    type = type.toPresentation(),
    actionUrl = actionUrl
)

fun SocialChannelType.toPresentation(): SocialChannelTypePR = when (this) {
    SocialChannelType.EMAIL -> SocialChannelTypePR.EMAIL
    SocialChannelType.FAQ -> SocialChannelTypePR.FAQ
    SocialChannelType.WHATSAPP -> SocialChannelTypePR.WHATSAPP
    SocialChannelType.IGAP -> SocialChannelTypePR.IGAP
    SocialChannelType.BALE -> SocialChannelTypePR.BALE
    SocialChannelType.BISPHONE -> SocialChannelTypePR.BISPHONE
    SocialChannelType.GAP -> SocialChannelTypePR.GAP
    SocialChannelType.SOROUSH -> SocialChannelTypePR.SOROUSH
    SocialChannelType.RUBIKA -> SocialChannelTypePR.RUBIKA
    SocialChannelType.EITAA -> SocialChannelTypePR.EITAA
}

fun ContactDetailDN.toPresentation(): ContactDetailPR = ContactDetailPR(
    id = id,
    type = type.toPresentation(),
    title = title,
    value = value,
    actionUrl = actionUrl,
    canCopy = canCopy
)

fun ContactDetailType.toPresentation(): ContactDetailTypePR = when (this) {
    ContactDetailType.PHONE -> ContactDetailTypePR.PHONE
    ContactDetailType.FAX -> ContactDetailTypePR.FAX
    ContactDetailType.ADDRESS -> ContactDetailTypePR.ADDRESS
    ContactDetailType.POSTAL_CODE -> ContactDetailTypePR.POSTAL_CODE
    ContactDetailType.WEBSITE -> ContactDetailTypePR.WEBSITE
    ContactDetailType.NEWS -> ContactDetailTypePR.NEWS
    ContactDetailType.EMAIL -> ContactDetailTypePR.EMAIL
}
