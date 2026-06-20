package com.tamin.taminhamrah.model.pension.installment

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class DeferredInstallmentCertificatePR(
    val request: RequestCertificatePR? = null
)

@Immutable
@Serializable
data class RequestCertificatePR(
    val refCode: String? = null
)
