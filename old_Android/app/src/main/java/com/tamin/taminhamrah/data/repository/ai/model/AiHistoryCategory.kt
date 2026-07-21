package com.tamin.taminhamrah.data.repository.ai.model

import android.os.Parcelable
import com.tamin.taminhamrah.data.entity.ai.AiHistoryCategoryEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class AiHistoryCategory(
    val id: String,
    val title: String,
    val userNationalCode: String,
    val date:  Long,
    val lastMessageDate: Long,
    val messageCount: Int
) : Parcelable


fun AiHistoryCategory.toEntity() = AiHistoryCategoryEntity(
    id = id,
    title = title,
    userNationalCode = this.userNationalCode,
    date = lastMessageDate, // Assuming PersianDate can be converted
    lastMessageDate = lastMessageDate,
    messageCount = messageCount
)