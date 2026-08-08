package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.contactUs.*

internal fun ContactUsInfoDto.toDomain(): ContactUsInfoDN = ContactUsInfoDN(
    hotline = hotline.toDomain(),
    socialChannels = socialChannels.map { it.toDomain() },
    contactDetails = contactDetails.map { it.toDomain() },
    footerTitle = footerTitle,
    footerSubtitle = footerSubtitle
)

internal fun HotlineDto.toDomain(): HotlineDN = HotlineDN(
    title = title,
    number = number,
    dialNumber = dialNumber
)

internal fun SocialChannelDto.toDomain(): SocialChannelDN = SocialChannelDN(
    id = id,
    title = title,
    type = try {
        SocialChannelType.valueOf(type)
    } catch (e: Exception) {
        SocialChannelType.EMAIL // Fallback
    },
    actionUrl = actionUrl
)

internal fun ContactDetailDto.toDomain(): ContactDetailDN = ContactDetailDN(
    id = id,
    type = try {
        ContactDetailType.valueOf(type)
    } catch (e: Exception) {
        ContactDetailType.PHONE // Fallback
    },
    title = title,
    value = value,
    actionUrl = actionUrl,
    canCopy = canCopy
)
