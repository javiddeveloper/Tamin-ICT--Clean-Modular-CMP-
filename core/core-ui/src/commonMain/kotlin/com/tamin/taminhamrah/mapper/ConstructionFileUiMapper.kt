package com.tamin.taminhamrah.mapper

import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileDN
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFilePR
import com.tamin.taminhamrah.model.constructionInsurance.WorkshopIdInfoDN
import com.tamin.taminhamrah.model.constructionInsurance.WorkshopIdInfoPR

fun ConstructionFileDN.toPR(): ConstructionFilePR {
    return ConstructionFilePR(
        fileNumber = fileNumber,
        requestNumber = requestNumber,
        requestDate = requestDate,
        workshopInfo = workshopInfo?.toPR(),
        postalCode = postalCode,
        address = address,
        mainPlaque = mainPlaque,
        subPlaque = subPlaque,
        block = block,
        propertyConstruction = propertyConstruction,
        apartment = apartment,
        trade = trade,
        partPlaque = partPlaque,
        sumOfComplications = sumOfComplications,
        debitNumber = debitNumber,
        totalPayment = totalPayment,
        meterage = meterage,
        debitStatusCode = debitStatusCode,
        protrusion = protrusion,
        applicationFees = applicationFees,
        residentialServiceInfrastructureFees = residentialServiceInfrastructureFees,
        excessDensitySurchargeFees = excessDensitySurchargeFees,
        increasePropertyValue = increasePropertyValue,
        issuanceFencingWallConstructionFees = issuanceFencingWallConstructionFees,
        coveredClause3Fees = coveredClause3Fees,
        article100 = article100,
        paymentDeadLine = paymentDeadLine,
    )
}

fun WorkshopIdInfoDN.toPR(): WorkshopIdInfoPR {
    return WorkshopIdInfoPR(
        workshopRegisterDate = workshopRegisterDate,
        workshopId = workshopId,
        brhCode = brhCode,
    )
}
