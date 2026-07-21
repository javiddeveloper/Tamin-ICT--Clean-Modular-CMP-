package com.tamin.taminhamrah.data.local.services.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.tamin.taminhamrah.data.local.services.converter.LastSeenServiceTypeConverter
import com.tamin.taminhamrah.data.local.services.converter.TimeStampTypeConverter
import java.sql.Timestamp

@Entity(tableName = "service_log")
class LastSeenServiceEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @TypeConverters(LastSeenServiceTypeConverter::class)
    var lastSeenService:ServiceEntity?,
    @TypeConverters(TimeStampTypeConverter::class)
    var visitTime:Timestamp?,
    var testItem : String
)
