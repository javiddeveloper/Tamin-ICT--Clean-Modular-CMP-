package com.tamin.taminhamrah.mapper.changeMobile

import com.tamin.taminhamrah.model.changeMobile.ChronologyPR
import com.tamin.taminhamrah.model.changeMobile.EditMobilePR
import com.tamin.taminhamrah.model.changeMobile.EditMobileResponsePR
import com.tamin.taminhamrah.model.changeMobile.ExpirationTimePR
import com.tamin.taminhamrah.model.user.ChronologyDN
import com.tamin.taminhamrah.model.user.EditMobileDN
import com.tamin.taminhamrah.model.user.EditMobileResponseDN
import com.tamin.taminhamrah.model.user.ExpirationTimeDN

fun EditMobileResponseDN.toPresentation(): EditMobileResponsePR {
    return EditMobileResponsePR(
        traceId = this.traceId ?: "",
        data = this.data?.toPresentation()
    )
}

fun EditMobileDN.toPresentation(): EditMobilePR {
    return EditMobilePR(
        hash = this.hash ?: "",
        expirationTime = this.expirationTime?.toPresentation()
    )
}

fun ExpirationTimeDN.toPresentation(): ExpirationTimePR {
    return ExpirationTimePR(
        year = this.year ?: 0,
        month = this.month ?: "",
        nano = this.nano ?: 0L,
        monthValue = this.monthValue ?: 0,
        dayOfMonth = this.dayOfMonth ?: 0,
        hour = this.hour ?: 0,
        minute = this.minute ?: 0,
        second = this.second ?: 0,
        dayOfWeek = this.dayOfWeek ?: "",
        dayOfYear = this.dayOfYear ?: 0,
        chronology = this.chronology?.toPresentation()
    )
}

fun ChronologyDN.toPresentation(): ChronologyPR {
    return ChronologyPR(
        calendarType = this.calendarType ?: "",
        id = this.id ?: ""
    )
}
