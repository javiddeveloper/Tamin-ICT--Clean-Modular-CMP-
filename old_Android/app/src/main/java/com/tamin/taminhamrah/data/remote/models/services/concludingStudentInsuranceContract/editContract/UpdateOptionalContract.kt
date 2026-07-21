package com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.editContract

import com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract.GuardianShipDetail
import com.tamin.taminhamrah.ui.home.services.studentContract.model.ContractDataModel

data class UpdateOptionalContract( var provinceCode: String = "")

data class UpdateGuardianOptionalContract(val contract:UpdateOptionalContract,
val protector: GuardianShipDetail){

    constructor(data: ContractDataModel) : this(
        UpdateOptionalContract(),
        GuardianShipDetail(
            data.nationalId,
            data.guardianshipImage?.guid ?: "",
            "قیم نامه",
            data.guardianNationalId,
            data.guardianName,
            data.guardianNumber, data.guardianDateFormatted)
    )
}