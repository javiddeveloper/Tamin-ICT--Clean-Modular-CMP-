package com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract

import com.tamin.taminhamrah.ui.home.services.studentContract.model.ContractDataModel

data class OptionalContractRequest(
    var brchCodeNew: String? = null,
    var cityCode: String? = null,
    var provinceCode: String? = null

) {

    constructor(data: ContractDataModel) : this(
        data.brchCodeNew,
        data.cityCode,
        data.provinceCode
    )
}
