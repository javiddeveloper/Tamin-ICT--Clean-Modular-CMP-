package com.tamin.taminhamrah.mapper

import com.tamin.taminhamrah.model.constructionInsurance.BuildingRequestSummaryDN
import com.tamin.taminhamrah.model.constructionInsurance.BuildingRequestSummaryPR
import com.tamin.taminhamrah.model.constructionInsurance.PaymentSheetConstructionFileDN
import com.tamin.taminhamrah.model.constructionInsurance.PaymentSheetConstructionFilePR

fun PaymentSheetConstructionFileDN.toPR(): PaymentSheetConstructionFilePR {
    return PaymentSheetConstructionFilePR(
        orderNumber = orderNumber,
        paymentCode = paymentCode,
        paymentSheetAmount = paymentSheetAmount,
        status = status,
        paymentDate = paymentDate,
        buildingRequest = buildingRequest?.toPR(),
    )
}

fun BuildingRequestSummaryDN.toPR(): BuildingRequestSummaryPR {
    return BuildingRequestSummaryPR(
        debitNumber = debitNumber,
        fileNumber = fileNumber,
        requestNumber = requestNumber,
        requestDate = requestDate,
        totalPayment = totalPayment,
        paymentDeadLine = paymentDeadLine,
        workshopInfo = workshopInfo?.toPR(),
    )
}
