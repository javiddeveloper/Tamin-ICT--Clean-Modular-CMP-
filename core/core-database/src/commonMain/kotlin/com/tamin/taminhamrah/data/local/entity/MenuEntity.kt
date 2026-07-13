package com.tamin.taminhamrah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tamin.taminhamrah.model.common.MenuServiceStatusDN

@Entity(tableName = "menu_items")
data class MenuEntity(
    @PrimaryKey
    val id: Int,
    val name: String?,
    val subtitle: String?,
    val icon: String?,
    val active: Boolean?,
    val newService: Boolean?,
    val sorting: Int?,
    val url: String?,
    val status: MenuServiceStatusDN?,
    val message: String?,
    val showRole: List<Int?>,
    val hiddenForVersions: List<Int?>
)
