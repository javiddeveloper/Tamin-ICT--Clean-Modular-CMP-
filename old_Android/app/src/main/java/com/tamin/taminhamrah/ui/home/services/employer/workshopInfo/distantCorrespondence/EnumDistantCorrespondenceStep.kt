package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.distantCorrespondence

enum class EnumDistantCorrespondenceStep constructor(var title: String, var step: Int) {
    STEP_CORRESPONDENCE_DETAILS("ثبت جزئیات درخواست", 1),
    STEP_DESCRIPTION("توضیحات", 2),
    STEP_UPLOAD_IMAGE("بارگذاری مدارک", 3)
}
