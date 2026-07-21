package com.tamin.taminhamrah.data.local.services.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.tamin.taminhamrah.data.entity.ServiceModel
import com.tamin.taminhamrah.data.local.services.converter.AppliedServiceConverter

@Entity(tableName = "AppliedServicesTbl")
class AppliedServiceEntity(
    @TypeConverters(AppliedServiceConverter::class)
    var serviceModel: ServiceModel,
    var type: Int
) {
    @PrimaryKey
    var id: Int = serviceModel.id
    var visitCount: Int = 0
    var lastVisitTime: Long = -1

    override fun toString(): String {
        return "id=$id service=$serviceModel   visitCount=$visitCount lastVisitTime=$lastVisitTime type=$type"
    }
}
