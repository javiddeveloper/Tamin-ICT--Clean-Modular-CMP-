package com.tamin.taminhamrah.model.pension.retirement

/**
 * The identity and workshop details that create a retirement request.
 *
 * Every field is non-null with an empty default because the server rejects nulls here — the legacy
 * request model carries the same defaults. [status] is the request's lifecycle code at creation.
 */
data class RetirementRequestFormDN(
    val activityType: String = "",
    val address: String = "",
    val age: String = "",
    val birthDate: Long = 0L,
    val branchCode: String = "",
    val fatherName: String = "",
    val firstName: String = "",
    val gender: String = "",
    val idNumber: String = "",
    val insuranceNumber: String = "",
    val issuePlace: String = "",
    val lastName: String = "",
    val managerName: String = "",
    val mobileNumber: String = "",
    val nationalCode: String = "",
    val phoneNumber: String = "",
    val status: String = RETIREMENT_REQUEST_STATUS_CREATED,
    val workshopAddress: String = "",
    val workshopCode: String = "",
    val workshopName: String = "",
)

/** Lifecycle code a freshly created request carries. */
const val RETIREMENT_REQUEST_STATUS_CREATED: String = "0"

/** Lifecycle code the document upload (`PUT pension-request/{id}`) raises the request to. */
const val RETIREMENT_REQUEST_STATUS_DOCUMENTS: String = "4"
