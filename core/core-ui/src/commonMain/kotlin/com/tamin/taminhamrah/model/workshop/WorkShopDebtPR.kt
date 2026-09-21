package com.tamin.taminhamrah.model.workshop

import androidx.compose.runtime.Immutable

/**
 * One بدهی row, shown by both the payable list and the objectionable list.
 *
 * [debitNumber] and [agreementRow] stay raw because they address documents, PDFs and the payment
 * call; the `…Label` twin of each is the same value as the row prints it.
 */
@Immutable
data class WorkShopDebtPR(
    val debitNumber: String = "",
    val debitNumberLabel: String = "",
    val agreementRow: String = "",
    val agreementRowLabel: String = "",
    val notifyDate: String = "",
    val customerCode: String = "",
    val amount: String = "",
    val remainingAmount: String = "",
    val fromDate: String = "",
    val toDate: String = "",
    /** The بدوی vote block is only part of the row when the service sent a vote number. */
    val hasPrimaryVote: Boolean = false,
    val primaryVoteNumber: String = "",
    val primaryVoteDate: String = "",
    val objectionKind: ObjectionKind = ObjectionKind.FILED,
    /** Addresses the filed objection's PDF; null while no objection has been filed. */
    val objectionSeqNo: Long? = null,
)

/** One سند مطالبه of a debt. */
@Immutable
data class WorkshopDemandDocPR(
    val docNumber: String = "",
    val docNumberLabel: String = "",
    val docDate: String = "",
    val docType: String = "",
    val step: String = "",
    val state: String = "",
    val isViewable: Boolean = false,
)
