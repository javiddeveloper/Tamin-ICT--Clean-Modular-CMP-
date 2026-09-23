package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.constructionInsurance.BuildingRequestSummaryDN
import com.tamin.taminhamrah.model.constructionInsurance.BuildingRequestSummaryDTO
import com.tamin.taminhamrah.model.constructionInsurance.PaymentSheetConstructionFileDN
import com.tamin.taminhamrah.model.constructionInsurance.PaymentSheetConstructionFileDTO

fun PaymentSheetConstructionFileDTO.toDomain(): PaymentSheetConstructionFileDN {
    return PaymentSheetConstructionFileDN(
        orderNumber = orderNumber,
        paymentCode = paymentCode,
        paymentSheetAmount = paymentSheetAmount,
        status = status,
        paymentDate = paymentDate,
        buildingRequest = buildingRequest?.toDomain(),
    )
}

fun BuildingRequestSummaryDTO.toDomain(): BuildingRequestSummaryDN {
    return BuildingRequestSummaryDN(
        debitNumber = debitNumber,
        fileNumber = fileNumber,
        requestNumber = requestNumber,
        requestDate = requestDate,
        totalPayment = totalPayment,
        paymentDeadLine = paymentDeadLine,
        workshopInfo = workshopInfo?.toDomain(),
    )
}
