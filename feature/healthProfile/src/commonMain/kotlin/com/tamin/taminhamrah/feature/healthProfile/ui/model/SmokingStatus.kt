package com.tamin.taminhamrah.feature.healthProfile.ui.model

enum class SmokingStatus(val id: Int,val type: String) {
    NONE(0,"هرگز مصرف نشده"),
    NEVER_CONSUMED(3,"هرگز مصرف نشده");

    companion object {
        fun fromId(id: Int?): SmokingStatus? =
            entries.firstOrNull { it.id == id }
    }
}
