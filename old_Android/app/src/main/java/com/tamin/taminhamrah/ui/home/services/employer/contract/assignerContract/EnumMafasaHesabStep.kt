package com.tamin.taminhamrah.ui.home.services.employer.contract.assignerContract

enum class EnumMafasaHesabStep constructor(var title: String, var step: Int) {
    STEP_CONTRACT_INFO("اطلاعات قرارداد", 1),
    STEP_LETTER_INFO("اعلام مشخصات نامه و ارسال آن", 2),
    STEP_UPLOAD_IMAGE("بارگذاری مدارک", 3),
    STEP_CONTRACT_STATUS_BY_SUBJECT("شرایط قرارداد با توجه به موضوع کار", 4)
}
