package com.tamin.taminhamrah.feature.taminServices.workersPayment.model

import com.tamin.taminhamrah.model.workersPayment.WorkersPaymentInfoDN

fun WorkersPaymentInfoDN.toPR(): WorkersPaymentInfoPR = WorkersPaymentInfoPR(
    pay = pay,
    payable = payable,
    month = month,
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
    salary = salary,
    payDay = payDay,
    payableDes = payableDes,
    paymentDate = paymentDate,
    fishStatus = fishStatus,
    maharatStatus = maharatStatus,
    bazresiStatus = bazresiStatus,
    kargarStatus = kargarStatus,
    type = type,
    fromDateToDate = fromDateToDate,
)
