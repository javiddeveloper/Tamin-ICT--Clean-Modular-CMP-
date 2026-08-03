package com.tamin.taminhamrah.feature.healthProfile.ui.model

enum class LifeStyleStatus(val id: Int, val title: String) {
    EVERY_DAY(1, "هر روز"),
    ONCE_IN_TWO_DAYS(2, "یک روز در میان"),
    WEEKLY(3, "هفتگی"),
    MONTHLY(4, "ماهانه"),
    RARELY(5, "به ندرت"),
    NEVER(6, "هرگز");

    companion object {
        fun fromStyleId(id: Int?): LifeStyleStatus? =
            entries.firstOrNull { it.id == id }
    }
}
