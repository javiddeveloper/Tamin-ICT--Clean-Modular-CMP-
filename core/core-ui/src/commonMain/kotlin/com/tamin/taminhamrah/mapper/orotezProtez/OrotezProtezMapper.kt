package com.tamin.taminhamrah.mapper.orotezProtez

import com.tamin.taminhamrah.model.orotezProtez.BranchWorkshopDN
import com.tamin.taminhamrah.model.orotezProtez.BranchWorkshopPR
import com.tamin.taminhamrah.model.orotezProtez.InsuredPersonDN
import com.tamin.taminhamrah.model.orotezProtez.InsuredPersonPR
import com.tamin.taminhamrah.model.orotezProtez.RequestInsuredMainInfoDN
import com.tamin.taminhamrah.util.toFormattedDate

fun BranchWorkshopDN.toPresentation(): BranchWorkshopPR {
    return BranchWorkshopPR(
        id = "$branchCode-$workshopCode",
        label = listOfNotNull(branchName, workshopName).joinToString(" - "),
    )
}

fun RequestInsuredMainInfoDN.toBranchWorkshopPresentationList(): List<BranchWorkshopPR> =
    branchWorkshops.map { it.toPresentation() }

fun InsuredPersonDN.toPresentation(): InsuredPersonPR {
    val fullName = listOfNotNull(firstName, lastName).joinToString(" ")
    return InsuredPersonPR(
        id = insuredId.orEmpty(),
        label = listOfNotNull(relationship, fullName.ifBlank { null }).joinToString(" - "),
        fullName = fullName,
        relation = relationship.orEmpty(),
        nationalCode = nationalCode.orEmpty(),
        birthCertificateNumber = birthCertificateNumber.orEmpty(),
        issuePlace = cityName.orEmpty(),
        birthDateLabel = birthDate?.toFormattedDate().orEmpty(),
        bookletValidUntilLabel = bookletValidUntil.orEmpty(),
    )
}
