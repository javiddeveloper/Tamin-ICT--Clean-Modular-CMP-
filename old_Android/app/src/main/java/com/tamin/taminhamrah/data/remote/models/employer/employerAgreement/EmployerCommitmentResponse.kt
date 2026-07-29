package com.tamin.taminhamrah.data.remote.models.employer.employerAgreement

import com.tamin.taminhamrah.data.remote.models.BaseResponseNew
import com.tamin.taminhamrah.data.remote.models.services.request_pregnancy_pay.CurrentUserModel

class EmployerCommitmentResponse(
    var data: CurrentUserModel? = null
) : BaseResponseNew()