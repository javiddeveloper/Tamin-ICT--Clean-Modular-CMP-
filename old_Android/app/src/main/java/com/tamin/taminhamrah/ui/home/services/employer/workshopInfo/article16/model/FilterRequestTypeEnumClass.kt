package com.tamin.taminhamrah.ui.home.services.employer.workshopInfo.article16.model

import com.tamin.taminhamrah.R

enum class FilterRequestTypeEnumClass(var title:Int, var id:String?) {
    DELETE_FILTER(title = R.string.delete_filter, id = "0"),
    NEW_REQUEST(title = R.string.label_new_request, id = "1"),
    DOC_VIOLATION_STATE(title = R.string.doc_violation_state, id = "7"),
    REJECT_REQUEST_STATE(title = R.string.reject_request_state, id = "8"),
    CONFIRM_REQUEST_STATE(title = R.string.confirm_request_state, id = "9"),
    UNKNOWN_STATE(title = R.string.unknown_state, id = null)
}

