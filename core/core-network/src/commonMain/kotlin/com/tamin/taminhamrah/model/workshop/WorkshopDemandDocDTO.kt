package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * One row of `debit-objection/eclaim-detail-objection-workshop-debit/{debitNumber}/{branchCode}`
 * — اسناد مطالبه for a single debt.
 *
 * Four of the keys are all lower case on the wire (`debitstepdesc`, `debitstatedisc` — the second
 * misspelt as well). Both are the contract.
 */
@Serializable
data class WorkshopDemandDocDTO(
    @SerialName("docNumber") val docNumber: String? = null,
    @SerialName("docDate") val docDate: String? = null,
    @SerialName("docTypeCode") val docTypeCode: String? = null,
    @SerialName("docTypeDescription") val docTypeDescription: String? = null,
    @SerialName("debitNumber") val debitNumber: String? = null,
    @SerialName("debitstepdesc") val debitStepDesc: String? = null,
    @SerialName("debitstatedisc") val debitStateDesc: String? = null,
    @SerialName("debitcrtreasoncode") val debitCreateReasonCode: Int? = null,
    @SerialName("debitcrtreasondesc") val debitCreateReasonDesc: String? = null,
    @SerialName("claimSequens") val claimSequence: String? = null,
    @SerialName("claimdescription") val claimDescription: String? = null,
)
