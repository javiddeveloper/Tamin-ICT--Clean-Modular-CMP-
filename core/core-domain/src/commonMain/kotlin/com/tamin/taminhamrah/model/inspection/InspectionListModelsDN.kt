package com.tamin.taminhamrah.model.inspection

data class InspectionPerformedListDN(
    val total: Int,
    val list: List<InspectionPerformedDN>
)

data class BranchListDN(
    val total: Int,
    val list: List<BranchDN>
)

data class JobListDN(
    val total: Int,
    val list: List<JobDN>
)
