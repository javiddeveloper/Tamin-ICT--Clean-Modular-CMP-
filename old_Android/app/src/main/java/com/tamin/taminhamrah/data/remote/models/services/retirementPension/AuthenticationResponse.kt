package com.tamin.taminhamrah.data.remote.models.services.retirementPension

import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

data class AuthenticationResponse(val data: AuthenticationModel? = null) : BaseResponseNew()

data class AuthenticationModel(val mobileNumber: String)
