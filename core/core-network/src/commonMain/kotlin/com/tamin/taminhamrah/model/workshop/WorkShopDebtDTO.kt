package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A workshop debt row. Served by two endpoints with the same shape:
 * `debit-online-payment/workshop-debit/…` (جزئیات محاسبه گردش حساب بدهی, payable) and
 * `debit-objection/objection-workshop-debit/…` (اعتراض به بدهی, objectionable).
 *
 * `seqNo` is what separates the two states of an objectionable row: null means no objection has
 * been filed yet, non-null addresses the filed objection's PDF.
 */
@Serializable
data class WorkShopDebtDTO(
    @SerialName("rowNum") val rowNum: Long? = null,
    @SerialName("debitNumber") val debitNumber: String? = null,
    @SerialName("orderRecipeDate") val orderRecipeDate: String? = null,
    @SerialName("mastCustomerCode") val mastCustomerCode: String? = null,
    @SerialName("mastCustomerTypeCode") val mastCustomerTypeCode: String? = null,
    @SerialName("debitAmount") val debitAmount: Long? = null,
    @SerialName("debitRemain") val debitRemain: Long? = null,
    @SerialName("debitStartDate") val debitStartDate: String? = null,
    @SerialName("debitEndDate") val debitEndDate: String? = null,
    @SerialName("peymanSequence") val peymanSequence: String? = null,
    @SerialName("debitCreateReasonCode") val debitCreateReasonCode: String? = null,
    @SerialName("debitCreateReasonDesc") val debitCreateReasonDesc: String? = null,
    @SerialName("debitNumberInstallment") val debitNumberInstallment: String? = null,
    @SerialName("mande") val remainder: String? = null,
    @SerialName("bimehAmount") val insuranceAmount: String? = null,
    @SerialName("bikariAmount") val unemploymentAmount: String? = null,
    @SerialName("sayerAmount") val otherAmount: String? = null,
    /** With [debitStatCode], decides whether the row objects to a برآوردی debt or a بدوی vote. */
    @SerialName("debitStepCode") val debitStepCode: String? = null,
    @SerialName("debitStepDesc") val debitStepDesc: String? = null,
    @SerialName("debitStatCode") val debitStatCode: String? = null,
    @SerialName("debitStatDesc") val debitStatDesc: String? = null,
    @SerialName("stepCat") val stepCategory: String? = null,
    @SerialName("docDateEblaghEjra") val executiveNotifyDate: String? = null,
    @SerialName("badviNo") val primaryVoteNumber: String? = null,
    @SerialName("badviDate") val primaryVoteDate: String? = null,
    @SerialName("calculateDate") val calculateDate: String? = null,
    @SerialName("seqNo") val seqNo: Long? = null,
)
