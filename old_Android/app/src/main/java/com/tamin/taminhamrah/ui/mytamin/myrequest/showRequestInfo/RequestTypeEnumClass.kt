package com.tamin.taminhamrah.ui.mytamin.myrequest.showRequestInfo

import com.tamin.taminhamrah.R

enum class RequestTypeEnumClass (val serviceId:Int,val serviceNameRes:Int) {
    ILL_DAY(serviceId = 10 , serviceNameRes = R.string.request_ill_day),
    ORTHOTICS_PROSTHESIS(serviceId = 12 , serviceNameRes = R.string.label_allowances_orthotics_prosthesis),
    ARTICLE16(serviceId = 26 , serviceNameRes = R.string.article_16_service),
    PREGNANCY(serviceId = 11 ,serviceNameRes = R.string.request_pregnancy_wage),
    DEFERRED_INSTALLMENT_CERTIFICATE(serviceId = 22 ,serviceNameRes = R.string.show_deferred_installment_certificate),
    FOLLOW_UP_RESULT_OBJECTION_HISTORY_NONE_EXIST(serviceId = 8 ,serviceNameRes = R.string.register_request_objection_non_exist),
    MEDICAL_COMMISSION(serviceId = 27 ,serviceNameRes = R.string.medial_commission)
}
