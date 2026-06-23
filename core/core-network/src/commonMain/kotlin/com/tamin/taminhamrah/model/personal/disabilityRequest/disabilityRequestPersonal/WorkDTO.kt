package com.tamin.taminhamrah.model.personal.disabilityRequest.disabilityRequestPersonal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class WorkDTO (
    @SerialName("job") val job: JobDTO? = null,
    @SerialName("workshopId") val workshopId: String? = null,
)
