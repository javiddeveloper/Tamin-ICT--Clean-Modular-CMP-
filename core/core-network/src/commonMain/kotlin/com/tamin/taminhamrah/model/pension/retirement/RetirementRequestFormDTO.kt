package com.tamin.taminhamrah.model.pension.retirement

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Body of `POST pension-request?ticketCode={otp}` — the identity and workshop details the insured
 * person confirms in steps 3 and 4, which is what actually creates the request row.
 *
 * `status` is the request's own lifecycle code and is `"0"` at creation; the document PUT that
 * follows raises it. Both values come from the legacy app, which is the wire contract here.
 */
@Serializable
data class RetirementRequestFormDTO(
    @SerialName("activityType") val activityType: String = "",
    @SerialName("address") val address: String = "",
    @SerialName("age") val age: String = "",
    @SerialName("birthDate") val birthDate: Long = 0L,
    @SerialName("branchCode") val branchCode: String = "",
    @SerialName("fatherName") val fatherName: String = "",
    @SerialName("firstName") val firstName: String = "",
    @SerialName("gender") val gender: String = "",
    @SerialName("idNumber") val idNumber: String = "",
    @SerialName("insuranceNumber") val insuranceNumber: String = "",
    @SerialName("issuePlace") val issuePlace: String = "",
    @SerialName("lastName") val lastName: String = "",
    @SerialName("managerName") val managerName: String = "",
    @SerialName("mobileNumber") val mobileNumber: String = "",
    @SerialName("nationalCode") val nationalCode: String = "",
    @SerialName("phoneNumber") val phoneNumber: String = "",
    @SerialName("status") val status: String = "0",
    @SerialName("workshopAddress") val workshopAddress: String = "",
    @SerialName("workshopCode") val workshopCode: String = "",
    @SerialName("workshopName") val workshopName: String = "",
)
