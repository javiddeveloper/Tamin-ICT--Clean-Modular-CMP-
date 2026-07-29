package com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.editContract

import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.GuardianShipDetail
import com.tamin.taminhamrah.ui.home.services.studentContract.model.ContractDataModel

data class UpdateContractRequest(
    var brchCodeNew: String? = null,
    var cityCode: String? = null,
    val cntDrmn: String? = null,
    val cntFreeJobCode: String? = null,
    val guid: String? = null,
    val guidName: String? = null,
    val premiumRateCode: String? = null,
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


data class UpdateGuardianContract(
    val contract: UpdateContractRequest,
    val protector: GuardianShipDetail
) {
    constructor(data: ContractDataModel, jobCode: String? = null) : this(
        UpdateContractRequest(data, jobCode),
        GuardianShipDetail(
            data.nationalId,
            data.guardianshipImage?.guid ?: "",
            "قیم نامه",
            data.guardianNationalId,
            data.guardianName,
            data.guardianNumber,
            data.guardianDateFormatted
        )
    )
}