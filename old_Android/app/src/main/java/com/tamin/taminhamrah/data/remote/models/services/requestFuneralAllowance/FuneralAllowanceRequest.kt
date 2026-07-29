package com.tamin.taminhamrah.data.remote.models.services.requestFuneralAllowance

import com.google.gson.annotations.SerializedName

data class FuneralAllowanceRequest(
    @SerializedName("deadnationalId")
    var deadNationalId: String = "",
    var shorttermRequest: InsuranceInfoModel? = InsuranceInfoModel()
)
    data class InsuranceInfoModel(
        var branchCode: String? = "",
        var branchName: String? = "",
        var insuranceFirstName: String? = "",
        var insuranceLastName: String? = "",
        var mobilNumber: String? = "",
        var nationalCode: String? = "",
        var request: Request? = Request,
        var requestFileList: List<String>? = emptyList(),
        var requestHelpType: String? = "",
        var risuid: String? = ""
    )
    object Request
