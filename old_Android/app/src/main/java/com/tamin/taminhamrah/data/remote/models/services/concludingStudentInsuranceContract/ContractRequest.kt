package com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract

import com.tamin.taminhamrah.ui.home.services.studentContract.model.ContractDataModel


/***
On 1400/12/08, because inside the api response, the guid value and guidName are filled in the form of "00" !!!
 */
data class ContractRequest(
    var brchCodeNew: String? = null,
    var cityCode: String? = null,
    var cntDrmn: String? = null,
    var cntFreeJobCode: String = "",
    var guid: String = "00",
    var guidName: String = "00",
    var premiumRateCode: String = "00",
    var provinceCode: String? = null
    ) {
    constructor(data: ContractDataModel, jobCode: String? = null) : this(
        data.brchCodeNew,
        data.cityCode,
        data.cntDrmn,
        jobCode ?: data.cntFreeJobCode,
        data.imageFile?.guid ?: "00",
        data.imageFile?.imageName ?: "00",
        data.premiumRateCode,
        data.provinceCode
    )
}

