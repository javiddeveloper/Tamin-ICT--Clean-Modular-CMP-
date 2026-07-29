package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.article16.model

import com.tamin.taminhamrah.R

enum class FilterWorkshopEnumClass(var title:Int, var id:String) {
    DELETE_FILTER(title = R.string.delete_filter, id = "00"),
    ACTIVE_WORKSHOP(title = R.string.active, id = "01"),
    SEMI_ACTIVE_WORKSHOP(title = R.string.semi_active, id = "02"),
    INACTIVE_WORKSHOP(title = R.string.inactive, id = "03")
}
