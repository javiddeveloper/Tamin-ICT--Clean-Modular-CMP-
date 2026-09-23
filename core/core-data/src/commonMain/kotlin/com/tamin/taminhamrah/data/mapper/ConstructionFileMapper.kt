package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.data.local.entity.ConstructionFileEntity
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileDN
import com.tamin.taminhamrah.model.constructionInsurance.ConstructionFileDTO
import com.tamin.taminhamrah.model.constructionInsurance.WorkshopIdInfoDN
import com.tamin.taminhamrah.model.constructionInsurance.WorkshopIdInfoDTO

fun ConstructionFileDTO.toDomain(): ConstructionFileDN {
    return ConstructionFileDN(
        fileNumber = fileNumber,
        requestNumber = requestNumber,
        requestDate = requestDate,
        workshopInfo = workshopInfo?.toDomain(),
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
        paymentDeadLine = paymentDeadLine
    )
}

fun WorkshopIdInfoDTO.toDomain(): WorkshopIdInfoDN {
    return WorkshopIdInfoDN(
        workshopRegisterDate = workshopRegisterDate,
        workshopId = workshopId,
        brhCode = brhCode ?: branchCode
    )
}

fun ConstructionFileEntity.toDomain(): ConstructionFileDN {
    return ConstructionFileDN(
        fileNumber = fileNumber,
        requestNumber = requestNumber,
        requestDate = requestDate,
        workshopInfo = WorkshopIdInfoDN(
            workshopRegisterDate = workshopRegisterDate,
            workshopId = workshopId,
            brhCode = brhCode
        ),
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
        paymentDeadLine = paymentDeadLine
    )
}

fun ConstructionFileDTO.toEntity(): ConstructionFileEntity {
    return ConstructionFileEntity(
        fileNumber = fileNumber ?: 0L,
        requestNumber = requestNumber,
        requestDate = requestDate,
        workshopId = workshopInfo?.workshopId,
        workshopRegisterDate = workshopInfo?.workshopRegisterDate,
        brhCode = workshopInfo?.brhCode ?: workshopInfo?.branchCode,
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
        paymentDeadLine = paymentDeadLine
    )
}
