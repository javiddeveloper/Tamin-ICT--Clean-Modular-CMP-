package com.tamin.taminhamrah.feature.taminServices.workersPayment.model

import com.tamin.taminhamrah.model.workersPayment.WorkersPaymentInfoDN

fun WorkersPaymentInfoDN.toPR(): WorkersPaymentInfoPR = WorkersPaymentInfoPR(
    payable = payable,
    monthTitle = monthTitle,
    year = year,
    professionalTitle = professionalTitle,
    professional = professional,
    rate = rate,
    days = days,
    fromDatePersian = fromDatePersian,
    toDatePersian = toDatePersian,
    amount = amount,
    amountFines = amountFines ?: 0L,
    totalPayable = totalPayable,
    payDay = payDay,
    payableDes = payableDes,
    fishStatus = fishStatus,
    maharatStatus = maharatStatus,
    bazresiStatus = bazresiStatus,
    kargarStatus = kargarStatus,
    type = type,
    fromDateToDate = fromDateToDate,
)
