package com.tamin.taminhamrah.model.pension

data class EdictPensionerDN(
    val lastName: String? = null,
    val branchName: String? = null,
    val insuranceId: String? = null,
    val title: String? = null,
    val edictYear: String = "0",
    val edictMonth: String = "0",
    val edictInfo: EdictInfoDN? = null,
    val survivorInfo: List<SurvivorInfoDN>? = null,
    val detail: List<EdictPensionerDetailDN>? = null,
)
