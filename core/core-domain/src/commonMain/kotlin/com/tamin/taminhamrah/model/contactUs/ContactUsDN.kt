package com.tamin.taminhamrah.model.contactUs

data class ContactUsInfoDN(
    val hotline: HotlineDN,
    val socialChannels: List<SocialChannelDN>,
    val contactDetails: List<ContactDetailDN>,
    val footerTitle: String,
    val footerSubtitle: String
)

data class HotlineDN(
    val title: String,
    val number: String,
    val dialNumber: String
)

data class SocialChannelDN(
    val id: String,
    val title: String,
    val type: SocialChannelType,
    val actionUrl: String
)

enum class SocialChannelType {
    EMAIL,
    FAQ,
    WHATSAPP,
    IGAP,
    BALE,
    BISPHONE,
    GAP,
    SOROUSH,
    RUBIKA,
    EITAA
}

data class ContactDetailDN(
    val id: String,
    val type: ContactDetailType,
    val title: String,
    val value: String,
    val actionUrl: String?,
    val canCopy: Boolean
)

enum class ContactDetailType {
    PHONE,
    FAX,
    ADDRESS,
    POSTAL_CODE,
    WEBSITE,
    NEWS,
    EMAIL
}
