package com.tamin.taminhamrah.data.local.converter

import androidx.room.TypeConverter
import com.tamin.taminhamrah.model.common.MenuServiceStatusDN

import kotlinx.serialization.json.Json
import com.tamin.taminhamrah.data.local.entity.UserInfoEntity
import com.tamin.taminhamrah.data.local.entity.StoryChannelEntity
import com.tamin.taminhamrah.data.local.entity.CampaignEntity
import com.tamin.taminhamrah.data.local.entity.QuickAccessEntity
import com.tamin.taminhamrah.data.local.entity.SpecialServiceEntity
import com.tamin.taminhamrah.data.local.entity.RequestEntity

class TaminHamrahConverters {
    @TypeConverter
    fun fromMenuServiceStatus(status: MenuServiceStatusDN?): String? {
        return status?.name
    }

    @TypeConverter
    fun toMenuServiceStatus(name: String?): MenuServiceStatusDN? {
        return name?.let { MenuServiceStatusDN.valueOf(it) }
    }

    @TypeConverter
    fun fromIntList(list: List<Int?>): String {
        return list.joinToString(",") { it?.toString() ?: "" }
    }

    @TypeConverter
    fun toIntList(data: String?): List<Int?> {
        if (data.isNullOrEmpty()) return emptyList()
        return data.split(",").map { it.toIntOrNull() }
    }

    @TypeConverter
    fun fromUserInfoEntity(value: UserInfoEntity?): String? = value?.let { Json.encodeToString(it) }

    @TypeConverter
    fun toUserInfoEntity(value: String?): UserInfoEntity? = value?.let { Json.decodeFromString(it) }

    @TypeConverter
    fun fromStoryChannelEntityList(value: List<StoryChannelEntity>?): String? = value?.let { Json.encodeToString(it) }

    @TypeConverter
    fun toStoryChannelEntityList(value: String?): List<StoryChannelEntity>? = value?.let { Json.decodeFromString(it) }

    @TypeConverter
    fun fromCampaignEntityList(value: List<CampaignEntity>?): String? = value?.let { Json.encodeToString(it) }

    @TypeConverter
    fun toCampaignEntityList(value: String?): List<CampaignEntity>? = value?.let { Json.decodeFromString(it) }

    @TypeConverter
    fun fromQuickAccessEntityList(value: List<QuickAccessEntity>?): String? = value?.let { Json.encodeToString(it) }

    @TypeConverter
    fun toQuickAccessEntityList(value: String?): List<QuickAccessEntity>? = value?.let { Json.decodeFromString(it) }

    @TypeConverter
    fun fromSpecialServiceEntityList(value: List<SpecialServiceEntity>?): String? = value?.let { Json.encodeToString(it) }

    @TypeConverter
    fun toSpecialServiceEntityList(value: String?): List<SpecialServiceEntity>? = value?.let { Json.decodeFromString(it) }

    @TypeConverter
    fun fromRequestEntityList(value: List<RequestEntity>?): String? = value?.let { Json.encodeToString(it) }

    @TypeConverter
    fun toRequestEntityList(value: String?): List<RequestEntity>? = value?.let { Json.decodeFromString(it) }
}
