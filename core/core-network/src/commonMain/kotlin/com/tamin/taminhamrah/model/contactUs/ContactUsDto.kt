package com.tamin.taminhamrah.model.contactUs

import kotlinx.serialization.Serializable

@Serializable
data class ContactUsInfoDto(
    val hotline: HotlineDto,
    val socialChannels: List<SocialChannelDto>,
    val contactDetails: List<ContactDetailDto>,
    val footerTitle: String,
    val footerSubtitle: String
)

@Serializable
data class HotlineDto(
    val title: String,
    val number: String,
    val dialNumber: String
)

@Serializable
data class SocialChannelDto(
    val id: String,
    val title: String,
    val type: String,
    val actionUrl: String
)

@Serializable
data class ContactDetailDto(
    val id: String,
    val type: String,
    val title: String,
    val value: String,
    val actionUrl: String,
    val canCopy: Boolean
)
