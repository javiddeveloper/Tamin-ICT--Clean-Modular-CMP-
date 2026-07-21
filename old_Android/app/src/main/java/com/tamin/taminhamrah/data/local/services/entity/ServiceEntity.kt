package com.tamin.taminhamrah.data.local.services.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.tamin.taminhamrah.data.local.services.converter.ServiceTypeConverter

@Entity(tableName = "service")
class ServiceEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var caption: String,
    var toolbarTitle2: String?,
    var description: String?,
    var disable: Boolean? = false,
    var url: String?,
    var urlType: String?,
//    var tags: List<Any>?,
    var smallIcon: String?,
    var largeIcon: String?,
    @TypeConverters(ServiceTypeConverter::class)
    var items: List<ServiceEntity>? = null,
    var backColor: String? = null,
    var isParent: Boolean,
    var isEmployerServices: Boolean?,
    var isInsuredService: Boolean,
    var isPensionerService: Boolean
)

/*
fun ServiceEntity.asDomainModel(): ServiceModel {
    return ServiceModel(
        id = this.id,
        caption = this.caption,
        toolbarTitle2 = this.toolbarTitle2,
        description = this.description,
        disable = this.disable,
        url = this.url,
        iconPath = this.smallIcon,
        items = this.items?.asDomainModel(),
        isParent = this.isParent,
        isEmployerServices = this.isEmployerServices,
        isInsuredService = this.isInsuredService,
        isPensionerService = this.isPensionerService,
        backColor = this.backColor

    )
}

fun List<ServiceEntity>.asDomainModel(): List<ServiceModel> {
    return map {
        it.asDomainModel()
    }
}
*/



