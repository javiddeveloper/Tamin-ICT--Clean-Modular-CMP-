package com.tamin.taminhamrah.feature.taminServices.workersPayment.ui.components

import com.tamin.taminhamrah.feature.taminServices.workersPayment.model.WorkersPaymentInfoPR

/** Shared sample row for the workers-payment component previews. */
internal fun workersPaymentPreviewItem(
    amountFines: Long = 0L,
): WorkersPaymentInfoPR = WorkersPaymentInfoPR(
    pay = false,
    payable = true,
    month = "05",
    monthTitle = "حق بیمه مرداد",
    year = "1404",
    professionalTitle = "بنّای سفت‌کار",
    professional = "7112",
    rate = "1.9",
    days = "31",
    fromDatePersian = "14040501",
    toDatePersian = "14040531",
    amount = 8940000,
    amountFines = amountFines,
    totalPayable = 8940000 + amountFines,
    salary = 2312000,
    payDay = 5541850,
    payableDes = "هست",
    paymentDate = null,
    fishStatus = "دارد",
    maharatStatus = "دارد",
    bazresiStatus = "دارد",
    kargarStatus = "فعال می‌باشد.",
    type = "Premium",
    fromDateToDate = "1404050114040531",
)
