package com.tamin.taminhamrah.feature.profile.data.mapper

import com.tamin.taminhamrah.model.subDominant.insuredActiveBranch.InsuredActiveBranchDTO
import com.tamin.taminhamrah.model.subdominant.insuredActiveBranch.InsuredActiveBranchDN

fun  InsuredActiveBranchDTO.toDomain() : InsuredActiveBranchDN {
    return InsuredActiveBranchDN(
        branchCode = branchCode,
        branchName = branchName,
        workshopCode = workshopCode,
        workshopName = workshopName,
    )
}
