package com.tamin.taminhamrah.data.mapper.workersPayment

import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitDTO
import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitParamsDN
import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitRequestDTO
import com.tamin.taminhamrah.model.workersPayment.WorkersPayDebitResultDN
import com.tamin.taminhamrah.model.workersPayment.WorkersPaymentInfoDTO
import com.tamin.taminhamrah.model.workersPayment.WorkersPaymentInfoDataDTO
import com.tamin.taminhamrah.model.workersPayment.WorkersPaymentInfoListDN
import com.tamin.taminhamrah.model.workersPayment.WorkersPaymentInfoDN

internal fun WorkersPaymentInfoDTO.toDomain(): WorkersPaymentInfoDN = WorkersPaymentInfoDN(
    pay = pay ?: false,
    payable = payable ?: false,
    fines = fines ?: false,
    year = year.orEmpty(),
    month = month.orEmpty(),
    days = days.orEmpty(),
    professional = professional.orEmpty(),
    professionalTitle = professionalTitle.orEmpty(),
    rate = rate.orEmpty(),
    amount = amount ?: 0L,
    amountFines = amountFines?.trim()?.toLongOrNull(),
    fromDatePersian = fromDatePersian.orEmpty(),
    toDatePersian = toDatePersian.orEmpty(),
    fromDateToDate = fromDateToDate.orEmpty(),
    monthTitle = monthTitle.orEmpty(),
    type = type.orEmpty(),
    salary = salary ?: 0L,
    payDay = payDay ?: 0L,
    paymentStatus = paymentStatus.orEmpty(),
    paymentDate = paymentDate?.trim()?.takeIf { it.isNotEmpty() },
    payableDes = payableDes.orEmpty(),
    fishStatus = fishStatus.orEmpty(),
    maharatStatus = maharatStatus.orEmpty(),
    bazresiStatus = bazresiStatus.orEmpty(),
    kargarStatus = kargarStatus.orEmpty(),
)

internal fun WorkersPaymentInfoDataDTO.toDomain(): WorkersPaymentInfoListDN = WorkersPaymentInfoListDN(
    totalAmount = totalAmount ?: 0L,
    totalPenalty = totalPenalty ?: 0L,
    totalPremium = totalPremium ?: 0L,
    total = total,
    list = list?.map { it.toDomain() }.orEmpty(),
)

/** `redirectUrl` is intentionally dropped here — it travels as the `?type=` query, not in the body. */
internal fun WorkersPayDebitParamsDN.toRequestDTO(): WorkersPayDebitRequestDTO = WorkersPayDebitRequestDTO(
    amount = amount,
    dates = dates,
    fromToDate = fromToDate,
)

internal fun WorkersPayDebitDTO.toDomain(): WorkersPayDebitResultDN = WorkersPayDebitResultDN(
    paymentUrl = list.getOrNull(0),
    ticket = list.getOrNull(1),
    paymentInfo = list.getOrNull(3),
)
