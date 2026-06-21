package com.tamin.taminhamrah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personal_inbox_items")
data class PersonalInboxItemEntity(
    @PrimaryKey
    val id: Long,
    val nationalCode: String?,
    val mobileNumber: String?,
    val email: String?,
    val read: String?,
    val data: String?,
    val sentDate: Long?,
    val receiveDate: Long?,
    val seenDate: Long?,
    val seen: Boolean?,
    val hasImage: Boolean?,
    val hasText: Boolean?,
    val hasPdf: Boolean?,
    val updateable: Boolean?,
    val status: String?,
    val referenceId: String?,
    val pdf: String?,
    val typeDesc: String?,
    val typeCode: String?,
    val subTypeDesc: String?,
    val subTypeCode: String?,
    val permissionPassword: Long?,
    val permissionDateFrom: Long?,
    val permissionDateTo: Long?,
)

@Entity(tableName = "personal_inbox_size")
data class PersonalInboxSizeEntity(
    @PrimaryKey
    val id: Int = SINGLE_ROW_ID,
    val usage: String?,
    val total: String?,
) {
    companion object {
        const val SINGLE_ROW_ID = 1
    }
}
