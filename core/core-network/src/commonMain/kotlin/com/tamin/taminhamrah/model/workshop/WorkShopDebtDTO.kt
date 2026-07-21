package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkShopDebtDTO(
    @SerialName("rowNum") val rowNum: Long?,
    @SerialName("debitNumber") val debitNumber: String?,
    @SerialName("orderRecipeDate") val orderRecipeDate: String?,
    @SerialName("mastCustomerCode") val mastCustomerCode: String?,
    @SerialName("debitAmount") val debitAmount: Long?,
    @SerialName("debitRemain") val debitRemain: Long?,
    @SerialName("debitStartDate") val debitStartDate: String?,
    @SerialName("debitEndDate") val debitEndDate: String?,
    @SerialName("mastCustomerTypeCode") val mastCustomerTypeCode: String?,
    @SerialName("peymanSequence") val peymanSequence: String?,
    @SerialName("debitCreateReasonCode") val debitCreateReasonCode: String?,
    @SerialName("debitCreateReasonDesc") val debitCreateReasonDesc: String?,
    @SerialName("debitNumberInstallment") val debitNumberInstallment: String?,
    @SerialName("mande") val mande: String?,
    @SerialName("bimehAmount") val bimehAmount: String?,
    @SerialName("bikariAmount") val bikariAmount: String?,
    @SerialName("sayerAmount") val sayerAmount: String?,
    @SerialName("debitStepCode") val debitStepCode: String?,
    @SerialName("debitStepDesc") val debitStepDesc: String?,
    @SerialName("debitStatDesc") val debitStatDesc: String?,
    @SerialName("debitStatCode") val debitStatCode: String?,
    @SerialName("stepCat") val stepCat: String?,
    @SerialName("docDateEblaghEjra") val docDateEblaghEjra: String?,
    @SerialName("badviNo") val badviNo: String?,
    @SerialName("badviDate") val badviDate: String?,
    @SerialName("calculateDate") val calculateDate: String?,
    @SerialName("seqNo") val seqNo: Long?
)
