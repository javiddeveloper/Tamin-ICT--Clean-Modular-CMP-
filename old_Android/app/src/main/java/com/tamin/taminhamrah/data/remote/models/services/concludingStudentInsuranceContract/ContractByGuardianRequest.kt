package com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract

import com.tamin.taminhamrah.ui.home.services.studentContract.model.ContractDataModel

data class ContractByGuardianRequest(
    val contract: ContractRequest,
    val protector: GuardianShipDetail
) {
    constructor(data: ContractDataModel, jobCode: String? = null) : this(
        ContractRequest(data, jobCode),
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


data class GuardianShipDetail(
    val proCode: String,
    val guid: String,
    val guidName: String,
    val nid: String,
    val fullName: String,
    val protectorLetterNo: String,
    val protectorLetterDate: String
)

/*
   "proCode":"3860387200",
      "guid":"6f1c66e4-1ecf-441a-8868-96e1b2f29157",
      "guidName":"قیم نامه",
      "nid":"0083834001",
      "fullName":"رضا نادری",
      "protectorLetterNo":"222222222222",
      "protectorLetterDate":"2022-09-18T19:30:00.000Z"
 */


/*
  "proCode":"0017312213",
      "guid":"29579345-8889-4b22-b701-12409fe6ee67",
      "guidName":"قیم نامه",
      "nid":"1990321488",
      "fullName":"ABCD",
      "protectorLetterNo":"123456",
      "protectorLetterDate":"2022-07-26T19:30:00.000Z"
 */