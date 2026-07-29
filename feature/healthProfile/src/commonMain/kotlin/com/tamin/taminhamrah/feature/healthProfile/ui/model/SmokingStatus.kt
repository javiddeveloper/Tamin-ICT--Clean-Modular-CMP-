package com.tamin.taminhamrah.feature.healthProfile.ui.model

enum class SmokingStatus(val id: Int) {
    NONE(0),
    NEVER_CONSUMED(3);

    companion object {
        fun fromId(id: Int?): SmokingStatus? =
            entries.firstOrNull { it.id == id }
    }
}
