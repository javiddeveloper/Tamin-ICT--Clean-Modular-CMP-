package com.tamin.taminhamrah.model.contactUs

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class ContactUsPR(
    val hotline: HotlinePR,
    val socialChannels: ImmutableList<SocialChannelPR>,
    val contactDetails: ImmutableList<ContactDetailPR>,
    val footerTitle: String,
    val footerSubtitle: String
)

@Immutable
@Serializable
data class HotlinePR(
    val title: String,
    val number: String,
    val dialNumber: String
)

@Immutable
@Serializable
data class SocialChannelPR(
    val id: String,
    val title: String,
    val type: SocialChannelTypePR,
    val actionUrl: String
)

enum class SocialChannelTypePR {
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

@Immutable
@Serializable
data class ContactDetailPR(
    val id: String,
    val type: ContactDetailTypePR,
    val title: String,
    val value: String,
    val actionUrl: String?,
    val canCopy: Boolean
)

enum class ContactDetailTypePR {
    PHONE,
    FAX,
    ADDRESS,
    POSTAL_CODE,
    WEBSITE,
    NEWS,
    EMAIL
}
