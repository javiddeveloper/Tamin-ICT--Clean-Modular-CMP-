package com.tamin.taminhamrah.model.workshop

import androidx.compose.runtime.Immutable

/** One row of پیگیری وضعیت اعتراض — a previously-filed objection and its current status. */
@Immutable
data class WorkShopObjectionPR(
    val seqNo: Long?,
    val workshopId: String,
    val debitNumber: String,
    val objectionNumber: String,
    val objectionDate: String,
    val objectionDescription: String,
    val voteTypeDescription: String,
    val objectionType: WorkShopObjectionType,
    val status: WorkShopObjectionStatus,
)

/** One پیامک tied to one objection — [id] is the message's own identity, not the objection's `seqNo`. */
@Immutable
data class SmsMessagePR(
    val id: Long?,
    val description: String,
    val status: WorkShopObjectionStatus,
)
