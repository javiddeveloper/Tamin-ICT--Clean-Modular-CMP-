package com.tamin.taminhamrah.utils

import com.tamin.taminhamrah.data.remote.models.services.CheckUpdateResponse
import com.tamin.taminhamrah.data.remote.models.services.PostLogResponse
import com.tamin.taminhamrah.data.remote.models.services.WeddingPresentResponse
import com.tamin.taminhamrah.data.remote.models.services.orthosisInfoResponse.InsuredOrthosisInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.requestFuneralAllowance.FuneralAllowanceResponse
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.LatestInsuranceInfoResponse
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.BranchListResponse
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.DependentInfoResponse

object SpecialResponses {

    val dismissDialogForResponse = listOf(CheckUpdateResponse::class.java.simpleName,PostLogResponse::class.java.simpleName)

    val finishPageForResponse = listOf(InsuredOrthosisInfoResponse::class.java.simpleName,
        LatestInsuranceInfoResponse::class.java.simpleName, WeddingPresentResponse::class.java.simpleName,
        BranchListResponse::class.java.simpleName,DependentInfoResponse::class.java.simpleName, FuneralAllowanceResponse::class.java.simpleName)

}