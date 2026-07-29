package com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract

import com.tamin.taminhamrah.ui.home.services.studentContract.model.ContractDataModel

data class OptionalContractByGuardian(
    val contract: OptionalContractRequest,
    val protector: GuardianShipDetail
) {
    constructor(data: ContractDataModel) : this(
        OptionalContractRequest(data),
        GuardianShipDetail(
            data.nationalId,
            data.guardianshipImage?.guid ?: "",
            "تصویر قیم نامه",
            data.guardianNationalId,
            data.guardianName,
            data.guardianNumber,
            data.guardianDateFormatted
        )
    )
}
