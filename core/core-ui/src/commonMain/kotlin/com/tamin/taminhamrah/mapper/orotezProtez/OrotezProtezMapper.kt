package com.tamin.taminhamrah.mapper.orotezProtez

import com.tamin.taminhamrah.model.orotezProtez.BranchWorkshopDN
import com.tamin.taminhamrah.model.orotezProtez.BranchWorkshopPR
import com.tamin.taminhamrah.model.orotezProtez.RequestInsuredMainInfoDN

fun BranchWorkshopDN.toPresentation(): BranchWorkshopPR {
    return BranchWorkshopPR(
        id = "$branchCode-$workshopCode",
        label = listOfNotNull(branchName, workshopName).joinToString(" - "),
    )
}

fun RequestInsuredMainInfoDN.toBranchWorkshopPresentationList(): List<BranchWorkshopPR> =
    branchWorkshops.map { it.toPresentation() }
