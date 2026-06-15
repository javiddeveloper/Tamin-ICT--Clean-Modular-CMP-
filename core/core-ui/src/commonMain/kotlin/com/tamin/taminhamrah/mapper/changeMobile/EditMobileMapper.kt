package com.tamin.taminhamrah.mapper.changeMobile

import com.tamin.taminhamrah.model.changeMobile.EditMobilePR
import com.tamin.taminhamrah.model.changeMobile.EditMobileResponsePR
import com.tamin.taminhamrah.model.user.EditMobileDN
import com.tamin.taminhamrah.model.user.EditMobileResponseDN

fun EditMobileResponseDN.toPresentation(): EditMobileResponsePR {
    return EditMobileResponsePR(
        traceId = this.traceId ?: "",
        data = this.data?.toPresentation()
    )
}

fun EditMobileDN.toPresentation(): EditMobilePR {
    return EditMobilePR(
        hash = this.hash ?: "",
        expirationTime = this.expirationTime
    )
}
