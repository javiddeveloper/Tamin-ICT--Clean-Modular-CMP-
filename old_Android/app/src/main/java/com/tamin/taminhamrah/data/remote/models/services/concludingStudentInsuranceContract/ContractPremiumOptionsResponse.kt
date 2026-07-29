package com.tamin.taminhamrah.data.remote.models.services.concludingStudentInsuranceContract

import androidx.room.Ignore
import com.tamin.taminhamrah.data.remote.models.ListDataModel


class ContractPremiumOptionsResponse : ListDataModel<PremiumOptionsModel>()

data class PremiumOptionsModel(
    val spcrateDescription: String? = null,
    val spcrateCode: String? = null,
    val insurDpercent : String = "",
    @Ignore
    var idSelected: Boolean = false,
    @Ignore
    var isDisable: Boolean = false
)
